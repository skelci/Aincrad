package com.aincrad.endgame;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton manager for all leaderboards.
 * <p>
 * Maintains separate leaderboards for each {@link LeaderboardCategory},
 * tracks player entries, and provides top-N queries. The top 100 players
 * are tracked per category.
 * </p>
 */
public final class LeaderboardManager {

    private static class Holder {
        static final LeaderboardManager INSTANCE = new LeaderboardManager();
    }

    /** Maximum entries per leaderboard. */
    public static final int MAX_ENTRIES = 100;

    /** Category → list of entries (sorted). */
    private final Map<LeaderboardCategory, List<Leaderboard>> leaderboards = new ConcurrentHashMap<>();

    /** Player UUID → their entries by category. */
    private final Map<UUID, Map<LeaderboardCategory, Leaderboard>> playerEntries = new ConcurrentHashMap<>();

    private LeaderboardManager() {
        for (LeaderboardCategory cat : LeaderboardCategory.values()) {
            leaderboards.put(cat, new CopyOnWriteArrayList<>());
        }
    }

    /**
     * Returns the singleton instance of the LeaderboardManager.
     *
     * @return the LeaderboardManager instance
     */
    public static LeaderboardManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── Entry Management ────────────────────────────────────────────────

    /**
     * Submits or updates a player's entry in a leaderboard category.
     * If the player already has an entry, it is replaced only if the new
     * score is better.
     *
     * @param category the leaderboard category
     * @param entry the leaderboard entry
     */
    public void submitEntry(LeaderboardCategory category, Leaderboard entry) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(entry, "entry");

        List<Leaderboard> board = leaderboards.get(category);
        UUID playerId = entry.getPlayerId();

        // Remove existing entry for this player
        board.removeIf(e -> e.getPlayerId().equals(playerId));

        // Add new entry
        board.add(entry);

        // Sort
        sortBoard(category, board);

        // Trim to max
        if (board.size() > MAX_ENTRIES) {
            List<Leaderboard> trimmed = new ArrayList<>(board.subList(0, MAX_ENTRIES));
            leaderboards.put(category, new CopyOnWriteArrayList<>(trimmed));
        }

        // Track player's entry
        playerEntries.computeIfAbsent(playerId, k -> new EnumMap<>(LeaderboardCategory.class))
                .put(category, entry);
    }

    /**
     * Returns the top N entries for a category.
     *
     * @param category the leaderboard category
     * @param n the number of entries to return
     * @return list of top entries (up to n)
     */
    public List<Leaderboard> getTop(LeaderboardCategory category, int n) {
        Objects.requireNonNull(category, "category");
        List<Leaderboard> board = leaderboards.get(category);
        if (board.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(board.subList(0, Math.min(n, board.size())));
    }

    /**
     * Returns the top 100 entries for a category.
     *
     * @param category the leaderboard category
     * @return list of top entries
     */
    public List<Leaderboard> getTop100(LeaderboardCategory category) {
        return getTop(category, MAX_ENTRIES);
    }

    /**
     * Returns a player's entry for a category, or empty if not ranked.
     *
     * @param category the leaderboard category
     * @param playerId the player's UUID
     * @return the entry, or null
     */
    public Leaderboard getEntry(LeaderboardCategory category, UUID playerId) {
        Map<LeaderboardCategory, Leaderboard> entries = playerEntries.get(playerId);
        if (entries == null) {
            return null;
        }
        return entries.get(category);
    }

    /**
     * Returns a player's rank (1-based) for a category, or -1 if not ranked.
     *
     * @param category the leaderboard category
     * @param playerId the player's UUID
     * @return the rank, or -1
     */
    public int getRank(LeaderboardCategory category, UUID playerId) {
        List<Leaderboard> board = leaderboards.get(category);
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getPlayerId().equals(playerId)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Clears all entries for a category.
     *
     * @param category the leaderboard category
     */
    public void clearCategory(LeaderboardCategory category) {
        leaderboards.get(category).clear();
        for (Map<LeaderboardCategory, Leaderboard> entries : playerEntries.values()) {
            entries.remove(category);
        }
    }

    /**
     * Clears all leaderboards.
     */
    public void clearAll() {
        for (List<Leaderboard> board : leaderboards.values()) {
            board.clear();
        }
        playerEntries.clear();
    }

    // ── Internal ────────────────────────────────────────────────────────

    private void sortBoard(LeaderboardCategory category, List<Leaderboard> board) {
        if (category.isLowerBetter()) {
            board.sort(Comparator.comparingLong(Leaderboard::getTimeInMillis)
                    .thenComparing(Comparator.comparingLong(Leaderboard::getScore).reversed()));
        } else {
            board.sort(Comparator.comparingLong(Leaderboard::getScore).reversed()
                    .thenComparing(Comparator.comparingInt(Leaderboard::getFloor).reversed()));
        }
    }
}
