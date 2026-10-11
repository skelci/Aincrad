package com.aincrad.endgame;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for achievements.
 * <p>
 * Tracks unlocked achievements per player, validates conditions, and
 * manages rewards. Achievements cover NG+ cycles, prestige levels,
 * leaderboard rankings, raid completions, and more.
 * </p>
 */
public final class AchievementManager {

    private static class Holder {
        static final AchievementManager INSTANCE = new AchievementManager();
    }

    /** All registered achievements by ID. */
    private final Map<String, Achievement> achievements = new ConcurrentHashMap<>();

    /** Player UUID → set of unlocked achievement IDs. */
    private final Map<UUID, Set<String>> unlockedAchievements = new ConcurrentHashMap<>();

    /** Player UUID → total achievement points. */
    private final Map<UUID, Integer> achievementPoints = new ConcurrentHashMap<>();

    private AchievementManager() {
        registerDefaultAchievements();
    }

    /**
     * Returns the singleton instance of the AchievementManager.
     *
     * @return the AchievementManager instance
     */
    public static AchievementManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── Achievement Registration ────────────────────────────────────────

    /**
     * Registers an achievement.
     *
     * @param achievement the achievement to register
     */
    public void registerAchievement(Achievement achievement) {
        Objects.requireNonNull(achievement, "achievement");
        achievements.put(achievement.getId(), achievement);
    }

    /**
     * Returns all registered achievements.
     *
     * @return collection of all achievements
     */
    public Collection<Achievement> getAllAchievements() {
        return Collections.unmodifiableCollection(achievements.values());
    }

    /**
     * Returns an achievement by ID.
     *
     * @param id the achievement ID
     * @return the Achievement, or null
     */
    public Achievement getAchievement(String id) {
        return achievements.get(id);
    }

    // ── Unlock Tracking ─────────────────────────────────────────────────

    /**
     * Checks whether a player has unlocked an achievement.
     *
     * @param playerId the player's UUID
     * @param achievementId the achievement ID
     * @return {@code true} if unlocked
     */
    public boolean isUnlocked(UUID playerId, String achievementId) {
        Set<String> unlocked = unlockedAchievements.get(playerId);
        return unlocked != null && unlocked.contains(achievementId);
    }

