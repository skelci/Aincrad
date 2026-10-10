package com.aincrad.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DungeonInstance {

    private final String dungeonId;
    private final DungeonTier tier;
    private final int floor;
    private final List<UUID> players;
    private DungeonState state;

    public DungeonInstance(String dungeonId, DungeonTier tier, int floor) {
        this.dungeonId = dungeonId;
        this.tier = tier;
        this.floor = floor;
        this.players = new ArrayList<>();
        this.state = DungeonState.LOBBY;
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public DungeonTier getTier() {
        return tier;
    }

    public int getFloor() {
        return floor;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public DungeonState getState() {
        return state;
    }

    public void setState(DungeonState state) {
        this.state = state;
    }
}
