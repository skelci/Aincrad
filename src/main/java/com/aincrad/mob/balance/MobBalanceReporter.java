package com.aincrad.mob.balance;

import java.util.List;

/**
 * Generates a formatted balance report for debugging and admin commands.
 * <p>
 * Usage:
 * <pre>
 * String report = MobBalanceReporter.generate();
 * player.sendMessage(report);
 * </pre>
 */
public final class MobBalanceReporter {

    private MobBalanceReporter() {}

    /**
     * Generate a full balance report showing all theme multipliers and floor scaling steps.
     *
     * @return formatted multi-line report string
     */
    public static String generate() {
        return generate(MobBalanceConfig.getInstance());
    }

    /**
     * Generate a full balance report for the given config.
     *
     * @param config the config to report on
     * @return formatted multi-line report string
     */
    public static String generate(MobBalanceConfig config) {
        if (config == null) {
            return "§c[MobBalance] Config is null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("§8§m==========§r §6Mob Balance Report §8§m==========§r\n");

        // ---- Floor scaling ----
        sb.append("§eFloor Scaling Steps:§r\n");
        sb.append(String.format("  HP: §a%.2f§r per floor%n", config.getHpStep()));
        sb.append(String.format("  DMG: §a%.2f§r per floor%n", config.getDmgStep()));
        sb.append(String.format("  SPD: §a%.2f§r per floor%n", config.getSpdStep()));

        // ---- Themes ----
        sb.append("\n§eTheme Multipliers:§r\n");
        int themeCount = config.getThemeCount();
        if (themeCount == 0) {
            sb.append("  §cNo themes configured§r\n");
        } else {
            sb.append(String.format("  %-12s %6s %6s %6s%n", "Theme", "HP", "DMG", "SPD"));
            sb.append("  §8----------------------------§r\n");
            for (int i = 0; i < themeCount; i++) {
                MobBalanceConfig.ThemeEntry theme = config.getTheme(i);
                if (theme == null) continue;
                sb.append(String.format("  §f%-12s§r §c%.2fx§r §c%.2fx§r §a%.2fx§r%n",
                    theme.name(), theme.hp(), theme.dmg(), theme.spd()));
            }
        }

        // ---- Validation summary ----
        List<String> issues = MobBalanceValidator.validate(config);
        sb.append("\n§eValidation:§r\n");
        if (issues.isEmpty()) {
            sb.append("  §a✔ All values are valid§r\n");
        } else {
            sb.append(String.format("  §c✘ %d issue(s) found:§r%n", issues.size()));
            for (String issue : issues) {
                sb.append("    §c•§r ").append(issue).append('\n');
            }
        }

        sb.append("§8§m====================================§r");
        return sb.toString();
    }
}
