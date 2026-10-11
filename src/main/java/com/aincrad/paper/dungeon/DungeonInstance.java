package com.aincrad.paper.dungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Dungeon instance with mutable state and immutable identity.
 */
public final class DungeonInstance {
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

    public String getDungeonId() { return dungeonId; }
    public DungeonTier getTier() { return tier; }
    public int getFloor() { return floor; }
    public DungeonState getState() { return state; }

    public void setState(DungeonState state) { this.state = state; }

    public List<UUID> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }

    public boolean addPlayer(UUID uuid) {
        if (state != DungeonState.LOBBY) return false;
        if (players.contains(uuid)) return false;
        players.add(uuid);
        return true;
    }

    public boolean removePlayer(UUID uuid) {
        return players.remove(uuid);
    }

    public int getPlayerCount() { return players.size(); }

    @Override
    public String toString() {
        return "Dungeon[" + dungeonId + " " + tier + " floor=" + floor
                + " state=" + state + " players=" + players.size() + "]";
    }
}
