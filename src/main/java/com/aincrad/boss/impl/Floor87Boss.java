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
 * Floor 87 boss — Celestial Guardian.
 * A sky-themed boss with a divine protector. 5 attack patterns.
 */
public class Floor87Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor87_boss"));

    public static final EntityType<Floor87Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor87Boss::new, MobCategory.MONSTER)
            .sized(1.2f, 2.4f)
            .build(TYPE_KEY));

    private static final int FLOOR = 87;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor87Boss(EntityType<? extends Monster> entityType, Level level) {
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
        // Celestial Guardian Heavy Strike
        patterns.add(BossPatternFactory.createHeavyStrike("HeavyStrike", BASE_DAMAGE * 1.4f, 70));
        patternCooldowns.add(0);
        // Celestial Guardian AoE Slam
        patterns.add(BossPatternFactory.createAoeSlam("AoeSlam", BASE_DAMAGE * 1.5f, 6.0f, 85));
        patternCooldowns.add(0);
        // Celestial Guardian Charge
        patterns.add(BossPatternFactory.createCharge("Charge", BASE_DAMAGE * 1.3f, 12.0f, 95));
        patternCooldowns.add(0);
        // Celestial Guardian Summon
        patterns.add(BossPatternFactory.createSummon("Summon", 2, BASE_DAMAGE * 0.25f, 160));
        patternCooldowns.add(0);

    }

    @Override
    public String getBossName() {
        return "Celestial Guardian";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;

        String msg2 = switch (phase) {
            case PHASE_2 -> "The Celestial Guardian's power increases!";
            default -> null;
        };
        if (msg2 != null) {
            final String finalMsg2 = msg2;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.WHITE + finalMsg2)));
        }
        String msg3 = switch (phase) {
            case PHASE_3 -> "The Celestial Guardian enters a frenzy!";
            default -> null;
        };
        if (msg3 != null) {
            final String finalMsg3 = msg3;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.WHITE + finalMsg3)));
        }
        String msg4 = switch (phase) {
            case PHASE_4 -> "The Celestial Guardian unleashes its full might!";
            default -> null;
        };
        if (msg4 != null) {
            final String finalMsg4 = msg4;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.WHITE + finalMsg4)));
        }
        String msg5 = switch (phase) {
            case PHASE_5 -> "The Celestial Guardian is desperate!";
            default -> null;
        };
        if (msg5 != null) {
            final String finalMsg5 = msg5;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.WHITE + finalMsg5)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
            case PHASE_3 -> this.getAttribute(Attributes.ARMOR).setBaseValue(53f);
            case PHASE_4 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.735f);
            case PHASE_5 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor87_celestial_guardian", Floor87Boss.class, FLOOR);
    }
}
