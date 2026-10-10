package com.aincrad.mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobSpawner {
    private final Map<Integer, Integer> floorSpawnCaps = new HashMap<>();
    private final Map<Integer, Integer> floorSpawnCounts = new HashMap<>();
    private final Map<UUID, Long> respawnTimers = new HashMap<>();

    public void setFloorCap(int floor, int cap) {
        this.floorSpawnCaps.put(floor, cap);
    }

    public int getFloorCap(int floor) {
        return this.floorSpawnCaps.getOrDefault(floor, 20);
    }

    public void incrementFloorCount(int floor) {
        this.floorSpawnCounts.merge(floor, 1, Integer::sum);
    }

    public void decrementFloorCount(int floor) {
        this.floorSpawnCounts.merge(floor, -1, Integer::sum);
    }

    public int getFloorCount(int floor) {
        return this.floorSpawnCounts.getOrDefault(floor, 0);
    }

    public boolean canSpawn(int floor) {
        return getFloorCount(floor) < getFloorCap(floor);
    }

    public void setRespawnTimer(UUID mobId, long respawnAt) {
        this.respawnTimers.put(mobId, respawnAt);
    }

    public boolean isRespawnReady(UUID mobId, long now) {
        Long timer = this.respawnTimers.get(mobId);
        return timer == null || now >= timer;
    }

    public void clearRespawnTimer(UUID mobId) {
        this.respawnTimers.remove(mobId);
    }

    public void clearFloor(int floor) {
        this.floorSpawnCounts.remove(floor);
    }

    public void clearAll() {
        this.floorSpawnCounts.clear();
        this.respawnTimers.clear();
    }
}
