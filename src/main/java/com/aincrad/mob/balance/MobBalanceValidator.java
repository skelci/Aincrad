package com.aincrad.mob.balance;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates all values in {@link MobBalanceConfig}.
 * <p>
 * Checks:
 * <ul>
 *   <li>All theme multipliers are positive</li>
 *   <li>HP multiplier in [0.1, 10.0]</li>
 *   <li>Damage multiplier in [0.1, 10.0]</li>
 *   <li>Speed multiplier in [0.1, 5.0]</li>
 *   <li>At least one theme is configured</li>
 *   <li>Floor scaling steps are non-negative</li>
 * </ul>
 */
public final class MobBalanceValidator {

    private static final double MIN_HP = 0.1, MAX_HP = 10.0;
    private static final double MIN_DMG = 0.1, MAX_DMG = 10.0;
    private static final double MIN_SPD = 0.1, MAX_SPD = 5.0;

    private MobBalanceValidator() {}

    /**
     * Validate the given config and return a list of issues.
     *
     * @param config the config to validate (must not be {@code null})
     * @return list of issue descriptions; empty if everything is valid
     */
    public static List<String> validate(MobBalanceConfig config) {
        List<String> issues = new ArrayList<>();

        if (config == null) {
            issues.add("Config is null");
            return issues;
        }

        int themeCount = config.getThemeCount();
        if (themeCount == 0) {
            issues.add("No themes are configured");
            return issues;
        }

        for (int i = 0; i < themeCount; i++) {
            MobBalanceConfig.ThemeEntry theme = config.getTheme(i);
            if (theme == null) {
                issues.add(String.format("Theme %d is null", i));
                continue;
            }

            String prefix = theme.name() != null ? theme.name() : "Theme " + i;

            if (!isInRange(theme.hp(), MIN_HP, MAX_HP)) {
                issues.add(String.format("%s HP multiplier %.2f is out of range [%.1f, %.1f]",
                    prefix, theme.hp(), MIN_HP, MAX_HP));
            }
            if (!isInRange(theme.dmg(), MIN_DMG, MAX_DMG)) {
                issues.add(String.format("%s damage multiplier %.2f is out of range [%.1f, %.1f]",
                    prefix, theme.dmg(), MIN_DMG, MAX_DMG));
            }
            if (!isInRange(theme.spd(), MIN_SPD, MAX_SPD)) {
                issues.add(String.format("%s speed multiplier %.2f is out of range [%.1f, %.1f]",
                    prefix, theme.spd(), MIN_SPD, MAX_SPD));
            }

            if (theme.hp() <= 0) {
                issues.add(String.format("%s HP multiplier %.2f is not positive", prefix, theme.hp()));
            }
            if (theme.dmg() <= 0) {
                issues.add(String.format("%s damage multiplier %.2f is not positive", prefix, theme.dmg()));
            }
            if (theme.spd() <= 0) {
                issues.add(String.format("%s speed multiplier %.2f is not positive", prefix, theme.spd()));
            }
        }

        // Floor scaling
        if (config.getHpStep() < 0) {
            issues.add(String.format("HP floor step %.2f is negative", config.getHpStep()));
        }
        if (config.getDmgStep() < 0) {
            issues.add(String.format("Damage floor step %.2f is negative", config.getDmgStep()));
        }
        if (config.getSpdStep() < 0) {
            issues.add(String.format("Speed floor step %.2f is negative", config.getSpdStep()));
        }

        return issues;
    }

    /** Shorthand: validate the singleton config. */
    public static List<String> validate() {
        return validate(MobBalanceConfig.getInstance());
    }

    private static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
