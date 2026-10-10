package com.aincrad.social.guild;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core guild data model. Owns the roster, rank assignments, bank, XP tracker,
 * skill ranks and guild metadata. Mutations go through guarded helpers that
 * enforce caps and permissions; lookups return unmodifiable snapshots.
 */
public final class Guild {

    private final String name;
    private final String tag;
    private final String ownerUuid;
    private final Instant createdAt;

    private final Map<String, GuildRank> members = new ConcurrentHashMap<>();
    private final Map<GuildSkill, Integer> skillRanks = new EnumMap<>(GuildSkill.class);

    private GuildBank bank;
    private GuildXP xp;
    private String notice = "";
    private Instant disbandedAt;

    /**
     * Creates a new guild in an active state.
     *
     * @param name     guild display name, 1..{@link GuildLevel#MAX_NAME_LENGTH}
     * @param tag      guild tag, exactly {@link GuildLevel#TAG_LENGTH} chars
     * @param ownerUuid unique id of the founding player (becomes MASTER)
     * @throws IllegalArgumentException if name/tag are blank or malformed
     */
    public Guild(String name, String tag, String ownerUuid) {
        this.name = validateName(name);
        this.tag = validateTag(tag);
        this.ownerUuid = Objects.requireNonNull(ownerUuid, "ownerUuid");
        this.createdAt = Instant.now();
        this.bank = new GuildBank(1);
        this.xp = new GuildXP();
        this.members.put(ownerUuid, GuildRank.MASTER);
    }

    // -------------------------------------------------------------------------
    // Metadata
    // -------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /** Returns true once the guild has been disbanded and should not be used. */
    public boolean isDisbanded() {
        return disbandedAt != null;
    }

    public Instant getDisbandedAt() {
        return disbandedAt;
    }

    public String getNotice() {
        return notice;
    }

    /**
     * Updates the guild notice.
     *
     * @param notice new notice; blank is treated as clearing
     * @throws IllegalArgumentException if length exceeds {@link GuildLevel#MAX_NOTICE_LENGTH}
     */
    public void setNotice(String notice) {
        String cleaned = notice == null ? "" : notice.strip();
        if (cleaned.length() > GuildLevel.MAX_NOTICE_LENGTH) {
            throw new IllegalArgumentException(
                    "notice must be <= " + GuildLevel.MAX_NOTICE_LENGTH + " chars");
        }
        this.notice = cleaned;
    }

    /** Marks the guild as disbanded. Idempotent — only the first call sticks. */
    public void markDisbanded() {
        if (this.disbandedAt == null) {
            this.disbandedAt = Instant.now();
        }
    }

    // -------------------------------------------------------------------------
    // Roster
    // -------------------------------------------------------------------------

    /** Current guild level (delegates to XP tracker). */
    public int getLevel() {
        return xp.getLevel();
    }

    /** Inclusive member cap at the current level. */
    public int getMemberCap() {
        return GuildLevel.memberCap(getLevel());
    }

    /** Inclusive officer cap at the current level. */
    public int getOfficerCap() {
        return GuildLevel.officerCap(getLevel());
    }

    /** Unmodifiable view of every member mapped to their current rank. */
    public Map<String, GuildRank> getMembers() {
        return Collections.unmodifiableMap(new HashMap<>(members));
    }

    /** Current rank for a player, or empty if they are not in the guild. */
    public Optional<GuildRank> getRank(String uuid) {
        return Optional.ofNullable(members.get(uuid));
    }

    /** True when the player holds the given rank (or higher weight). */
    public boolean hasRankOrAbove(String uuid, GuildRank required) {
        GuildRank current = members.get(uuid);
        return current != null && current.getWeight() >= required.getWeight();
    }

    /**
     * Adds a player at MEMBER rank. Fails when the guild is full or the player
     * is already a member.
     *
     * @param uuid player id
     * @return true when the player was added
     * @throws IllegalStateException if the guild is full or already contains the player
     */
    public boolean addMember(String uuid) {
        Objects.requireNonNull(uuid, "uuid");
        if (isDisbanded()) {
            throw new IllegalStateException("guild is disbanded");
        }
        if (members.containsKey(uuid)) {
            throw new IllegalStateException(uuid + " is already a member");
        }
        if (members.size() >= getMemberCap()) {
            throw new IllegalStateException(
                    "guild is full (" + members.size() + "/" + getMemberCap() + ")");
        }
        members.put(uuid, GuildRank.MEMBER);
        return true;
    }

    /**
     * Removes a player from the guild. The MASTER rank cannot be removed —
     * transfer ownership or disband instead.
     *
     * @param uuid player id
     * @return true when the player was removed
     * @throws IllegalStateException if the player is the guild master
     */
    public boolean removeMember(String uuid) {
        Objects.requireNonNull(uuid, "uuid");
        if (uuid.equals(ownerUuid)) {
            throw new IllegalStateException("cannot remove the guild master");
        }
        return members.remove(uuid) != null;
    }

