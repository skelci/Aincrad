package com.aincrad.progression;

/**
 * Represents a progression milestone achieved at specific level thresholds.
 * <p>
 * Each milestone corresponds to unlocking a new floor of the Aincrad tower
 * and provides a descriptive unlock message for the player.
 * </p>
 */
public enum Milestone {

    /** Floor 10 milestone — the first major checkpoint. */
    FLOOR_10(10, "Unlocked Floor 10 of Aincrad"),

    /** Floor 25 milestone — a quarter of the tower conquered. */
    FLOOR_25(25, "Unlocked Floor 25 of Aincrad"),

    /** Floor 50 milestone — halfway to the top of Aincrad. */
    FLOOR_50(50, "Unlocked Floor 50 — Halfway to the Summit"),

    /** Floor 75 milestone — the final stretch of the tower. */
    FLOOR_75(75, "Unlocked Floor 75 — The Final Stretch"),

    /** Floor 100 milestone — max level, the summit of Aincrad. */
    FLOOR_100(100, "Reached Floor 100 — Summit of Aincrad");

    private final int level;
    private final String description;

    /**
     * Creates a milestone.
     *
     * @param level       the level at which this milestone is achieved
     * @param description the unlock description shown to the player
     */
    Milestone(int level, String description) {
        this.level = level;
        this.description = description;
    }

    /**
     * Returns the level threshold for this milestone.
     *
     * @return the level (10, 25, 50, 75, or 100)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the unlock description.
     *
     * @return the human-readable description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Finds the milestone for a given level, or {@code null} if none.
     *
     * @param level the level to look up
     * @return the matching Milestone, or {@code null}
     */
    public static Milestone forLevel(int level) {
        for (Milestone m : values()) {
            if (m.level == level) return m;
        }
        return null;
    }

    /**
     * Checks whether a given level qualifies for this milestone.
     *
     * @param level the level to check
     * @return {@code true} if the level meets or exceeds this milestone's threshold
     */
    public boolean isAchievedAt(int level) {
        return level >= this.level;
    }
}
