package com.aincrad.mob.spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.world.entity.EntityType;

/**
 * Static registry mapping theme indices to lists of EntityType.
 */
public class MobSpawnRegistry {

    private static final Map<Integer, List<EntityType<?>>> THEME_MOBS = new HashMap<>();

    private MobSpawnRegistry() {}

    /**
     * Registers a list of mob EntityTypes for a given theme index.
     */
    public static void registerTheme(int themeIndex, List<EntityType<?>> mobTypes) {
        THEME_MOBS.put(themeIndex, new ArrayList<>(mobTypes));
    }

    /**
     * Returns the list of mob EntityTypes for the given theme index.
     */
    public static List<EntityType<?>> getThemeMobs(int themeIndex) {
        return THEME_MOBS.getOrDefault(themeIndex, List.of());
    }

    /**
     * Returns a random mob EntityType from the given theme, or null if theme is empty.
     */
    public static EntityType<?> getRandomMob(int themeIndex, Random random) {
        List<EntityType<?>> mobs = THEME_MOBS.getOrDefault(themeIndex, List.of());
        if (mobs.isEmpty()) {
            return null;
        }
        return mobs.get(random.nextInt(mobs.size()));
    }
}
