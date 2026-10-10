package com.aincrad.boss;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static registry for all boss types.
 *
 * Allows registration of boss classes by name and floor, and lookup by
 * either key. The registry is populated during mod initialisation.
 *
 * <pre>{@code
 * BossRegistry.register("crystal_guardian", CrystalGuardianBoss.class, 1);
 * BossRegistry.register("abyssal_overlord", AbyssalOverlordBoss.class, 3);
 * }</pre>
 */
public final class BossRegistry {

    // ------------------------------------------------------------------
    // Entry record
    // ------------------------------------------------------------------

    /**
     * Holds a registered boss class and its associated metadata.
     *
     * @param clazz     the concrete BossEntity subclass
     * @param floor     the floor this boss guards (1-based)
     * @param bossClass reference to the class literal
     */
    public record BossEntry(Class<? extends BossEntity> clazz, int floor) {

        /**
         * @return the simple class name as the boss name
         */
        public String name() {
            return clazz.getSimpleName();
        }
    }

    // ------------------------------------------------------------------
    // Registry storage
    // ------------------------------------------------------------------

    private static final Map<String, BossEntry> REGISTRY = new LinkedHashMap<>();

    // ------------------------------------------------------------------
    // Registration
    // ------------------------------------------------------------------

    /**
     * Registers a boss class.
     *
     * @param name  unique identifier (used for lookups)
     * @param clazz the concrete BossEntity subclass
     * @param floor the floor this boss guards (1-based)
     * @throws IllegalArgumentException if the name is already registered
     */
    public static void register(String name, Class<? extends BossEntity> clazz, int floor) {
        if (REGISTRY.containsKey(name)) {
            throw new IllegalArgumentException(
                "Boss '" + name + "' is already registered."
            );
        }
        REGISTRY.put(name, new BossEntry(clazz, floor));
    }

    // ------------------------------------------------------------------
    // Lookup
    // ------------------------------------------------------------------

    /**
     * Looks up a boss by its registered name.
     *
     * @param name the unique identifier
     * @return the BossEntry, or {@code null} if not found
     */
    public static BossEntry get(String name) {
        return REGISTRY.get(name);
    }

    /**
     * Returns all bosses registered for a specific floor.
     *
     * @param floor the floor number (1-based)
     * @return an unmodifiable collection of matching entries
     */
    public static Collection<BossEntry> getByFloor(int floor) {
        return REGISTRY.values().stream()
            .filter(e -> e.floor() == floor)
            .collect(java.util.stream.Collectors.toUnmodifiableList());
    }

    /**
     * @return all registered entries (unmodifiable)
     */
    public static Collection<BossEntry> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    /**
     * @return the number of registered bosses
     */
    public static int size() {
        return REGISTRY.size();
    }

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    private BossRegistry() {
        // Utility class — no instantiation.
    }
}
