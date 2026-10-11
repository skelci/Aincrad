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
 * Floor 6 boss — Giant Rat.
 * A grassland-themed boss with an oversized rodent. 3 attack patterns.
 */
public class Floor6Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor6_boss"));

    public static final EntityType<Floor6Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor6Boss::new, MobCategory.MONSTER)
            .sized(1.0f, 0.8f)
            .build(TYPE_KEY));

    private static final int FLOOR = 6;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor6Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Standard pattern library
        var libPatterns = BossAttackLibrary.getStandardPatterns(FLOOR);
        for (var p : libPatterns) {
            patterns.add(p);
            patternCooldowns.add(0);
        }
        // Giant Rat Heavy Strike
        patterns.add(BossPatternFactory.createHeavyStrike("HeavyStrike", BASE_DAMAGE * 1.4f, 70));
        patternCooldowns.add(0);
        // Giant Rat AoE Slam
        patterns.add(BossPatternFactory.createAoeSlam("AoeSlam", BASE_DAMAGE * 1.5f, 6.0f, 85));
        patternCooldowns.add(0);

    }

    @Override
    public String getBossName() {
        return "Giant Rat";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;

        String msg2 = switch (phase) {
            case PHASE_2 -> "The Giant Rat's power increases!";
            default -> null;
        };
        if (msg2 != null) {
            final String finalMsg2 = msg2;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GREEN + finalMsg2)));
        }
        String msg3 = switch (phase) {
            case PHASE_3 -> "The Giant Rat enters a frenzy!";
            default -> null;
        };
        if (msg3 != null) {
            final String finalMsg3 = msg3;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GREEN + finalMsg3)));
        }
        String msg4 = switch (phase) {
            case PHASE_4 -> "The Giant Rat unleashes its full might!";
            default -> null;
        };
        if (msg4 != null) {
            final String finalMsg4 = msg4;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GREEN + finalMsg4)));
        }
        String msg5 = switch (phase) {
            case PHASE_5 -> "The Giant Rat is desperate!";
            default -> null;
        };
        if (msg5 != null) {
            final String finalMsg5 = msg5;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GREEN + finalMsg5)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
            case PHASE_3 -> this.getAttribute(Attributes.ARMOR).setBaseValue(13f);
            case PHASE_4 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.32999999999999996f);
            case PHASE_5 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor6_giant_rat", Floor6Boss.class, FLOOR);
    }
}
