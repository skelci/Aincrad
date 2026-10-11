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
 * Floor 15 boss — Warg Alpha.
 * A forest-themed standard boss with wolf pack tactics and savage strikes.
 */
public class Floor15Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor15_boss"));

    public static final EntityType<Floor15Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor15Boss::new, MobCategory.MONSTER)
            .sized(1.2f, 1.2f)
            .build(TYPE_KEY));

    private static final int FLOOR = 15;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor15Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Savage Bite — heavy melee strike
        patterns.add(BossPatternFactory.createHeavyStrike("SavageBite", BASE_DAMAGE * 1.4f, 70));
        patternCooldowns.add(0);

        // Feral Charge — knockback charge
        patterns.add(BossPatternFactory.createCharge("FeralCharge", BASE_DAMAGE * 1.2f, 10.0f, 90));
        patternCooldowns.add(0);

        // Alpha Howl — buff damage
        patterns.add(BossPatternFactory.createBuff("AlphaHowl",
            Attributes.ATTACK_DAMAGE, 1.25f, 110));
        patternCooldowns.add(0);

        // Pack Hunt — summon support
        patterns.add(BossPatternFactory.createSummon("PackHunt", 2, BASE_DAMAGE * 0.3f, 160));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Warg Alpha";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Warg Alpha's eyes glow with primal fury!";
            case PHASE_3 -> "The Warg Alpha howls, calling the pack!";
            case PHASE_4 -> "The Warg Alpha enters a savage rage!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GREEN + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5f);
            case PHASE_3 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
            case PHASE_4 -> this.getAttribute(Attributes.ARMOR).setBaseValue(12.0f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor15_warg_alpha", Floor15Boss.class, FLOOR);
    }
}