    /**
     * Unlocks an achievement for a player.
     *
     * @param playerId the player's UUID
     * @param achievementId the achievement ID
     * @return {@code true} if newly unlocked, {@code false} if already unlocked
     */
    public boolean unlock(UUID playerId, String achievementId) {
        if (isUnlocked(playerId, achievementId)) {
            return false;
        }
        unlockedAchievements.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet())
                .add(achievementId);
        Achievement achievement = achievements.get(achievementId);
        if (achievement != null) {
            achievementPoints.merge(playerId, achievement.getPoints(), Integer::sum);
        }
        return true;
    }

    /**
     * Returns all unlocked achievement IDs for a player.
     *
     * @param playerId the player's UUID
     * @return set of unlocked achievement IDs
     */
    public Set<String> getUnlockedAchievements(UUID playerId) {
        return Collections.unmodifiableSet(
                unlockedAchievements.getOrDefault(playerId, Collections.emptySet()));
    }

    /**
     * Returns the total achievement points for a player.
     *
     * @param playerId the player's UUID
     * @return the total points
     */
    public int getPoints(UUID playerId) {
        return achievementPoints.getOrDefault(playerId, 0);
    }

    // ── Condition Checking ──────────────────────────────────────────────

    /**
     * Checks and unlocks achievements based on a player's current stats.
     * Call this whenever a player's stats change.
     *
     * @param playerId the player's UUID
     * @param stats the player's current endgame stats
     * @return list of newly unlocked achievements
     */
    public List<Achievement> checkAndUnlock(UUID playerId, EndgameStats stats) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        for (Achievement achievement : achievements.values()) {
            if (isUnlocked(playerId, achievement.getId())) {
                continue;
            }
            if (checkCondition(achievement, stats)) {
                unlock(playerId, achievement.getId());
                newlyUnlocked.add(achievement);
            }
        }
        return newlyUnlocked;
    }

    private boolean checkCondition(Achievement achievement, EndgameStats stats) {
        return switch (achievement.getCondition()) {
            case FLOOR_CLEAR -> stats.highestFloorCleared() >= getRequiredValue(achievement);
            case NG_PLUS_CYCLE -> stats.ngPlusCycle() >= getRequiredValue(achievement);
            case PRESTIGE_LEVEL -> stats.prestigeLevel() >= getRequiredValue(achievement);
            case WEEKLY_RAID_COMPLETIONS -> stats.weeklyRaidCompletions() >= getRequiredValue(achievement);
            case LEADERBOARD_RANK -> stats.leaderboardRank() <= getRequiredValue(achievement)
                    && stats.leaderboardRank() > 0;
            case COL_EARNED -> stats.totalColEarned() >= getRequiredValue(achievement);
            case BOSS_KILLS -> stats.bossKills() >= getRequiredValue(achievement);
        };
    }

    private int getRequiredValue(Achievement achievement) {
        // The required value is encoded in the achievement ID after the last underscore
        // e.g., "floor_clear_100" → 100
        String id = achievement.getId();
        int lastUnderscore = id.lastIndexOf('_');
        if (lastUnderscore >= 0 && lastUnderscore < id.length() - 1) {
            try {
                return Integer.parseInt(id.substring(lastUnderscore + 1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // ── Display ─────────────────────────────────────────────────────────

    /**
     * Returns a formatted display string for a player's achievements.
     *
     * @param playerId the player's UUID
     * @return formatted achievement list
     */
    public String getDisplay(UUID playerId) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Achievements ===\n");
        sb.append("Points: ").append(getPoints(playerId)).append("\n");
        Set<String> unlocked = getUnlockedAchievements(playerId);
        for (Achievement a : achievements.values()) {
            sb.append(unlocked.contains(a.getId()) ? "[✓] " : "[ ] ")
                    .append(a.getName()).append(" - ").append(a.getDescription())
                    .append(" (").append(a.getPoints()).append(" pts)\n");
        }
        return sb.toString();
    }

    // ── Default Achievements ────────────────────────────────────────────

    private void registerDefaultAchievements() {
        registerAchievement(new Achievement("floor_clear_50", "Halfway There",
                "Clear Floor 50", Achievement.AchievementCondition.FLOOR_CLEAR,
                "Bronze Prestige", 10));

        registerAchievement(new Achievement("floor_clear_100", "Summit Reached",
                "Clear Floor 100", Achievement.AchievementCondition.FLOOR_CLEAR,
                "Gold Prestige", 25));

        registerAchievement(new Achievement("ng_plus_1", "New Game Plus",
                "Complete NG+ cycle 1", Achievement.AchievementCondition.NG_PLUS_CYCLE,
                "NG+ Veteran Title", 15));

        registerAchievement(new Achievement("ng_plus_5", "Veteran of Cycles",
                "Complete NG+ cycle 5", Achievement.AchievementCondition.NG_PLUS_CYCLE,
                "NG+ Master Title", 50));

        registerAchievement(new Achievement("prestige_bronze", "Bronze Prestige",
                "Reach Bronze prestige", Achievement.AchievementCondition.PRESTIGE_LEVEL,
                "+5% Col Bonus", 10));

        registerAchievement(new Achievement("prestige_aincrad", "Aincrad Prestige",
                "Reach Aincrad prestige", Achievement.AchievementCondition.PRESTIGE_LEVEL,
                "+50% Col Bonus, Aincrad Title", 100));

        registerAchievement(new Achievement("raid_1", "Raid Initiate",
                "Complete 1 weekly raid", Achievement.AchievementCondition.WEEKLY_RAID_COMPLETIONS,
                "Raid Chest", 5));

        registerAchievement(new Achievement("raid_10", "Raid Veteran",
                "Complete 10 weekly raids", Achievement.AchievementCondition.WEEKLY_RAID_COMPLETIONS,
                "Raid Master Title", 30));

        registerAchievement(new Achievement("leaderboard_1", "Top of the Board",
                "Reach rank 1 on any leaderboard", Achievement.AchievementCondition.LEADERBOARD_RANK,
                "Legendary Title", 50));

        registerAchievement(new Achievement("col_1000000", "Millionaire",
                "Earn 1,000,000 Col total", Achievement.AchievementCondition.COL_EARNED,
                "Wealthy Title", 20));

        registerAchievement(new Achievement("boss_kills_100", "Centurion",
                "Kill 100 bosses", Achievement.AchievementCondition.BOSS_KILLS,
                "Slayer Title", 15));
    }

    /**
     * Record of player endgame stats for achievement checking.
     */
    public record EndgameStats(
            int highestFloorCleared,
            int ngPlusCycle,
            int prestigeLevel,
            int weeklyRaidCompletions,
            int leaderboardRank,
            long totalColEarned,
            int bossKills
    ) {}
}
