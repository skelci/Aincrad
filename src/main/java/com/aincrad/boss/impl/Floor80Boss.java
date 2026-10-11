package com.aincrad.boss.impl;

import com.aincrad.boss.BossAttackPattern;
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
 * Floor 80 tier boss — Kraken.
 * Ocean-themed tier boss with tentacle slams, ink clouds, and whirlpool.
 */
public class Floor80Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor80_boss"));

    public static final EntityType<Floor80Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor80Boss::new, MobCategory.MONSTER)
            .sized(2.0f, 3.5f)
            .build(TYPE_KEY));

    private static final int FLOOR = 80;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor80Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Tier boss patterns from library
        for (BossAttackPattern p : BossAttackLibrary.getTierBossPatterns(FLOOR)) {
            patterns.add(p);
            patternCooldowns.add(0);
        }

        // Custom: Tentacle Slam — heavy AoE
        patterns.add(BossPatternFactory.createAoeSlam("TentacleSlam", BASE_DAMAGE * 2.0f, 7.0f, 95));
        patternCooldowns.add(0);

        // Custom: Ink Cloud — projectile debuff
        patterns.add(BossPatternFactory.createProjectile("InkCloud", BASE_DAMAGE * 1.0f, 18.0f, 70));
        patternCooldowns.add(0);

        // Custom: Whirlpool — summon adds
        patterns.add(BossPatternFactory.createSummon("Whirlpool", 3, BASE_DAMAGE * 0.35f, 180));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Kraken";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
                broadcastPhaseMessage("Kraken's tentacles thrash violently!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.55f);
                broadcastPhaseMessage("Kraken surges forward with primal fury!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(25.0f);
                broadcastPhaseMessage("Kraken's skin hardens like abyssal stone!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.6f);
                broadcastPhaseMessage("Kraken unleashes its devastating final form!");
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(
                    ChatFormatting.DARK_BLUE + msg)));
        }
    }

    static {
        BossRegistry.register("floor80_kraken", Floor80Boss.class, FLOOR);
    }
}
