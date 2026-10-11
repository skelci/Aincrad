package com.aincrad.dungeon.layout;

/**
 * Represents a loot table for a chest within a dungeon layout.
 * Defines chest position, loot tier, and drop rate.
 */
public final class DungeonLootTable {

    private final int chestX;
    private final int chestY;
    private final int chestZ;
    private final String lootTier;
    private final double dropRate;

    public DungeonLootTable(int chestX, int chestY, int chestZ, String lootTier, double dropRate) {
        this.chestX = chestX;
        this.chestY = chestY;
        this.chestZ = chestZ;
        this.lootTier = lootTier;
        this.dropRate = dropRate;
    }

    public int getChestX() {
        return chestX;
    }

    public int getChestY() {
        return chestY;
    }

    public int getChestZ() {
        return chestZ;
    }

    public String getLootTier() {
        return lootTier;
    }

    public double getDropRate() {
        return dropRate;
    }
}
