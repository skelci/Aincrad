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
 * Floor 95 standard boss — Ender Spider.
 * Void-themed standard boss with void bites, web snares, and shadow teleport.
 */
public class Floor95Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor95_boss"));

    public static final EntityType<Floor95Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor95Boss::new, MobCategory.MONSTER)
            .sized(1.3f, 1.8f)
            .build(TYPE_KEY));

    private static final int FLOOR = 95;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor95Boss(EntityType<? extends Monster> entityType, Level level) {
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

        // Custom: Void Bite — heavy single target
        patterns.add(BossPatternFactory.createHeavyStrike("VoidBite", BASE_DAMAGE * 2.0f, 90));
        patternCooldowns.add(0);

        // Custom: Web Snare — debuff
        patterns.add(BossPatternFactory.createDebuff("WebSnare", BASE_DAMAGE * 0.5f, 12, 95));
        patternCooldowns.add(0);

        // Custom: Shadow Teleport — reposition
        patterns.add(BossPatternFactory.createTeleport("ShadowTeleport", 85));
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Ender Spider";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.18f);
                broadcastPhaseMessage("Ender Spider's eyes glow with void energy!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.55f);
                broadcastPhaseMessage("Ender Spider skitters through the shadows!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(22.0f);
                broadcastPhaseMessage("Ender Spider's exoskeleton hardens!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.55f);
                broadcastPhaseMessage("Ender Spider enters its void frenzy!");
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(
                    ChatFormatting.DARK_PURPLE + msg)));
        }
    }

    static {
        BossRegistry.register("floor95_ender_spider", Floor95Boss.class, FLOOR);
    }
}
