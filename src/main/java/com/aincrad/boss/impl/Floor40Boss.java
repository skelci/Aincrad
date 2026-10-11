package com.aincrad.boss.impl;

import com.aincrad.boss.BossEntity;
import com.aincrad.boss.BossPhase;
import com.aincrad.boss.BossRegistry;
import com.aincrad.boss.patterns.BossAttackLibrary;
import com.aincrad.boss.patterns.BossPatternFactory;
import com.aincrad.boss.util.BossStatScaler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Floor 40 boss — Magma Lord.
 * A lava-themed tier boss with magma waves, flame charges, and infernal wrath.
 */
public class Floor40Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor40_boss"));

    public static final EntityType<Floor40Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor40Boss::new, MobCategory.MONSTER)
            .sized(1.4f, 2.6f)
            .build(TYPE_KEY));

    private static final int FLOOR = 40;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor40Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Tier boss pattern library
        var libPatterns = BossAttackLibrary.getTierBossPatterns(FLOOR);
        for (var p : libPatterns) {
            patterns.add(p);
            patternCooldowns.add(0);
        }

        // Infernal Charge — fiery charge with knockback
        patterns.add(BossPatternFactory.createCharge("InfernalCharge", BASE_DAMAGE * 1.4f, 14.0f, 95));
        patternCooldowns.add(0);

        // Magma Eruption — large AoE slam
        patterns.add(BossPatternFactory.createAoeSlam("MagmaEruption", BASE_DAMAGE * 1.7f, 8.0f, 85));
        patternCooldowns.add(0);

        // Flame Wave — expanding AoE wave
        patterns.add(BossPatternFactory.createAoeWave("FlameWave", BASE_DAMAGE * 1.4f, 110));
        patternCooldowns.add(0);

        // Summon Fire Elementals — summon minions
        patterns.add(BossPatternFactory.createSummon("SummonFireElementals", 2, BASE_DAMAGE * 0.3f, 175));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Magma Lord";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Magma Lord's flames burn hotter!";
            case PHASE_3 -> "The Magma Lord's body drips with molten rock!";
            case PHASE_4 -> "The Magma Lord channels the inferno's wrath!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GOLD + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
            case PHASE_3 -> this.getAttribute(Attributes.ARMOR).setBaseValue(22.0f);
            case PHASE_4 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor40_magma_lord", Floor40Boss.class, FLOOR);
    }
}
