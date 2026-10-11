package com.aincrad.paper.quest;

import java.util.List;
import java.util.Objects;

/**
 * Immutable quest definition.
 * Quests have an ID, name, type, floor, giver NPC, prerequisites,
 * objectives, rewards, and a turn-in method.
 */
public final class Quest {

    public enum TurnInMethod { AUTO, NPC, UI }

    private final String id;
    private final String name;
    private final QuestType type;
    private final int floor;
    private final String giverNpc;
    private final List<String> prerequisites;
    private final List<QuestObjective> objectives;
    private final QuestReward rewards;
    private final TurnInMethod turnInMethod;
    private final boolean repeatable;

    public Quest(String id, String name, QuestType type, int floor, String giverNpc,
                 List<String> prerequisites, List<QuestObjective> objectives,
                 QuestReward rewards, TurnInMethod turnInMethod, boolean repeatable) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rewards, "rewards");
        Objects.requireNonNull(turnInMethod, "turnInMethod");
        if (id.isBlank()) throw new IllegalArgumentException("id must not be blank");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        if (floor < 1 || floor > 100) throw new IllegalArgumentException("floor must be in [1, 100]");
        this.id = id;
        this.name = name;
        this.type = type;
        this.floor = floor;
        this.giverNpc = giverNpc;
        this.prerequisites = prerequisites == null ? List.of() : List.copyOf(prerequisites);
        this.objectives = objectives == null ? List.of() : List.copyOf(objectives);
        this.rewards = rewards;
        this.turnInMethod = turnInMethod;
        this.repeatable = repeatable;
    }

    public String id() { return id; }
    public String name() { return name; }
    public QuestType type() { return type; }
    public int floor() { return floor; }
    public String giverNpc() { return giverNpc; }
    public List<String> prerequisites() { return prerequisites; }
    public List<QuestObjective> objectives() { return objectives; }
    public QuestReward rewards() { return rewards; }
    public TurnInMethod turnInMethod() { return turnInMethod; }
    public boolean isRepeatable() { return repeatable; }

    public boolean isComplete() {
        return objectives.stream().allMatch(QuestObjective::isComplete);
    }

    public double getXpMultiplier(int playerLevel) {
        if (playerLevel > floor + 10) return 0.25;
        if (playerLevel < floor - 5) return 1.5;
        return 1.0;
    }

    public Quest withObjectives(List<QuestObjective> newObjectives) {
        return new Quest(id, name, type, floor, giverNpc, prerequisites, newObjectives, rewards, turnInMethod, repeatable);
    }

    public Quest withRewards(QuestReward newRewards) {
        return new Quest(id, name, type, floor, giverNpc, prerequisites, objectives, newRewards, turnInMethod, repeatable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((Quest) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Quest[" + id + " " + name + " type=" + type + " floor=" + floor + "]";
    }
}
