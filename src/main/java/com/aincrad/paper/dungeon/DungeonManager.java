package com.aincrad.paper.dungeon;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for all dungeon instances and player lockouts.
 */
public final class DungeonManager {
    private static final DungeonManager INSTANCE = new DungeonManager();

    private final Map<String, DungeonInstance> instances = new ConcurrentHashMap<>();
    private final Map<UUID, DungeonLockout> lockouts = new ConcurrentHashMap<>();

    private DungeonManager() {}

    public static DungeonManager getInstance() { return INSTANCE; }

    public DungeonInstance createInstance(String dungeonId, DungeonTier tier, int floor) {
        DungeonInstance instance = new DungeonInstance(dungeonId, tier, floor);
        instances.put(dungeonId, instance);
        return instance;
    }

    public boolean join(String dungeonId, UUID playerUuid) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance == null || instance.getState() != DungeonState.LOBBY) return false;
        if (isLocked(playerUuid)) return false;
        return instance.addPlayer(playerUuid);
    }

    public void leave(String dungeonId, UUID playerUuid) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance != null) {
            instance.removePlayer(playerUuid);
        }
    }

    public void complete(String dungeonId) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance != null) {
            instance.setState(DungeonState.COMPLETED);
        }
    }

    public void fail(String dungeonId) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance != null) {
            instance.setState(DungeonState.FAILED);
        }
    }

    public void start(String dungeonId) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance != null && instance.getState() == DungeonState.LOBBY) {
            instance.setState(DungeonState.IN_PROGRESS);
        }
    }

    public DungeonLockout getLockout(UUID playerUuid) {
        return lockouts.get(playerUuid);
    }

    public boolean isLocked(UUID playerUuid) {
        DungeonLockout lockout = lockouts.get(playerUuid);
        if (lockout == null || lockout.isBypassed()) return false;
        return !lockout.isExpired(System.currentTimeMillis());
    }

    public void setLockout(UUID playerUuid, DungeonLockout lockout) {
        lockouts.put(playerUuid, lockout);
    }

    public void clearLockout(UUID playerUuid) {
        lockouts.remove(playerUuid);
    }

    public DungeonInstance getInstance(String dungeonId) {
        return instances.get(dungeonId);
    }

    public List<DungeonInstance> getAllInstances() {
        return List.copyOf(instances.values());
    }

    public List<DungeonInstance> getInstancesByTier(DungeonTier tier) {
        return instances.values().stream()
                .filter(i -> i.getTier() == tier)
                .toList();
    }

    public List<DungeonInstance> getInstancesByFloor(int floor) {
        return instances.values().stream()
                .filter(i -> i.getFloor() == floor)
                .toList();
    }

    public void removeInstance(String dungeonId) {
        instances.remove(dungeonId);
    }

    public void reset() {
        instances.clear();
        lockouts.clear();
    }
}
