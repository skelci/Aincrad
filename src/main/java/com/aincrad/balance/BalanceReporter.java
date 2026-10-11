package com.aincrad.balance;

import java.util.Map;

/**
 * Generates a human-readable balance report showing all configured values.
 * <p>
 * The report is formatted as a multi-line string with sections for each
 * balance category. Use {@link #generateReport()} to get the full report,
 * or {@link #generateReport(BalanceConfig)} for a one-shot report.
 * </p>
 */
public final class BalanceReporter {

    private final BalanceConfig config;

    public BalanceReporter(BalanceConfig config) {
        this.config = config;
    }

    /**
     * Generates a complete balance report from the given config.
     */
    public static String generateReport(BalanceConfig config) {
        return new BalanceReporter(config).generateReport();
    }

    /**
     * Generates a complete balance report.
     */
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Aincrad Balance Report ==========\n\n");

        // XP Curve
        sb.append("--- XP Curve ---\n");
        sb.append(String.format("  Base: %d\n", config.getXpBase()));
        sb.append(String.format("  Exponent: %.2f\n", config.getXpExponent()));
        sb.append(String.format("  Max Level: %d\n\n", config.getMaxLevel()));

        // Stat Points
        sb.append("--- Stat Points ---\n");
        sb.append(String.format("  Per Level: %d\n", config.getStatPointsPerLevel()));
        sb.append(String.format("  Total: %d\n\n", config.getStatPointsTotal()));

        // Skill Points
        sb.append("--- Skill Points ---\n");
        sb.append(String.format("  Per Level: %d\n", config.getSkillPointsPerLevel()));
        sb.append(String.format("  Total: %d\n\n", config.getSkillPointsTotal()));

        // HP / MP
        sb.append("--- HP / MP Per Level ---\n");
        sb.append(String.format("  HP per Level: %d + VIT * %.1f\n", config.getHpPerLevel(), config.getHpVitMultiplier()));
        sb.append(String.format("  MP per Level: %d + INT * %.1f\n\n", config.getMpPerLevel(), config.getMpIntMultiplier()));

        // Damage
        sb.append("--- Damage Formula ---\n");
        sb.append(String.format("  STR Multiplier: %.1f\n", config.getDamageStrMultiplier()));
        sb.append(String.format("  Weapon ATK Multiplier: %.1f\n\n", config.getDamageWeaponAtkMultiplier()));

        // Crit
        sb.append("--- Critical Hits ---\n");
        sb.append(String.format("  DEX Multiplier: %.2f%%\n", config.getCritDexMultiplier()));
        sb.append(String.format("  Max Chance: %.1f%%\n", config.getCritMaxChance()));
        sb.append(String.format("  Base Damage: %.1f%%\n", config.getCritBaseDamage()));
        sb.append(String.format("  STR Damage Multiplier: %.2f%%\n\n", config.getCritStrDamageMultiplier()));

        // Evasion
        sb.append("--- Evasion ---\n");
        sb.append(String.format("  AGI Multiplier: %.2f%%\n", config.getEvasionAgiMultiplier()));
        sb.append(String.format("  Max Chance: %.1f%%\n\n", config.getEvasionMaxChance()));

        // Regen
        sb.append("--- Regeneration (per second) ---\n");
        sb.append(String.format("  HP: %.1f + VIT * %.1f\n", config.getRegenHpBase(), config.getRegenHpVitMultiplier()));
        sb.append(String.format("  MP: %.1f + INT * %.1f\n\n", config.getRegenMpBase(), config.getRegenMpIntMultiplier()));

        // Weight
        sb.append("--- Weight Limit ---\n");
        sb.append(String.format("  Base: %d\n", config.getWeightBase()));
        sb.append(String.format("  STR Multiplier: %.1f\n\n", config.getWeightStrMultiplier()));

