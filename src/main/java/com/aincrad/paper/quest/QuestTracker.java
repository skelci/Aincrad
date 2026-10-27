package com.aincrad.paper.quest;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-player quest state: active quests, completed set, cooldowns, completion counts.
 */
public final class QuestTracker {
    private final UUID playerId;
    private final Map<String, Quest> activeQuests = new LinkedHashMap<>();
    private final Set<String> completedQuests = ConcurrentHashMap.newKeySet();
    private final Map<String, Instant> cooldowns = new ConcurrentHashMap<>();
    private final Map<String, Integer> completionCounts = new ConcurrentHashMap<>();
    private Instant lastDailyReset;
    private Instant lastWeeklyReset;

    public QuestTracker(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.lastDailyReset = Instant.now();
        this.lastWeeklyReset = Instant.now();
    }

    public UUID getPlayerId() { return playerId; }

    public boolean addQuest(Quest quest) {
        Objects.requireNonNull(quest, "quest");
        return activeQuests.putIfAbsent(quest.id(), quest) == null;
    }

    public Quest removeQuest(String questId) {
        Objects.requireNonNull(questId, "questId");
        return activeQuests.remove(questId);
    }

    public Collection<Quest> activeQuests() {
        return Collections.unmodifiableCollection(activeQuests.values());
    }

    public boolean isActive(String questId) {
        return activeQuests.containsKey(questId);
    }

    public Set<String> completedQuests() {
        return Collections.unmodifiableSet(completedQuests);
    }

    public boolean isCompleted(String questId) {
        return completedQuests.contains(questId);
    }

    public boolean markCompleted(String questId) {
        Objects.requireNonNull(questId, "questId");
        activeQuests.remove(questId);
        return completedQuests.add(questId);
    }

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

    public void setCooldown(String questId, Duration cooldown) {
        Objects.requireNonNull(questId, "questId");
        Objects.requireNonNull(cooldown, "cooldown");
        cooldowns.put(questId, Instant.now().plus(cooldown));
    }

    public boolean isOnCooldown(String questId) {
        Instant expiresAt = cooldowns.get(questId);
        if (expiresAt == null) return false;
        if (Instant.now().isAfter(expiresAt)) {
            cooldowns.remove(questId);
            return false;
        }
        return true;
    }

    public Duration getRemainingCooldown(String questId) {
        Instant expiresAt = cooldowns.get(questId);
        if (expiresAt == null) return Duration.ZERO;
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    public void incrementCompletionCount(String questId) {
        completionCounts.merge(questId, 1, Integer::sum);
    }

    public int getCompletionCount(String questId) {
        return completionCounts.getOrDefault(questId, 0);
    }

    public void performDailyReset() {
        lastDailyReset = Instant.now();
        cooldowns.entrySet().removeIf(entry ->
                Duration.between(Instant.now(), entry.getValue()).compareTo(Duration.ofHours(24)) <= 0);
    }

    public void performWeeklyReset() {
        lastWeeklyReset = Instant.now();
        cooldowns.clear();
        completionCounts.clear();
    }

    public Map<String, Instant> cooldowns() {
        return Collections.unmodifiableMap(cooldowns);
    }

    public Map<String, Integer> completionCounts() {
        return Collections.unmodifiableMap(completionCounts);
    }

    public Instant getLastDailyReset() { return lastDailyReset; }
    public Instant getLastWeeklyReset() { return lastWeeklyReset; }
    public void setLastDailyReset(Instant t) { lastDailyReset = t; }
    public void setLastWeeklyReset(Instant t) { lastWeeklyReset = t; }

    public void restoreQuest(Quest quest, List<Integer> progress) {
        List<QuestObjective> objectives = new ArrayList<>();
        for (int i = 0; i < quest.objectives().size(); i++) {
            int p = i < progress.size() ? progress.get(i) : 0;
            objectives.add(quest.objectives().get(i).withProgress(p));
        }
        activeQuests.put(quest.id(), quest.withObjectives(objectives));
    }

    public void addCompleted(String questId) {
        completedQuests.add(questId);
    }

    public void setCooldownInstant(String questId, Instant instant) {
        cooldowns.put(questId, instant);
    }

    public void setCompletionCount(String questId, int count) {
        completionCounts.put(questId, count);
    }

    @Override
    public String toString() {
        return "QuestTracker[player=" + playerId + " active=" + activeQuests.size()
                + " completed=" + completedQuests.size() + "]";
    }
}
