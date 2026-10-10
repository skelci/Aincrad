package com.aincrad.quest;

/**
 * Enum representing the different types of quests available in Aincrad.
 * Each type has a description and a repeatable flag indicating whether
 * the quest can be completed multiple times.
 */
public enum QuestType {

    /** Main storyline quest — advances the plot on each floor. */
    MAIN("Main storyline quest that advances the plot of Aincrad.", false),

    /** Side quest — optional content, often with lore or extra rewards. */
    SIDE("Side quest offering optional content and additional rewards.", false),

    /** Daily quest — resets every 24 hours, repeatable. */
    DAILY("Daily quest that resets every 24 hours. Repeatable.", true),

    /** Weekly quest — resets every 7 days, repeatable. */
    WEEKLY("Weekly quest that resets every 7 days. Repeatable.", true);

    private final String description;
    private final boolean repeatable;

    QuestType(String description, boolean repeatable) {
        this.description = description;
        this.repeatable = repeatable;
    }

    /**
     * @return Human-readable description of this quest type.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return {@code true} if quests of this type can be repeated after completion.
     */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     * Checks whether this quest type requires a cooldown timer.
     * Repeatable quest types always have a cooldown.
     *
     * @return {@code true} if a cooldown applies to this type.
     */
    public boolean hasCooldown() {
        return this == DAILY || this == WEEKLY;
    }

    /**
     * Checks whether this quest type has a fixed limit on completions per cycle.
     *
     * @return {@code true} if the type has a per-cycle completion cap.
     */
    public boolean hasCompletionCap() {
        return this == DAILY || this == WEEKLY;
    }
}
