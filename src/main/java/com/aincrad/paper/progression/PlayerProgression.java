package com.aincrad.paper.progression;

import java.util.*;

/**
 * Data model for a single player's progression state.
 * Tracks level (capped at 100), accumulated XP, skill points, stat points,
 * achieved milestones, and NG+ count.
 */
public final class PlayerProgression {
    public static final int MAX_LEVEL = 100;
    public static final int NG_PLUS_RESET_LEVEL = 60;
    public static final int SKILL_POINTS_PER_LEVEL = 3;
    public static final int STAT_POINTS_PER_LEVEL = 5;

    private final UUID playerId;
    private int level;
    private double xp;
    private int skillPoints;
    private int statPoints;
    private final Set<Milestone> milestonesAchieved;
    private int ngPlusCount;

    public PlayerProgression(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.level = 1;
        this.xp = 0.0;
        this.skillPoints = 0;
        this.statPoints = 0;
        this.milestonesAchieved = EnumSet.noneOf(Milestone.class);
        this.ngPlusCount = 0;
    }

    public UUID getPlayerId() { return playerId; }
    public int getLevel() { return level; }
    public double getXp() { return xp; }
    public int getSkillPoints() { return skillPoints; }
    public int getStatPoints() { return statPoints; }
    public int getNgPlusCount() { return ngPlusCount; }

    public Set<Milestone> getMilestonesAchieved() {
        return Collections.unmodifiableSet(milestonesAchieved);
    }

    public boolean isMaxLevel() { return level >= MAX_LEVEL; }

    public boolean hasMilestone(Milestone milestone) {
        return milestonesAchieved.contains(milestone);
    }

    public double getProgressToNextLevel() {
        if (isMaxLevel()) return 1.0;
        double currentLevelXp = ProgressionManager.xpForLevel(level);
        double nextLevelXp = ProgressionManager.xpForLevel(level + 1);
        double progress = (xp - currentLevelXp) / (nextLevelXp - currentLevelXp);
        return Math.max(0.0, Math.min(1.0, progress));
    }

    void setLevel(int level) {
        if (level < 1 || level > MAX_LEVEL) throw new IllegalArgumentException("Level must be between 1 and " + MAX_LEVEL);
        this.level = level;
    }

    void addXp(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Cannot add negative XP");
        this.xp += amount;
    }

    void addSkillPoints(int amount) { this.skillPoints += amount; }
    void addStatPoints(int amount) { this.statPoints += amount; }

    void achieveMilestone(Milestone milestone) {
        this.milestonesAchieved.add(milestone);
    }

    void incrementNgPlus() { this.ngPlusCount++; }

    void resetXp(double xp) { this.xp = xp; }

    @Override
    public String toString() {
        return "PlayerProgression{player=" + playerId + " level=" + level
                + " xp=" + String.format("%.1f", xp) + " skill=" + skillPoints
                + " stat=" + statPoints + " ng+=" + ngPlusCount
                + " milestones=" + milestonesAchieved + "}";
    }
}
