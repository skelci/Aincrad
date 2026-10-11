package com.aincrad.endgame;

import java.util.Objects;

/**
 * Represents an endgame achievement with unlock condition and reward.
 * <p>
 * Achievements track player milestones across NG+ cycles, prestige levels,
 * leaderboard rankings, and raid completions. Each achievement has a unique
 * ID, name, description, condition, and reward.
 * </p>
 */
public final class Achievement {

    private final String id;
    private final String name;
    private final String description;
    private final AchievementCondition condition;
    private final String reward;
    private final int points;

    /**
     * Creates an achievement.
     *
     * @param id the unique achievement ID
     * @param name the display name
     * @param description the description shown to players
     * @param condition the unlock condition
     * @param reward the reward description
     * @param points the achievement points awarded
     */
    public Achievement(String id, String name, String description,
                       AchievementCondition condition, String reward, int points) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.condition = Objects.requireNonNull(condition, "condition");
        this.reward = Objects.requireNonNull(reward, "reward");
        this.points = points;
    }

    /**
     * Returns the unique achievement ID.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the display name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the unlock condition.
     *
     * @return the condition
     */
    public AchievementCondition getCondition() {
        return condition;
    }

    /**
     * Returns the reward description.
     *
     * @return the reward
     */
    public String getReward() {
        return reward;
    }

    /**
     * Returns the achievement points.
     *
     * @return the points
     */
    public int getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Achievement{" + id + ": " + name + "}";
    }

    /**
     * Condition types for achievement unlocking.
     */
    public enum AchievementCondition {
        /** Unlock after clearing a specific floor. */
        FLOOR_CLEAR,

        /** Unlock after reaching a specific NG+ cycle. */
        NG_PLUS_CYCLE,

        /** Unlock after reaching a specific prestige level. */
        PRESTIGE_LEVEL,

        /** Unlock after completing a specific number of weekly raids. */
        WEEKLY_RAID_COMPLETIONS,

        /** Unlock after reaching a specific leaderboard rank. */
        LEADERBOARD_RANK,

        /** Unlock after earning a specific amount of Col. */
        COL_EARNED,

        /** Unlock after killing a specific number of bosses. */
        BOSS_KILLS
    }
}
