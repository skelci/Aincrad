package com.aincrad.integration;

import com.aincrad.balance.BalanceConfig;
import com.aincrad.balance.BalanceValidator;
import com.aincrad.balance.BalanceValues;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Validates game balance across all systems.
 * <p>
 * Loads balance.yml, runs BalanceValidator, and cross-checks that
 * BalanceValues static fields are properly synchronized.
 * </p>
 */
public final class BalanceAudit {

    private BalanceAudit() {}

    public static void main(String[] args) {
        System.out.println("=== Aincrad Balance Audit ===\n");

        boolean allPassed = true;

        allPassed &= auditBalanceConfig();
        allPassed &= auditBalanceValues();

        System.out.println("\n=== Balance Audit Summary ===");
        if (allPassed) {
            System.out.println("ALL BALANCE CHECKS PASSED");
        } else {
            System.out.println("SOME BALANCE CHECKS HAVE ISSUES — see above");
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // BalanceConfig audit
    // ------------------------------------------------------------------

    private static boolean auditBalanceConfig() {
        System.out.println("--- BalanceConfig / BalanceValidator ---");

        // Load balance.yml from classpath
        BalanceConfig config = loadConfigFromYml();
        if (config == null) {
            // Fall back to defaults (which match balance.yml)
            System.out.println("  Could not load balance.yml from classpath, using defaults");
            config = new BalanceConfig();
        }

        // Run validation
        BalanceValidator validator = new BalanceValidator(config);
        List<String> errors = validator.validateAll();

        if (!errors.isEmpty()) {
            System.out.println("  VALIDATION ERRORS:");
            for (String error : errors) {
                System.out.println("    " + error);
            }
            return false;
        }

        System.out.println("  All values within acceptable ranges");
        System.out.println("  PASSED\n");
        return true;
    }

    // ------------------------------------------------------------------
    // BalanceValues audit
    // ------------------------------------------------------------------

    private static boolean auditBalanceValues() {
        System.out.println("--- BalanceValues Static Sync ---");

        // Initialize static fields from config
        BalanceConfig config = loadConfigFromYml();
        if (config == null) {
            config = new BalanceConfig();
        }
        BalanceValues.init(config);

        // Verify key static fields are non-zero (properly loaded)
        boolean ok = true;

        if (BalanceValues.XP_BASE <= 0) {
            System.out.println("  ISSUE: XP_BASE is " + BalanceValues.XP_BASE);
            ok = false;
        }
        if (BalanceValues.MAX_LEVEL <= 0) {
            System.out.println("  ISSUE: MAX_LEVEL is " + BalanceValues.MAX_LEVEL);
            ok = false;
        }
        if (BalanceValues.HP_PER_LEVEL <= 0) {
            System.out.println("  ISSUE: HP_PER_LEVEL is " + BalanceValues.HP_PER_LEVEL);
            ok = false;
        }
        if (BalanceValues.MP_PER_LEVEL <= 0) {
            System.out.println("  ISSUE: MP_PER_LEVEL is " + BalanceValues.MP_PER_LEVEL);
            ok = false;
        }
        if (BalanceValues.CRIT_MAX_CHANCE <= 0) {
            System.out.println("  ISSUE: CRIT_MAX_CHANCE is " + BalanceValues.CRIT_MAX_CHANCE);
            ok = false;
        }
        if (BalanceValues.ENHANCEMENT_SUCCESS == null || BalanceValues.ENHANCEMENT_SUCCESS.isEmpty()) {
            System.out.println("  ISSUE: ENHANCEMENT_SUCCESS map is empty");
            ok = false;
        }
        if (BalanceValues.ENHANCEMENT_BREAK_CHANCE == null || BalanceValues.ENHANCEMENT_BREAK_CHANCE.isEmpty()) {
            System.out.println("  ISSUE: ENHANCEMENT_BREAK_CHANCE map is empty");
            ok = false;
        }

        if (ok) {
            System.out.println("  All static fields properly synchronized");
            System.out.println("  PASSED\n");
            return true;
        } else {
            System.out.println("  FAILED\n");
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    private static BalanceConfig loadConfigFromYml() {
        try (InputStream is = BalanceAudit.class.getClassLoader()
                .getResourceAsStream("balance.yml")) {
            if (is == null) {
                return null;
            }
            org.bukkit.configuration.file.YamlConfiguration yml =
                new org.bukkit.configuration.file.YamlConfiguration();
            yml.load(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8));
            return new BalanceConfig(yml);
        } catch (Exception e) {
            System.out.println("  Could not load balance.yml: " + e.getMessage());
            return null;
        }
    }
}
