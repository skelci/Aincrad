package com.aincrad.dungeon.layout;

/**
 * Represents a mob placement within a dungeon layout.
 * Defines what mob spawns, where, how many, and its respawn timer.
 */
public final class DungeonMobPlacement {

    private final String mobType;
    private final int x;
    private final int y;
    private final int z;
    private final int count;
    private final int respawnTicks;

    public DungeonMobPlacement(String mobType, int x, int y, int z, int count, int respawnTicks) {
        this.mobType = mobType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.count = count;
        this.respawnTicks = respawnTicks;
    }

    public String getMobType() {
        return mobType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getCount() {
        return count;
    }

    public int getRespawnTicks() {
        return respawnTicks;
    }
}
