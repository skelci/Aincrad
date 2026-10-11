package com.aincrad.paper.progression;

/**
 * Progression milestones achieved at specific level thresholds.
 * Each milestone corresponds to unlocking a new floor of the Aincrad tower.
 */
public enum Milestone {
    FLOOR_10(10, "Unlocked Floor 10 of Aincrad"),
    FLOOR_25(25, "Unlocked Floor 25 of Aincrad"),
    FLOOR_50(50, "Unlocked Floor 50 — Halfway to the Summit"),
    FLOOR_75(75, "Unlocked Floor 75 — The Final Stretch"),
    FLOOR_100(100, "Reached Floor 100 — Summit of Aincrad");

    private final int level;
    private final String description;

    Milestone(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() { return level; }
    public String getDescription() { return description; }

    public static Milestone forLevel(int level) {
        for (Milestone m : values()) {
            if (m.level == level) return m;
        }
        return null;
    }

    public boolean isAchievedAt(int level) {
        return level >= this.level;
    }
}
