package com.aincrad.paper.progression;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for all player progression.
 * Implements XP curve 50 * L^1.5, handles level-ups, milestone checks, and NG+ resets.
 */
public final class ProgressionManager {

    private static class Holder {
        static final ProgressionManager INSTANCE = new ProgressionManager();
    }

    private static final double XP_COEFFICIENT = 70.0;
    private static final double XP_EXPONENT = 1.5;
    private static final double NG_PLUS_BONUS_PER_CYCLE = 0.05;
    private static final double NG_PLUS_MAX_BONUS = 0.25;

    private final Map<UUID, PlayerProgression> progressions = new ConcurrentHashMap<>();

    private ProgressionManager() {}

    public static ProgressionManager getInstance() { return Holder.INSTANCE; }

    public static double xpForLevel(int level) {
        if (level < 1) throw new IllegalArgumentException("Level must be >= 1");
        return XP_COEFFICIENT * Math.pow(level, XP_EXPONENT);
    }

    public static double xpForNextLevel(int currentLevel) {
        if (currentLevel < 1) throw new IllegalArgumentException("Level must be >= 1");
        return xpForLevel(currentLevel + 1) - xpForLevel(currentLevel);
    }

    public PlayerProgression getProgression(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return progressions.computeIfAbsent(playerId, PlayerProgression::new);
    }

    public boolean hasProgression(UUID playerId) {
        return progressions.containsKey(playerId);
    }

    public int addXp(UUID playerId, double baseAmount) {
        if (baseAmount < 0) throw new IllegalArgumentException("XP amount must be non-negative");
        PlayerProgression progression = getProgression(playerId);
        double multiplier = getXpMultiplier(progression.getNgPlusCount());
        double adjustedAmount = baseAmount * multiplier;
        progression.addXp(adjustedAmount);
        return processLevelUps(progression);
    }

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

    private void checkMilestones(PlayerProgression progression, int oldLevel, int newLevel) {
        for (Milestone milestone : Milestone.values()) {
            if (milestone.getLevel() > oldLevel && milestone.getLevel() <= newLevel) {
                progression.achieveMilestone(milestone);
            }
        }
    }

    public PlayerProgression initiateNgPlus(UUID playerId) {
        PlayerProgression progression = getProgression(playerId);
        if (progression.getLevel() < PlayerProgression.NG_PLUS_RESET_LEVEL) {
            throw new IllegalStateException("Player must be at least level " + PlayerProgression.NG_PLUS_RESET_LEVEL + " to enter NG+");
        }
        progression.incrementNgPlus();
        progression.setLevel(PlayerProgression.NG_PLUS_RESET_LEVEL);
        progression.resetXp(xpForLevel(PlayerProgression.NG_PLUS_RESET_LEVEL));
        return progression;
    }

    public static double getXpMultiplier(int ngPlusCount) {
        double bonus = Math.min(ngPlusCount * NG_PLUS_BONUS_PER_CYCLE, NG_PLUS_MAX_BONUS);
        return 1.0 + bonus;
    }

    public Map<UUID, PlayerProgression> getAllProgressions() {
        return Collections.unmodifiableMap(progressions);
    }

    public PlayerProgression removeProgression(UUID playerId) {
        return progressions.remove(playerId);
    }

    public void reset() {
        progressions.clear();
    }
}
