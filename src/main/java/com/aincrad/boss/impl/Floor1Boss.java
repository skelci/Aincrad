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
 * Floor 1 boss — Goblin King.
 * A weak melee boss with basic attacks. 3 attack patterns.
 */
public class Floor1Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor1_boss"));

    public static final EntityType<Floor1Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor1Boss::new, MobCategory.MONSTER)
            .sized(1.0f, 2.0f)
            .build(TYPE_KEY));

    private static final int FLOOR_LEVEL = 1;
    private static final float BASE_HP = 100.0f;
    private static final float BASE_DAMAGE = 5.0f;

    public Floor1Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }

    @Override
    protected void initPatterns() {
        // Basic melee slash
        patterns.add(new BossAttackPattern("Slash", 20, 40) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 3.0f, BASE_DAMAGE);
            }
        });
        patternCooldowns.add(0);

        // Double strike
        patterns.add(new BossAttackPattern("DoubleStrike", 30, 60) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 2.5f, BASE_DAMAGE * 0.7f);
            }
        });
        patternCooldowns.add(0);

        // War cry — buffs self
        patterns.add(new BossAttackPattern("WarCry", 40, 120) {
            @Override
            public void execute(BossEntity boss, Level level) {
                boss.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.RED + "Goblin King lets out a war cry!")));
            }
        });
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Goblin King";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35f);
                broadcastPhaseMessage("The Goblin King grows angrier!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.5f);
                broadcastPhaseMessage("The Goblin King is enraged!");
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(
                    ChatFormatting.RED + msg)));
        }
    }
}
