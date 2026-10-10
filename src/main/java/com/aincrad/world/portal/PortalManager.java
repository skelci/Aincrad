package com.aincrad.world.portal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Singleton manager for portal transitions between floors.
 *
 * Handles:
 *  - Teleporting players to specific floors
 *  - Teleporting players back to the hub (Floor 1)
 *  - Activating/deactivating exit portals (post-boss defeat)
 *
 * NOTE: Actual dimension switching requires full multi-dimension setup
 * (custom DimensionType, WorldStem, etc.). This is currently a stub that
 * logs transitions. The teleportation call sites are marked for
 * integration when the dimension infrastructure is ready.
 */
public class PortalManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-portal");

    private static PortalManager instance;

    /** Tracks which floors have their exit portal activated (boss defeated). */
    private final Set<Integer> activeExitPortals;

    /** Cache of target spawn positions per floor. */
    private final Map<Integer, BlockPos> floorSpawnCache;

    private PortalManager() {
        this.activeExitPortals = new HashSet<>();
        this.floorSpawnCache = new HashMap<>();
    }

    public static synchronized PortalManager getInstance() {
        if (instance == null) {
            instance = new PortalManager();
        }
        return instance;
    }

    /**
     * Teleport a player to the specified floor.
     *
     * @param player     The player to teleport
     * @param targetFloor The floor number to send them to
     */
    public void teleportToFloor(PlayerEntity player, int targetFloor) {
        if (player == null) {
            LOGGER.warn("Attempted to teleport null player to floor {}", targetFloor);
            return;
        }

        LOGGER.info("Portal transition: {} -> Floor {}",
                player.getName().getString(), targetFloor);

        if (player instanceof ServerPlayerEntity serverPlayer) {
            teleportServerPlayer(serverPlayer, targetFloor);
        } else {
            LOGGER.debug("Non-server player teleport requested: {}", player.getName().getString());
        }
    }

    /**
     * Teleport a player back to the hub (Floor 1 safe zone).
     *
     * @param player The player to teleport
     */
    public void teleportToHub(PlayerEntity player) {
        if (player == null) {
            LOGGER.warn("Attempted to teleport null player to hub");
            return;
        }

        LOGGER.info("Hub portal transition: {} -> Floor 1 (Safe Zone)",
                player.getName().getString());

        if (player instanceof ServerPlayerEntity serverPlayer) {
            teleportServerPlayer(serverPlayer, 1);
        } else {
            LOGGER.debug("Non-server player hub teleport requested: {}", player.getName().getString());
        }
    }

    /**
     * Activate the exit portal on a floor (called after boss defeat).
     *
     * @param floor The floor number whose exit portal should be activated
     */
    public void activateExitPortal(int floor) {
        activeExitPortals.add(floor);
        LOGGER.info("Exit portal ACTIVATED on floor {}", floor);
    }

    /**
     * Deactivate the exit portal on a floor.
     *
     * @param floor The floor number whose exit portal should be deactivated
     */
    public void deactivateExitPortal(int floor) {
        activeExitPortals.remove(floor);
        LOGGER.info("Exit portal DEACTIVATED on floor {}", floor);
    }

    /**
     * Check if the exit portal on a given floor is active.
     */
    public boolean isExitPortalActive(int floor) {
        return activeExitPortals.contains(floor);
    }

    /**
     * Cache the spawn position for a floor (used by FloorPortalEntity.getTargetPos).
     */
    public void setFloorSpawn(int floor, BlockPos spawnPos) {
        floorSpawnCache.put(floor, spawnPos);
    }

    /**
     * Get the cached spawn position for a floor.
     */
    public BlockPos getFloorSpawn(int floor) {
        return floorSpawnCache.getOrDefault(floor, BlockPos.ZERO);
    }

    /**
     * Internal method to perform the actual server player teleport.
     *
     * STUB: Actual dimension switching requires:
     *   - A custom DimensionType registered in the dimension registry
     *   - A WorldStem with the floor's LevelStem
     *   - ServerPlayerEntity.teleportTo(ServerLevel, x, y, z, yaw, pitch)
     *
     * When ready, replace the LOGGER.info line below with:
     *   ServerLevel targetWorld = serverPlayer.getServer().getLevel(targetDimensionKey);
     *   if (targetWorld != null) {
     *       BlockPos spawn = getFloorSpawn(targetFloor);
     *       serverPlayer.teleportTo(targetWorld, spawn.getX(), spawn.getY(), spawn.getZ(),
     *                               serverPlayer.getYRot(), serverPlayer.getXRot());
     *   }
     */
    private void teleportServerPlayer(ServerPlayerEntity player, int targetFloor) {
        // TODO: Replace with actual dimension teleport when infrastructure is ready
        LOGGER.info("[STUB] Would teleport {} to floor {} at {}",
                player.getName().getString(),
                targetFloor,
                getFloorSpawn(targetFloor));
    }
}
