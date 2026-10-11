package com.aincrad.mob.spawn;

import com.aincrad.world.FloorDimensions;
import com.aincrad.world.FloorRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * Tick handler that triggers mob spawning every 100 ticks (5 seconds).
 * <p>
 * For each player in a floor dimension, calls {@link MobSpawnManager#spawnFloorMob}.
 * Checks {@link com.aincrad.mob.MobSpawner#canSpawn} before spawning.
 * <p>
 * Register with {@link #register()} during mod initialization.
 */
public final class MobSpawnTick {

    private static final int SPAWN_INTERVAL = 100; // ticks (5 seconds at 20 TPS)
    private static int tickCounter = 0;

    private MobSpawnTick() {}

    /**
     * Registers the tick handler with Fabric's server tick event.
     * Call this once during mod initialization.
     */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(MobSpawnTick::onTick);
    }

    /**
     * Called every server tick. Triggers spawning every {@value #SPAWN_INTERVAL} ticks.
     */
    private static void onTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < SPAWN_INTERVAL) {
            return;
        }
        tickCounter = 0;

        MobSpawnManager manager = MobSpawnManager.getInstance();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Level level = player.level();
            if (level == null || level.isClientSide()) continue;

            int floor = getFloorFromLevel(level);
            if (floor < 0) continue; // Not in a floor dimension

            // Check if we can spawn on this floor
            if (!manager.getSpawner().canSpawn(floor)) continue;

            // Determine how many mobs to spawn (1-3)
            int mobCount = 1 + (int) (Math.random() * 3);
            manager.spawnFloorMob(level, floor, mobCount);
        }
    }

    /**
     * Determines which floor a level corresponds to.
     *
     * @return Floor number (1-100), or -1 if not a floor dimension
     */
    private static int getFloorFromLevel(Level level) {
        for (int floor = 1; floor <= FloorDimensions.FLOOR_COUNT; floor++) {
            if (level.dimension().equals(FloorDimensions.getFloorKey(floor))) {
                return floor;
            }
        }
        return -1;
    }
}
