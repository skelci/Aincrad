package com.aincrad.boss.impl;

import com.aincrad.boss.BossAttackPattern;
import com.aincrad.boss.BossEntity;
import com.aincrad.boss.BossPhase;
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
 * Floor 5 boss — Stone Golem.
 * A tanky boss with ground slam and rock throw attacks. 4 attack patterns.
 */
public class Floor5Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor5_boss"));

    public static final EntityType<Floor5Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor5Boss::new, MobCategory.MONSTER)
            .sized(1.5f, 2.5f)
            .build(TYPE_KEY));

    private static final int FLOOR_LEVEL = 5;
    private static final float BASE_HP = 250.0f;
    private static final float BASE_DAMAGE = 8.0f;

    public Floor5Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
        this.setBossTier(2);
    }

    @Override
    protected void initPatterns() {
        // Ground slam — AoE around boss
        patterns.add(new BossAttackPattern("GroundSlam", 40, 80) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 5.0f, BASE_DAMAGE);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_GREEN + "Stone Golem raises its fists!")));
            }
        });
        patternCooldowns.add(0);

        // Rock throw — ranged damage
        patterns.add(new BossAttackPattern("RockThrow", 30, 60) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 15.0f)
                    .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), BASE_DAMAGE * 0.8f));
            }
        });
        patternCooldowns.add(0);

        // Boulder roll — charge attack
        patterns.add(new BossAttackPattern("BoulderRoll", 50, 100) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 4.0f, BASE_DAMAGE * 1.2f);
            }
        });
        patternCooldowns.add(0);

        // Stone skin — damage reduction
        patterns.add(new BossAttackPattern("StoneSkin", 20, 150) {
            @Override
            public void execute(BossEntity boss, Level level) {
                // Reduce incoming damage by setting armor
                boss.getAttribute(Attributes.ARMOR).setBaseValue(20.0f);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.GREEN + "Stone Golem hardens its skin!")));
            }
        });
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Stone Golem";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(10.0f);
                broadcastPhaseMessage("Stone Golem's armor thickens!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
                broadcastPhaseMessage("Stone Golem cracks with rage!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4f);
                broadcastPhaseMessage("Stone Golem enters berserk mode!");
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(
                    ChatFormatting.DARK_GREEN + msg)));
        }
    }
}
