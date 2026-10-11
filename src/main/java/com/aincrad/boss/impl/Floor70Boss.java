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
 * Floor 70 boss — Bog Overlord.
 * A swamp-themed tier boss with mire grasp, toxic cloud, and bog surge.
 */
public class Floor70Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor70_boss"));

    public static final EntityType<Floor70Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor70Boss::new, MobCategory.MONSTER)
            .sized(2.5f, 3.0f)
            .build(TYPE_KEY));

    private static final int FLOOR = 70;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor70Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Mire Grasp — heavy strike that pulls players in
        patterns.add(BossPatternFactory.createHeavyStrike("MireGrasp", BASE_DAMAGE * 1.7f, 85));
        patternCooldowns.add(0);

        // Toxic Cloud — debuff reducing player movement speed
        patterns.add(BossPatternFactory.createDebuff("ToxicCloud", BASE_DAMAGE * 0.6f, 15, 105));
        patternCooldowns.add(0);

        // Bog Surge — massive AoE slam
        patterns.add(BossPatternFactory.createAoeSlam("BogSurge", BASE_DAMAGE * 2.0f, 10.0f, 130));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Bog Overlord";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        if (this.level().isClientSide()) return;
        String msg = switch (phase) {
            case PHASE_2 -> "The Bog Overlord rises from the mire!";
            case PHASE_3 -> "Toxic fumes billow from the Bog Overlord!";
            case PHASE_4 -> "The Bog Overlord enters its swamp fury!";
            default -> null;
        };
        if (msg != null) {
            final String finalMsg = msg;
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(ChatFormatting.DARK_GREEN + finalMsg)));
        }

        switch (phase) {
            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
            case PHASE_3 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4f);
            case PHASE_4 -> this.getAttribute(Attributes.ARMOR).setBaseValue(22.0f);
            default -> {}
        }
    }

    static {
        BossRegistry.register("floor70_bog_overlord", Floor70Boss.class, FLOOR);
    }
}
