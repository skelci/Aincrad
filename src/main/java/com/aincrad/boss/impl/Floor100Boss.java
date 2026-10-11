package com.aincrad.boss.impl;

import com.aincrad.boss.BossAttackPattern;
import com.aincrad.boss.BossEntity;
import com.aincrad.boss.BossPhase;
import com.aincrad.boss.BossRegistry;
import com.aincrad.boss.patterns.BossAttackLibrary;
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

import java.util.List;

/**
 * Floor 100 final boss — Heathcliff / Administrator.
 *
 * The ultimate boss of Aincrad, wielding void manipulation, reality warping,
 * and administrator-level powers. Features 6 phases with unique mechanics,
 * custom attack patterns, and an extended 15-minute enrage timer.
 *
 * <h2>Phase Breakdown</h2>
 * <ul>
 *   <li><b>PHASE_1 (100-80%)</b>: Base raid patterns — melee, AoE, projectile, summon, buff, debuff, charge, teleport</li>
 *   <li><b>PHASE_2 (80-60%)</b>: +15% damage, void energy intensifies</li>
 *   <li><b>PHASE_3 (60-40%)</b>: +Speed boost, reality warping</li>
 *   <li><b>PHASE_4 (40-20%)</b>: +30 armor, void shield activates</li>
 *   <li><b>PHASE_5 (20-5%)</b>: +30% damage, true power unleashed</li>
 *   <li><b>ENRAGE (5-0%)</b>: +50% damage, +speed, +40 armor, enrage patterns + TimeLock</li>
 * </ul>
 *
 * <h2>Custom Patterns</h2>
 * <ul>
 *   <li><b>VoidCollapse</b>: Massive AoE (12 block radius, 2x damage)</li>
 *   <li><b>Administrator's Decree</b>: Debuffs all players in range</li>
 *   <li><b>RealityTear</b>: Teleports all players to random positions near the boss</li>
 *   <li><b>VoidShield</b>: Doubles armor</li>
 *   <li><b>AnnihilationBeam</b>: Heavy single-target strike (2.5x damage)</li>
 *   <li><b>SummonVoidSpawn</b>: Summons 3 void spawn (damage pulses)</li>
 *   <li><b>TimeLock</b> (enrage only): Freezes all players (15 block AoE, 1.5x damage)</li>
 * </ul>
 */
public class Floor100Boss extends BossEntity {

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor100_boss"));

