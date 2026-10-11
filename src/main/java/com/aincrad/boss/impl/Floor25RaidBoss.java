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
 * Floor 25 raid boss — Dragon Lord.
 * A fearsome dragon with fire breath, tail sweep, and wing gust. 5 attack patterns.
 */
public class Floor25RaidBoss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor25_raid_boss"));

    public static final EntityType<Floor25RaidBoss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor25RaidBoss::new, MobCategory.MONSTER)
            .sized(2.0f, 2.5f)
            .build(TYPE_KEY));

    private static final int FLOOR_LEVEL = 25;
    private static final float BASE_HP = 1000.0f;
    private static final float BASE_DAMAGE = 20.0f;

    public Floor25RaidBoss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
        this.setBossTier(7);
    }

    @Override
    protected void initPatterns() {
        // Fire breath — cone damage
        patterns.add(new BossAttackPattern("FireBreath", 50, 100) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 8.0f, BASE_DAMAGE * 1.2f);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.GOLD + "Dragon Lord inhales deeply...")));
            }
        });
        patternCooldowns.add(0);

        // Tail sweep — behind area damage
        patterns.add(new BossAttackPattern("TailSweep", 35, 70) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 6.0f, BASE_DAMAGE * 0.9f);
            }
        });
        patternCooldowns.add(0);

        // Wing gust — knockback all nearby
        patterns.add(new BossAttackPattern("WingGust", 45, 90) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 10.0f)
                    .forEach(p -> {
                        p.hurt(p.damageSources().mobAttack(boss), BASE_DAMAGE * 0.5f);
                        p.push(1.0, 0.5, 1.0);
                    });
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.GOLD + "Dragon Lord spreads its wings!")));
            }
        });
        patternCooldowns.add(0);

        // Dragon claw — heavy melee
        patterns.add(new BossAttackPattern("DragonClaw", 25, 50) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 4.0f, BASE_DAMAGE * 1.4f);
            }
        });
        patternCooldowns.add(0);

        // Inferno — massive AoE
        patterns.add(new BossAttackPattern("Inferno", 60, 150) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 12.0f, BASE_DAMAGE * 1.8f);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.RED + "Dragon Lord prepares its ultimate attack!")));
            }
        });
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Dragon Lord";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
                broadcastPhaseMessage("Dragon Lord's scales glow red-hot!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45f);
                broadcastPhaseMessage("Dragon Lord takes to the skies!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(20.0f);
                broadcastPhaseMessage("Dragon Lord's hide becomes impenetrable!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.5f);
                broadcastPhaseMessage("Dragon Lord enters its final fury!");
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
}
