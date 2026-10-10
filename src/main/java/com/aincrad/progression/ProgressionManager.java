package com.aincrad.progression;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for all player progression.
 * <p>
 * Implements the XP curve {@code 50 * L^1.5}, handles level-ups, milestone
 * checks, and NG+ resets. The NG+ system resets the player to level 60,
 * preserves cosmetics (skill points, stat points, milestones), and grants
 * a stacking +5% XP/Col bonus per NG+ cycle up to a +25% cap.
 * </p>
 */
public final class ProgressionManager {

    private static class Holder {
        static final ProgressionManager INSTANCE = new ProgressionManager();
    }

    /** XP curve coefficient. */
    private static final double XP_COEFFICIENT = 50.0;

    /** XP curve exponent. */
    private static final double XP_EXPONENT = 1.5;

    /** NG+ XP/Col bonus per cycle (5%). */
    private static final double NG_PLUS_BONUS_PER_CYCLE = 0.05;

    /** Maximum NG+ bonus cap (25%). */
    private static final double NG_PLUS_MAX_BONUS = 0.25;

    /** Player UUID → their progression data. */
    private final Map<UUID, PlayerProgression> progressions = new ConcurrentHashMap<>();

    private ProgressionManager() {
    }

    /**
     * Returns the singleton instance of the ProgressionManager.
     *
     * @return the ProgressionManager instance
     */
    public static ProgressionManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── XP Curve ───────────────────────────────────────────────────────

    /**
     * Calculates the total XP required to reach a given level using the
     * formula {@code 50 * L^1.5}.
     *
     * @param level the target level (must be >= 1)
     * @return total XP required
     */
    public static double xpForLevel(int level) {
        if (level < 1) throw new IllegalArgumentException("Level must be >= 1");
        return XP_COEFFICIENT * Math.pow(level, XP_EXPONENT);
    }

    /**
     * Calculates the XP needed to advance from one level to the next.
     *
     * @param currentLevel the current level
     * @return XP difference between currentLevel+1 and currentLevel
     */
    public static double xpForNextLevel(int currentLevel) {
        if (currentLevel < 1) throw new IllegalArgumentException("Level must be >= 1");
        return xpForLevel(currentLevel + 1) - xpForLevel(currentLevel);
    }

    // ── Progression Access ─────────────────────────────────────────────

    /**
     * Returns the progression data for a player, creating it if absent.
     *
     * @param playerId the player's UUID
     * @return the PlayerProgression (never {@code null})
     */
    public PlayerProgression getProgression(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return progressions.computeIfAbsent(playerId, PlayerProgression::new);
    }

    /**
     * Checks whether progression data exists for a player.
     *
     * @param playerId the player's UUID
     * @return {@code true} if data exists
     */
    public boolean hasProgression(UUID playerId) {
        return progressions.containsKey(playerId);
    }

    // ── XP & Level-Ups ─────────────────────────────────────────────────

    /**
     * Grants XP to a player, applying the NG+ multiplier and processing
     * any resulting level-ups.
     *
     * @param playerId  the player receiving XP
     * @param baseAmount the base XP amount (before NG+ bonus)
     * @return the number of levels gained
     */
    public int addXp(UUID playerId, double baseAmount) {
        if (baseAmount < 0) throw new IllegalArgumentException("XP amount must be non-negative");
        PlayerProgression progression = getProgression(playerId);
        double multiplier = getXpMultiplier(progression.getNgPlusCount());
        double adjustedAmount = baseAmount * multiplier;
        progression.addXp(adjustedAmount);
        return processLevelUps(progression);
    }

    /**
     * Processes all pending level-ups for a player.
     *
     * @param progression the player's progression data
     * @return the number of levels gained
     */
    private int processLevelUps(PlayerProgression progression) {
        int levelsGained = 0;
        while (!progression.isMaxLevel()
                && progression.getXp() >= xpForLevel(progression.getLevel() + 1)) {
            int oldLevel = progression.getLevel();
            progression.setLevel(oldLevel + 1);
            progression.addSkillPoints(PlayerProgression.SKILL_POINTS_PER_LEVEL);
            progression.addStatPoints(PlayerProgression.STAT_POINTS_PER_LEVEL);
            checkMilestones(progression, oldLevel, progression.getLevel());
            levelsGained++;
        }
        return levelsGained;
    }

    /**
     * Checks and awards any milestones crossed during a level-up.
     *
     * @param progression the player's progression data
     * @param oldLevel    the level before the level-up
     * @param newLevel    the level after the level-up
     */
    private void checkMilestones(PlayerProgression progression, int oldLevel, int newLevel) {
        for (Milestone milestone : Milestone.values()) {
            if (milestone.getLevel() > oldLevel && milestone.getLevel() <= newLevel) {
                progression.achieveMilestone(milestone);
            }
        }
    }

    // ── NG+ System ─────────────────────────────────────────────────────

    /**
     * Initiates NG+ for a player: resets level to 60, resets XP to the
     * level-60 threshold, increments the NG+ counter, and preserves all
     * skill points, stat points, and milestones.
     *
     * @param playerId the player entering NG+
     * @return the updated PlayerProgression
     * @throws IllegalStateException if the player is below the NG+ reset level
     */
    public PlayerProgression initiateNgPlus(UUID playerId) {
        PlayerProgression progression = getProgression(playerId);
        if (progression.getLevel() < PlayerProgression.NG_PLUS_RESET_LEVEL) {
            throw new IllegalStateException(
                    "Player must be at least level %d to enter NG+ (current: %d)".formatted(
                            PlayerProgression.NG_PLUS_RESET_LEVEL, progression.getLevel()));
        }
        progression.incrementNgPlus();
        progression.setLevel(PlayerProgression.NG_PLUS_RESET_LEVEL);
        progression.resetXp(xpForLevel(PlayerProgression.NG_PLUS_RESET_LEVEL));
        return progression;
    }

    /**
     * Returns the XP/Col multiplier based on NG+ count.
     * <p>
     * Formula: {@code 1.0 + 0.05 * min(ngPlusCount, 5)}, capped at 1.25.
     * </p>
     *
     * @param ngPlusCount the player's NG+ count
     * @return the multiplier (1.0 to 1.25)
     */
    public static double getXpMultiplier(int ngPlusCount) {
        double bonus = Math.min(ngPlusCount * NG_PLUS_BONUS_PER_CYCLE, NG_PLUS_MAX_BONUS);
        return 1.0 + bonus;
    }

    // ── Utility ────────────────────────────────────────────────────────

    /**
     * Returns all tracked progression data.
     *
     * @return unmodifiable map of player UUID → PlayerProgression
     */
    public Map<UUID, PlayerProgression> getAllProgressions() {
        return Collections.unmodifiableMap(progressions);
    }

    /**
     * Removes a player's progression data (e.g., on server leave if not persisting).
     *
     * @param playerId the player's UUID
     * @return the removed PlayerProgression, or {@code null}
     */
    public PlayerProgression removeProgression(UUID playerId) {
        return progressions.remove(playerId);
    }
}
