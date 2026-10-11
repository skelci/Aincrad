package com.aincrad.mob.spawn;

import com.aincrad.mob.MobEntity;
import com.aincrad.mob.MobSpawner;
import com.aincrad.mob.MobType;
import com.aincrad.world.FloorRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Static manager that handles per-floor mob spawning.
 * <p>
 * Uses {@link MobSpawner} for floor caps and respawn timers.
 * Uses {@link MobSpawnConfig} for per-theme mob types, weights, and caps.
 * <p>
 * Only spawns mobs whose {@link MobType} is HOSTILE, PACK, RANGED, or AMBUSHER.
 * PASSIVE and NEUTRAL mobs are never spawned by this manager.
 */
public final class MobSpawnManager {

    private static final MobSpawnManager INSTANCE = new MobSpawnManager();

    private final MobSpawner spawner = new MobSpawner();
    private final MobSpawnConfig config = new MobSpawnConfig();
    private final Random random = new Random();

    private MobSpawnManager() {}

    public static MobSpawnManager getInstance() {
        return INSTANCE;
    }

    /**
     * Spawns mobs on the given floor.
     *
     * @param level    The floor dimension level
     * @param floor    Floor number (1-100)
     * @param mobCount Number of mobs to attempt spawning
     */
    public void spawnFloorMob(Level level, int floor, int mobCount) {
        if (level == null || level.isClientSide()) return;
        if (!FloorRegistry.isValidFloor(floor)) return;

        int themeIndex = getThemeIndexForFloor(floor);
        List<MobSpawnConfig.MobEntry> themeMobs = config.getMobsForTheme(themeIndex);
        if (themeMobs.isEmpty()) return;

        // Get players in this level
        List<Player> players = new ArrayList<>(level.players());
        if (players.isEmpty()) return;

        int spawned = 0;
        int attempts = 0;
        int maxAttempts = mobCount * 3;

        while (spawned < mobCount && attempts < maxAttempts) {
            attempts++;

            // Check global floor cap
            if (!spawner.canSpawn(floor)) break;

            // Pick a random mob from the theme using weighted selection
            MobSpawnConfig.MobEntry entry = pickWeightedMob(themeMobs);
            if (entry == null) continue;

            // Check per-mob cap
            if (!canSpawnMobType(floor, entry.id(), entry.cap())) continue;

            // Pick a random player to spawn near
            Player targetPlayer = players.get(random.nextInt(players.size()));

            // Calculate spawn position (20-40 blocks from player)
            double[] pos = calculateSpawnPosition(targetPlayer);
            if (pos == null) continue;

            // Create and spawn the mob
            MobEntity mob = createMob(entry.mobClass(), level);
            if (mob == null) continue;

            // Only spawn HOSTILE, PACK, RANGED, AMBUSHER mobs
            if (!isAllowedMobType(mob.getMobType())) continue;

            mob.setFloorLevel(floor);
            mob.setPos(pos[0], pos[1], pos[2]);

            level.addFreshEntity(mob);
            spawner.incrementFloorCount(floor);
            spawned++;
        }
    }

    /**
     * Returns the MobSpawner instance for direct cap/timer access.
     */
    public MobSpawner getSpawner() {
        return spawner;
    }

    /**
     * Returns the MobSpawnConfig instance.
     */
    public MobSpawnConfig getConfig() {
        return config;
    }

    /**
     * Maps a floor number to a theme index (0-9).
     */
    private int getThemeIndexForFloor(int floor) {
        int tier = FloorRegistry.getTierForFloor(floor);
        return tier - 1;
    }

    /**
     * Picks a random mob from the theme using weighted random selection.
     */
    private MobSpawnConfig.MobEntry pickWeightedMob(List<MobSpawnConfig.MobEntry> mobs) {
        int totalWeight = 0;
        for (MobSpawnConfig.MobEntry mob : mobs) {
            totalWeight += mob.weight();
        }
        if (totalWeight <= 0) return null;

        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (MobSpawnConfig.MobEntry mob : mobs) {
            cumulative += mob.weight();
            if (roll < cumulative) {
                return mob;
            }
        }
        return mobs.get(mobs.size() - 1);
    }

    /**
     * Checks if a mob type can be spawned (per-mob cap check).
     */
    private boolean canSpawnMobType(int floor, String mobId, int cap) {
        // For now, we use the global floor cap from MobSpawner
        // Per-mob-type caps would require tracking counts per type
        return spawner.canSpawn(floor);
    }

    /**
     * Calculates a random spawn position 20-40 blocks from the player.
     */
    private double[] calculateSpawnPosition(Player player) {
        int minRadius = config.getSetting("spawnRadiusMin", 20);
        int maxRadius = config.getSetting("spawnRadiusMax", 40);

        double angle = random.nextDouble() * Math.PI * 2;
        double distance = minRadius + random.nextDouble() * (maxRadius - minRadius);

        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        double y = player.getY();

        return new double[]{x, y, z};
    }

    /**
     * Creates a mob instance using reflection.
     */
    private MobEntity createMob(String className, Level level) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!MobEntity.class.isAssignableFrom(clazz)) {
                return null;
            }
            @SuppressWarnings("unchecked")
            Constructor<? extends MobEntity> constructor =
                (Constructor<? extends MobEntity>) clazz.getConstructor(EntityType.class, Level.class);

            // We need an EntityType to pass to the constructor.
            // Since we don't have the specific type here, we use a workaround:
            // Create with null and let the mob handle it, or use the first registered type.
            // Actually, looking at the mob classes, they all take (EntityType, Level).
            // We can use any EntityType since the mob will use its own TYPE internally.
            // But we need a non-null EntityType. Let's use a dummy approach.

            // Get the TYPE field from the class
            try {
                java.lang.reflect.Field typeField = clazz.getField("TYPE");
                EntityType<?> entityType = (EntityType<?>) typeField.get(null);
                return constructor.newInstance(entityType, level);
            } catch (NoSuchFieldException e) {
                // Fallback: try to create with null (may not work for all mobs)
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns true if the mob type is allowed to spawn.
     * Only HOSTILE, PACK, RANGED, and AMBUSHER are allowed.
     */
    private boolean isAllowedMobType(MobType type) {
        return switch (type) {
            case HOSTILE, PACK, RANGED, AMBUSHER -> true;
            default -> false;
        };
    }
}
