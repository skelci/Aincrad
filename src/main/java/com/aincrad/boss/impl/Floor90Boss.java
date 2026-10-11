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
 * Floor 90 tier boss — Storm Lord.
 * Sky-themed tier boss with thunderous strikes, tempest shields, and hurricane.
 */
public class Floor90Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor90_boss"));

    public static final EntityType<Floor90Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor90Boss::new, MobCategory.MONSTER)
            .sized(1.2f, 2.8f)
            .build(TYPE_KEY));

    private static final int FLOOR = 90;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor90Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Custom: Thunderous Strike — heavy AoE slam
        patterns.add(BossPatternFactory.createAoeSlam("ThunderousStrike", BASE_DAMAGE * 2.2f, 8.0f, 100));
        patternCooldowns.add(0);

        // Custom: Tempest Shield — buff armor
        patterns.add(BossPatternFactory.createBuff("TempestShield", Attributes.ARMOR, 1.5f, 140));
        patternCooldowns.add(0);

        // Custom: Hurricane — summon adds
        patterns.add(BossPatternFactory.createSummon("Hurricane", 2, BASE_DAMAGE * 0.4f, 160));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Storm Lord";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
                broadcastPhaseMessage("Storm Lord's thunder grows louder!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.55f);
                broadcastPhaseMessage("Storm Lord commands the winds!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(28.0f);
                broadcastPhaseMessage("Storm Lord is encased in a tempest shield!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.6f);
                broadcastPhaseMessage("Storm Lord unleashes the ultimate tempest!");
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(
                    ChatFormatting.GOLD + msg)));
        }
    }

    static {
        BossRegistry.register("floor90_storm_lord", Floor90Boss.class, FLOOR);
    }
}
