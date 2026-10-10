package com.aincrad.quest;

import java.util.List;
import java.util.Objects;

/**
 * Represents a quest in the Aincrad quest system.
 * Quests have an ID, name, type, floor, giver NPC, prerequisites,
 * objectives, rewards, and a turn-in method.
 */
public final class Quest {

    /** Enum for how the quest is turned in. */
    public enum TurnInMethod {
        /** Automatically turned in upon completion. */
        AUTO,
        /** Must be turned in to the giver NPC. */
        NPC,
        /** Turned in via a quest UI panel. */
        UI
    }

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

    /**
     * Creates a quest definition.
     *
     * @param id             Unique quest identifier; must not be null or blank.
     * @param name           Human-readable quest name; must not be null or blank.
     * @param type           Quest type; must not be null.
     * @param floor          The floor this quest belongs to (1-100).
     * @param giverNpc       The NPC who gives this quest; may be null for system quests.
     * @param prerequisites List of prerequisite quest IDs that must be completed first.
     * @param objectives     List of objectives that must be completed.
     * @param rewards        Quest rewards; must not be null.
     * @param turnInMethod   How the quest is turned in; must not be null.
     * @param repeatable     Whether this quest can be repeated.
     */
    public Quest(String id, String name, QuestType type, int floor, String giverNpc,
                 List<String> prerequisites, List<QuestObjective> objectives,
                 QuestReward rewards, TurnInMethod turnInMethod, boolean repeatable) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rewards, "rewards");
        Objects.requireNonNull(turnInMethod, "turnInMethod");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank.");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank.");
        }
        if (floor < 1 || floor > 100) {
            throw new IllegalArgumentException("floor must be in [1, 100]: " + floor);
        }
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

    /** Returns the unique quest identifier. */
    public String id() {
        return id;
    }

    /** Returns the human-readable quest name. */
    public String name() {
        return name;
    }

    /** Returns the quest type. */
    public QuestType type() {
        return type;
    }

    /** Returns the floor this quest belongs to. */
    public int floor() {
        return floor;
    }

    /** Returns the giver NPC identifier, or null for system quests. */
    public String giverNpc() {
        return giverNpc;
    }

    /** Returns an unmodifiable list of prerequisite quest IDs. */
    public List<String> prerequisites() {
        return prerequisites;
    }

    /** Returns an unmodifiable list of objectives. */
    public List<QuestObjective> objectives() {
        return objectives;
    }

    /** Returns the quest rewards. */
    public QuestReward rewards() {
        return rewards;
    }

    /** Returns the turn-in method. */
    public TurnInMethod turnInMethod() {
        return turnInMethod;
    }

    /** Returns whether this quest is repeatable. */
    public boolean isRepeatable() {
        return repeatable;
    }

    /**
     * Checks if all objectives are complete.
     *
     * @return true if every objective is complete
     */
    public boolean isComplete() {
        return objectives.stream().allMatch(QuestObjective::isComplete);
    }

    /**
     * Calculates the XP multiplier based on player level relative to floor.
     * Scaling rules:
     * <ul>
     *   <li>player level &gt; floor+10 → x0.25</li>
     *   <li>player level &lt; floor-5 → x1.5</li>
     *   <li>otherwise → x1.0</li>
     * </ul>
     *
     * @param playerLevel The player's current level.
     * @return The XP multiplier.
     */
    public double getXpMultiplier(int playerLevel) {
        if (playerLevel > floor + 10) {
            return 0.25;
        }
        if (playerLevel < floor - 5) {
            return 1.5;
        }
        return 1.0;
    }

    /**
     * Returns a quest with the given objectives (used during hot-reload).
     *
     * @param newObjectives Updated objectives list.
     * @return A new Quest with substituted objectives.
     */
    public Quest withObjectives(List<QuestObjective> newObjectives) {
        return new Quest(id, name, type, floor, giverNpc, prerequisites, newObjectives, rewards, turnInMethod, repeatable);
    }

    /**
     * Returns a quest with the given rewards (used during hot-reload).
     *
     * @param newRewards Updated rewards.
     * @return A new Quest with substituted rewards.
     */
    public Quest withRewards(QuestReward newRewards) {
        return new Quest(id, name, type, floor, giverNpc, prerequisites, objectives, newRewards, turnInMethod, repeatable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quest quest = (Quest) o;
        return id.equals(quest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Quest[id=" + id
                + ", name=" + name
                + ", type=" + type
                + ", floor=" + floor
                + ", giverNpc=" + giverNpc
                + ", prerequisites=" + prerequisites
                + ", objectives=" + objectives
                + ", rewards=" + rewards
                + ", turnInMethod=" + turnInMethod
                + ", repeatable=" + repeatable + "]";
    }
}
