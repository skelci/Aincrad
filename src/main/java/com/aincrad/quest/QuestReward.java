package com.aincrad.quest;

import java.util.List;
import java.util.Objects;

/**
 * Represents the rewards granted upon quest completion.
 * Includes experience points, Col (the in-game currency), item drops,
 * and achievement points.
 */
public final class QuestReward {

    /** Immutable item drop definition used by {@link QuestReward}. */
    public record ItemDrop(String itemId, int count) {
        /**
         * Creates a validated item drop.
         *
         * @param itemId Non-null, non-empty item identifier (e.g. {@code "minecraft:diamond"}).
         * @param count  Positive stack size.
         * @throws IllegalArgumentException if {@code itemId} is blank or {@code count} is non-positive.
         * @throws NullPointerException     if {@code itemId} is null.
         */
        public ItemDrop {
            Objects.requireNonNull(itemId, "itemId");
            if (itemId.isBlank()) {
                throw new IllegalArgumentException("itemId must not be blank.");
            }
            if (count <= 0) {
                throw new IllegalArgumentException("count must be positive: " + count);
            }
        }
    }

    private final int experience;
    private final int col;
    private final List<ItemDrop> items;
    private final int achievementPoints;

    /**
     * Creates a quest reward.
     *
     * @param experience       Experience points to grant (clamped to {@code >= 0}).
     * @param col              Col currency to grant (clamped to {@code >= 0}).
     * @param items            Item drops; may be null or empty. Defensive copy is made.
     * @param achievementPoints Achievement points to grant (clamped to {@code >= 0}).
     * @throws IllegalArgumentException if any numeric value is negative.
     */
    public QuestReward(int experience, int col, List<ItemDrop> items, int achievementPoints) {
        if (experience < 0) {
            throw new IllegalArgumentException("experience must be >= 0: " + experience);
        }
        if (col < 0) {
            throw new IllegalArgumentException("col must be >= 0: " + col);
        }
        if (achievementPoints < 0) {
            throw new IllegalArgumentException("achievementPoints must be >= 0: " + achievementPoints);
        }
        this.experience = experience;
        this.col = col;
        this.items = items == null ? List.of() : List.copyOf(items);
        this.achievementPoints = achievementPoints;
    }

    /** Convenience factory for an XP-only reward. */
    public static QuestReward ofExperience(int experience) {
        return new QuestReward(experience, 0, List.of(), 0);
    }

    /** Convenience factory for an XP + Col reward. */
    public static QuestReward ofExperienceAndCol(int experience, int col) {
        return new QuestReward(experience, col, List.of(), 0);
    }

    /** Convenience factory for an item-only reward. */
    public static QuestReward ofItems(List<ItemDrop> items) {
        return new QuestReward(0, 0, items, 0);
    }

    /**
     * @return The experience points granted.
     */
    public int experience() {
        return experience;
    }

    /**
     * @return The Col currency granted.
     */
    public int col() {
        return col;
    }

    /**
     * @return An unmodifiable view of item drops. Never null.
     */
    public List<ItemDrop> items() {
        return items;
    }

    /**
     * @return The achievement points granted.
     */
    public int achievementPoints() {
        return achievementPoints;
    }

    /**
     * Combines this reward with another, summing all fields and concatenating items.
     *
     * @param other The other reward; must not be null.
     * @return A new combined reward.
     */
    public QuestReward add(QuestReward other) {
        Objects.requireNonNull(other, "other");
        return new QuestReward(
                this.experience + other.experience,
                this.col + other.col,
                concatItems(this.items, other.items),
                this.achievementPoints + other.achievementPoints
        );
    }

    private static List<ItemDrop> concatItems(List<ItemDrop> first, List<ItemDrop> second) {
        return java.util.stream.Stream.concat(first.stream(), second.stream()).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestReward that = (QuestReward) o;
        return experience == that.experience
                && col == that.col
                && achievementPoints == that.achievementPoints
                && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experience, col, items, achievementPoints);
    }

    @Override
    public String toString() {
        return "QuestReward[experience=" + experience
                + ", col=" + col
                + ", items=" + items
                + ", achievementPoints=" + achievementPoints + "]";
    }
}
