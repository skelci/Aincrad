package com.aincrad.paper.guild;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core guild data model.
 * Owns roster, ranks, bank, XP, skills, and metadata.
 */
public final class Guild {
    private final String name;
    private final String tag;
    private final UUID ownerUuid;
    private final Instant createdAt;
    private final Map<UUID, GuildRank> members = new ConcurrentHashMap<>();
    private final Map<GuildSkill, Integer> skillRanks = new EnumMap<>(GuildSkill.class);
    private GuildBank bank;
    private GuildXP xp;
    private String notice = "";
    private Instant disbandedAt;

    public Guild(String name, String tag, UUID ownerUuid) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Guild name cannot be blank");
        if (name.length() > GuildLevel.MAX_NAME_LENGTH) throw new IllegalArgumentException("Guild name too long");
        if (tag == null || tag.isBlank()) throw new IllegalArgumentException("Guild tag cannot be blank");
        if (tag.length() > GuildLevel.TAG_LENGTH) throw new IllegalArgumentException("Guild tag too long");
        this.name = name;
        this.tag = tag;
        this.ownerUuid = ownerUuid;
        this.createdAt = Instant.now();
        this.bank = new GuildBank(GuildLevel.bankSlots(GuildLevel.MIN_LEVEL));
        this.xp = new GuildXP();
        this.members.put(ownerUuid, GuildRank.MASTER);
    }

    // ── Metadata getters ──────────────────────────────────────────────

    public String getName() { return name; }
    public String getTag() { return tag; }
    public UUID getOwnerUuid() { return ownerUuid; }
    public Instant getCreatedAt() { return createdAt; }
    public String getNotice() { return notice; }
    public Instant getDisbandedAt() { return disbandedAt; }

    public void setNotice(String notice) {
        if (notice != null && notice.length() > GuildLevel.MAX_NOTICE_LENGTH) {
            throw new IllegalArgumentException("Notice too long");
        }
        this.notice = notice != null ? notice : "";
    }

    // ── Member operations ─────────────────────────────────────────────

    public boolean isMember(UUID uuid) {
        return members.containsKey(uuid);
    }

    public GuildRank getRank(UUID uuid) {
        return members.get(uuid);
    }

    public boolean addMember(UUID uuid) {
        if (members.size() >= GuildLevel.memberCap(xp.getLevel())) return false;
        return members.putIfAbsent(uuid, GuildRank.MEMBER) == null;
    }

    public boolean removeMember(UUID uuid) {
        if (uuid.equals(ownerUuid)) return false;
        return members.remove(uuid) != null;
    }

    public boolean setRank(UUID uuid, GuildRank newRank) {
        if (!members.containsKey(uuid)) return false;
        if (newRank == GuildRank.OFFICER) {
            long officerCount = members.values().stream().filter(r -> r == GuildRank.OFFICER).count();
            if (officerCount >= GuildLevel.officerCap(xp.getLevel())) return false;
        }
        members.put(uuid, newRank);
        return true;
    }

    public boolean transferOwner(UUID from, UUID to) {
        if (!ownerUuid.equals(from) || !members.containsKey(to)) return false;
        members.put(from, GuildRank.OFFICER);
        members.put(to, GuildRank.MASTER);
        return true;
    }

    public Map<UUID, GuildRank> getMembers() {
        return Collections.unmodifiableMap(new ConcurrentHashMap<>(members));
    }

    public int getMemberCount() { return members.size(); }

    // ── Bank delegation ───────────────────────────────────────────────

    public GuildBank getBank() { return bank; }

    // ── XP & Skills ───────────────────────────────────────────────────

    public GuildXP getXp() { return xp; }

    public int addXp(long amount) {
        int gained = xp.addXp(amount);
        if (gained > 0) {
            bank = new GuildBank(GuildLevel.bankSlots(xp.getLevel()));
        }
        return gained;
    }

    public int getSkillRank(GuildSkill skill) {
        return skillRanks.getOrDefault(skill, 0);
    }

    public boolean trainSkill(GuildSkill skill) {
        if (!skill.isUnlockedAt(xp.getLevel())) return false;
        int current = skillRanks.getOrDefault(skill, 0);
        if (current >= GuildSkill.MAX_RANK) return false;
        skillRanks.put(skill, current + 1);
        return true;
    }

    public Map<GuildSkill, Integer> getSkillRanks() {
        return Collections.unmodifiableMap(new EnumMap<>(skillRanks));
    }

    // ── Disband ───────────────────────────────────────────────────────

    public void markDisbanded() {
        this.disbandedAt = Instant.now();
        this.members.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return name.equals(((Guild) o).name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }

    @Override
    public String toString() {
        return "Guild[" + name + " (" + tag + ") Lv" + xp.getLevel()
                + " " + members.size() + " members]";
    }
}
