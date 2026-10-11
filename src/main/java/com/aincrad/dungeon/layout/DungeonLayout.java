package com.aincrad.dungeon.layout;

import com.aincrad.dungeon.DungeonTier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a complete dungeon layout including room connections,
 * spawn points, mob placements, boss room, and loot chests.
 */
public class DungeonLayout {

    private final String layoutId;
    private final String theme;
    private final int floor;
    private final DungeonTier tier;
    private final int spawnX;
    private final int spawnY;
    private final int spawnZ;
    private final int bossRoomX;
    private final int bossRoomY;
    private final int bossRoomZ;
    private final List<String> roomConnections;
    private final List<DungeonMobPlacement> mobPlacements;
    private final List<DungeonLootTable> lootTables;

    public DungeonLayout(String layoutId, String theme, int floor, DungeonTier tier,
                         int spawnX, int spawnY, int spawnZ,
                         int bossRoomX, int bossRoomY, int bossRoomZ,
                         List<String> roomConnections,
                         List<DungeonMobPlacement> mobPlacements,
                         List<DungeonLootTable> lootTables) {
        this.layoutId = layoutId;
        this.theme = theme;
        this.floor = floor;
        this.tier = tier;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.bossRoomX = bossRoomX;
        this.bossRoomY = bossRoomY;
        this.bossRoomZ = bossRoomZ;
        this.roomConnections = new ArrayList<>(roomConnections);
        this.mobPlacements = new ArrayList<>(mobPlacements);
        this.lootTables = new ArrayList<>(lootTables);
    }

    public String getLayoutId() {
        return layoutId;
    }

    public String getTheme() {
        return theme;
    }

    public int getFloor() {
        return floor;
    }

    public DungeonTier getTier() {
        return tier;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public int getBossRoomX() {
        return bossRoomX;
    }

    public int getBossRoomY() {
        return bossRoomY;
    }

    public int getBossRoomZ() {
        return bossRoomZ;
    }

    public List<String> getRoomConnections() {
        return Collections.unmodifiableList(roomConnections);
    }

    public List<DungeonMobPlacement> getMobPlacements() {
        return Collections.unmodifiableList(mobPlacements);
    }

    public List<DungeonLootTable> getLootTables() {
        return Collections.unmodifiableList(lootTables);
    }
}