        // Boss
        sb.append("--- Boss Scaling ---\n");
        sb.append(String.format("  HP: %.0f * floor^%.1f\n", config.getBossHpBase(), config.getBossHpExponent()));
        sb.append(String.format("  Damage: %.0f * floor^%.1f\n\n", config.getBossDamageBase(), config.getBossDamageExponent()));

        // Mob
        sb.append("--- Mob Scaling ---\n");
        sb.append(String.format("  HP: %.0f * floor^%.1f\n", config.getMobHpBase(), config.getMobHpExponent()));
        sb.append(String.format("  Damage: %.0f * floor^%.1f\n\n", config.getMobDamageBase(), config.getMobDamageExponent()));

        // XP Scaling
        sb.append("--- XP Scaling ---\n");
        sb.append(String.format("  Base: %.0f * floor^%.1f\n\n", config.getXpScalingBase(), config.getXpScalingExponent()));

        // Loot
        sb.append("--- Loot Drop Rates ---\n");
        sb.append(String.format("  Common: %.1f%%\n", config.getLootCommon()));
        sb.append(String.format("  Uncommon: %.1f%%\n", config.getLootUncommon()));
        sb.append(String.format("  Rare: %.1f%%\n", config.getLootRare()));
        sb.append(String.format("  Epic: %.1f%%\n", config.getLootEpic()));
        sb.append(String.format("  Legendary: %.1f%%\n", config.getLootLegendary()));
        sb.append(String.format("  Mythic: %.1f%%\n\n", config.getLootMythic()));

        // Enhancement
        sb.append("--- Enhancement Success Rates ---\n");
        appendEnhancementMap(sb, "Success", config.getEnhancementSuccess());
        sb.append("\n--- Enhancement Break Chances ---\n");
        appendEnhancementMap(sb, "Break", config.getEnhancementBreakChance());
        sb.append("\n");

        // Crystal
        sb.append("--- Crystal Drop Rates ---\n");
        sb.append(String.format("  Tier 1: %.1f%%\n", config.getCrystalTier1()));
        sb.append(String.format("  Tier 2: %.1f%%\n", config.getCrystalTier2()));
        sb.append(String.format("  Tier 3: %.1f%%\n", config.getCrystalTier3()));
        sb.append(String.format("  Tier 4: %.1f%%\n", config.getCrystalTier4()));
        sb.append(String.format("  Tier 5: %.1f%%\n\n", config.getCrystalTier5()));

        // Armor Set
        sb.append("--- Armor Set Bonus ---\n");
        sb.append(String.format("  2 pieces: +%d%%\n", config.getArmorSetTwoPieces()));
        sb.append(String.format("  4 pieces: +%d%%\n", config.getArmorSetFourPieces()));
        sb.append(String.format("  6 pieces: +%d%%\n\n", config.getArmorSetSixPieces()));

        // Party
        sb.append("--- Party ---\n");
        sb.append(String.format("  XP Bonus per Member: +%d%%\n\n", config.getPartyXpBonusPerMember()));

        // Guild
        sb.append("--- Guild ---\n");
        sb.append(String.format("  XP Base: %.0f * level^%.1f\n", config.getGuildXpBase(), config.getGuildXpExponent()));
        sb.append(String.format("  Max Level: %d\n\n", config.getGuildMaxLevel()));

        // Dungeon
        sb.append("--- Dungeon ---\n");
        sb.append(String.format("  Lockout: %d days\n\n", config.getDungeonLockoutDays()));

        // Economy
        sb.append("--- Economy ---\n");
        sb.append(String.format("  Auction House Fee: %.1f%%\n", config.getAuctionHouseFeePercent()));
        sb.append(String.format("  Shop Markup: %.1f%%\n\n", config.getShopMarkupPercent()));

        sb.append("========== End of Balance Report ==========");
        return sb.toString();
    }

    private void appendEnhancementMap(StringBuilder sb, String label, Map<String, Double> map) {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            sb.append(String.format("  +%s: %.1f%%\n", entry.getKey(), entry.getValue()));
        }
    }
}
