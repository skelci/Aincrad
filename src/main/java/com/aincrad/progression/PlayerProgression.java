package com.aincrad.progression;

import java.util.*;

/**
 * Data model for a single player's progression state.
 * <p>
 * Tracks level (capped at 100), accumulated XP, skill points, stat points,
 * achieved milestones, and NG+ count. Instances are created and managed by
 * {@link ProgressionManager}.
 * </p>
 */
public final class PlayerProgression {

    /** Maximum achievable level. */
    public static final int MAX_LEVEL = 100;

    /** Level to which a player resets upon entering NG+. */
    public static final int NG_PLUS_RESET_LEVEL = 60;

    /** Skill points granted per level. */
    public static final int SKILL_POINTS_PER_LEVEL = 3;

    /** Stat points granted per level. */
    public static final int STAT_POINTS_PER_LEVEL = 5;

    private final UUID playerId;
    private int level;
    private double xp;
    private int skillPoints;
    private int statPoints;
    private final Set<Milestone> milestonesAchieved;
    private int ngPlusCount;

    /**
     * Creates a new progression record for a player.
     *
     * @param playerId the player's UUID
     */
    public PlayerProgression(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.level = 1;
        this.xp = 0.0;
        this.skillPoints = 0;
        this.statPoints = 0;
        this.milestonesAchieved = EnumSet.noneOf(Milestone.class);
        this.ngPlusCount = 0;
    }

    // ── Getters ────────────────────────────────────────────────────────

    public UUID getPlayerId() {
        return playerId;
    }

    public int getLevel() {
        return level;
    }

    public double getXp() {
        return xp;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public int getNgPlusCount() {
        return ngPlusCount;
    }

    /**
     * Returns an unmodifiable view of achieved milestones.
     *
     * @return set of achieved milestones
     */
    public Set<Milestone> getMilestonesAchieved() {
        return Collections.unmodifiableSet(milestonesAchieved);
    }

    // ── State Queries ──────────────────────────────────────────────────

    /**
     * Checks whether the player has reached max level.
     *
     * @return {@code true} if level >= {@value #MAX_LEVEL}
     */
    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    /**
     * Checks whether a specific milestone has been achieved.
     *
     * @param milestone the milestone to check
     * @return {@code true} if achieved
     */
    public boolean hasMilestone(Milestone milestone) {
        return milestonesAchieved.contains(milestone);
    }

    /**
     * Returns the XP progress toward the next level as a fraction [0.0, 1.0].
     *
     * @return progress fraction, or 1.0 if at max level
     */
    public double getProgressToNextLevel() {
        if (isMaxLevel()) return 1.0;
        double currentLevelXp = ProgressionManager.xpForLevel(level);
        double nextLevelXp = ProgressionManager.xpForLevel(level + 1);
        double progress = (xp - currentLevelXp) / (nextLevelXp - currentLevelXp);
        return Math.max(0.0, Math.min(1.0, progress));
    }

    // ── Package-Private Mutators (used by ProgressionManager) ──────────

    /**
     * Sets the player's level. Only called by {@link ProgressionManager}.
     *
     * @param level the new level (1–{@value #MAX_LEVEL})
     */
    void setLevel(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("Level must be between 1 and %d".formatted(MAX_LEVEL));
        }
        this.level = level;
    }

    /**
     * Adds XP to the player's total. Only called by {@link ProgressionManager}.
     *
     * @param amount the XP amount to add (must be non-negative)
     */
    void addXp(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Cannot add negative XP");
        this.xp += amount;
    }

    /**
     * Adds skill points. Only called by {@link ProgressionManager}.
     *
     * @param amount the amount to add
     */
    void addSkillPoints(int amount) {
        this.skillPoints += amount;
    }

    /**
     * Adds stat points. Only called by {@link ProgressionManager}.
     *
     * @param amount the amount to add
     */
    void addStatPoints(int amount) {
        this.statPoints += amount;
    }

    /**
     * Marks a milestone as achieved. Only called by {@link ProgressionManager}.
     *
     * @param milestone the milestone to achieve
     */
    void achieveMilestone(Milestone milestone) {
        this.milestonesAchieved.add(milestone);
    }

    /**
     * Increments the NG+ counter. Only called by {@link ProgressionManager}.
     */
    void incrementNgPlus() {
        this.ngPlusCount++;
    }

    /**
     * Resets XP to the given value. Only called by {@link ProgressionManager} during NG+.
     *
     * @param xp the new XP value
     */
    void resetXp(double xp) {
        this.xp = xp;
    }

    @Override
    public String toString() {
        return "PlayerProgression{player=%s, level=%d, xp=%.1f, skill=%d, stat=%d, ng+=%d, milestones=%s}"
                .formatted(playerId, level, xp, skillPoints, statPoints, ngPlusCount, milestonesAchieved);
    }
}
