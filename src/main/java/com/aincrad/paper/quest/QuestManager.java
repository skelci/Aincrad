package com.aincrad.paper.quest;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton managing all quest definitions and player progress.
 * Supports hot-reloading quest definitions without restarting the server.
 */
public final class QuestManager {

    private static volatile QuestManager instance;

    private volatile Map<String, Quest> questDefinitions = new ConcurrentHashMap<>();
    private final Map<UUID, QuestTracker> playerTrackers = new ConcurrentHashMap<>();
    private final Map<QuestType, Duration> cooldownConfig = new EnumMap<>(QuestType.class);
    private volatile int maxActiveQuests = 20;
    private volatile int dailyCap = 3;
    private volatile int weeklyCap = 1;

    private QuestManager() {
        cooldownConfig.put(QuestType.DAILY, Duration.ofHours(24));
        cooldownConfig.put(QuestType.WEEKLY, Duration.ofDays(7));
    }

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

    public Quest registerQuest(Quest quest) {
        Objects.requireNonNull(quest, "quest");
        return questDefinitions.put(quest.id(), quest);
    }

    public void registerQuests(Collection<Quest> quests) {
        Objects.requireNonNull(quests, "quests");
        Map<String, Quest> newDefs = new ConcurrentHashMap<>();
        for (Quest q : quests) {
            newDefs.put(q.id(), q);
        }
        this.questDefinitions = newDefs;
    }

    public Quest unregisterQuest(String questId) {
        Objects.requireNonNull(questId, "questId");
        return questDefinitions.remove(questId);
    }

    public Quest getQuest(String questId) {
        return questDefinitions.get(questId);
    }

    public Collection<Quest> getAllQuests() {
        return Collections.unmodifiableCollection(questDefinitions.values());
    }

    public List<Quest> getQuestsByType(QuestType type) {
        return questDefinitions.values().stream()
                .filter(q -> q.type() == type)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Quest> getQuestsByFloor(int floor) {
        return questDefinitions.values().stream()
                .filter(q -> q.floor() == floor)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Quest> getMainQuests(int floor) {
        return questDefinitions.values().stream()
                .filter(q -> q.floor() == floor && q.type() == QuestType.MAIN)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Quest> getSideQuests(int floor) {
        return questDefinitions.values().stream()
                .filter(q -> q.floor() == floor && q.type() == QuestType.SIDE)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Quest> getDailyQuests() {
        return getQuestsByType(QuestType.DAILY);
    }

    public List<Quest> getWeeklyQuests() {
        return getQuestsByType(QuestType.WEEKLY);
    }

    // ── Player Progress Management ──────────────────────────────────

    public QuestTracker getTracker(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerTrackers.computeIfAbsent(playerId, QuestTracker::new);
    }

    public boolean hasTracker(UUID playerId) {
        return playerTrackers.containsKey(playerId);
    }

    public QuestTracker removeTracker(UUID playerId) {
        return playerTrackers.remove(playerId);
    }

    public boolean acceptQuest(UUID playerId, String questId) throws QuestException {
        Quest quest = questDefinitions.get(questId);
        if (quest == null) throw new QuestException("Quest not found: " + questId);

        QuestTracker tracker = getTracker(playerId);
        if (tracker.isActive(questId)) throw new QuestException("Quest already active: " + questId);
        if (tracker.isOnCooldown(questId)) throw new QuestException("Quest is on cooldown: " + questId);
        if (tracker.isCompleted(questId) && !quest.isRepeatable())
            throw new QuestException("Quest already completed and not repeatable: " + questId);
        if (tracker.activeQuests().size() >= maxActiveQuests)
            throw new QuestException("Active quest limit reached (" + maxActiveQuests + ")");
        if (quest.type() == QuestType.DAILY && tracker.getCompletionCount(questId) >= dailyCap)
            throw new QuestException("Daily quest cap reached for: " + questId);
        if (quest.type() == QuestType.WEEKLY && tracker.getCompletionCount(questId) >= weeklyCap)
            throw new QuestException("Weekly quest cap reached for: " + questId);
        for (String prereq : quest.prerequisites()) {
            if (!tracker.isCompleted(prereq)) throw new QuestException("Prerequisite not met: " + prereq);
        }
        tracker.addQuest(quest);
        return true;
    }

    public QuestReward turnInQuest(UUID playerId, String questId) throws QuestException {
        QuestTracker tracker = getTracker(playerId);
        Quest quest = tracker.activeQuests().stream()
                .filter(q -> q.id().equals(questId))
                .findFirst()
                .orElseThrow(() -> new QuestException("Quest not active: " + questId));
        if (!quest.isComplete()) throw new QuestException("Quest objectives not complete: " + questId);
        tracker.markCompleted(questId);
        tracker.incrementCompletionCount(questId);
        if (quest.type().isRepeatable()) {
            Duration cooldown = cooldownConfig.get(quest.type());
            if (cooldown != null) tracker.setCooldown(questId, cooldown);
        }
        return quest.rewards();
    }

    public boolean abandonQuest(UUID playerId, String questId) {
        QuestTracker tracker = getTracker(playerId);
        return tracker.removeQuest(questId) != null;
    }

    public boolean updateProgress(UUID playerId, String questId, int objectiveIndex, int amount) {
        QuestTracker tracker = getTracker(playerId);
        return tracker.updateProgress(questId, objectiveIndex, amount);
    }

    // ── Config ───────────────────────────────────────────────────────

    public void setMaxActiveQuests(int max) { this.maxActiveQuests = max; }
    public void setDailyCap(int cap) { this.dailyCap = cap; }
    public void setWeeklyCap(int cap) { this.weeklyCap = cap; }
    public int getMaxActiveQuests() { return maxActiveQuests; }
    public int getDailyCap() { return dailyCap; }
    public int getWeeklyCap() { return weeklyCap; }

    public void reset() {
        questDefinitions.clear();
        playerTrackers.clear();
    }

    /**
     * Exception for quest-related errors.
     */
    public static class QuestException extends Exception {
        public QuestException(String message) { super(message); }
    }
}
