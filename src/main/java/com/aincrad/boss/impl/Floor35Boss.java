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
 * Floor 35 boss — Obsidian Golem.
 * A lava-themed standard boss with molten slams, fire waves, and volcanic fury.
 */
public class Floor35Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor35_boss"));

    public static final EntityType<Floor35Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor35Boss::new, MobCategory.MONSTER)
            .sized(1.6f, 2.8f)
            .build(TYPE_KEY));

    private static final int FLOOR = 35;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor35Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Molten Slam — AoE lava damage
        patterns.add(BossPatternFactory.createAoeSlam("MoltenSlam", BASE_DAMAGE * 1.6f, 6.5f, 80));
        patternCooldowns.add(0);

        // Obsidian Fist — heavy melee strike
        patterns.add(BossPatternFactory.createHeavyStrike("ObsidianFist", BASE_DAMAGE * 1.7f, 85));
        patternCooldowns.add(0);

        // Volcanic Wave — expanding AoE
        patterns.add(BossPatternFactory.createAoeWave("VolcanicWave", BASE_DAMAGE * 1.3f, 120));
        patternCooldowns.add(0);

        // Magma Shield — buff armor
        patterns.add(BossPatternFactory.createBuff("MagmaShield",
            Attributes.ARMOR, 1.35f, 140));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Obsidian Golem";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Obsidian Golem's cracks glow with molten fury!";
            case PHASE_3 -> "The Obsidian Golem's fists drip with lava!";
            case PHASE_4 -> "The Obsidian Golem erupts with volcanic rage!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GOLD + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
            case PHASE_3 -> this.getAttribute(Attributes.ARMOR).setBaseValue(20.0f);
            case PHASE_4 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor35_obsidian_golem", Floor35Boss.class, FLOOR);
    }
}
