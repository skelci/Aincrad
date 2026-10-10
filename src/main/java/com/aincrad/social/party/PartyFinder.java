package com.aincrad.social.party;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LFG (Looking For Group) service that lets players discover open parties
 * and submit join requests.
 * <p>
 * This class is stateless with respect to party data — it operates on
 * collections of {@link Party} instances supplied by the caller (typically
 * {@link PartyManager}).
 */
public final class PartyFinder {

    /** Type of content the party is formed for. */
    public enum PartyType {
        DUNGEON, RAID, PVP, EXPLORATION
    }

    /** Lifecycle status of a join request. */
    public enum JoinRequestStatus {
        PENDING, ACCEPTED, DECLINED, CANCELLED
    }

    /**
     * Immutable record representing a join request.
     *
     * @param requestId unique identifier for this request
     * @param playerId  the requesting player
     * @param partyId   the target party
     * @param status    current status of the request
     * @param timestamp when the request was created
     */
    public record JoinRequest(
            UUID requestId,
            UUID playerId,
            UUID partyId,
            JoinRequestStatus status,
            Instant timestamp
    ) {}

    /**
     * Filter criteria for party searches.
     *
     * @param minFloor minimum dungeon floor (inclusive)
     * @param maxFloor maximum dungeon floor (inclusive)
     * @param type     the type of party (null = any)
     * @param minLevel minimum average member level
     */
    public record PartyFilter(int minFloor, int maxFloor, PartyType type, int minLevel) {

        /**
         * Tests whether a party matches this filter.
         *
         * @param party     the party to test
         * @param partyFloor the floor the party is on (0 if N/A)
         * @return {@code true} if the party matches all criteria
         */
        public boolean matches(Party party, int partyFloor) {
            if (partyFloor < minFloor || partyFloor > maxFloor) return false;
            if (type != null && type != PartyType.DUNGEON) return false;
            return party.getMembers().stream()
                    .anyMatch(m -> m.getLevel() >= minLevel);
        }
    }

    private final List<JoinRequest> joinRequests = new ArrayList<>();

    /**
     * Finds open, non-full parties matching the given filter.
     *
     * @param allParties the collection of all active parties
     * @param filter     the filter criteria (null = no filtering)
     * @return a list of matching parties, sorted by fewest members first
     */
    public List<Party> findOpenParties(Collection<Party> allParties, PartyFilter filter) {
        return allParties.stream()
                .filter(p -> p.getJoinMode() == Party.JoinMode.OPEN)
                .filter(p -> !p.isFull())
                .filter(p -> filter == null || filter.matches(p, 0))
                .sorted(Comparator.comparingInt(Party::getMemberCount))
                .collect(Collectors.toList());
    }

    /**
     * Submits a join request for a player to a specific party.
     *
     * @param playerId the requesting player's UUID
     * @param partyId  the target party's UUID
     * @return the created {@link JoinRequest}
     */
    public JoinRequest submitJoinRequest(UUID playerId, UUID partyId) {
        JoinRequest request = new JoinRequest(
                UUID.randomUUID(), playerId, partyId, JoinRequestStatus.PENDING, Instant.now());
        joinRequests.add(request);
        return request;
    }

    /**
     * Responds to a pending join request.
     *
     * @param requestId the request to respond to
     * @param accept    {@code true} to accept, {@code false} to decline
     * @return {@code true} if the request was found and updated
     */
    public boolean respondToJoinRequest(UUID requestId, boolean accept) {
        for (int i = 0; i < joinRequests.size(); i++) {
            JoinRequest req = joinRequests.get(i);
            if (req.requestId().equals(requestId) && req.status() == JoinRequestStatus.PENDING) {
                JoinRequestStatus newStatus = accept
                        ? JoinRequestStatus.ACCEPTED
                        : JoinRequestStatus.DECLINED;
                joinRequests.set(i, new JoinRequest(
                        req.requestId(), req.playerId(), req.partyId(), newStatus, req.timestamp()));
                return true;
            }
        }
        return false;
    }

    /**
     * Cancels a pending request (called by the requesting player).
     *
     * @param requestId the request to cancel
     * @return {@code true} if the request was found and cancelled
     */
    public boolean cancelJoinRequest(UUID requestId) {
        for (int i = 0; i < joinRequests.size(); i++) {
            JoinRequest req = joinRequests.get(i);
            if (req.requestId().equals(requestId) && req.status() == JoinRequestStatus.PENDING) {
                joinRequests.set(i, new JoinRequest(
                        req.requestId(), req.playerId(), req.partyId(),
                        JoinRequestStatus.CANCELLED, req.timestamp()));
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all pending requests for a given party.
     *
     * @param partyId the party's UUID
     * @return list of pending join requests
     */
    public List<JoinRequest> getPendingRequests(UUID partyId) {
        return joinRequests.stream()
                .filter(r -> r.partyId().equals(partyId))
                .filter(r -> r.status() == JoinRequestStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Returns all requests made by a specific player.
     *
     * @param playerId the player's UUID
     * @return list of the player's join requests
     */
    public List<JoinRequest> getRequestsByPlayer(UUID playerId) {
        return joinRequests.stream()
                .filter(r -> r.playerId().equals(playerId))
                .collect(Collectors.toList());
    }
}
