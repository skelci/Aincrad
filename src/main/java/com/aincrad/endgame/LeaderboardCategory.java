package com.aincrad.endgame;

/**
 * Categories for leaderboard rankings.
 * <p>
 * Each category represents a different way to rank players by their
 * endgame performance and progression.
 * </p>
 */
public enum LeaderboardCategory {

    /** Ranked by fastest Floor 100 clear time (lower is better). */
    FASTEST_CLEAR("Fastest Clear", "Fastest Floor 100 Clear Time"),

    /** Ranked by highest player level. */
    HIGHEST_LEVEL("Highest Level", "Highest Player Level"),

    /** Ranked by total Col earned. */
    RICHEST("Richest", "Total Col Earned"),

    /** Ranked by total boss kills. */
    MOST_KILLS("Most Kills", "Total Boss Kills");

    private final String displayName;
    private final String description;

    LeaderboardCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Returns the display name of this category.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the description of this category.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether lower values are better for this category.
     * For FASTEST_CLEAR, lower time is better.
     *
     * @return {@code true} if lower is better
     */
    public boolean isLowerBetter() {
        return this == FASTEST_CLEAR;
    }
}
