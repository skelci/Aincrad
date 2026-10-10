package com.aincrad.social.party;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager that owns all active parties and provides
 * high-level operations for party lifecycle management.
 * <p>
 * Supports standard parties (up to {@value Party#MAX_MEMBERS} members) and
 * raid parties (up to {@value #RAID_PARTY_COUNT} linked parties =
 * {@value #RAID_MAX_PLAYERS} players).
 * <p>
 * This class is thread-safe. All internal maps use
 * {@link ConcurrentHashMap} for concurrent access.
 */
public final class PartyManager {

    private static volatile PartyManager instance;
    private static final Object LOCK = new Object();

    /** Number of parties that constitute a full raid group. */
    public static final int RAID_PARTY_COUNT = 4;

    /** Maximum players in a full raid group (4 × 6). */
    public static final int RAID_MAX_PLAYERS = RAID_PARTY_COUNT * Party.MAX_MEMBERS;

    private final Map<UUID, Party> parties = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerToParty = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> raidGroups = new ConcurrentHashMap<>();
    private final PartyFinder partyFinder = new PartyFinder();

    private PartyManager() {
        // prevent external instantiation
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     * Uses double-checked locking for thread-safe lazy initialization.
     */
    public static PartyManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new PartyManager();
                }
            }
        }
        return instance;
    }

    // ── Party Lifecycle ──────────────────────────────────────────────

    /**
     * Creates a new party with the given leader.
     *
     * @param leaderId the UUID of the party leader
     * @param lootRule the loot distribution rule
     * @param joinMode the join mode
     * @return the newly created {@link Party}
     * @throws IllegalStateException if the leader is already in a party
     */
    public Party createParty(UUID leaderId, PartyLootRule lootRule, Party.JoinMode joinMode) {
        if (playerToParty.containsKey(leaderId)) {
            throw new IllegalStateException("Player is already in a party");
        }
        Party party = new Party(leaderId, lootRule, joinMode);
        party.addMember(new PartyMember(leaderId, "Leader", 1, PartyMember.Role.DPS));
        parties.put(party.getPartyId(), party);
        playerToParty.put(leaderId, party.getPartyId());
        return party;
    }

    /**
     * Disbands a party, removing all members and cleaning up raid associations.
     *
     * @param partyId the UUID of the party to disband
     * @return {@code true} if the party was found and removed
     */
    public boolean disbandParty(UUID partyId) {
        Party party = parties.remove(partyId);
        if (party == null) return false;
        party.getMembers().forEach(m -> playerToParty.remove(m.getPlayerId()));
        raidGroups.values().forEach(set -> set.remove(partyId));
        return true;
    }

    // ── Membership Operations ────────────────────────────────────────

    /**
     * Sends an invitation from a party leader to a target player.
     *
     * @param partyId   the party's UUID
     * @param inviterId the inviting player (must be the leader)
     * @param targetId  the invited player
     * @throws IllegalStateException    if the inviter is not the leader or target is already in a party
     * @throws IllegalArgumentException if the party does not exist
     */
    public boolean invitePlayer(UUID partyId, UUID inviterId, UUID targetId) {
        Party party = parties.get(partyId);
        if (party == null) throw new IllegalArgumentException("Party not found");
        if (!party.isLeader(inviterId)) throw new IllegalStateException("Only the leader can invite");
        if (playerToParty.containsKey(targetId)) throw new IllegalStateException("Player is already in a party");
        return true;
    }

    /**
     * Adds a player to a party (open join or after invite acceptance).
     *
     * @param partyId the party's UUID
     * @param member  the member to add
     * @throws IllegalStateException    if the party is invite-only or full
     * @throws IllegalArgumentException if the party does not exist
     */
    public boolean joinParty(UUID partyId, PartyMember member) {
        Party party = parties.get(partyId);
        if (party == null) throw new IllegalArgumentException("Party not found");
        if (party.getJoinMode() == Party.JoinMode.INVITE_ONLY) {
            throw new IllegalStateException("Party is invite-only");
        }
        party.addMember(member);
        playerToParty.put(member.getPlayerId(), partyId);
        return true;
    }

    /**
     * Removes a member from a party.
     *
     * @param partyId  the party's UUID
     * @param kickerId the player performing the kick (must be the leader)
     * @param targetId the player to kick
     * @throws IllegalStateException    if the kicker is not the leader or target is the leader
     * @throws IllegalArgumentException if the party does not exist
     */
    public boolean kickMember(UUID partyId, UUID kickerId, UUID targetId) {
        Party party = parties.get(partyId);
        if (party == null) throw new IllegalArgumentException("Party not found");
        if (!party.isLeader(kickerId)) throw new IllegalStateException("Only the leader can kick members");
        if (party.isLeader(targetId)) throw new IllegalStateException("Cannot kick the party leader");
        party.removeMember(targetId);
        playerToParty.remove(targetId);
        return true;
    }

    /**
     * Transfers leadership to another member.
     *
     * @param partyId      the party's UUID
     * @param currentLeader the current leader's UUID
     * @param newLeader    the new leader's UUID
     * @throws IllegalStateException    if the current leader is not the actual leader
     * @throws IllegalArgumentException if the party or target member does not exist
     */
    public boolean transferLeadership(UUID partyId, UUID currentLeader, UUID newLeader) {
        Party party = parties.get(partyId);
        if (party == null) throw new IllegalArgumentException("Party not found");
        if (!party.isLeader(currentLeader)) throw new IllegalStateException("Only the leader can transfer leadership");
        party.transferLeadership(newLeader);
        return true;
    }

    // ── Lookups ───────────────────────────────────────────────────────

    public Party getParty(UUID partyId) {
        return parties.get(partyId);
    }

    /**
     * Returns the party a player belongs to, or {@code null}.
     */
    public Party getPartyForPlayer(UUID playerId) {
        UUID partyId = playerToParty.get(playerId);
        return partyId != null ? parties.get(partyId) : null;
    }

    /**
     * @return an unmodifiable collection of all active parties
     */
    public Collection<Party> getAllParties() {
        return List.copyOf(parties.values());
    }

    public PartyFinder getPartyFinder() {
        return partyFinder;
    }

    // ── Raid Party Support ────────────────────────────────────────────

    /**
     * Links exactly {@value #RAID_PARTY_COUNT} parties into a raid group.
     *
     * @param partyIds the UUIDs of the 4 parties to link
     * @return the UUID of the newly created raid group
     * @throws IllegalArgumentException if the list does not contain exactly 4 parties
     */
    public UUID createRaidGroup(List<UUID> partyIds) {
        if (partyIds.size() != RAID_PARTY_COUNT) {
            throw new IllegalArgumentException(
                    "Raid requires exactly " + RAID_PARTY_COUNT + " parties, got " + partyIds.size());
        }
        UUID raidId = UUID.randomUUID();
        Set<UUID> group = new LinkedHashSet<>(partyIds);
        raidGroups.put(raidId, group);
        return raidId;
    }

    /**
     * Returns the set of party UUIDs belonging to a raid group.
     *
     * @param raidId the raid group's UUID
     * @return an unmodifiable set of party UUIDs (empty if not found)
     */
    public Set<UUID> getRaidParties(UUID raidId) {
        return Set.copyOf(raidGroups.getOrDefault(raidId, Set.of()));
    }

    /**
     * Disbands a raid group link (does not disband the individual parties).
     *
     * @param raidId the raid group's UUID
     * @return {@code true} if the raid group was found and removed
     */
    public boolean disbandRaidGroup(UUID raidId) {
        return raidGroups.remove(raidId) != null;
    }

    /**
     * Returns all raid groups as a map of raid ID → set of party IDs.
     */
    public Map<UUID, Set<UUID>> getAllRaidGroups() {
        return Map.copyOf(raidGroups);
    }
}
