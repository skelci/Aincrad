package com.aincrad.social.trade;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for trade sessions between players.
 * <p>
 * Handles the full lifecycle: initiation, acceptance, item offers,
 * confirmation, completion, and cancellation. All sessions are tracked
 * by UUID for quick lookup.
 * </p>
 */
public final class TradeManager {

    private static class Holder {
        static final TradeManager INSTANCE = new TradeManager();
    }

    /** Active trade sessions keyed by session UUID. */
    private final Map<UUID, TradeSession> sessions = new ConcurrentHashMap<>();

    /** Player UUID → their current active session UUID (for quick lookup). */
    private final Map<UUID, UUID> playerSessions = new ConcurrentHashMap<>();

    private TradeManager() {
    }

    /**
     * Returns the singleton instance of the TradeManager.
     *
     * @return the TradeManager instance
     */
    public static TradeManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── Trade Lifecycle ────────────────────────────────────────────────

    /**
     * Initiates a new trade session between two players.
     *
     * @param initiatorId the player initiating the trade
     * @param targetId    the player being invited to trade
     * @return the created TradeSession
     * @throws IllegalArgumentException if either player is already in a trade
     */
    public TradeSession initiateTrade(UUID initiatorId, UUID targetId) {
        if (initiatorId.equals(targetId)) {
            throw new IllegalArgumentException("Cannot trade with yourself");
        }
        if (playerSessions.containsKey(initiatorId) || playerSessions.containsKey(targetId)) {
            throw new IllegalStateException("One or both players are already in a trade session");
        }
        UUID sessionId = UUID.randomUUID();
        TradeSession session = new TradeSession(sessionId, initiatorId, targetId);
        sessions.put(sessionId, session);
        playerSessions.put(initiatorId, sessionId);
        playerSessions.put(targetId, sessionId);
        return session;
    }

    /**
     * Accepts a pending trade session, transitioning it to ACTIVE.
     *
     * @param sessionId the session to accept
     * @param playerId  the player accepting (must be the target)
     * @return the accepted TradeSession
     * @throws IllegalArgumentException if player is not the target
     * @throws IllegalStateException    if session is not PENDING
     */
    public TradeSession acceptTrade(UUID sessionId, UUID playerId) {
        TradeSession session = getSessionOrThrow(sessionId);
        if (!session.getTargetId().equals(playerId)) {
            throw new IllegalArgumentException("Only the target player can accept the trade");
        }
        session.activate();
        return session;
    }

    /**
     * Declines a pending trade session, cancelling it.
     *
     * @param sessionId the session to decline
     * @param playerId  the player declining
     * @return the cancelled TradeSession
     */
    public TradeSession declineTrade(UUID sessionId, UUID playerId) {
        TradeSession session = getSessionOrThrow(sessionId);
        if (!session.getTargetId().equals(playerId)) {
            throw new IllegalArgumentException("Only the target player can decline the trade");
        }
        return cancelTrade(sessionId);
    }

    /**
     * Cancels an active or pending trade session.
     *
     * @param sessionId the session to cancel
     * @return the cancelled TradeSession
     * @throws IllegalStateException if the session is already COMPLETED or CANCELLED
     */
    public TradeSession cancelTrade(UUID sessionId) {
        TradeSession session = getSessionOrThrow(sessionId);
        session.cancel();
        playerSessions.remove(session.getInitiatorId());
        playerSessions.remove(session.getTargetId());
        return session;
    }

    /**
     * Completes a trade session where both players have confirmed.
     *
     * @param sessionId the session to complete
     * @return the completed TradeSession
     * @throws IllegalStateException if not both confirmed or not in CONFIRMED state
     */
    public TradeSession completeTrade(UUID sessionId) {
        TradeSession session = getSessionOrThrow(sessionId);
        if (!session.isBothConfirmed()) {
            throw new IllegalStateException("Both players must confirm before completing");
        }
        session.complete();
        playerSessions.remove(session.getInitiatorId());
        playerSessions.remove(session.getTargetId());
        return session;
    }

    // ── Item Offer Operations ──────────────────────────────────────────

    /**
     * Adds an item to a player's offer in the given session.
     *
     * @param sessionId the trade session
     * @param playerId  the player offering the item
     * @param itemId    the item ID to offer
     */
    public void addItemToOffer(UUID sessionId, UUID playerId, String itemId) {
        getSessionOrThrow(sessionId).addItem(playerId, itemId);
    }

    /**
     * Removes an item from a player's offer in the given session.
     *
     * @param sessionId the trade session
     * @param playerId  the player removing the item
     * @param index     the index of the item to remove
     * @return the removed item ID
     */
    public String removeItemFromOffer(UUID sessionId, UUID playerId, int index) {
        return getSessionOrThrow(sessionId).removeItem(playerId, index);
    }

    // ── Confirmation ───────────────────────────────────────────────────

    /**
     * Sets the confirmation state for a player in a trade session.
     *
     * @param sessionId the trade session
     * @param playerId  the player confirming
     * @param confirmed {@code true} to confirm, {@code false} to unconfirm
     */
    public void setConfirmed(UUID sessionId, UUID playerId, boolean confirmed) {
        getSessionOrThrow(sessionId).setConfirmed(playerId, confirmed);
    }

    /**
     * Checks whether both players have confirmed the trade.
     *
     * @param sessionId the trade session
     * @return {@code true} if both sides confirmed
     */
    public boolean isBothConfirmed(UUID sessionId) {
        return getSessionOrThrow(sessionId).isBothConfirmed();
    }

    // ── Lookup ─────────────────────────────────────────────────────────

    /**
     * Returns the session for the given UUID, or {@code null} if not found.
     *
     * @param sessionId the session UUID
     * @return the TradeSession, or {@code null}
     */
    public TradeSession getSession(UUID sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Returns the active session between two players, or {@code null} if none exists.
     *
     * @param playerId the first player
     * @param otherId  the second player
     * @return the TradeSession, or {@code null}
     */
    public TradeSession getSessionBetween(UUID playerId, UUID otherId) {
        UUID sessionId = playerSessions.get(playerId);
        if (sessionId == null) return null;
        TradeSession session = sessions.get(sessionId);
        if (session == null) return null;
        // Verify both players are in this session
        boolean hasPlayer = session.getInitiatorId().equals(playerId) || session.getTargetId().equals(playerId);
        boolean hasOther = session.getInitiatorId().equals(otherId) || session.getTargetId().equals(otherId);
        return (hasPlayer && hasOther) ? session : null;
    }

    /**
     * Returns all sessions involving the given player.
     *
     * @param playerId the player to look up
     * @return list of TradeSessions, never {@code null}
     */
    public List<TradeSession> getSessionsForPlayer(UUID playerId) {
        UUID sessionId = playerSessions.get(playerId);
        if (sessionId == null) return Collections.emptyList();
        TradeSession session = sessions.get(sessionId);
        return session == null ? Collections.emptyList() : List.of(session);
    }

    // ── Internal Helpers ───────────────────────────────────────────────

    private TradeSession getSessionOrThrow(UUID sessionId) {
        TradeSession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Trade session %s not found".formatted(sessionId));
        }
        return session;
    }
}
