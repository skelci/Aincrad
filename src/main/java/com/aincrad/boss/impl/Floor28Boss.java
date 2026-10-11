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
 * Floor 28 boss — Echo Beast.
 * A cave-themed boss with a sound-sensitive hunter. 4 attack patterns.
 */
public class Floor28Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor28_boss"));

    public static final EntityType<Floor28Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor28Boss::new, MobCategory.MONSTER)
            .sized(1.2f, 1.4f)
            .build(TYPE_KEY));

    private static final int FLOOR = 28;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor28Boss(EntityType<? extends Monster> entityType, Level level) {
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
        // Echo Beast Heavy Strike
        patterns.add(BossPatternFactory.createHeavyStrike("HeavyStrike", BASE_DAMAGE * 1.4f, 70));
        patternCooldowns.add(0);
        // Echo Beast AoE Slam
        patterns.add(BossPatternFactory.createAoeSlam("AoeSlam", BASE_DAMAGE * 1.5f, 6.0f, 85));
        patternCooldowns.add(0);
        // Echo Beast Charge
        patterns.add(BossPatternFactory.createCharge("Charge", BASE_DAMAGE * 1.3f, 12.0f, 95));
        patternCooldowns.add(0);

    }

    @Override
    public String getBossName() {
        return "Echo Beast";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;

        String msg2 = switch (phase) {
            case PHASE_2 -> "The Echo Beast's power increases!";
            default -> null;
        };
        if (msg2 != null) {
            final String finalMsg2 = msg2;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GRAY + finalMsg2)));
        }
        String msg3 = switch (phase) {
            case PHASE_3 -> "The Echo Beast enters a frenzy!";
            default -> null;
        };
        if (msg3 != null) {
            final String finalMsg3 = msg3;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GRAY + finalMsg3)));
        }
        String msg4 = switch (phase) {
            case PHASE_4 -> "The Echo Beast unleashes its full might!";
            default -> null;
        };
        if (msg4 != null) {
            final String finalMsg4 = msg4;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GRAY + finalMsg4)));
        }
        String msg5 = switch (phase) {
            case PHASE_5 -> "The Echo Beast is desperate!";
            default -> null;
        };
        if (msg5 != null) {
            final String finalMsg5 = msg5;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GRAY + finalMsg5)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
            case PHASE_3 -> this.getAttribute(Attributes.ARMOR).setBaseValue(24f);
            case PHASE_4 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.44f);
            case PHASE_5 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor28_echo_beast", Floor28Boss.class, FLOOR);
    }
}
