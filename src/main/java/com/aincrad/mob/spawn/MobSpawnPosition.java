package com.aincrad.mob.spawn;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Utility for generating valid mob spawn positions near a player.
 */
public class MobSpawnPosition {

    private static final int MAX_ATTEMPTS = 10;

    /**
     * Returns a random valid spawn position near the player, or null if none found.
     */
    public static BlockPos getRandomSpawnPosition(Level level, Player player, int minDist, int maxDist) {
        if (level == null || player == null) {
            return null;
        }
        Random random = new Random();
        BlockPos playerPos = player.blockPosition();

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            int distance = minDist + random.nextInt(Math.max(1, maxDist - minDist + 1));
            int x = playerPos.getX() + (int) Math.round(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int) Math.round(Math.sin(angle) * distance);

            int y = findGroundY(level, x, z);
            if (y == -1) {
                continue;
            }

            BlockPos candidate = new BlockPos(x, y, z);
            if (isValidSpawn(level, candidate)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Scans down from the top of the world to find the first solid block.
     * Returns the Y position of the air block above it, or -1 if none found.
     */
    private static int findGroundY(Level level, int x, int z) {
        int minY = level.dimensionType().minY();
        int maxY = minY + level.dimensionType().logicalHeight();
        for (int y = maxY - 1; y >= minY; y--) {
            if (level.getBlockState(new BlockPos(x, y, z)).isSolid()) {
                return y + 1;
            }
        }
        return -1;
    }

    /**
     * Checks if a position is valid for spawning:
     * solid block below, air at the position and above.
     */
    public static boolean isValidSpawn(Level level, BlockPos pos) {
        if (pos == null || level == null) {
            return false;
        }
        return level.getBlockState(pos.below()).isSolid()
                && level.isEmptyBlock(pos)
                && level.isEmptyBlock(pos.above());
    }
}
