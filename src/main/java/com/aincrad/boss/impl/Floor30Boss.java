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
 * Floor 30 boss — Abyssal Spider.
 * A cave-themed tier boss with web snares, venom bites, and dark ambushes.
 */
public class Floor30Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor30_boss"));

    public static final EntityType<Floor30Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor30Boss::new, MobCategory.MONSTER)
            .sized(2.0f, 1.5f)
            .build(TYPE_KEY));

    private static final int FLOOR = 30;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor30Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Venom Bite — heavy melee with poison
        patterns.add(BossPatternFactory.createHeavyStrike("VenomBite", BASE_DAMAGE * 1.5f, 75));
        patternCooldowns.add(0);

        // Web Snare — AoE slam trapping players
        patterns.add(BossPatternFactory.createAoeSlam("WebSnare", BASE_DAMAGE * 1.2f, 8.0f, 95));
        patternCooldowns.add(0);

        // Dark Ambush — teleport behind prey
        patterns.add(BossPatternFactory.createTeleport("DarkAmbush", 100));
        patternCooldowns.add(0);

        // Brood Call — summon spiderlings
        patterns.add(BossPatternFactory.createSummon("BroodCall", 3, BASE_DAMAGE * 0.2f, 180));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Abyssal Spider";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Abyssal Spider's eyes gleam in the darkness!";
            case PHASE_3 -> "The Abyssal Spider weaves a web of shadows!";
            case PHASE_4 -> "The Abyssal Spider skitters with unnatural speed!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_PURPLE + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.55f);
            case PHASE_3 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.25f);
            case PHASE_4 -> this.getAttribute(Attributes.ARMOR).setBaseValue(18.0f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor30_abyssal_spider", Floor30Boss.class, FLOOR);
    }
}
