package com.aincrad.endgame;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for New Game Plus (NG+) cycles across all players.
 * <p>
 * Provides static access to NG+ cycle tracking, bonus multiplier calculation,
 * entry requirements, and cycle leaderboards. The NG+ system allows players
 * who have cleared Floor 100 to restart the game with a stacking bonus.
 * </p>
 */
public final class NGPlusManager {

    private static class Holder {
        static final NGPlusManager INSTANCE = new NGPlusManager();
    }

    /** Player UUID → their NG+ data. */
    private final Map<UUID, NewGamePlus> ngPlusData = new ConcurrentHashMap<>();

    private NGPlusManager() {
    }

    /**
     * Returns the singleton instance of the NGPlusManager.
     *
     * @return the NGPlusManager instance
     */
    public static NGPlusManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── NG+ Access ──────────────────────────────────────────────────────

    /**
     * Returns the NG+ data for a player, creating it if absent.
     *
     * @param playerId the player's UUID
     * @return the NewGamePlus (never {@code null})
     */
    public NewGamePlus getNGPlus(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return ngPlusData.computeIfAbsent(playerId, NewGamePlus::new);
    }

    /**
     * Returns the current NG+ cycle for a player.
     *
     * @param playerId the player's UUID
     * @return the cycle count (0 = base game)
     */
    public static int getCycle(UUID playerId) {
        return getInstance().getNGPlus(playerId).getCycle();
    }

    /**
     * Returns the bonus multiplier for a player's current NG+ cycle.
     *
     * @param playerId the player's UUID
     * @return the bonus multiplier (0.0 to 0.25)
     */
    public static double getBonusMultiplier(UUID playerId) {
        return getInstance().getNGPlus(playerId).getBonusMultiplier();
    }

    /**
     * Returns the bonus multiplier for a given cycle count.
     *
     * @param cycle the NG+ cycle count
     * @return the bonus multiplier (0.0 to 0.25)
     */
    public static double getBonusMultiplier(int cycle) {
        return Math.min(cycle * NewGamePlus.BONUS_PER_CYCLE, NewGamePlus.MAX_BONUS);
    }

    // ── NG+ Entry ───────────────────────────────────────────────────────

    /**
     * Checks whether a player can enter NG+.
     * Requirements: must have cleared Floor 100.
     *
     * @param playerId the player's UUID
     * @param highestFloorCleared the player's highest floor cleared
     * @return {@code true} if the player meets all requirements
     */
    public static boolean canEnterNGPlus(UUID playerId, int highestFloorCleared) {
        return highestFloorCleared >= NewGamePlus.UNLOCK_FLOOR;
    }

    /**
     * Starts NG+ for a player: increments the cycle and returns the new cycle.
     *
     * @param playerId the player's UUID
     * @return the new cycle count
     * @throws IllegalStateException if the player has not cleared Floor 100
     */
    public static int startNGPlus(UUID playerId, int highestFloorCleared) {
        if (!canEnterNGPlus(playerId, highestFloorCleared)) {
            throw new IllegalStateException(
                    "Player must clear Floor " + NewGamePlus.UNLOCK_FLOOR + " to enter NG+");
        }
        return getInstance().getNGPlus(playerId).incrementCycle();
    }

    // ── Leaderboard ─────────────────────────────────────────────────────

    /**
     * Returns the top 100 players by NG+ cycle count.
     *
     * @return list of Map.Entry (UUID → cycle), sorted descending by cycle
     */
    public static List<Map.Entry<UUID, Integer>> getLeaderboard() {
        NGPlusManager mgr = getInstance();
        List<Map.Entry<UUID, Integer>> entries = new ArrayList<>();
        for (Map.Entry<UUID, NewGamePlus> e : mgr.ngPlusData.entrySet()) {
            entries.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().getCycle()));
        }
        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        return entries.subList(0, Math.min(100, entries.size()));
    }

    /**
     * Returns the rank of a player on the NG+ leaderboard.
     *
     * @param playerId the player's UUID
     * @return the 1-based rank, or -1 if not ranked
     */
    public static int getRank(UUID playerId) {
        List<Map.Entry<UUID, Integer>> board = getLeaderboard();
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getKey().equals(playerId)) {
                return i + 1;
            }
        }
        return -1;
    }
}
