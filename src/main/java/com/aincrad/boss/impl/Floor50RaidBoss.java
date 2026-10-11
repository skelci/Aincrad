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
 * Floor 50 raid boss — Void Emperor.
 * The final boss with void orbs, teleportation, and add summoning. 5 attack patterns.
 */
public class Floor50RaidBoss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor50_raid_boss"));

    public static final EntityType<Floor50RaidBoss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor50RaidBoss::new, MobCategory.MONSTER)
            .sized(1.2f, 2.8f)
            .build(TYPE_KEY));

    private static final int FLOOR_LEVEL = 50;
    private static final float BASE_HP = 2500.0f;
    private static final float BASE_DAMAGE = 35.0f;

    public Floor50RaidBoss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
        this.setBossTier(10);
    }

    @Override
    protected void initPatterns() {
        // Void orb — ranged projectile damage
        patterns.add(new BossAttackPattern("VoidOrb", 30, 60) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 20.0f)
                    .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), BASE_DAMAGE * 0.7f));
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "Void Emperor conjures a void orb!")));
            }
        });
        patternCooldowns.add(0);

        // Teleport — reposition near a random player
        patterns.add(new BossAttackPattern("Teleport", 20, 80) {
            @Override
            public void execute(BossEntity boss, Level level) {
                if (!level.players().isEmpty()) {
                    var target = level.players().get(level.getRandom().nextInt(level.players().size()));
                    boss.teleportTo(target.getX(), target.getY(), target.getZ());
                }
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "Void Emperor fades into the void...")));
            }
        });
        patternCooldowns.add(0);

        // Summon adds — spawn minions
        patterns.add(new BossAttackPattern("SummonAdds", 60, 200) {
            @Override
            public void execute(BossEntity boss, Level level) {
                // Summon 2 Void Spawn minions near the boss
                for (int i = 0; i < 2; i++) {
                    double angle = level.getRandom().nextDouble() * Math.PI * 2;
                    double dist = 3.0 + level.getRandom().nextDouble() * 3.0;
                    double sx = boss.getX() + Math.cos(angle) * dist;
                    double sz = boss.getZ() + Math.sin(angle) * dist;
                    // Note: Actual minion spawning would use EntityType creation
                    // For now, we simulate with a damage pulse
                    damagePlayersInRadius(boss, level, 2.0f, BASE_DAMAGE * 0.3f);
                }
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "Void Emperor calls its minions!")));
            }
        });
        patternCooldowns.add(0);

        // Void wave — expanding AoE
        patterns.add(new BossAttackPattern("VoidWave", 45, 100) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 10.0f, BASE_DAMAGE * 1.0f);
            }
        });
        patternCooldowns.add(0);

        // Obliterate — devastating single-target
        patterns.add(new BossAttackPattern("Obliterate", 70, 180) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 6.0f)
                    .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), BASE_DAMAGE * 2.0f));
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_RED + "Void Emperor channels oblivion!")));
            }
        });
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Void Emperor";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.1f);
                broadcastPhaseMessage("Void Emperor's power grows!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5f);
                broadcastPhaseMessage("Void Emperor phases between dimensions!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(25.0f);
                broadcastPhaseMessage("Void Emperor's void shield activates!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.4f);
                broadcastPhaseMessage("Void Emperor unleashes its true form!");
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
