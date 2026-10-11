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
 * Floor 60 boss — Pharaoh's Wrath.
 * A desert-themed tier boss with curse of the sands, mummy's touch, and tomb guardian.
 */
public class Floor60Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor60_boss"));

    public static final EntityType<Floor60Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor60Boss::new, MobCategory.MONSTER)
            .sized(1.0f, 2.5f)
            .build(TYPE_KEY));

    private static final int FLOOR = 60;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor60Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Curse of the Sands — debuff reducing player movement
        patterns.add(BossPatternFactory.createDebuff("CurseOfTheSands", BASE_DAMAGE * 0.7f, 12, 100));
        patternCooldowns.add(0);

        // Mummy's Touch — heavy single-target strike
        patterns.add(BossPatternFactory.createHeavyStrike("MummysTouch", BASE_DAMAGE * 1.8f, 95));
        patternCooldowns.add(0);

        // Tomb Guardian — summon undead minions
        patterns.add(BossPatternFactory.createSummon("TombGuardian", 2, BASE_DAMAGE * 0.35f, 160));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Pharaoh's Wrath";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Pharaoh's Wrath radiates ancient power!";
            case PHASE_3 -> "The sands obey the Pharaoh's command!";
            case PHASE_4 -> "The Pharaoh's Wrath unleashes its curse!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.GOLD + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
            case PHASE_3 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45f);
            case PHASE_4 -> this.getAttribute(Attributes.ARMOR).setBaseValue(20.0f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor60_pharaohs_wrath", Floor60Boss.class, FLOOR);
    }
}