    /**
     * Assigns a rank to an existing member. Promotions above OFFICER are
     * rejected when the officer cap is reached.
     *
     * @param uuid player id
     * @param rank target rank
     * @return the previous rank
     * @throws IllegalStateException if the player is not a member, the master
     *         rank is targeted, or the officer cap would be exceeded
     */
    public GuildRank assignRank(String uuid, GuildRank rank) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(rank, "rank");
        GuildRank previous = members.get(uuid);
        if (previous == null) {
            throw new IllegalStateException(uuid + " is not a member");
        }
        if (rank == GuildRank.MASTER) {
            throw new IllegalStateException(
                    "MASTER cannot be assigned; transfer ownership instead");
        }
        if (rank == GuildRank.OFFICER) {
            long officers = members.values().stream()
                    .filter(r -> r == GuildRank.OFFICER)
                    .count();
            if (previous != GuildRank.OFFICER && officers >= getOfficerCap()) {
                throw new IllegalStateException(
                        "officer cap reached (" + getOfficerCap() + ")");
            }
        }
        members.put(uuid, rank);
        return previous;
    }

    /**
     * Package-private hook used by {@link GuildManager} to swap ownership
     * without exposing a public MASTER-assignment path.
     *
     * @param fromUuid outgoing master
     * @param toUuid   incoming master
     */
    void transferOwner(String fromUuid, String toUuid) {
        members.put(toUuid, GuildRank.MASTER);
        members.put(fromUuid, GuildRank.OFFICER);
    }

    /** Number of officers currently appointed. */
    public long getOfficerCount() {
        return members.values().stream()
                .filter(r -> r == GuildRank.OFFICER)
                .count();
    }

    // -------------------------------------------------------------------------
    // Bank
    // -------------------------------------------------------------------------

    public GuildBank getBank() {
        return bank;
    }

    /**
     * Resizes the bank to match a new guild level. Existing stacks are
     * preserved; shrinking below the current occupancy truncates the tail.
     *
     * @param newLevel the level being applied
     */
    public void resizeBankForLevel(int newLevel) {
        GuildBank resized = new GuildBank(newLevel);
        for (GuildBank.Stack stack : bank.getSlots()) {
            if (stack == null) continue;
            if (resized.deposit(stack, "level-up") < 0) break; // dropped on shrink
        }
        this.bank = resized;
    }

    // -------------------------------------------------------------------------
    // XP & skills
    // -------------------------------------------------------------------------

    public GuildXP getXp() {
        return xp;
    }

    /**
     * Adds XP through the shared tracker and, if levels were gained, resizes
     * the bank to the new capacity.
     *
     * @param amount XP to add; must be &gt;= 0
     * @return number of levels gained
     */
    public int addXp(long amount) {
        int gained = xp.addXp(amount);
        if (gained > 0) {
            resizeBankForLevel(xp.getLevel());
        }
        return gained;
    }

    /** Current trained rank for a skill, or 0 when untrained. */
    public int getSkillRank(GuildSkill skill) {
        return skillRanks.getOrDefault(skill, 0);
    }

    /**
     * Trains a skill up one rank when unlocked and not already maxed.
     *
     * @param skill skill to train
     * @return the new rank after training
     * @throws IllegalStateException if the skill is locked or already at max rank
     */
    public int trainSkill(GuildSkill skill) {
        Objects.requireNonNull(skill, "skill");
        if (!skill.isUnlockedAt(getLevel())) {
            throw new IllegalStateException(
                    skill.getDisplayName() + " unlocks at level " + skill.getUnlockLevel());
        }
        int current = skillRanks.getOrDefault(skill, 0);
        if (current >= GuildSkill.MAX_RANK) {
            throw new IllegalStateException(skill.getDisplayName() + " is already max rank");
        }
        skillRanks.put(skill, current + 1);
        return current + 1;
    }

    /** Unmodifiable view of skill ranks (skills at rank 0 are omitted). */
    public Map<GuildSkill, Integer> getSkillRanks() {
        return Collections.unmodifiableMap(new EnumMap<>(skillRanks));
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name");
        String cleaned = name.strip();
        if (cleaned.isEmpty() || cleaned.length() > GuildLevel.MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "name must be 1.." + GuildLevel.MAX_NAME_LENGTH + " chars");
        }
        return cleaned;
    }

    private static String validateTag(String tag) {
        Objects.requireNonNull(tag, "tag");
        String cleaned = tag.strip();
        if (cleaned.length() != GuildLevel.TAG_LENGTH) {
            throw new IllegalArgumentException(
                    "tag must be exactly " + GuildLevel.TAG_LENGTH + " chars");
        }
        return cleaned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guild other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Guild[" + tag + "] " + name
                + " (Lv." + getLevel() + ", " + members.size() + "/" + getMemberCap() + ")";
    }
}
