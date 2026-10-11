package com.aincrad.paper.quest;

import java.util.Objects;

/**
 * Single quest objective with type, target, and progress tracking.
 */
public final class QuestObjective {
    public enum Type { KILL, COLLECT, DEFEAT, TALK }

    private final Type type;
    private final String target;
    private final int required;
    private int current;

    public QuestObjective(Type type, String target, int required) {
        this.type = Objects.requireNonNull(type, "type");
        this.target = Objects.requireNonNull(target, "target");
        if (required < 1) throw new IllegalArgumentException("Required must be >= 1");
        this.required = required;
        this.current = 0;
    }

    public Type getType() { return type; }
    public String getTarget() { return target; }
    public int getRequired() { return required; }
    public int getCurrent() { return current; }

    public boolean isComplete() { return current >= required; }

    public QuestObjective progress(int amount) {
        if (amount <= 0) return this;
        return withProgress(current + amount);
    }

    public QuestObjective withProgress(int amount) {
        QuestObjective copy = new QuestObjective(type, target, required);
        copy.current = Math.min(amount, required);
        return copy;
    }

    @Override
    public String toString() {
        return type + " " + target + " (" + current + "/" + required + ")";
    }
}
