package com.aincrad.mob.impl;

/**
 * Central registration for all mob types.
 * Registers entity types with the game registry during mod initialization.
 */
public final class MobRegistration {

    private MobRegistration() {}

    public static void registerAll() {
        // Entity types are registered via static initializers in each mob class.
        // This method serves as the central registration entry point for future use
        // (e.g., spawning, attribute overrides, etc.).
        // Theme 4 - Desert (floors 31-40)
        // SandSpiderMob, DesertSkeletonMob, SandSlimeMob, CactusMob, SandWraithMob

        // Theme 5 - Volcano/Lava (floors 41-50)
        // LavaBatMob, MagmaSlimeMob, FireSkeletonMob, ObsidianGolemMob, FlameWraithMob

        // Theme 6 - Ocean/Underwater (floors 51-60)
        // DrownedMob, CoralSlimeMob, SeaSerpentMob, JellyfishMob, DeepOneMob
    }
}