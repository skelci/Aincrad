package com.aincrad.boss.patterns;

import com.aincrad.boss.BossAttackPattern;
import com.aincrad.boss.BossEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;

/**
 * Factory for creating pre-configured attack patterns.
 * Provides static factory methods for each pattern type, reducing boilerplate
 * in individual boss implementations.
 */
public final class BossPatternFactory {

    private BossPatternFactory() {
    }

    // ------------------------------------------------------------------
    // Melee patterns
    // ------------------------------------------------------------------

    /**
     * Creates a basic melee swipe pattern.
     *
     * @param name     pattern name
     * @param damage   base damage
     * @param radius   damage radius in blocks
     * @param cooldown cooldown in ticks
     */
    public static BossAttackPattern createMeleeSwipe(String name, float damage, float radius, int cooldown) {
        return new BossAttackPattern(name, 20, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, radius, damage);
            }
        };
    }

    /**
     * Creates a heavy single-target melee pattern.
     */
    public static BossAttackPattern createHeavyStrike(String name, float damage, int cooldown) {
        return new BossAttackPattern(name, 40, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 3.0f, damage);
            }
        };
    }

    // ------------------------------------------------------------------
    // AoE patterns
    // ------------------------------------------------------------------

    /**
     * Creates an AoE slam pattern.
     */
    public static BossAttackPattern createAoeSlam(String name, float damage, float radius, int cooldown) {
        return new BossAttackPattern(name, 50, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, radius, damage);
            }
        };
    }

    /**
     * Creates an expanding AoE wave pattern.
     */
    public static BossAttackPattern createAoeWave(String name, float damage, int cooldown) {
        return new BossAttackPattern(name, 45, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                damagePlayersInRadius(boss, level, 8.0f, damage);
            }
        };
    }

    // ------------------------------------------------------------------
    // Projectile patterns
    // ------------------------------------------------------------------

    /**
     * Creates a ranged projectile damage pattern.
     */
    public static BossAttackPattern createProjectile(String name, float damage, float range, int cooldown) {
        return new BossAttackPattern(name, 30, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < range)
                    .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), damage));
            }
        };
    }

    // ------------------------------------------------------------------
    // Summon patterns
    // ------------------------------------------------------------------

    /**
     * Creates a summon pattern (damage pulse simulating add spawn).
     */
    public static BossAttackPattern createSummon(String name, int count, float damage, int cooldown) {
        return new BossAttackPattern(name, 60, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                for (int i = 0; i < count; i++) {
                    damagePlayersInRadius(boss, level, 2.0f, damage);
                }
            }
        };
    }

    // ------------------------------------------------------------------
    // Buff patterns
    // ------------------------------------------------------------------

    /**
     * Creates a self-buff pattern that increases an attribute.
     */
    public static BossAttackPattern createBuff(String name, Holder<Attribute> attr, float multiplier, int cooldown) {
        return new BossAttackPattern(name, 30, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                double base = boss.getAttribute(attr).getBaseValue();
                boss.getAttribute(attr).setBaseValue(base * multiplier);
            }
        };
    }

    // ------------------------------------------------------------------
    // Debuff patterns
    // ------------------------------------------------------------------

    /**
     * Creates a debuff pattern that reduces player attributes.
     */
    public static BossAttackPattern createDebuff(String name, float reduction, int duration, int cooldown) {
        return new BossAttackPattern(name, 40, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < 8.0f)
                    .forEach(p -> {
                        p.hurt(p.damageSources().mobAttack(boss), reduction);
                    });
            }
        };
    }

    // ------------------------------------------------------------------
    // Charge patterns
    // ------------------------------------------------------------------

    /**
     * Creates a charge pattern with knockback.
     */
    public static BossAttackPattern createCharge(String name, float damage, float distance, int cooldown) {
        return new BossAttackPattern(name, 50, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                level.players().stream()
                    .filter(p -> p.distanceTo(boss) < distance)
                    .forEach(p -> {
                        p.hurt(p.damageSources().mobAttack(boss), damage);
                        p.push(0.8, 0.4, 0.8);
                    });
            }
        };
    }

    // ------------------------------------------------------------------
    // Teleport patterns
    // ------------------------------------------------------------------

    /**
     * Creates a teleport pattern that repositions the boss near a random player.
     */
    public static BossAttackPattern createTeleport(String name, int cooldown) {
        return new BossAttackPattern(name, 20, cooldown) {
            @Override
            public void execute(BossEntity boss, Level level) {
                if (!level.players().isEmpty()) {
                    var target = level.players().get(level.getRandom().nextInt(level.players().size()));
                    boss.teleportTo(target.getX(), target.getY(), target.getZ());
                }
            }
        };
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    /**
     * Damages all players within a radius of the boss.
     */
    private static void damagePlayersInRadius(BossEntity boss, Level level, float radius, float damage) {
        level.players().stream()
            .filter(p -> p.blockPosition().distSqr(boss.blockPosition()) <= radius * radius)
            .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), damage));
    }
}
