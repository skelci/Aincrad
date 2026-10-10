package com.aincrad.world;

import net.minecraft.util.math.BlockPos;

/**
 * Metadata for a single Aincrad floor.
 *
 * Each floor is a 1024x1024 block area enclosed by bedrock walls.
 * Floor 1 is a safe zone plaza; all others contain a boss arena.
 */
public class FloorDimension {

    public static final int FLOOR_SIZE = 1024;
    public static final int HALF_SIZE = FLOOR_SIZE / 2;
    public static final int WALL_HEIGHT = 20;
    public static final int GROUND_LEVEL = 64;

    private final int floorNumber;
    private final int tier;
    private final FloorTheme theme;
    private final String bossName;
    private final boolean safeZone;
    private boolean bossDefeated;

    public FloorDimension(int floorNumber, int tier, FloorTheme theme, String bossName, boolean safeZone) {
        if (floorNumber < 1 || floorNumber > 100) {
            throw new IllegalArgumentException("Floor number must be 1-100, got: " + floorNumber);
        }
        this.floorNumber = floorNumber;
        this.tier = tier;
        this.theme = theme;
        this.bossName = bossName;
        this.safeZone = safeZone;
        this.bossDefeated = false;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getTier() {
        return tier;
    }

    public FloorTheme getTheme() {
        return theme;
    }

    public String getBossName() {
        return bossName;
    }

    public boolean isSafeZone() {
        return safeZone;
    }

    public boolean isBossDefeated() {
        return bossDefeated;
    }

    public void setBossDefeated(boolean defeated) {
        this.bossDefeated = defeated;
    }

    /**
     * Returns the spawn position at the floor edge (south-west corner).
     */
    public BlockPos getSpawnPos() {
        return new BlockPos(-HALF_SIZE + 8, GROUND_LEVEL + 1, -HALF_SIZE + 8);
    }

    /**
     * Returns the arena center position (middle of the 1024x1024 area).
     */
    public BlockPos getArenaCenter() {
        return new BlockPos(0, GROUND_LEVEL, 0);
    }

    /**
     * Returns the boss arena radius in blocks.
     */
    public int getArenaRadius() {
        return 64;
    }

    @Override
    public String toString() {
        return "FloorDimension{floor=" + floorNumber + ", tier=" + tier +
                ", theme=" + theme.getDisplayName() + ", boss=" + bossName +
                ", safeZone=" + safeZone + ", bossDefeated=" + bossDefeated + "}";
    }
}
