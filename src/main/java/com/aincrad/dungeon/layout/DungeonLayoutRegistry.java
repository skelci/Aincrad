package com.aincrad.dungeon.layout;

import com.aincrad.dungeon.DungeonTier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static registry that maps (floor, tier) pairs to DungeonLayout instances.
 * Provides lookup by floor number and tier, as well as access to all registered layouts.
 */
public final class DungeonLayoutRegistry {

    private static final Map<String, DungeonLayout> REGISTRY = new HashMap<>();

    private DungeonLayoutRegistry() {
        // Utility class
    }

    /**
     * Registers a layout for the given floor and tier.
     */
    public static void register(int floor, DungeonTier tier, DungeonLayout layout) {
        REGISTRY.put(key(floor, tier), layout);
    }

    /**
     * Retrieves the layout for the given floor and tier.
     *
     * @return the registered layout, or null if none exists
     */
    public static DungeonLayout getLayout(int floor, DungeonTier tier) {
        return REGISTRY.get(key(floor, tier));
    }

    /**
     * Returns all registered layouts as an unmodifiable list.
     */
    public static List<DungeonLayout> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(REGISTRY.values()));
    }

    /**
     * Returns the number of registered layouts.
     */
    public static int size() {
        return REGISTRY.size();
    }

    /**
     * Clears all registered layouts.
     */
    public static void clear() {
        REGISTRY.clear();
    }

    private static String key(int floor, DungeonTier tier) {
        return tier.name() + "_" + floor;
    }
}
