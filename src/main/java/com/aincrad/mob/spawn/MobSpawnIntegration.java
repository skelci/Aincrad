package com.aincrad.mob.spawn;

import com.aincrad.mob.MobRegistration;
import net.minecraft.world.entity.EntityType;

import java.util.List;

/**
 * Integration point that wires together MobSpawnPosition, MobSpawnRegistry,
 * and MobSpawnWeight for the spawning system.
 */
public class MobSpawnIntegration {

    private MobSpawnIntegration() {}

    /**
     * Initializes the spawning system.
     * Registers all mobs, then registers theme mob lists for the registry.
     * Called from AincradMod.onInitialize() after all mobs are registered.
     */
    public static void initialize() {
        // Ensure all mob EntityTypes are registered
        MobRegistration.registerAll();

        // Register theme mob lists in MobSpawnRegistry
        registerThemes();
    }

    private static void registerThemes() {
        // Forest (0)
        MobSpawnRegistry.registerTheme(0, List.of(
                com.aincrad.mob.impl.SlimeMob.TYPE,
                com.aincrad.mob.impl.ForestWolfMob.TYPE,
                com.aincrad.mob.impl.WoodlandSkeletonMob.TYPE,
                com.aincrad.mob.impl.ThornSpiderMob.TYPE,
                com.aincrad.mob.impl.MushroomCowMob.TYPE
        ));

        // Cave (1)
        MobSpawnRegistry.registerTheme(1, List.of(
                com.aincrad.mob.impl.CaveBatMob.TYPE,
                com.aincrad.mob.impl.StoneZombieMob.TYPE,
                com.aincrad.mob.impl.CrystalSpiderMob.TYPE,
                com.aincrad.mob.impl.RuinSkeletonMob.TYPE,
                com.aincrad.mob.impl.SlimeMob.TYPE
        ));

        // Ice (2)
        MobSpawnRegistry.registerTheme(2, List.of(
                com.aincrad.mob.impl.FrostWolfMob.TYPE,
                com.aincrad.mob.impl.IceSkeletonMob.TYPE,
                com.aincrad.mob.impl.SnowSlimeMob.TYPE,
                com.aincrad.mob.impl.FrostBatMob.TYPE,
                com.aincrad.mob.impl.GlacierGolemMob.TYPE
        ));

        // Desert (3)
        MobSpawnRegistry.registerTheme(3, List.of(
                com.aincrad.mob.impl.SandSpiderMob.TYPE,
                com.aincrad.mob.impl.DesertSkeletonMob.TYPE,
                com.aincrad.mob.impl.SandSlimeMob.TYPE,
                com.aincrad.mob.impl.CactusMob.TYPE,
                com.aincrad.mob.impl.SandWraithMob.TYPE
        ));

        // Volcano (4)
        MobSpawnRegistry.registerTheme(4, List.of(
                com.aincrad.mob.impl.LavaBatMob.TYPE,
                com.aincrad.mob.impl.LavaSlimeMob.TYPE,
                com.aincrad.mob.impl.FireSkeletonMob.TYPE,
                com.aincrad.mob.impl.ObsidianGolemMob.TYPE,
                com.aincrad.mob.impl.FlameWraithMob.TYPE
        ));

        // Ocean (5)
        MobSpawnRegistry.registerTheme(5, List.of(
                com.aincrad.mob.impl.DrownedMob.TYPE,
                com.aincrad.mob.impl.CoralSlimeMob.TYPE,
                com.aincrad.mob.impl.SeaSerpentMob.TYPE,
                com.aincrad.mob.impl.JellyfishMob.TYPE,
                com.aincrad.mob.impl.DeepOneMob.TYPE
        ));

        // Mountain (6)
        MobSpawnRegistry.registerTheme(6, List.of(
                com.aincrad.mob.impl.SkyBatMob.TYPE,
                com.aincrad.mob.impl.MountainWolfMob.TYPE,
                com.aincrad.mob.impl.CloudSlimeMob.TYPE,
                com.aincrad.mob.impl.StormSkeletonMob.TYPE,
                com.aincrad.mob.impl.CliffGolemMob.TYPE
        ));

        // Swamp (7)
        MobSpawnRegistry.registerTheme(7, List.of(
                com.aincrad.mob.impl.PoisonSpiderMob.TYPE,
                com.aincrad.mob.impl.SwampSlimeMob.TYPE,
                com.aincrad.mob.impl.BogBatMob.TYPE,
                com.aincrad.mob.impl.MarshSkeletonMob.TYPE,
                com.aincrad.mob.impl.VenomWraithMob.TYPE
        ));

        // Crystal (8)
        MobSpawnRegistry.registerTheme(8, List.of(
                com.aincrad.mob.impl.CrystalBatMob.TYPE,
                com.aincrad.mob.impl.DarkSlimeMob.TYPE,
                com.aincrad.mob.impl.GemSpiderMob.TYPE,
                com.aincrad.mob.impl.CrystalSkeletonMob.TYPE,
                com.aincrad.mob.impl.VoidWraithMob.TYPE
        ));

        // Final Tower (9)
        MobSpawnRegistry.registerTheme(9, List.of(
                com.aincrad.mob.impl.ShadowBatMob.TYPE,
                com.aincrad.mob.impl.VoidSlimeMob.TYPE,
                com.aincrad.mob.impl.ChaosSpiderMob.TYPE,
                com.aincrad.mob.impl.DarkSkeletonMob.TYPE,
                com.aincrad.mob.impl.VoidGolemMob.TYPE
        ));
    }
}