    public static final EntityType<Floor100Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor100Boss::new, MobCategory.MONSTER)
            .sized(1.5f, 3.0f)
            .build(TYPE_KEY));

    private static final int FLOOR = 100;
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);
    private static final int ENRAGE_TICKS = 18000; // 15 minutes

    public Floor100Boss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
        this.enrageTicksRemaining = ENRAGE_TICKS;
        this.onPhaseEnter(BossPhase.PHASE_1);
    }

    @Override
    protected void initPatterns() {
        // === Base Raid Patterns (8 patterns from BossAttackLibrary) ===
        List<BossAttackPattern> raidPatterns = BossAttackLibrary.getRaidPatterns(FLOOR);
        for (BossAttackPattern pattern : raidPatterns) {
            patterns.add(pattern);
            patternCooldowns.add(0);
        }

        // === Custom Pattern 1: VoidCollapse ===
        // Massive AoE that damages all players in a 12-block radius
        patterns.add(new BossAttackPattern("VoidCollapse", 50, 120) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 12.0f, BASE_DAMAGE * 2.0f);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "The void begins to collapse!")));
            }
        });
        patternCooldowns.add(0);

        // === Custom Pattern 2: Administrator's Decree ===
        // Debuffs all players within 8 blocks
        patterns.add(new BossAttackPattern("AdministratorsDecree", 40, 150) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 8.0f)
                    .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), BASE_DAMAGE * 0.5f));
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.RED + "The Administrator issues a decree!")));
            }
        });
        patternCooldowns.add(0);

        // === Custom Pattern 3: RealityTear ===
        // Teleports all players to random positions near the boss
        patterns.add(new BossAttackPattern("RealityTear", 20, 100) {
            @Override
            public void execute(BossEntity boss, Level level) {
                for (var player : level.players()) {
                    double angle = level.getRandom().nextDouble() * Math.PI * 2;
                    double dist = 3.0 + level.getRandom().nextDouble() * 4.0;
                    double px = boss.getX() + Math.cos(angle) * dist;
                    double pz = boss.getZ() + Math.sin(angle) * dist;
                    player.teleportTo(px, boss.getY(), pz);
                }
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "Reality tears around you!")));
            }
        });
        patternCooldowns.add(0);

        // === Custom Pattern 4: VoidShield ===
        // Doubles the boss's armor
        patterns.add(new BossAttackPattern("VoidShield", 30, 200) {
            @Override
            public void execute(BossEntity boss, Level level) {
                double base = boss.getAttribute(Attributes.ARMOR).getBaseValue();
                boss.getAttribute(Attributes.ARMOR).setBaseValue(base * 2.0f);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "Heathcliff's void shield activates!")));
            }
        });
        patternCooldowns.add(0);

        // === Custom Pattern 5: AnnihilationBeam ===
        // Heavy single-target strike (2.5x damage, 3-block radius)
        patterns.add(new BossAttackPattern("AnnihilationBeam", 40, 180) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 3.0f, BASE_DAMAGE * 2.5f);
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_RED + "The Administrator channels annihilation!")));
            }
        });
        patternCooldowns.add(0);

        // === Custom Pattern 6: SummonVoidSpawn ===
        // Summons 3 void spawn (simulated with damage pulses)
        patterns.add(new BossAttackPattern("SummonVoidSpawn", 60, 250) {
            @Override
            public void execute(BossEntity boss, Level level) {
                for (int i = 0; i < 3; i++) {
                    damagePlayersInRadius(boss, level, 2.0f, BASE_DAMAGE * 0.4f);
                }
            }

            @Override
            public void onTelegraphStart(BossEntity boss, Level level) {
                level.players().forEach(p ->
                    p.sendSystemMessage(Component.literal(
                        ChatFormatting.DARK_PURPLE + "Void spawn emerge from the darkness!")));
            }
        });
        patternCooldowns.add(0);
    }

    @Override
    public String getBossName() {
        return "Heathcliff / Administrator";
    }

    @Override
    protected void onPhaseEnter(BossPhase phase) {
        switch (phase) {
            case PHASE_1 -> {
                broadcastPhaseMessage(ChatFormatting.DARK_PURPLE + "Heathcliff, the Administrator, descends upon you!");
                broadcastPhaseMessage(ChatFormatting.GRAY + "Floor 100 — The Final Battle");
            }
            case PHASE_2 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
                broadcastPhaseMessage(ChatFormatting.DARK_PURPLE + "Heathcliff's void energy intensifies!");
            }
            case PHASE_3 -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5f);
                broadcastPhaseMessage(ChatFormatting.DARK_PURPLE + "The Administrator rewrites reality around you!");
            }
            case PHASE_4 -> {
                this.getAttribute(Attributes.ARMOR).setBaseValue(30.0f);
                broadcastPhaseMessage(ChatFormatting.DARK_PURPLE + "Heathcliff's void shield activates!");
            }
            case PHASE_5 -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
                broadcastPhaseMessage(ChatFormatting.DARK_RED + "The Administrator's true power is unleashed!");
            }
            case ENRAGE -> {
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.5f);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.6f);
                this.getAttribute(Attributes.ARMOR).setBaseValue(40.0f);
                broadcastPhaseMessage(ChatFormatting.DARK_RED + "⚠ HEATHCLIFF HAS ENTERED ADMINISTRATOR MODE ⚠");
                broadcastPhaseMessage(ChatFormatting.RED + "All hope is lost...");

                // Add enrage patterns from the library
                List<BossAttackPattern> enragePatterns = BossAttackLibrary.getEnragePatterns(FLOOR);
                for (BossAttackPattern pattern : enragePatterns) {
                    patterns.add(pattern);
                    patternCooldowns.add(0);
                }

                // Custom enrage pattern: TimeLock
                // Freezes all players in a 15-block radius
                patterns.add(new BossAttackPattern("TimeLock", 45, 60) {
                    @Override
                    public void execute(BossEntity boss, Level level) {
                        damagePlayersInRadius(boss, level, 15.0f, BASE_DAMAGE * 1.5f);
                    }

                    @Override
                    public void onTelegraphStart(BossEntity boss, Level level) {
                        level.players().forEach(p ->
                            p.sendSystemMessage(Component.literal(
                                ChatFormatting.DARK_RED + "Time itself freezes!")));
                    }
                });
                patternCooldowns.add(0);
            }
            default -> {}
        }
    }

    private void broadcastPhaseMessage(String msg) {
        if (!this.level().isClientSide()) {
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal(msg)));
        }
    }

    static {
        BossRegistry.register("heathcliff", Floor100Boss.class, 100);
    }
}
