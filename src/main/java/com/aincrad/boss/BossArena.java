package com.aincrad.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/**
 * Manages the bounds of a boss arena.
 *
 * Responsibilities:
 * <ul>
 *   <li>Define a rectangular (AABB) arena region</li>
 *   <li>Track which players are currently inside</li>
 *   <li>Lock the arena during combat (prevent players from leaving)</li>
 * </ul>
 *
 * Instances are typically created by a boss spawn event or a floor
 * definition and then assigned to a {@link BossEntity} via
 * {@link BossEntity#setArena(BossArena)}.
 */
public class BossArena {

    // ------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------

    /** Lower corner of the arena. */
    private final BlockPos min;

    /** Upper corner of the arena. */
    private final BlockPos max;

    /** Cached AABB for fast containment checks. */
    private final AABB bounds;

    /** Whether the arena is locked (players inside cannot leave). */
    private boolean locked = false;

    /** Display name for UI / messages. */
    private final String arenaName;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    /**
     * @param arenaName display name
     * @param min       lower corner (inclusive)
     * @param max       upper corner (inclusive)
     */
    public BossArena(String arenaName, BlockPos min, BlockPos max) {
        this.arenaName = arenaName;
        this.min = min;
        this.max = max;
        this.bounds = new AABB(
            min.getX(), min.getY(), min.getZ(),
            max.getX() + 1, max.getY() + 1, max.getZ() + 1
        );
    }

    // ------------------------------------------------------------------
    // Bounds
    // ------------------------------------------------------------------

    /**
     * @return the lower corner (inclusive)
     */
    public BlockPos getMin() {
        return min;
    }

    /**
     * @return the upper corner (inclusive)
     */
    public BlockPos getMax() {
        return max;
    }

    /**
     * @return the bounding box of the arena
     */
    public AABB getBounds() {
        return bounds;
    }

    /**
     * Checks whether a position is inside the arena.
     *
     * @param pos the position to check
     * @return {@code true} if inside
     */
    public boolean contains(BlockPos pos) {
        return this.bounds.contains(
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5
        );
    }

    // ------------------------------------------------------------------
    // Lock state
    // ------------------------------------------------------------------

    /**
     * Locks or unlocks the arena.
     *
     * @param locked {@code true} to lock, {@code false} to unlock
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * @return {@code true} if the arena is locked
     */
    public boolean isLocked() {
        return locked;
    }

    // ------------------------------------------------------------------
    // Player tracking
    // ------------------------------------------------------------------

    /**
     * @return the number of players currently inside the arena
     */
    public int getPlayerCount(java.util.List<? extends Player> allPlayers) {
        int count = 0;
        for (Player player : allPlayers) {
            if (contains(player.blockPosition())) {
                count++;
            }
        }
        return count;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    /**
     * @return the display name of the arena
     */
    public String getArenaName() {
        return arenaName;
    }

    // ------------------------------------------------------------------
    // Object
    // ------------------------------------------------------------------

    @Override
    public String toString() {
        return arenaName + " [" + min + " → " + max + "]"
            + (locked ? " (LOCKED)" : "");
    }
}
