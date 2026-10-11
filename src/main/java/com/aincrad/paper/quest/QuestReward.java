package com.aincrad.paper.quest;

import java.util.List;

/**
 * Quest rewards: XP, Col currency, item drops, achievement points.
 */
public final class QuestReward {
    private final double xp;
    private final long col;
    private final List<String> items;
    private final int achievementPoints;

    public QuestReward(double xp, long col, List<String> items, int achievementPoints) {
        if (xp < 0) throw new IllegalArgumentException("XP cannot be negative");
        if (col < 0) throw new IllegalArgumentException("Col cannot be negative");
        if (achievementPoints < 0) throw new IllegalArgumentException("Achievement points cannot be negative");
        this.xp = xp;
        this.col = col;
        this.items = items != null ? List.copyOf(items) : List.of();
        this.achievementPoints = achievementPoints;
    }

    public static QuestReward nothing() {
        return new QuestReward(0, 0, List.of(), 0);
    }

    public double getXp() { return xp; }
    public long getCol() { return col; }
    public List<String> getItems() { return items; }
    public int getAchievementPoints() { return achievementPoints; }

    public boolean isEmpty() {
        return xp == 0 && col == 0 && items.isEmpty() && achievementPoints == 0;
    }

    @Override
    public String toString() {
        return "Reward[xp=" + xp + " col=" + col + " items=" + items + " ap=" + achievementPoints + "]";
    }
}
