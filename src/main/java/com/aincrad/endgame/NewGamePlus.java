package com.aincrad.endgame;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's New Game Plus (NG+) cycle data.
 * <p>
 * NG+ cycles increment each time a player completes Floor 100 and chooses
 * to restart. Each cycle grants a stacking bonus to XP and Col gains,
 * capped at a maximum multiplier.
 * </p>
 */
public final class NewGamePlus {

    /** Bonus per NG+ cycle (5%). */
    public static final double BONUS_PER_CYCLE = 0.05;

    /** Maximum NG+ bonus cap (25%). */
    public static final double MAX_BONUS = 0.25;

    /** Level to which a player resets upon entering NG+. */
    public static final int RESET_LEVEL = 60;

    /** Floor required to unlock NG+. */
    public static final int UNLOCK_FLOOR = 100;

    private final UUID playerId;
    private int cycle;
    private long lastNGPlusTimestamp;

    /**
     * Creates a new NG+ record for a player.
     *
     * @param playerId the player's UUID
     */
    public NewGamePlus(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.cycle = 0;
        this.lastNGPlusTimestamp = 0;
    }

    /**
     * Returns the player's UUID.
     *
     * @return the player UUID
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Returns the current NG+ cycle count.
     *
     * @return the cycle count (0 = base game, 1+ = NG+ cycles)
     */
    public int getCycle() {
        return cycle;
    }

    /**
     * Sets the NG+ cycle count.
     *
     * @param cycle the cycle count (must be >= 0)
     * @throws IllegalArgumentException if cycle is negative
     */
    public void setCycle(int cycle) {
        if (cycle < 0) {
            throw new IllegalArgumentException("NG+ cycle cannot be negative: " + cycle);
        }
        this.cycle = cycle;
    }

    /**
     * Increments the cycle counter and records the timestamp.
     *
     * @return the new cycle count
     */
    public int incrementCycle() {
        this.cycle++;
        this.lastNGPlusTimestamp = System.currentTimeMillis();
        return this.cycle;
    }

    /**
     * Returns the timestamp of the last NG+ transition.
     *
     * @return timestamp in milliseconds, or 0 if never entered NG+
     */
    public long getLastNGPlusTimestamp() {
        return lastNGPlusTimestamp;
    }

    /**
     * Calculates the bonus multiplier for the current cycle.
     * Each cycle adds 5%, capped at 25%.
     *
     * @return the bonus multiplier (e.g., 0.15 for cycle 3)
     */
    public double getBonusMultiplier() {
        return Math.min(cycle * BONUS_PER_CYCLE, MAX_BONUS);
    }

    /**
     * Checks whether the player has entered NG+ at least once.
     *
     * @return {@code true} if cycle > 0
     */
    public boolean isInNGPlus() {
        return cycle > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewGamePlus that = (NewGamePlus) o;
        return cycle == that.cycle && playerId.equals(that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, cycle);
    }

    @Override
    public String toString() {
        return "NewGamePlus{playerId=" + playerId + ", cycle=" + cycle + "}";
    }
}
