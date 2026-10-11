package com.aincrad.mob.spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.world.entity.EntityType;

/**
 * Static spawn weights for each theme.
 * Each theme has 5 mobs with individual weights.
 */
public class MobSpawnWeight {

    /**
     * A weight entry pairing an EntityType with its spawn weight.
     */
    public record WeightEntry(EntityType<?> type, int weight) {}

    private static final Map<Integer, List<WeightEntry>> THEME_WEIGHTS = new HashMap<>();

    static {
        // Forest (index 0): slime=25, wolf=25, skeleton=20, spider=15, cow=15
        register(0, com.aincrad.mob.impl.SlimeMob.TYPE, 25);
        register(0, com.aincrad.mob.impl.ForestWolfMob.TYPE, 25);
        register(0, com.aincrad.mob.impl.WoodlandSkeletonMob.TYPE, 20);
        register(0, com.aincrad.mob.impl.ThornSpiderMob.TYPE, 15);
        register(0, com.aincrad.mob.impl.MushroomCowMob.TYPE, 15);

        // Cave (index 1): bat=25, zombie=25, spider=20, skeleton=15, slime=15
        register(1, com.aincrad.mob.impl.CaveBatMob.TYPE, 25);
        register(1, com.aincrad.mob.impl.StoneZombieMob.TYPE, 25);
        register(1, com.aincrad.mob.impl.CrystalSpiderMob.TYPE, 20);
        register(1, com.aincrad.mob.impl.RuinSkeletonMob.TYPE, 15);
        register(1, com.aincrad.mob.impl.SlimeMob.TYPE, 15);

        // Ice (index 2): wolf=25, skeleton=25, slime=20, bat=15, golem=15
        register(2, com.aincrad.mob.impl.FrostWolfMob.TYPE, 25);
        register(2, com.aincrad.mob.impl.IceSkeletonMob.TYPE, 25);
        register(2, com.aincrad.mob.impl.SnowSlimeMob.TYPE, 20);
        register(2, com.aincrad.mob.impl.FrostBatMob.TYPE, 15);
        register(2, com.aincrad.mob.impl.GlacierGolemMob.TYPE, 15);

        // Desert (index 3): spider=25, skeleton=25, slime=20, cactus=15, wraith=15
        register(3, com.aincrad.mob.impl.SandSpiderMob.TYPE, 25);
        register(3, com.aincrad.mob.impl.DesertSkeletonMob.TYPE, 25);
        register(3, com.aincrad.mob.impl.SandSlimeMob.TYPE, 20);
        register(3, com.aincrad.mob.impl.CactusMob.TYPE, 15);
        register(3, com.aincrad.mob.impl.SandWraithMob.TYPE, 15);

        // Volcano (index 4): bat=25, slime=25, skeleton=20, golem=15, wraith=15
        register(4, com.aincrad.mob.impl.LavaBatMob.TYPE, 25);
        register(4, com.aincrad.mob.impl.LavaSlimeMob.TYPE, 25);
        register(4, com.aincrad.mob.impl.FireSkeletonMob.TYPE, 20);
        register(4, com.aincrad.mob.impl.ObsidianGolemMob.TYPE, 15);
        register(4, com.aincrad.mob.impl.FlameWraithMob.TYPE, 15);

        // Ocean (index 5): drowned=25, slime=25, serpent=20, jellyfish=15, deepone=15
        register(5, com.aincrad.mob.impl.DrownedMob.TYPE, 25);
        register(5, com.aincrad.mob.impl.CoralSlimeMob.TYPE, 25);
        register(5, com.aincrad.mob.impl.SeaSerpentMob.TYPE, 20);
        register(5, com.aincrad.mob.impl.JellyfishMob.TYPE, 15);
        register(5, com.aincrad.mob.impl.DeepOneMob.TYPE, 15);

        // Mountain (index 6): bat=25, wolf=25, slime=20, skeleton=15, golem=15
        register(6, com.aincrad.mob.impl.SkyBatMob.TYPE, 25);
        register(6, com.aincrad.mob.impl.MountainWolfMob.TYPE, 25);
        register(6, com.aincrad.mob.impl.CloudSlimeMob.TYPE, 20);
        register(6, com.aincrad.mob.impl.StormSkeletonMob.TYPE, 15);
        register(6, com.aincrad.mob.impl.CliffGolemMob.TYPE, 15);

        // Swamp (index 7): spider=25, slime=25, bat=20, skeleton=15, wraith=15
        register(7, com.aincrad.mob.impl.PoisonSpiderMob.TYPE, 25);
        register(7, com.aincrad.mob.impl.SwampSlimeMob.TYPE, 25);
        register(7, com.aincrad.mob.impl.BogBatMob.TYPE, 20);
        register(7, com.aincrad.mob.impl.MarshSkeletonMob.TYPE, 15);
        register(7, com.aincrad.mob.impl.VenomWraithMob.TYPE, 15);

        // Crystal (index 8): bat=25, slime=25, spider=20, skeleton=15, wraith=15
        register(8, com.aincrad.mob.impl.CrystalBatMob.TYPE, 25);
        register(8, com.aincrad.mob.impl.DarkSlimeMob.TYPE, 25);
        register(8, com.aincrad.mob.impl.GemSpiderMob.TYPE, 20);
        register(8, com.aincrad.mob.impl.CrystalSkeletonMob.TYPE, 15);
        register(8, com.aincrad.mob.impl.VoidWraithMob.TYPE, 15);

        // Final Tower (index 9): bat=25, slime=25, spider=20, skeleton=15, golem=15
        register(9, com.aincrad.mob.impl.ShadowBatMob.TYPE, 25);
        register(9, com.aincrad.mob.impl.VoidSlimeMob.TYPE, 25);
        register(9, com.aincrad.mob.impl.ChaosSpiderMob.TYPE, 20);
        register(9, com.aincrad.mob.impl.DarkSkeletonMob.TYPE, 15);
        register(9, com.aincrad.mob.impl.VoidGolemMob.TYPE, 15);
    }

    private MobSpawnWeight() {}

    private static void register(int themeIndex, EntityType<?> type, int weight) {
        THEME_WEIGHTS.computeIfAbsent(themeIndex, k -> new ArrayList<>())
                .add(new WeightEntry(type, weight));
    }

    /**
     * Returns the total weight for the given theme.
     */
    public static int getTotalWeight(int themeIndex) {
        return THEME_WEIGHTS.getOrDefault(themeIndex, List.of()).stream()
                .mapToInt(WeightEntry::weight)
                .sum();
    }

    /**
     * Returns a randomly selected EntityType for the given theme using weighted selection.
     */
    public static EntityType<?> getRandomMobType(int themeIndex, Random random) {
        List<WeightEntry> entries = THEME_WEIGHTS.getOrDefault(themeIndex, List.of());
        if (entries.isEmpty()) {
            return null;
        }
        int total = getTotalWeight(themeIndex);
        int roll = random.nextInt(total);
        int cumulative = 0;
        for (WeightEntry entry : entries) {
            cumulative += entry.weight();
            if (roll < cumulative) {
                return entry.type();
            }
        }
        // Fallback (should not reach here)
        return entries.get(entries.size() - 1).type();
    }
}
