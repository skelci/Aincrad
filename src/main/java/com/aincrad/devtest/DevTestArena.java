package com.aincrad.devtest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Generates a flat test arena at a safe location.
 * Clears a 30x30 area and places signs at key locations.
 */
public final class DevTestArena {

    public static final int ARENA_SIZE = 30;
    public static final int HALF_SIZE = ARENA_SIZE / 2;
    public static final int GROUND_Y = 64;

    private DevTestArena() {
    }

    /**
     * Generates a flat test arena centered on the given position.
     *
     * @param level The server level
     * @param center The center position of the arena
     */
    public static void generate(ServerLevel level, BlockPos center) {
        int minX = center.getX() - HALF_SIZE;
        int maxX = center.getX() + HALF_SIZE;
        int minZ = center.getZ() - HALF_SIZE;
        int maxZ = center.getZ() + HALF_SIZE;
        int groundY = GROUND_Y;

        // Clear the area and set ground
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // Clear blocks above ground
                for (int y = groundY + 1; y <= groundY + 10; y++) {
                    level.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
                }
                // Set ground level
                level.setBlockAndUpdate(new BlockPos(x, groundY, z), Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }

        // Place signs at key locations
        placeSigns(level, center, groundY);
    }

    /**
     * Places signs at key locations in the arena.
     */
    private static void placeSigns(ServerLevel level, BlockPos center, int groundY) {
        int signY = groundY + 1;

        // Boss testing area sign
        BlockPos bossSignPos = new BlockPos(center.getX() - HALF_SIZE + 2, signY, center.getZ() - HALF_SIZE + 2);
        DevTestSigns.placeSign(level, bossSignPos, "TEST: Boss F1", "Spawn F1 boss", "Check phases", "Verify loot");

        // Mob testing area sign
        BlockPos mobSignPos = new BlockPos(center.getX() + HALF_SIZE - 2, signY, center.getZ() - HALF_SIZE + 2);
        DevTestSigns.placeSign(level, mobSignPos, "TEST: Mobs", "Spawn all mobs", "Check AI", "Verify drops");

        // Item testing area sign
        BlockPos itemSignPos = new BlockPos(center.getX() - HALF_SIZE + 2, signY, center.getZ() + HALF_SIZE - 2);
        DevTestSigns.placeSign(level, itemSignPos, "TEST: Items", "Give all items", "Check stats", "Verify crafting");

        // Skill testing area sign
        BlockPos skillSignPos = new BlockPos(center.getX() + HALF_SIZE - 2, signY, center.getZ() + HALF_SIZE - 2);
        DevTestSigns.placeSign(level, skillSignPos, "TEST: Skills", "Give all skills", "Check cooldowns", "Verify effects");

        // Crystal testing area sign
        BlockPos crystalSignPos = new BlockPos(center.getX(), signY, center.getZ() - HALF_SIZE + 2);
        DevTestSigns.placeSign(level, crystalSignPos, "TEST: Crystals", "Give crystals", "Check tiers", "Verify socketing");

        // Enhancement testing area sign
        BlockPos enhanceSignPos = new BlockPos(center.getX(), signY, center.getZ() + HALF_SIZE - 2);
        DevTestSigns.placeSign(level, enhanceSignPos, "TEST: Enhance", "Give materials", "Test +0 to +15", "Check break rates");
    }
}
