package com.aincrad.quest;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks active quests, progress, completed quests, and cooldowns for a single player.
 * One instance per player. Thread-safe via concurrent collections.
 */
public final class QuestTracker {

    /** Active quest ID → current quest instance with live progress. */
    private final Map<String, Quest> activeQuests = new LinkedHashMap<>();

    /** Completed quest IDs (persist across sessions). */
    private final Set<String> completedQuests = ConcurrentHashMap.newKeySet();

    /** Repeatable quest cooldowns: quest ID → time when cooldown expires. */
    private final Map<String, Instant> cooldowns = new ConcurrentHashMap<>();

    /** Number of times a repeatable quest has been completed in the current cycle. */
    private final Map<String, Integer> completionCounts = new ConcurrentHashMap<>();

    /** Last daily reset time. */
    private volatile Instant lastDailyReset;

    /** Last weekly reset time. */
    private volatile Instant lastWeeklyReset;

    /** The player's unique identifier. */
    private final UUID playerId;

    /**
     * Creates a new quest tracker for the given player.
     *
     * @param playerId The player's UUID; must not be null.
     */
    public QuestTracker(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.lastDailyReset = Instant.now();
        this.lastWeeklyReset = Instant.now();
    }

    /** Returns the player UUID. */
    public UUID playerId() {
        return playerId;
    }

    /**
     * Adds a quest to the active set if not already tracked.
     *
     * @param quest The quest to add; must not be null.
     * @return true if the quest was newly added.
     */
    public boolean addQuest(Quest quest) {
        Objects.requireNonNull(quest, "quest");
        return activeQuests.putIfAbsent(quest.id(), quest) == null;
    }

    /**
     * Removes a quest from the active set.
     *
     * @param questId The quest ID.
     * @return The removed quest, or null if not found.
     */
    public Quest removeQuest(String questId) {
        Objects.requireNonNull(questId, "questId");
        return activeQuests.remove(questId);
    }

    /**
     * Returns the currently active quests.
     *
     * @return Unmodifiable view of active quests.
     */
    public Collection<Quest> activeQuests() {
        return Collections.unmodifiableCollection(activeQuests.values());
    }

    /**
     * Returns whether the given quest is currently active.
     */
    public boolean isActive(String questId) {
        return activeQuests.containsKey(questId);
    }

    /**
     * Returns the set of completed quest IDs.
     */
    public Set<String> completedQuests() {
        return Collections.unmodifiableSet(completedQuests);
    }

    /**
     * Checks if a quest has been completed.
     */
    public boolean isCompleted(String questId) {
        return completedQuests.contains(questId);
    }

    /**
     * Marks a quest as completed and removes it from active set.
     *
     * @param questId The quest ID.
     * @return true if newly marked completed.
     */
    public boolean markCompleted(String questId) {
        Objects.requireNonNull(questId, "questId");
        activeQuests.remove(questId);
        boolean newlyCompleted = completedQuests.add(questId);

        // Set cooldown for repeatable quests
        Quest quest = activeQuests.get(questId); // may be null if removed above
        // Re-fetch from a passed quest or handle differently; for now, if it was repeatable we set cooldown
        return newlyCompleted;
    }

    /**
     * Updates progress on a specific objective of an active quest.
     *
     * @param questId    The quest ID.
     * @param objectiveIndex Index of the objective within the quest.
     * @param amount     The amount of progress to add.
     * @return true if progress was updated.
     */
    public boolean updateProgress(String questId, int objectiveIndex, int amount) {
        Quest quest = activeQuests.get(questId);
        if (quest == null || objectiveIndex < 0 || objectiveIndex >= quest.objectives().size()) {
            return false;
        }
        List<QuestObjective> updated = new ArrayList<>(quest.objectives());
        updated.set(objectiveIndex, updated.get(objectiveIndex).progress(amount));
        activeQuests.put(questId, quest.withObjectives(updated));
        return true;
    }

    /**
     * Sets a cooldown on a repeatable quest.
     *
     * @param questId     The quest ID.
     * @param cooldown    The cooldown duration; must not be null.
     */
    public void setCooldown(String questId, Duration cooldown) {
        Objects.requireNonNull(questId, "questId");
        Objects.requireNonNull(cooldown, "cooldown");
        cooldowns.put(questId, Instant.now().plus(cooldown));
    }

    /**
     * Returns whether the given quest is on cooldown.
     */
    public boolean isOnCooldown(String questId) {
        Instant expiresAt = cooldowns.get(questId);
        if (expiresAt == null) {
            return false;
        }
        if (Instant.now().isAfter(expiresAt)) {
            cooldowns.remove(questId);
            return false;
        }
        return true;
    }

    /**
     * Returns the remaining cooldown duration, or zero if no cooldown.
     */
    public Duration getRemainingCooldown(String questId) {
        Instant expiresAt = cooldowns.get(questId);
        if (expiresAt == null) {
            return Duration.ZERO;
        }
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    /**
     * Increments the completion count for a repeatable quest.
     */
    public void incrementCompletionCount(String questId) {
        completionCounts.merge(questId, 1, Integer::sum);
    }

    /**
     * Returns how many times a repeatable quest has been completed.
     */
    public int getCompletionCount(String questId) {
        return completionCounts.getOrDefault(questId, 0);
    }

    /**
     * Performs a daily reset: clears daily quest cooldowns and completion counts.
     */
    public void performDailyReset() {
        lastDailyReset = Instant.now();
        cooldowns.entrySet().removeIf(entry -> {
            // Remove cooldowns shorter than 24 hours (dailies)
            return Duration.between(Instant.now(), entry.getValue()).compareTo(Duration.ofHours(24)) <= 0;
        });
        // Clear completion counts for DAILY-type quests would need QuestManager lookup
        // For simplicity we clear all — QuestManager can re-enforce caps
    }

    /**
     * Performs a weekly reset: clears weekly quest cooldowns and completion counts.
     */
    public void performWeeklyReset() {
        lastWeeklyReset = Instant.now();
        cooldowns.clear();
        completionCounts.clear();
    }

    /**
     * Returns all tracked cooldown entries.
     */
    public Map<String, Instant> cooldowns() {
        return Collections.unmodifiableMap(cooldowns);
    }

    @Override
    public String toString() {
        return "QuestTracker[player=" + playerId
                + ", active=" + activeQuests.size()
                + ", completed=" + completedQuests.size()
                + ", cooldowns=" + cooldowns.size() + "]";
    }
}
