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
        registerDefaultQuests();
    }

    private void registerDefaultQuests() {
        QuestObjective.Type K = QuestObjective.Type.KILL;
        QuestObjective.Type D = QuestObjective.Type.DEFEAT;
        QuestObjective.Type I = QuestObjective.Type.INVESTIGATE;

        registerQuest(new Quest("f1_warm_steel", "Warm Steel, Cold Morning", QuestType.MAIN, 1, "Guide Asuna",
                List.of(),
                List.of(new QuestObjective(K, "Frenzy Cow", 4)),
                new QuestReward(35, 150, List.of(), 1), Quest.TurnInMethod.UI, false));
        registerQuest(new Quest("f1_ranch_trouble", "Ranch Trouble", QuestType.MAIN, 1, "Guild Receptionist",
                List.of("f1_warm_steel"),
                List.of(new QuestObjective(K, "Blood Fang Pig", 4),
                        new QuestObjective(K, "Mad Fleece", 4)),
                new QuestReward(55, 200, List.of(), 1), Quest.TurnInMethod.UI, false));
        registerQuest(new Quest("f1_cull_the_foe", "Cull the Foe", QuestType.SIDE, 1, "Guild Receptionist",
                List.of(),
                List.of(new QuestObjective(K, "any", 6)),
                new QuestReward(40, 160, List.of(), 1), Quest.TurnInMethod.UI, true));
        registerQuest(new Quest("f1_weeping_stone", "The Weeping Stone", QuestType.SIDE, 1, "Herbalist Suna",
                List.of(),
                List.of(new QuestObjective(I, "The Weeping Stone", 1)),
                new QuestReward(50, 220, List.of(), 1), Quest.TurnInMethod.UI, false));
        registerQuest(new Quest("f1_broken_seal", "A Seal Unmade", QuestType.SIDE, 1, "Watchman Corin",
                List.of(),
                List.of(new QuestObjective(I, "The Broken Seal", 1)),
                new QuestReward(55, 240, List.of(), 1), Quest.TurnInMethod.UI, false));
        registerQuest(new Quest("f1_hunters_ledger", "The Hunter's Ledger", QuestType.SIDE, 1, "Guild Receptionist",
                List.of(),
                List.of(new QuestObjective(I, "Hunter's Ledger", 1)),
                new QuestReward(45, 200, List.of(), 1), Quest.TurnInMethod.UI, false));
        registerQuest(new Quest("f1_ilfang_ending", "The End of Ilfang's Road", QuestType.MAIN, 1, "Guide Asuna",
                List.of("f1_ranch_trouble", "f1_cull_the_foe"),
                List.of(new QuestObjective(D, "Ilfang", 1)),
                new QuestReward(180, 800, List.of(), 2), Quest.TurnInMethod.UI, false));
        registerQuest(new Quest("f1_daily_round", "Round the Floor", QuestType.DAILY, 1, "Watchman Corin",
                List.of(),
                List.of(new QuestObjective(K, "any", 12)),
                new QuestReward(30, 120, List.of(), 0), Quest.TurnInMethod.UI, true));
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
