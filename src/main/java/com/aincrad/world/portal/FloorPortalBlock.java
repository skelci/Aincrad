package com.aincrad.world.portal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A themed portal block that detects player collision and triggers
 * teleportation to a target floor.
 *
 * The block color varies by target floor tier:
 *   Tier 1 - Green (safe zone)
 *   Tier 2 - Blue (forest)
 *   Tier 3 - Red (volcanic)
 *   Tier 4 - Purple (dark)
 *   Tier 5 - Gold (endgame)
 */
public class FloorPortalBlock extends Block {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-portal");

    /** The floor number this portal leads to. */
    private final int targetFloor;

    /** Whether this portal is currently active (can teleport). */
    private boolean isActive;

    /** Tier of the target floor, used for color selection. */
    private final int targetTier;

    public FloorPortalBlock(int targetFloor, int targetTier, boolean isActive) {
        // Use iron block as base; in production you'd create proper settings
        super(Properties.copy(Blocks.IRON_BLOCK));
        this.targetFloor = targetFloor;
        this.targetTier = targetTier;
        this.isActive = isActive;
    }

    public FloorPortalBlock(int targetFloor, int targetTier) {
        this(targetFloor, targetTier, true);
    }

    /**
     * Called when an entity collides with this block.
     * If the entity is a player and the portal is active, trigger teleportation.
     */
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!isActive) {
            return;
        }
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }
        if (world.isClient()) {
            return;
        }

        LOGGER.info("Floor portal activated: player {} entering portal to floor {}",
                player.getName().getString(), targetFloor);

        // Trigger teleportation via PortalManager
        PortalManager.getInstance().teleportToFloor(player, targetFloor);

        // Particle effect marker (no-op for skeleton — add particles later)
        spawnPortalParticles(world, pos);
    }

    /**
     * Spawn portal particle effects. Currently a no-op stub.
     */
    private void spawnPortalParticles(World world, BlockPos pos) {
        // TODO: Implement particle spawning using world.addParticle()
        // Example (once API is confirmed):
        // world.addParticle(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0, 0, 0);
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public int getTargetTier() {
        return targetTier;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Returns the color for this portal based on the target floor tier.
     * Used for block rendering (tint).
     *
     * @return ARGB color integer
     */
    public int getTierColor() {
        return switch (targetTier) {
            case 1 -> 0xFF4CAF50; // Green
            case 2 -> 0xFF2196F3; // Blue
            case 3 -> 0xFFF44336; // Red
            case 4 -> 0xFF9C27B0; // Purple
            case 5 -> 0xFFFFD700; // Gold
            default -> 0xFFFFFFFF; // White (unknown tier)
        };
    }

    @Override
    public String toString() {
        return "FloorPortalBlock{floor=" + targetFloor + ", tier=" + targetTier + ", active=" + isActive + "}";
    }
}
