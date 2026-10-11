package com.aincrad.endgame;

import java.util.Objects;
import java.util.UUID;

/**
 * A single leaderboard entry for a player.
 * <p>
 * Stores the player's ID, score, floor reached, and completion time.
 * Used across all leaderboard categories.
 * </p>
 */
public final class Leaderboard {

    private final UUID playerId;
    private final String playerName;
    private final long score;
    private final int floor;
    private final long timeInMillis;
    private final long timestamp;

    /**
     * Creates a leaderboard entry.
     *
     * @param playerId the player's UUID
     * @param playerName the player's display name
     * @param score the score value (floor clear time, level, Col, or kills)
     * @param floor the floor reached
     * @param timeInMillis the time taken (for speed clears) or 0
     */
    public Leaderboard(UUID playerId, String playerName, long score, int floor, long timeInMillis) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.playerName = Objects.requireNonNull(playerName, "playerName");
        this.score = score;
        this.floor = floor;
        this.timeInMillis = timeInMillis;
        this.timestamp = System.currentTimeMillis();
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
     * Returns the player's display name.
     *
     * @return the player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns the score value.
     *
     * @return the score
     */
    public long getScore() {
        return score;
    }

    /**
     * Returns the floor reached.
     *
     * @return the floor
     */
    public int getFloor() {
        return floor;
    }

    /**
     * Returns the time in milliseconds.
     *
     * @return the time
     */
    public long getTimeInMillis() {
        return timeInMillis;
    }

    /**
     * Returns the entry creation timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leaderboard that = (Leaderboard) o;
        return score == that.score && floor == that.floor && timeInMillis == that.timeInMillis
                && playerId.equals(that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, score, floor, timeInMillis);
    }

    @Override
    public String toString() {
        return "Leaderboard{" + playerName + ", score=" + score + ", floor=" + floor + "}";
    }
}
