package com.aincrad.quest;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton managing all quest definitions and player progress.
 * Supports hot-reloading quest definitions without restarting the server.
 * Thread-safe.
 */
public final class QuestManager {

    private static volatile QuestManager instance;

    /** Quest definitions by ID. Hot-reloadable. */
    private volatile Map<String, Quest> questDefinitions = new ConcurrentHashMap<>();

    /** Player UUID → their quest tracker. */
    private final Map<UUID, QuestTracker> playerTrackers = new ConcurrentHashMap<>();

    /** Cooldown durations per quest type. */
    private final Map<QuestType, Duration> cooldownConfig = new EnumMap<>(QuestType.class);

    /** Maximum active quests per player. */
    private volatile int maxActiveQuests = 20;

    /** Daily quest completions per day cap. */
    private volatile int dailyCap = 3;

    /** Weekly quest completions per week cap. */
    private volatile int weeklyCap = 1;

    private QuestManager() {
        // Default cooldowns
        cooldownConfig.put(QuestType.DAILY, Duration.ofHours(24));
        cooldownConfig.put(QuestType.WEEKLY, Duration.ofDays(7));
    }

    /**
     * Returns the singleton instance, creating it if needed.
     *
     * @return the QuestManager singleton
     */
    public static QuestManager getInstance() {
        if (instance == null) {
            synchronized (QuestManager.class) {
                if (instance == null) {
                    instance = new QuestManager();
                }
            }
        }
        return instance;
    }

    // ── Quest Definition Management ─────────────────────────────────

    /**
     * Registers a quest definition. If a quest with the same ID already exists,
     * it is replaced (hot-reload).
     *
     * @param quest The quest definition; must not be null.
     * @return The previous quest with the same ID, or null.
     */
    public Quest registerQuest(Quest quest) {
        Objects.requireNonNull(quest, "quest");
        return questDefinitions.put(quest.id(), quest);
    }

    /**
     * Registers multiple quest definitions at once (hot-reload batch).
     *
     * @param quests Collection of quest definitions.
     */
    public void registerQuests(Collection<Quest> quests) {
        Objects.requireNonNull(quests, "quests");
        Map<String, Quest> newDefs = new ConcurrentHashMap<>();
        for (Quest q : quests) {
            newDefs.put(q.id(), q);
        }
        this.questDefinitions = newDefs;
    }

    /**
     * Removes a quest definition.
     *
     * @param questId The quest ID to remove.
     * @return The removed quest, or null if not found.
     */
    public Quest unregisterQuest(String questId) {
        Objects.requireNonNull(questId, "questId");
        return questDefinitions.remove(questId);
    }

    /**
     * Returns a quest definition by ID.
     *
     * @param questId The quest ID.
     * @return The quest definition, or null if not found.
     */
    public Quest getQuest(String questId) {
        return questDefinitions.get(questId);
    }

    /**
     * Returns all registered quest definitions.
     *
     * @return Unmodifiable view of all quest definitions.
     */
    public Collection<Quest> getAllQuests() {
        return Collections.unmodifiableCollection(questDefinitions.values());
    }

