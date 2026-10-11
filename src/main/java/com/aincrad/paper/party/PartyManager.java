package com.aincrad.paper.party;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton manager for all parties on the server.
 * Handles lifecycle, membership, raid groups, and lookups.
 */
public final class PartyManager {
    public static final int RAID_PARTY_COUNT = 4;
    public static final int RAID_MAX_PLAYERS = 24;

    private static volatile PartyManager instance;

    private final Map<UUID, Party> parties = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerToParty = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> raidGroups = new ConcurrentHashMap<>();

    private PartyManager() {}

    public static PartyManager getInstance() {
        if (instance == null) {
            synchronized (PartyManager.class) {
                if (instance == null) {
                    instance = new PartyManager();
                }
            }
        }
        return instance;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────

    public Party createParty(UUID leaderId, String leaderName, int leaderLevel) {
        if (playerToParty.containsKey(leaderId)) {
            throw new IllegalStateException("Player already in a party");
        }
        Party party = new Party(leaderId, leaderName, leaderLevel);
        parties.put(party.getPartyId(), party);
        playerToParty.put(leaderId, party.getPartyId());
        return party;
    }

    public boolean disbandParty(UUID partyId, UUID actorUuid) {
        Party party = parties.get(partyId);
        if (party == null) return false;
        if (!party.isLeader(actorUuid)) return false;
        for (PartyMember member : party.getMembers()) {
            playerToParty.remove(member.getPlayerId());
        }
        parties.remove(partyId);
        return true;
    }

    // ── Membership ────────────────────────────────────────────────────

    public boolean invitePlayer(UUID partyId, UUID targetUuid, String targetName, int targetLevel) {
        Party party = parties.get(partyId);
        if (party == null) return false;
        if (playerToParty.containsKey(targetUuid)) return false;
        return party.addMember(targetUuid, targetName, targetLevel);
    }

    public boolean joinParty(UUID partyId, UUID playerUuid, String playerName, int playerLevel) {
        Party party = parties.get(partyId);
        if (party == null) return false;
        if (party.getJoinMode() == Party.JoinMode.INVITE_ONLY) return false;
        if (playerToParty.containsKey(playerUuid)) return false;
        if (!party.addMember(playerUuid, playerName, playerLevel)) return false;
        playerToParty.put(playerUuid, partyId);
        return true;
    }

    public boolean kickMember(UUID partyId, UUID targetUuid, UUID actorUuid) {
        Party party = parties.get(partyId);
        if (party == null) return false;
        if (!party.isLeader(actorUuid)) return false;
        if (targetUuid.equals(actorUuid)) return false;
        if (!party.removeMember(targetUuid)) return false;
        playerToParty.remove(targetUuid);
        return true;
    }

    public boolean leaveParty(UUID playerUuid) {
        UUID partyId = playerToParty.get(playerUuid);
        if (partyId == null) return false;
        Party party = parties.get(partyId);
        if (party == null) return false;
        if (party.isLeader(playerUuid)) {
            // Transfer leadership or disband
            Optional<PartyMember> next = party.getMembers().stream()
                    .filter(m -> !m.getPlayerId().equals(playerUuid))
                    .findFirst();
            if (next.isPresent()) {
                party.transferLeadership(next.get().getPlayerId());
            } else {
                disbandParty(partyId, playerUuid);
                return true;
            }
        }
        party.removeMember(playerUuid);
        playerToParty.remove(playerUuid);
        return true;
    }

    public boolean transferLeadership(UUID partyId, UUID newLeaderId, UUID actorUuid) {
        Party party = parties.get(partyId);
        if (party == null) return false;
        if (!party.isLeader(actorUuid)) return false;
        return party.transferLeadership(newLeaderId);
    }

    // ── Raid Groups ───────────────────────────────────────────────────

    public boolean createRaidGroup(UUID partyId) {
        Party party = parties.get(partyId);
        if (party == null) return false;
        Set<UUID> group = new LinkedHashSet<>();
        group.add(partyId);
        raidGroups.put(partyId, group);
        return true;
    }

    public boolean linkPartyToRaid(UUID existingPartyId, UUID newPartyId) {
        Set<UUID> group = raidGroups.get(existingPartyId);
        if (group == null) return false;
        if (group.size() >= RAID_PARTY_COUNT) return false;
        Party newParty = parties.get(newPartyId);
        if (newParty == null) return false;
        int totalPlayers = group.stream()
                .map(parties::get)
                .filter(Objects::nonNull)
                .mapToInt(Party::getMemberCount)
                .sum();
        if (totalPlayers + newParty.getMemberCount() > RAID_MAX_PLAYERS) return false;
        group.add(newPartyId);
        raidGroups.put(newPartyId, group);
        return true;
    }

    // ── Lookups ───────────────────────────────────────────────────────

    public Party getParty(UUID partyId) { return parties.get(partyId); }

    public Party getPartyForPlayer(UUID playerUuid) {
        UUID partyId = playerToParty.get(playerUuid);
        return partyId != null ? parties.get(partyId) : null;
    }

    public List<Party> getAllParties() {
        return List.copyOf(parties.values());
    }

    public boolean isInParty(UUID playerUuid) {
        return playerToParty.containsKey(playerUuid);
    }

    public void reset() {
        parties.clear();
        playerToParty.clear();
        raidGroups.clear();
    }
}
