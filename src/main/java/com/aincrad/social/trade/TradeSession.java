package com.aincrad.social.trade;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an active or completed trade session between two players.
 * <p>
 * Each player offers items; both must confirm before the trade completes.
 * The session tracks the full lifecycle from initiation through completion
 * or cancellation.
 * </p>
 */
public final class TradeSession {

    /**
     * Lifecycle states of a trade session.
     */
    public enum State {
        /** Session created but not yet accepted by the target. */
        PENDING,
        /** Both players actively trading. */
        ACTIVE,
        /** Both players confirmed; trade is completing. */
        CONFIRMED,
        /** Trade completed successfully. */
        COMPLETED,
        /** Trade was cancelled by either party. */
        CANCELLED
    }

    private final UUID sessionId;
    private final UUID initiatorId;
    private final UUID targetId;
    private final List<String> initiatorOffer;
    private final List<String> targetOffer;
    private boolean initiatorConfirmed;
    private boolean targetConfirmed;
    private State state;
    private final Instant createdAt;
    private Instant completedAt;

    /**
     * Creates a new pending trade session.
     *
     * @param sessionId   the unique session ID
     * @param initiatorId the player who initiated the trade
     * @param targetId    the player being traded with
     */
    public TradeSession(UUID sessionId, UUID initiatorId, UUID targetId) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.initiatorId = Objects.requireNonNull(initiatorId, "initiatorId");
        this.targetId = Objects.requireNonNull(targetId, "targetId");
        if (initiatorId.equals(targetId)) {
            throw new IllegalArgumentException("Cannot trade with yourself");
        }
        this.initiatorOffer = new ArrayList<>();
        this.targetOffer = new ArrayList<>();
        this.initiatorConfirmed = false;
        this.targetConfirmed = false;
        this.state = State.PENDING;
        this.createdAt = Instant.now();
    }

    // ── Getters ────────────────────────────────────────────────────────

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getInitiatorId() {
        return initiatorId;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public State getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    /**
     * Returns an unmodifiable view of the initiator's offered items.
     *
     * @return list of ItemStacks offered by the initiator
     */
    public List<String> getInitiatorOffer() {
        return Collections.unmodifiableList(initiatorOffer);
    }

    /**
     * Returns an unmodifiable view of the target's offered items.
     *
     * @return list of ItemStacks offered by the target
     */
    public List<String> getTargetOffer() {
        return Collections.unmodifiableList(targetOffer);
    }

    public boolean isInitiatorConfirmed() {
        return initiatorConfirmed;
    }

    public boolean isTargetConfirmed() {
        return targetConfirmed;
    }

    // ── Item Offer Management ───────────────────────────────────────────

    /**
     * Adds an item to the given player's offer.
     *
     * @param playerId the player making the offer
     * @param itemId   the item ID to offer
     * @throws IllegalStateException if the session is not ACTIVE
     */
    public void addItem(UUID playerId, String itemId) {
        requireActive();
        Objects.requireNonNull(itemId, "itemId must not be null");
        if (itemId.isEmpty()) throw new IllegalArgumentException("Cannot offer an empty item");
        if (isInitiator(playerId)) {
            initiatorOffer.add(itemId);
        } else if (isTarget(playerId)) {
            targetOffer.add(itemId);
        } else {
            throw new IllegalArgumentException("Player %s is not part of this trade".formatted(playerId));
        }
        // Adding items invalidates both confirmations
        resetConfirmations();
    }

    /**
     * Removes an item from the given player's offer.
     *
     * @param playerId the player removing the item
     * @param index    the index of the item to remove
     * @return the removed item ID
     * @throws IllegalStateException if the session is not ACTIVE
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public String removeItem(UUID playerId, int index) {
        requireActive();
        List<String> offer = isInitiator(playerId) ? initiatorOffer : targetOffer;
        if (index < 0 || index >= offer.size()) {
            throw new IndexOutOfBoundsException("Index %d out of bounds for offer size %d".formatted(index, offer.size()));
        }
        String removed = offer.remove(index);
        resetConfirmations();
        return removed;
    }

    // ── Confirmation ───────────────────────────────────────────────────

    /**
     * Sets the confirmation state for the given player.
     *
     * @param playerId  the player confirming
     * @param confirmed {@code true} to confirm, {@code false} to unconfirm
     */
    public void setConfirmed(UUID playerId, boolean confirmed) {
        requireActive();
        if (isInitiator(playerId)) {
            initiatorConfirmed = confirmed;
        } else if (isTarget(playerId)) {
            targetConfirmed = confirmed;
        } else {
            throw new IllegalArgumentException("Player %s is not part of this trade".formatted(playerId));
        }
        if (confirmed && initiatorConfirmed && targetConfirmed) {
            state = State.CONFIRMED;
        }
    }

    /**
     * Checks whether both players have confirmed the trade.
     *
     * @return {@code true} if both sides confirmed
     */
    public boolean isBothConfirmed() {
        return initiatorConfirmed && targetConfirmed;
    }

    // ── State Transitions ──────────────────────────────────────────────

    /**
     * Transitions the session from PENDING to ACTIVE.
     *
     * @throws IllegalStateException if not in PENDING state
     */
    public void activate() {
        if (state != State.PENDING) {
            throw new IllegalStateException("Cannot activate session in state %s".formatted(state));
        }
        this.state = State.ACTIVE;
    }

    /**
     * Marks the trade as completed.
     *
     * @throws IllegalStateException if not in CONFIRMED state
     */
    public void complete() {
        if (state != State.CONFIRMED) {
            throw new IllegalStateException("Cannot complete session in state %s".formatted(state));
        }
        this.state = State.COMPLETED;
        this.completedAt = Instant.now();
    }

    /**
     * Cancels the trade session.
     *
     * @throws IllegalStateException if already COMPLETED or CANCELLED
     */
    public void cancel() {
        if (state == State.COMPLETED || state == State.CANCELLED) {
            throw new IllegalStateException("Cannot cancel session in state %s".formatted(state));
        }
        this.state = State.CANCELLED;
        this.completedAt = Instant.now();
    }

    // ── Internal Helpers ───────────────────────────────────────────────

    private boolean isInitiator(UUID playerId) {
        return initiatorId.equals(playerId);
    }

    private boolean isTarget(UUID playerId) {
        return targetId.equals(playerId);
    }

    private void requireActive() {
        if (state != State.ACTIVE) {
            throw new IllegalStateException("Trade session is not active (state: %s)".formatted(state));
        }
    }

    private void resetConfirmations() {
        initiatorConfirmed = false;
        targetConfirmed = false;
    }

    @Override
    public String toString() {
        return "TradeSession{id=%s, initiator=%s, target=%s, state=%s, initiatorItems=%d, targetItems=%d}"
                .formatted(sessionId, initiatorId, targetId, state, initiatorOffer.size(), targetOffer.size());
    }
}
