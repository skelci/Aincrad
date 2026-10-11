package com.aincrad.mob;

import com.aincrad.mob.impl.*;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class MobRegistration {

    private static final List<EntityType<?>> ALL_MOBS = new ArrayList<>();

    public static void registerAll() {
        ALL_MOBS.add(SlimeMob.TYPE);
        ALL_MOBS.add(ForestWolfMob.TYPE);
        ALL_MOBS.add(ThornSpiderMob.TYPE);
        ALL_MOBS.add(WoodlandSkeletonMob.TYPE);
        ALL_MOBS.add(MushroomCowMob.TYPE);
        ALL_MOBS.add(CaveBatMob.TYPE);
        ALL_MOBS.add(StoneZombieMob.TYPE);
        ALL_MOBS.add(CrystalSpiderMob.TYPE);
        ALL_MOBS.add(RuinSkeletonMob.TYPE);
        ALL_MOBS.add(LavaSlimeMob.TYPE);
        ALL_MOBS.add(FrostWolfMob.TYPE);
        ALL_MOBS.add(IceSkeletonMob.TYPE);
        ALL_MOBS.add(SnowSlimeMob.TYPE);
        ALL_MOBS.add(FrostBatMob.TYPE);
        ALL_MOBS.add(GlacierGolemMob.TYPE);
    }

    public static List<EntityType<?>> getAllMobs() {
        return List.copyOf(ALL_MOBS);
    }

    private MobRegistration() {}
}
