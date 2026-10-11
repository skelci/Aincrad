package com.aincrad.paper.party;

import java.time.Instant;
import java.util.*;

/**
 * Core party model. Max 6 members.
 * Tracks leader, members, loot rule, join mode, and XP share.
 */
public final class Party {
    public static final int MAX_MEMBERS = 6;

    public enum JoinMode { OPEN, INVITE_ONLY }

    private final UUID partyId;
    private UUID leaderId;
    private final List<PartyMember> members;
    private PartyLootRule lootRule;
    private JoinMode joinMode;
    private final Instant creationTime;
    private final Map<UUID, Double> xpShareTracker;

    public Party(UUID leaderId, String leaderName, int leaderLevel) {
        this.partyId = UUID.randomUUID();
        this.leaderId = leaderId;
        this.members = new ArrayList<>();
        this.lootRule = PartyLootRule.NEED_GREED;
        this.joinMode = JoinMode.INVITE_ONLY;
        this.creationTime = Instant.now();
        this.xpShareTracker = new HashMap<>();
        this.members.add(new PartyMember(leaderId, leaderName, leaderLevel));
    }

    // ── Getters ───────────────────────────────────────────────────────

    public UUID getPartyId() { return partyId; }
    public UUID getLeaderId() { return leaderId; }
    public PartyLootRule getLootRule() { return lootRule; }
    public JoinMode getJoinMode() { return joinMode; }
    public Instant getCreationTime() { return creationTime; }

    public List<PartyMember> getMembers() {
        return Collections.unmodifiableList(new ArrayList<>(members));
    }

    public int getMemberCount() { return members.size(); }

    public boolean isLeader(UUID uuid) { return leaderId.equals(uuid); }

    public boolean isMember(UUID uuid) {
        return members.stream().anyMatch(m -> m.getPlayerId().equals(uuid));
    }

    public Optional<PartyMember> getMember(UUID uuid) {
        return members.stream().filter(m -> m.getPlayerId().equals(uuid)).findFirst();
    }

    // ── Mutators ──────────────────────────────────────────────────────

    public void setLootRule(PartyLootRule rule) {
        this.lootRule = Objects.requireNonNull(rule, "lootRule");
    }

    public void setJoinMode(JoinMode mode) {
        this.joinMode = Objects.requireNonNull(mode, "joinMode");
    }

    public boolean addMember(UUID uuid, String name, int level) {
        if (members.size() >= MAX_MEMBERS) return false;
        if (isMember(uuid)) return false;
        members.add(new PartyMember(uuid, name, level));
        return true;
    }

    public boolean removeMember(UUID uuid) {
        if (uuid.equals(leaderId)) return false;
        return members.removeIf(m -> m.getPlayerId().equals(uuid));
    }

    public boolean transferLeadership(UUID newLeaderId) {
        if (!isMember(newLeaderId)) return false;
        this.leaderId = newLeaderId;
        return true;
    }

    // ── XP Share ──────────────────────────────────────────────────────

    public void recordXpShare(UUID uuid, double xp) {
        xpShareTracker.merge(uuid, xp, Double::sum);
    }

    public double getXpShare(UUID uuid) {
        return xpShareTracker.getOrDefault(uuid, 0.0);
    }

    public Map<UUID, Double> getXpShareSnapshot() {
        return Collections.unmodifiableMap(new HashMap<>(xpShareTracker));
    }

    @Override
    public String toString() {
        return "Party[" + partyId.toString().substring(0, 8) + " " + members.size() + " members]";
    }
}
