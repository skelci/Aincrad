package com.aincrad.dungeon;

import java.util.*;

public final class DungeonManager {

    private static final Map<String, DungeonInstance> instances = new HashMap<>();
    private static final Map<UUID, DungeonLockout> lockouts = new HashMap<>();

    private DungeonManager() {
    }

    public static DungeonInstance createInstance(String dungeonId, DungeonTier tier, int floor) {
        DungeonInstance instance = new DungeonInstance(dungeonId, tier, floor);
        instances.put(dungeonId, instance);
        return instance;
    }

    public static boolean join(String dungeonId, UUID playerUuid) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance == null || instance.getState() != DungeonState.LOBBY) {
            return false;
        }
        if (isLocked(playerUuid)) {
            return false;
        }
        instance.getPlayers().add(playerUuid);
        return true;
    }

    public static void leave(String dungeonId, UUID playerUuid) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance != null) {
            instance.getPlayers().remove(playerUuid);
        }
    }

    public static void complete(String dungeonId) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance != null) {
            instance.setState(DungeonState.COMPLETED);
        }
    }

    public static void fail(String dungeonId) {
        DungeonInstance instance = instances.get(dungeonId);
        if (instance != null) {
            instance.setState(DungeonState.FAILED);
        }
    }

    public static DungeonLockout getLockout(UUID playerUuid) {
        return lockouts.get(playerUuid);
    }

    public static boolean isLocked(UUID playerUuid) {
        DungeonLockout lockout = lockouts.get(playerUuid);
        if (lockout == null || lockout.isBypassed()) {
            return false;
        }
        return !lockout.isExpired(System.currentTimeMillis());
    }

    public static void setLockout(UUID playerUuid, DungeonLockout lockout) {
        lockouts.put(playerUuid, lockout);
    }
}
