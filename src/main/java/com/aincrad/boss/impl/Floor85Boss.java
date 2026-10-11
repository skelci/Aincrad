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
 * Floor 85 standard boss — Storm Elemental.
 * Sky-themed standard boss with lightning strikes, wind gusts, and chain lightning.
 */
public class Floor85Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor85_boss"));

    public static final EntityType<Floor85Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor85Boss::new, MobCategory.MONSTER)
            .sized(1.0f, 2.5f)
            .build(TYPE_KEY));

    private static final int FLOOR = 85;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor85Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Standard patterns from library
        for (BossAttackPattern p : BossAttackLibrary.getStandardPatterns(FLOOR)) {
            patterns.add(p);
            patternCooldowns.add(0);
        }

        // Custom: Lightning Strike — heavy single target
        patterns.add(BossPatternFactory.createHeavyStrike("LightningStrike", BASE_DAMAGE * 1.8f, 85));
        patternCooldowns.add(0);

        // Custom: Wind Gust — charge with knockback
        patterns.add(BossPatternFactory.createCharge("WindGust", BASE_DAMAGE * 1.2f, 10.0f, 90));
        patternCooldowns.add(0);

        // Custom: Chain Lightning — AoE wave
        patterns.add(BossPatternFactory.createAoeWave("ChainLightning", BASE_DAMAGE * 1.3f, 110));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Storm Elemental";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
                broadcastPhaseMessage("Storm Elemental crackles with lightning!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5f);
                broadcastPhaseMessage("Storm Elemental rides the winds!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(18.0f);
                broadcastPhaseMessage("Storm Elemental's body solidifies into a tempest!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.5f);
                broadcastPhaseMessage("Storm Elemental unleashes the full power of the storm!");
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(
                    ChatFormatting.YELLOW + msg)));
        }
    }

    static {
        BossRegistry.register("floor85_storm_elemental", Floor85Boss.class, FLOOR);
    }
}
