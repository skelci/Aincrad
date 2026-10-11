package com.aincrad.integration;

import com.aincrad.boss.BossRegistry;
import com.aincrad.skill.SkillRegistry;
import com.aincrad.dungeon.layout.DungeonLayoutRegistry;

/**
 * Verifies that all entities are properly registered at runtime.
 * Run this after mod initialization to confirm registration counts.
 */
public final class RegistrationVerification {

    private RegistrationVerification() {
        // Utility class
    }

    /**
     * Verifies all registrations and prints results.
     *
     * @return true if all registrations pass verification
     */
    public static boolean verifyAll() {
        boolean bossesOk = verifyBosses();
        boolean skillsOk = verifySkills();
        boolean layoutsOk = verifyLayouts();

        System.out.println("=== Registration Verification ===");
        System.out.println("Bosses: " + (bossesOk ? "PASS" : "FAIL") + " (" + BossRegistry.size() + "/100)");
        System.out.println("Skills: " + (skillsOk ? "PASS" : "FAIL") + " (" + SkillRegistry.size() + "/90)");
        System.out.println("Layouts: " + (layoutsOk ? "PASS" : "FAIL") + " (" + DungeonLayoutRegistry.size() + "/75)");
        System.out.println("=================================");

        return bossesOk && skillsOk && layoutsOk;
    }

    /**
     * Verifies all 100 bosses are registered.
     */
    public static boolean verifyBosses() {
        int expected = 100;
        int actual = BossRegistry.size();
        if (actual != expected) {
            System.err.println("Boss registration mismatch: expected " + expected + ", got " + actual);
            return false;
        }
        return true;
    }

    /**
     * Verifies all 90 skills are registered.
     */
    public static boolean verifySkills() {
        int expected = 90;
        int actual = SkillRegistry.size();
        if (actual != expected) {
            System.err.println("Skill registration mismatch: expected " + expected + ", got " + actual);
            return false;
        }
        return true;
    }

    /**
     * Verifies all 75 dungeon layouts are registered.
     */
    public static boolean verifyLayouts() {
        int expected = 75;
        int actual = DungeonLayoutRegistry.size();
        if (actual != expected) {
            System.err.println("Layout registration mismatch: expected " + expected + ", got " + actual);
            return false;
        }
        return true;
    }
}
