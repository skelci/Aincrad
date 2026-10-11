package com.aincrad.integration;

import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.ArmorPieceRegistry;
import com.aincrad.boss.BossRegistry;
import com.aincrad.crystal.CrystalTier;
import com.aincrad.crystal.CrystalType;
import com.aincrad.dungeon.layout.DungeonLayoutRegistry;
import com.aincrad.skill.SkillRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Audits all registries in the Aincrad mod and reports missing entries.
 * <p>
 * Run this class to verify that all bosses, skills, dungeon layouts,
 * armor sets, and crystal types are properly registered.
 * </p>
 */
public final class RegistryAudit {

    private static final String SRC_ROOT = "src/main/java/com/aincrad";

    private RegistryAudit() {}

    public static void main(String[] args) throws IOException {
        System.out.println("=== Aincrad Registry Audit ===\n");

        boolean allPassed = true;

        allPassed &= auditBosses();
        allPassed &= auditSkills();
        allPassed &= auditDungeonLayouts();
        allPassed &= auditArmorSets();
        allPassed &= auditCrystalTypes();
        allPassed &= auditCrystalTiers();

        System.out.println("\n=== Audit Summary ===");
        if (allPassed) {
            System.out.println("ALL REGISTRIES PASSED");
        } else {
            System.out.println("SOME REGISTRIES HAVE ISSUES — see above");
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // Boss audit
    // ------------------------------------------------------------------

    private static boolean auditBosses() throws IOException {
        System.out.println("--- Boss Registry ---");

        // Trigger registration
        com.aincrad.AincradMod mod = new com.aincrad.AincradMod();
        mod.onInitialize();

        int registered = BossRegistry.size();
        int expected = countFiles(SRC_ROOT + "/boss/impl/", "Boss.java");

        System.out.println("  Registered: " + registered);
        System.out.println("  Expected (impl files): " + expected);

        if (registered < expected) {
            System.out.println("  MISSING: " + (expected - registered) + " bosses not registered");
            List<String> missing = findMissingBosses();
            for (String m : missing) {
                System.out.println("    - " + m);
            }
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    private static List<String> findMissingBosses() throws IOException {
        List<String> missing = new ArrayList<>();
        File dir = new File(SRC_ROOT + "/boss/impl/");
        if (!dir.exists()) return missing;

        File[] files = dir.listFiles((d, name) -> name.endsWith("Boss.java"));
        if (files == null) return missing;

        for (File f : files) {
            String className = f.getName().replace(".java", "");
            // Check if any registered boss has this class name
            boolean found = BossRegistry.getAll().stream()
                .anyMatch(e -> e.name().equals(className));
            if (!found) {
                missing.add(className);
            }
        }
        return missing;
    }

    // ------------------------------------------------------------------
    // Skill audit
    // ------------------------------------------------------------------

    private static boolean auditSkills() throws IOException {
        System.out.println("--- Skill Registry ---");

        int registered = SkillRegistry.size();
        int expected = countFiles(SRC_ROOT + "/skill/impl/", ".java");

        System.out.println("  Registered: " + registered);
        System.out.println("  Expected (impl files): " + expected);

        if (registered < expected) {
            System.out.println("  MISSING: " + (expected - registered) + " skills not registered");
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // Dungeon layout audit
    // ------------------------------------------------------------------

    private static boolean auditDungeonLayouts() throws IOException {
        System.out.println("--- Dungeon Layout Registry ---");

        int registered = DungeonLayoutRegistry.size();
        int expected = countFiles(SRC_ROOT + "/dungeon/layout/", "Layout.java");

        System.out.println("  Registered: " + registered);
        System.out.println("  Expected (layout files): " + expected);

        if (registered < expected) {
            System.out.println("  MISSING: " + (expected - registered) + " layouts not registered");
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // Armor set audit
    // ------------------------------------------------------------------

    private static boolean auditArmorSets() {
        System.out.println("--- Armor Sets ---");

        ArmorSet[] sets = ArmorSet.values();
        System.out.println("  Defined: " + sets.length);

        for (ArmorSet set : sets) {
            System.out.println("    " + set.name() + " (tier " + set.getTier() + ")");
        }

        if (sets.length < 10) {
            System.out.println("  MISSING: expected 10 armor sets, found " + sets.length);
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // Crystal type audit
    // ------------------------------------------------------------------

    private static boolean auditCrystalTypes() {
        System.out.println("--- Crystal Types ---");

        CrystalType[] types = CrystalType.values();
        System.out.println("  Defined: " + types.length);

        for (CrystalType type : types) {
            System.out.println("    " + type.name() + " (" + type.getDisplayName() + ")");
        }

        if (types.length < 6) {
            System.out.println("  MISSING: expected 6 crystal types, found " + types.length);
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // Crystal tier audit
    // ------------------------------------------------------------------

    private static boolean auditCrystalTiers() {
        System.out.println("--- Crystal Tiers ---");

        CrystalTier[] tiers = CrystalTier.values();
        System.out.println("  Defined: " + tiers.length);

        for (CrystalTier tier : tiers) {
            System.out.println("    " + tier.name() + " (x" + tier.getMultiplier() + ")");
        }

        if (tiers.length < 5) {
            System.out.println("  MISSING: expected 5 crystal tiers, found " + tiers.length);
            return false;
        }

        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    private static int countFiles(String dirPath, String suffix) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) return 0;

        File[] files = dir.listFiles((d, name) -> name.endsWith(suffix));
        return files == null ? 0 : files.length;
    }
}
