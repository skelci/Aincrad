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
 * Floor 10 boss — Dark Knight.
 * A skilled warrior with sword combos and shield bash. 4 attack patterns.
 */
public class Floor10Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor10_boss"));

    public static final EntityType<Floor10Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor10Boss::new, MobCategory.MONSTER)
            .sized(0.8f, 2.0f)
            .build(TYPE_KEY));

    private static final int FLOOR_LEVEL = 10;
    private static final float BASE_HP = 400.0f;
    private static final float BASE_DAMAGE = 12.0f;

    public Floor10Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
        this.setBossTier(3);
    }

    @Override
    protected void initPatterns() {
        // Sword combo — 3 rapid hits
        patterns.add(new BossAttackPattern("SwordCombo", 30, 50) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 3.0f, BASE_DAMAGE * 0.5f);
                damagePlayersInRadius(boss, level, 2.5f, BASE_DAMAGE * 0.4f);
            }
        });
        patternCooldowns.add(0);

        // Shield bash — knockback + damage
        patterns.add(new BossAttackPattern("ShieldBash", 25, 70) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 4.0f)
                    .forEach(p -> {
                        p.hurt(p.damageSources().mobAttack(boss), BASE_DAMAGE * 0.6f);
                        p.push(0.5, 0.3, 0.5);
                    });
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "Dark Knight raises its shield!")));
            }
        });
        patternCooldowns.add(0);

        // Dark slash — heavy single target
        patterns.add(new BossAttackPattern("DarkSlash", 40, 90) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 5.0f)
                    .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), BASE_DAMAGE * 1.5f));
            }
        });
        patternCooldowns.add(0);

        // Iron wall — temporary invulnerability
        patterns.add(new BossAttackPattern("IronWall", 15, 120) {
            @Override
            public void execute(BossEntity boss, Level level) {
                boss.getAttribute(Attributes.ARMOR).setBaseValue(30.0f);
            }
        });
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Dark Knight";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.2f);
                broadcastPhaseMessage("Dark Knight's blade glows with dark energy!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4f);
                broadcastPhaseMessage("Dark Knight quickens its stance!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(15.0f);
                broadcastPhaseMessage("Dark Knight unleashes its full power!");
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
}
