package com.aincrad.boss.patterns;

import com.aincrad.boss.BossAttackPattern;
import com.aincrad.boss.util.BossStatScaler;

import java.util.ArrayList;
import java.util.List;

/**
 * Pre-built attack pattern collections for different boss types.
 * Provides standard, raid, and enrage pattern sets that individual
 * bosses can use directly or extend.
 */
public final class BossAttackLibrary {

    private BossAttackLibrary() {
    }

    /**
     * Returns a standard pattern set for a regular floor boss.
     * Includes: melee swipe, AoE slam, projectile, and a buff.
     *
     * @param floor the floor number (used for stat scaling)
     * @return list of pre-configured patterns
     */
    public static List<BossAttackPattern> getStandardPatterns(int floor) {
        float damage = BossStatScaler.scaledDamage(floor);
        List<BossAttackPattern> patterns = new ArrayList<>();

        patterns.add(BossPatternFactory.createMeleeSwipe("MeleeSwipe", damage, 3.0f, 40));
        patterns.add(BossPatternFactory.createAoeSlam("AoeSlam", damage * 1.5f, 5.0f, 80));
        patterns.add(BossPatternFactory.createProjectile("Projectile", damage * 0.8f, 15.0f, 60));
        patterns.add(BossPatternFactory.createBuff("WarCry", net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 1.2f, 120));

        return patterns;
    }

    /**
     * Returns a raid pattern set for raid bosses (floors 25, 50, 75, 100).
     * Includes: melee, AoE, projectile, summon, buff, debuff, charge, teleport.
     *
     * @param floor the floor number
     * @return list of pre-configured patterns
     */
    public static List<BossAttackPattern> getRaidPatterns(int floor) {
        float damage = BossStatScaler.scaledDamage(floor);
        List<BossAttackPattern> patterns = new ArrayList<>();

        patterns.add(BossPatternFactory.createMeleeSwipe("MeleeSwipe", damage, 4.0f, 35));
        patterns.add(BossPatternFactory.createAoeSlam("AoeSlam", damage * 1.8f, 8.0f, 90));
        patterns.add(BossPatternFactory.createProjectile("Projectile", damage * 1.0f, 20.0f, 50));
        patterns.add(BossPatternFactory.createSummon("SummonAdds", 2, damage * 0.3f, 200));
        patterns.add(BossPatternFactory.createBuff("EnrageBuff", net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 1.3f, 150));
        patterns.add(BossPatternFactory.createDebuff("Debuff", damage * 0.5f, 10, 100));
        patterns.add(BossPatternFactory.createCharge("Charge", damage * 1.5f, 15.0f, 100));
        patterns.add(BossPatternFactory.createTeleport("Teleport", 80));

        return patterns;
    }

    /**
     * Returns enrage-only patterns (desperation attacks).
     *
     * @param floor the floor number
     * @return list of desperation patterns
     */
    public static List<BossAttackPattern> getEnragePatterns(int floor) {
        float damage = BossStatScaler.scaledDamage(floor);
        List<BossAttackPattern> patterns = new ArrayList<>();

        patterns.add(BossPatternFactory.createAoeSlam("Desperation", damage * 2.5f, 15.0f, 400));
        patterns.add(BossPatternFactory.createAoeWave("VoidWave", damage * 2.0f, 300));

        return patterns;
    }

    /**
     * Returns a tier boss pattern set (floors 10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
     * Mid-tier bosses get more patterns than standard but fewer than raid.
     *
     * @param floor the floor number
     * @return list of pre-configured patterns
     */
    public static List<BossAttackPattern> getTierBossPatterns(int floor) {
        float damage = BossStatScaler.scaledDamage(floor);
        List<BossAttackPattern> patterns = new ArrayList<>();

        patterns.add(BossPatternFactory.createMeleeSwipe("MeleeSwipe", damage, 3.5f, 35));
        patterns.add(BossPatternFactory.createAoeSlam("AoeSlam", damage * 1.6f, 6.0f, 75));
        patterns.add(BossPatternFactory.createProjectile("Projectile", damage * 0.9f, 18.0f, 55));
        patterns.add(BossPatternFactory.createSummon("SummonAdds", 1, damage * 0.25f, 180));
        patterns.add(BossPatternFactory.createBuff("PowerUp", net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 1.25f, 130));
        patterns.add(BossPatternFactory.createCharge("Charge", damage * 1.3f, 12.0f, 90));

        return patterns;
    }
}
