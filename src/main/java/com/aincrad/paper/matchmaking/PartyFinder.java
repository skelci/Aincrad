package com.aincrad.paper.matchmaking;

import com.aincrad.paper.party.Party;
import com.aincrad.paper.party.PartyManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Looking For Group (LFG) service for dungeons and raids.
 * Stateless w.r.t. party data — operates on caller-supplied collections.
 */
public final class PartyFinder {

    public enum PartyType { DUNGEON, RAID, PVP, EXPLORATION }

    public enum JoinRequestStatus { PENDING, ACCEPTED, DECLINED, CANCELLED }

    public record JoinRequest(UUID requestId, UUID playerId, UUID partyId,
                             JoinRequestStatus status, long timestamp) {}

    public record PartyFilter(int minFloor, int maxFloor, PartyType type, int minLevel) {
        public boolean matches(Party party, int partyFloor, int avgLevel) {
            if (partyFloor < minFloor || partyFloor > maxFloor) return false;
            if (avgLevel < minLevel) return false;
            return true;
        }
    }

    private final Map<UUID, JoinRequest> requests = new ConcurrentHashMap<>();

    public List<Party> findOpenParties(Collection<Party> allParties, PartyFilter filter, int partyFloor, int avgLevel) {
        return allParties.stream()
                .filter(p -> p.getJoinMode() == Party.JoinMode.OPEN)
                .filter(p -> filter.matches(p, partyFloor, avgLevel))
                .sorted(Comparator.comparingInt(Party::getMemberCount))
                .collect(Collectors.toUnmodifiableList());
    }

    public JoinRequest submitJoinRequest(UUID playerId, UUID partyId) {
        JoinRequest request = new JoinRequest(UUID.randomUUID(), playerId, partyId,
                JoinRequestStatus.PENDING, System.currentTimeMillis());
        requests.put(request.requestId(), request);
        return request;
    }

    public boolean respondToJoinRequest(UUID requestId, boolean accepted) {
        JoinRequest request = requests.get(requestId);
        if (request == null || request.status() != JoinRequestStatus.PENDING) return false;
        JoinRequest updated = new JoinRequest(request.requestId(), request.playerId(),
                request.partyId(),
                accepted ? JoinRequestStatus.ACCEPTED : JoinRequestStatus.DECLINED,
                request.timestamp());
        requests.put(requestId, updated);
        return true;
    }

    public boolean cancelJoinRequest(UUID requestId) {
        JoinRequest request = requests.get(requestId);
        if (request == null || request.status() != JoinRequestStatus.PENDING) return false;
        JoinRequest updated = new JoinRequest(request.requestId(), request.playerId(),
                request.partyId(), JoinRequestStatus.CANCELLED, request.timestamp());
        requests.put(requestId, updated);
        return true;
    }

    public List<JoinRequest> getPendingRequests(UUID partyId) {
        return requests.values().stream()
                .filter(r -> r.partyId().equals(partyId) && r.status() == JoinRequestStatus.PENDING)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<JoinRequest> getRequestsByPlayer(UUID playerId) {
        return requests.values().stream()
                .filter(r -> r.playerId().equals(playerId))
                .collect(Collectors.toUnmodifiableList());
    }

    public JoinRequest getRequest(UUID requestId) {
        return requests.get(requestId);
    }

    public void reset() {
        requests.clear();
    }
}
