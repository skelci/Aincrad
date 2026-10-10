package com.aincrad.quest;

import java.util.Objects;

/**
 * Represents a single objective within a quest.
 * Objective types are KILL, COLLECT, DEFEAT, and TALK.
 * Each objective tracks its target, current progress, and required amount.
 */
public final class QuestObjective {

    /** Enum of objective types. */
    public enum ObjectiveType {
        KILL,
        COLLECT,
        DEFEAT,
        TALK
    }

    private final ObjectiveType type;
    private final String target;
    private int current;
    private final int required;

    /**
     * Creates a quest objective.
     *
     * @param type     The objective type; must not be null.
     * @param target   The target identifier (entity ID, item ID, NPC ID); must not be null or blank.
     * @param required The number of completions needed; must be positive.
     * @throws NullPointerException     if {@code type} or {@code target} is null.
     * @throws IllegalArgumentException if {@code target} is blank or {@code required} is non-positive.
     */
    public QuestObjective(ObjectiveType type, String target, int required) {
        this(type, target, 0, required);
    }

    /**
     * Creates a quest objective with an initial progress value.
     *
     * @param type     The objective type; must not be null.
     * @param target   The target identifier; must not be null or blank.
     * @param current  The initial progress; must be {@code >= 0} and {@code <= required}.
     * @param required The number of completions needed; must be positive.
     * @throws NullPointerException     if {@code type} or {@code target} is null.
     * @throws IllegalArgumentException if parameters are invalid.
     */
    public QuestObjective(ObjectiveType type, String target, int current, int required) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(target, "target");
        if (target.isBlank()) {
            throw new IllegalArgumentException("target must not be blank.");
        }
        if (required <= 0) {
            throw new IllegalArgumentException("required must be positive: " + required);
        }
        if (current < 0 || current > required) {
            throw new IllegalArgumentException(
                    "current must be in [0, required]: current=" + current + ", required=" + required);
        }
        this.type = type;
        this.target = target;
        this.current = current;
        this.required = required;
    }

    /**
     * Returns the objective type.
     *
     * @return the type
     */
    public ObjectiveType type() {
        return type;
    }

    /**
     * Returns the target identifier.
     *
     * @return the target
     */
    public String target() {
        return target;
    }

    /**
     * Returns the current progress.
     *
     * @return the current progress
     */
    public int current() {
        return current;
    }

    /**
     * Returns the required completion amount.
     *
     * @return the required amount
     */
    public int required() {
        return required;
    }

    /**
     * Returns remaining completions needed.
     *
     * @return remaining count (never negative)
     */
    public int remaining() {
        return Math.max(0, required - current);
    }

    /**
     * Checks if this objective is complete.
     *
     * @return {@code true} if current >= required
     */
    public boolean isComplete() {
        return current >= required;
    }

    /**
     * Increments progress by the specified amount.
     * Progress is capped at the required amount.
     *
     * @param amount The amount to add; must be {@code >= 0}.
     * @return A new {@code QuestObjective} with updated progress.
     * @throws IllegalArgumentException if amount is negative.
     */
    public QuestObjective progress(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0: " + amount);
        }
        int newCurrent = Math.min(current + amount, required);
        return new QuestObjective(type, target, newCurrent, required);
    }

    /**
     * Creates a fresh copy of this objective with zero progress.
     *
     * @return A new objective at progress 0.
     */
    public QuestObjective reset() {
        return new QuestObjective(type, target, 0, required);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestObjective that = (QuestObjective) o;
        return current == that.current
                && required == that.required
                && type == that.type
                && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, current, required);
    }

    @Override
    public String toString() {
        return "QuestObjective[type=" + type
                + ", target=" + target
                + ", current=" + current
                + "/" + required + "]";
    }
}