    /**
     * Returns quests filtered by type.
     */
    public List<Quest> getQuestsByType(QuestType type) {
        return questDefinitions.values().stream()
                .filter(q -> q.type() == type)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns quests for a specific floor.
     */
    public List<Quest> getQuestsByFloor(int floor) {
        return questDefinitions.values().stream()
                .filter(q -> q.floor() == floor)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns main quests for a floor.
     */
    public List<Quest> getMainQuests(int floor) {
        return questDefinitions.values().stream()
                .filter(q -> q.floor() == floor && q.type() == QuestType.MAIN)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns side quests for a floor.
     */
    public List<Quest> getSideQuests(int floor) {
        return questDefinitions.values().stream()
                .filter(q -> q.floor() == floor && q.type() == QuestType.SIDE)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all daily quests.
     */
    public List<Quest> getDailyQuests() {
        return getQuestsByType(QuestType.DAILY);
    }

    /**
     * Returns all weekly quests.
     */
    public List<Quest> getWeeklyQuests() {
        return getQuestsByType(QuestType.WEEKLY);
    }

    // ── Player Progress Management ──────────────────────────────────

    /**
     * Returns (or creates) the quest tracker for a player.
     *
     * @param playerId The player UUID.
     * @return The player's quest tracker.
     */
    public QuestTracker getTracker(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerTrackers.computeIfAbsent(playerId, QuestTracker::new);
    }

    /**
     * Checks if a tracker exists for a player.
     */
    public boolean hasTracker(UUID playerId) {
        return playerTrackers.containsKey(playerId);
    }

    /**
     * Removes a player's tracker (e.g., on logout if persistence is elsewhere).
     *
     * @param playerId The player UUID.
     * @return The removed tracker, or null.
     */
    public QuestTracker removeTracker(UUID playerId) {
        return playerTrackers.remove(playerId);
    }

    /**
     * Attempts to accept a quest for a player.
     * Validates: quest exists, not already active, prerequisites met,
     * not on cooldown, active quest limit not exceeded, daily/weekly caps.
     *
     * @param playerId The player UUID.
     * @param questId  The quest ID to accept.
     * @return {@code true} if the quest was accepted.
     * @throws QuestException if the quest cannot be accepted.
     */
    public boolean acceptQuest(UUID playerId, String questId) throws QuestException {
        Quest quest = questDefinitions.get(questId);
        if (quest == null) {
            throw new QuestException("Quest not found: " + questId);
        }

        QuestTracker tracker = getTracker(playerId);

        // Already active
        if (tracker.isActive(questId)) {
            throw new QuestException("Quest already active: " + questId);
        }

        // Cooldown check
        if (tracker.isOnCooldown(questId)) {
            throw new QuestException("Quest is on cooldown: " + questId
                    + " (remaining: " + tracker.getRemainingCooldown(questId) + ")");
        }

        // Completed non-repeatable quest
        if (tracker.isCompleted(questId) && !quest.isRepeatable()) {
            throw new QuestException("Quest already completed and not repeatable: " + questId);
        }

        // Active quest limit
        if (tracker.activeQuests().size() >= maxActiveQuests) {
            throw new QuestException("Active quest limit reached (" + maxActiveQuests + ")");
        }

        // Daily/weekly caps
        if (quest.type() == QuestType.DAILY && tracker.getCompletionCount(questId) >= dailyCap) {
            throw new QuestException("Daily quest cap reached for: " + questId);
        }
        if (quest.type() == QuestType.WEEKLY && tracker.getCompletionCount(questId) >= weeklyCap) {
            throw new QuestException("Weekly quest cap reached for: " + questId);
        }

        // Prerequisites
        for (String prereq : quest.prerequisites()) {
            if (!tracker.isCompleted(prereq)) {
                throw new QuestException("Prerequisite not met: " + prereq);
            }
        }

        tracker.addQuest(quest);
        return true;
    }

    /**
     * Attempts to turn in (complete) a quest.
     * Validates: quest is active, all objectives complete.
     *
     * @param playerId The player UUID.
     * @param questId  The quest ID to turn in.
     * @return The rewards granted.
     * @throws QuestException if the quest cannot be turned in.
     */
    public QuestReward turnInQuest(UUID playerId, String questId) throws QuestException {
        QuestTracker tracker = getTracker(playerId);
        Quest quest = tracker.activeQuests().stream()
                .filter(q -> q.id().equals(questId))
                .findFirst()
                .orElseThrow(() -> new QuestException("Quest not active: " + questId));

        if (!quest.isComplete()) {
            throw new QuestException("Quest objectives not complete: " + questId);
        }

        tracker.markCompleted(questId);
        tracker.incrementCompletionCount(questId);

        // Set cooldown for repeatable quests
        if (quest.type().isRepeatable()) {
            Duration cooldown = cooldownConfig.get(quest.type());
            if (cooldown != null) {
                tracker.setCooldown(questId, cooldown);
            }
        }

        return quest.rewards();
    }

    /**
     * Abandons a quest for a player.
     *
     * @param playerId The player UUID.
     * @param questId  The quest ID to abandon.
     * @return true if the quest was abandoned.
     */
    public boolean abandonQuest(UUID playerId, String questId) {
        QuestTracker tracker = getTracker(playerId);
        return tracker.removeQuest(questId) != null;
    }

    /**
     * Updates progress on a quest objective.
     *
     * @param playerId       The player UUID.
     * @param questId        The quest ID.
     * @param objectiveIndex The objective index.
     * @param amount         The progress amount.
     * @return true if progress was updated.
     */
    public boolean updateProgress(UUID playerId, String questId, int objectiveIndex, int amount) {
        QuestTracker tracker = getTracker(playerId);
        return tracker.updateProgress(questId, objectiveIndex, amount);
    }

    // ── Hot-Reload Support ──────────────────────────────────────────

    /**
     * Hot-reloads quest definitions from a new collection.
     * Player progress on existing quests is preserved; their quest
     * definitions are updated to the new version.
     *
     * @param newQuests The new quest definitions.
     */
    public void hotReload(Collection<Quest> newQuests) {
        Objects.requireNonNull(newQuests, "newQuests");

        // Build new definition map
        Map<String, Quest> newDefs = new ConcurrentHashMap<>();
        for (Quest q : newQuests) {
            newDefs.put(q.id(), q);
        }

        // Update active quests in trackers to use new definitions
        for (QuestTracker tracker : playerTrackers.values()) {
            for (Quest activeQuest : tracker.activeQuests()) {
                Quest updatedDef = newDefs.get(activeQuest.id());
                if (updatedDef != null) {
                    // Preserve progress by merging objectives
                    Quest merged = preserveProgress(activeQuest, updatedDef);
                    tracker.removeQuest(activeQuest.id());
                    tracker.addQuest(merged);
                }
            }
        }

        this.questDefinitions = newDefs;
    }

    /**
     * Attempts to preserve player progress when merging old quest state
     * with new quest definition. Matches objectives by index.
     */
    private Quest preserveProgress(Quest oldQuest, Quest newQuest) {
        List<QuestObjective> mergedObjectives = new ArrayList<>();
        List<QuestObjective> oldObjectives = oldQuest.objectives();
        List<QuestObjective> newObjectives = newQuest.objectives();

        for (int i = 0; i < newObjectives.size(); i++) {
            if (i < oldObjectives.size()) {
                QuestObjective oldObj = oldObjectives.get(i);
                QuestObjective newObj = newObjectives.get(i);
                // Carry over progress if same type and target
                if (oldObj.type() == newObj.type() && oldObj.target().equals(newObj.target())) {
                    mergedObjectives.add(new QuestObjective(
                            newObj.type(), newObj.target(), oldObj.current(), newObj.required()));
                } else {
                    mergedObjectives.add(newObj);
                }
            } else {
                mergedObjectives.add(newObjectives.get(i));
            }
        }
        return newQuest.withObjectives(mergedObjectives);
    }

    // ── Configuration ───────────────────────────────────────────────

    /**
     * Sets the maximum number of active quests per player.
     */
    public void setMaxActiveQuests(int max) {
        if (max < 1) throw new IllegalArgumentException("maxActiveQuests must be >= 1");
        this.maxActiveQuests = max;
    }

    /**
     * Returns the maximum active quests per player.
     */
    public int maxActiveQuests() {
        return maxActiveQuests;
    }

    /**
     * Sets the daily completion cap.
     */
    public void setDailyCap(int cap) {
        if (cap < 0) throw new IllegalArgumentException("dailyCap must be >= 0");
        this.dailyCap = cap;
    }

    /**
     * Returns the daily completion cap.
     */
    public int dailyCap() {
        return dailyCap;
    }

    /**
     * Sets the weekly completion cap.
     */
    public void setWeeklyCap(int cap) {
        if (cap < 0) throw new IllegalArgumentException("weeklyCap must be >= 0");
        this.weeklyCap = cap;
    }

    /**
     * Returns the weekly completion cap.
     */
    public int weeklyCap() {
        return weeklyCap;
    }

    /**
     * Sets the cooldown duration for a quest type.
     */
    public void setCooldown(QuestType type, Duration cooldown) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(cooldown, "cooldown");
        cooldownConfig.put(type, cooldown);
    }

    // ── Reset / Lifecycle ───────────────────────────────────────────

    /**
     * Resets all data. Used on server shutdown or full wipe.
     */
    public void reset() {
        questDefinitions = new ConcurrentHashMap<>();
        playerTrackers.clear();
    }

    /**
     * Returns the total number of registered quest definitions.
     */
    public int questCount() {
        return questDefinitions.size();
    }

    /**
     * Returns the number of tracked players.
     */
    public int trackedPlayerCount() {
        return playerTrackers.size();
    }

    // ── QuestException ──────────────────────────────────────────────

    /**
     * Exception thrown when a quest operation fails.
     */
    public static class QuestException extends Exception {
        public QuestException(String message) {
            super(message);
        }
    }
}
