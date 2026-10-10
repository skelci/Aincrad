package com.aincrad.social.party;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a party of up to {@value #MAX_MEMBERS} players.
 * <p>
 * A party tracks its leader, members, loot distribution rule, join mode,
 * and per-member XP share. All mutating operations validate preconditions
 * and throw descriptive exceptions on failure.
 */
public final class Party {

    /** Maximum number of members allowed in a single party. */
    public static final int MAX_MEMBERS = 6;

    /** How players may join the party. */
    public enum JoinMode {
        /** Anyone may join without an invitation. */
        OPEN,
        /** Only invited players may join. */
        INVITE_ONLY
    }

    private final UUID partyId;
    private UUID leaderId;
    private final List<PartyMember> members;
    private PartyLootRule lootRule;
    private JoinMode joinMode;
    private final Instant creationTime;
    private final Map<UUID, Double> xpShareTracker;

    /**
     * Creates a new party.
     *
     * @param leaderId  the UUID of the party leader (non-null)
     * @param lootRule  the loot distribution rule (non-null)
     * @param joinMode  the join mode (non-null)
     * @throws NullPointerException if any argument is null
     */
    public Party(UUID leaderId, PartyLootRule lootRule, JoinMode joinMode) {
        this.partyId = UUID.randomUUID();
        this.leaderId = Objects.requireNonNull(leaderId, "leaderId cannot be null");
        this.lootRule = Objects.requireNonNull(lootRule, "lootRule cannot be null");
        this.joinMode = Objects.requireNonNull(joinMode, "joinMode cannot be null");
        this.members = new ArrayList<>();
        this.creationTime = Instant.now();
        this.xpShareTracker = new ConcurrentHashMap<>();
    }

    public UUID getPartyId() {
        return partyId;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public PartyLootRule getLootRule() {
        return lootRule;
    }

    public JoinMode getJoinMode() {
        return joinMode;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    /**
     * Returns the leader as a {@link PartyMember}, or {@code null} if not found.
     */
    public PartyMember getLeader() {
        return members.stream()
                .filter(m -> m.getPlayerId().equals(leaderId))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return an unmodifiable copy of the member list
     */
    public List<PartyMember> getMembers() {
        return List.copyOf(members);
    }

    public int getMemberCount() {
        return members.size();
    }

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public boolean isLeader(UUID playerId) {
        return leaderId.equals(playerId);
    }

    public boolean hasMember(UUID playerId) {
        return members.stream().anyMatch(m -> m.getPlayerId().equals(playerId));
    }

    /**
     * Transfers leadership to another member.
     *
     * @param newLeaderId the UUID of the new leader (must be a current member)
     * @throws IllegalArgumentException if the target is not a member
     */
    public void transferLeadership(UUID newLeaderId) {
        if (!hasMember(newLeaderId)) {
            throw new IllegalArgumentException("Target must be a current party member");
        }
        this.leaderId = newLeaderId;
    }

    public void setLootRule(PartyLootRule lootRule) {
        this.lootRule = Objects.requireNonNull(lootRule, "lootRule cannot be null");
    }

    public void setJoinMode(JoinMode joinMode) {
        this.joinMode = Objects.requireNonNull(joinMode, "joinMode cannot be null");
    }

    /**
     * Adds a member to the party.
     *
     * @param member the member to add (non-null)
     * @throws IllegalStateException    if the party is full
     * @throws IllegalArgumentException if the player is already in the party
     */
    public void addMember(PartyMember member) {
        Objects.requireNonNull(member, "member cannot be null");
        if (isFull()) {
            throw new IllegalStateException("Party is full (max " + MAX_MEMBERS + " members)");
        }
        if (hasMember(member.getPlayerId())) {
            throw new IllegalArgumentException("Player is already in this party");
        }
        members.add(member);
        xpShareTracker.put(member.getPlayerId(), 0.0);
    }

    /**
     * Removes a member from the party.
     *
     * @param playerId the UUID of the member to remove
     * @return {@code true} if the member was removed
     */
    public boolean removeMember(UUID playerId) {
        boolean removed = members.removeIf(m -> m.getPlayerId().equals(playerId));
        if (removed) {
            xpShareTracker.remove(playerId);
        }
        return removed;
    }

    /**
     * Records XP earned by a member for share tracking.
     *
     * @param playerId the member's UUID
     * @param xp       the amount of XP to add
     */
    public void recordXpShare(UUID playerId, double xp) {
        xpShareTracker.merge(playerId, xp, Double::sum);
    }

    /**
     * @return the accumulated XP share for a member, or 0.0 if not tracked
     */
    public double getXpShare(UUID playerId) {
        return xpShareTracker.getOrDefault(playerId, 0.0);
    }

    /**
     * @return an unmodifiable snapshot of the XP share tracker
     */
    public Map<UUID, Double> getXpShareSnapshot() {
        return Map.copyOf(xpShareTracker);
    }

    @Override
    public String toString() {
        return "Party[id=" + partyId + ", leader=" + leaderId
                + ", members=" + members.size() + "/" + MAX_MEMBERS
                + ", lootRule=" + lootRule + ", joinMode=" + joinMode + "]";
    }
}
