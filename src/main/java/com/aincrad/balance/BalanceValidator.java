package com.aincrad.balance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates that all balance values are within acceptable ranges.
 * <p>
 * Each validation rule checks a specific value or set of values against
 * predefined min/max bounds. Use {@link #validateAll()} to run all checks
 * and get a list of validation errors.
 * </p>
 */
public final class BalanceValidator {

    private final BalanceConfig config;

    public BalanceValidator(BalanceConfig config) {
        this.config = config;
    }

    /**
     * Runs all validation checks and returns a list of error messages.
     * An empty list means all values are valid.
     */
    public List<String> validateAll() {
        List<String> errors = new ArrayList<>();

        // XP Curve
        validateRange(errors, "xp.base", config.getXpBase(), 1, 10000);
        validateRange(errors, "xp.exponent", config.getXpExponent(), 0.1, 5.0);
        validateRange(errors, "xp.maxLevel", config.getMaxLevel(), 1, 1000);

        // Stat Points
        validateRange(errors, "statPoints.perLevel", config.getStatPointsPerLevel(), 0, 100);
        validateRange(errors, "statPoints.total", config.getStatPointsTotal(), 0, 10000);

        // Skill Points
        validateRange(errors, "skillPoints.perLevel", config.getSkillPointsPerLevel(), 0, 100);
        validateRange(errors, "skillPoints.total", config.getSkillPointsTotal(), 0, 10000);

        // HP / MP
        validateRange(errors, "hp.perLevel", config.getHpPerLevel(), 0, 1000);
        validateRange(errors, "hp.vitMultiplier", config.getHpVitMultiplier(), 0, 100);
        validateRange(errors, "mp.perLevel", config.getMpPerLevel(), 0, 1000);
        validateRange(errors, "mp.intMultiplier", config.getMpIntMultiplier(), 0, 100);

        // Damage
        validateRange(errors, "damage.strMultiplier", config.getDamageStrMultiplier(), 0, 100);
        validateRange(errors, "damage.weaponAtkMultiplier", config.getDamageWeaponAtkMultiplier(), 0, 100);

        // Crit
        validateRange(errors, "crit.dexMultiplier", config.getCritDexMultiplier(), 0, 10);
        validateRange(errors, "crit.maxChance", config.getCritMaxChance(), 0, 100);
        validateRange(errors, "crit.baseDamage", config.getCritBaseDamage(), 0, 1000);
        validateRange(errors, "crit.strDamageMultiplier", config.getCritStrDamageMultiplier(), 0, 100);

        // Evasion
        validateRange(errors, "evasion.agiMultiplier", config.getEvasionAgiMultiplier(), 0, 10);
        validateRange(errors, "evasion.maxChance", config.getEvasionMaxChance(), 0, 100);

        // Regen
        validateRange(errors, "regen.hpBase", config.getRegenHpBase(), 0, 1000);
        validateRange(errors, "regen.hpVitMultiplier", config.getRegenHpVitMultiplier(), 0, 100);
        validateRange(errors, "regen.mpBase", config.getRegenMpBase(), 0, 1000);
        validateRange(errors, "regen.mpIntMultiplier", config.getRegenMpIntMultiplier(), 0, 100);

        // Weight
        validateRange(errors, "weight.base", config.getWeightBase(), 0, 100000);
        validateRange(errors, "weight.strMultiplier", config.getWeightStrMultiplier(), 0, 1000);

        // Boss
        validateRange(errors, "boss.hpBase", config.getBossHpBase(), 0, 1000000);
        validateRange(errors, "boss.hpExponent", config.getBossHpExponent(), 0.1, 5.0);
        validateRange(errors, "boss.damageBase", config.getBossDamageBase(), 0, 100000);
        validateRange(errors, "boss.damageExponent", config.getBossDamageExponent(), 0.1, 5.0);

        // Mob
        validateRange(errors, "mob.hpBase", config.getMobHpBase(), 0, 100000);
        validateRange(errors, "mob.hpExponent", config.getMobHpExponent(), 0.1, 5.0);
        validateRange(errors, "mob.damageBase", config.getMobDamageBase(), 0, 10000);
        validateRange(errors, "mob.damageExponent", config.getMobDamageExponent(), 0.1, 5.0);

        // XP Scaling
        validateRange(errors, "xpScaling.base", config.getXpScalingBase(), 0, 10000);
        validateRange(errors, "xpScaling.exponent", config.getXpScalingExponent(), 0.1, 5.0);

        // Loot
        validateRange(errors, "loot.common", config.getLootCommon(), 0, 100);
        validateRange(errors, "loot.uncommon", config.getLootUncommon(), 0, 100);
        validateRange(errors, "loot.rare", config.getLootRare(), 0, 100);
        validateRange(errors, "loot.epic", config.getLootEpic(), 0, 100);
        validateRange(errors, "loot.legendary", config.getLootLegendary(), 0, 100);
        validateRange(errors, "loot.mythic", config.getLootMythic(), 0, 100);

        // Enhancement
        validateEnhancementMap(errors, "enhancement.success", config.getEnhancementSuccess(), 0, 100);
        validateEnhancementMap(errors, "enhancement.breakChance", config.getEnhancementBreakChance(), 0, 100);

        // Crystal
        validateRange(errors, "crystal.tier1", config.getCrystalTier1(), 0, 100);
        validateRange(errors, "crystal.tier2", config.getCrystalTier2(), 0, 100);
        validateRange(errors, "crystal.tier3", config.getCrystalTier3(), 0, 100);
        validateRange(errors, "crystal.tier4", config.getCrystalTier4(), 0, 100);
        validateRange(errors, "crystal.tier5", config.getCrystalTier5(), 0, 100);

        // Armor Set
        validateRange(errors, "armorSet.twoPieces", config.getArmorSetTwoPieces(), 0, 100);
        validateRange(errors, "armorSet.fourPieces", config.getArmorSetFourPieces(), 0, 100);
        validateRange(errors, "armorSet.sixPieces", config.getArmorSetSixPieces(), 0, 100);

        // Party
        validateRange(errors, "party.xpBonusPerMember", config.getPartyXpBonusPerMember(), 0, 100);

        // Guild
        validateRange(errors, "guild.xpBase", config.getGuildXpBase(), 0, 100000);
        validateRange(errors, "guild.xpExponent", config.getGuildXpExponent(), 0.1, 5.0);
        validateRange(errors, "guild.maxLevel", config.getGuildMaxLevel(), 1, 1000);

        // Dungeon
        validateRange(errors, "dungeon.lockoutDays", config.getDungeonLockoutDays(), 0, 365);

        // Economy
        validateRange(errors, "auctionHouse.feePercent", config.getAuctionHouseFeePercent(), 0, 100);
        validateRange(errors, "shop.markupPercent", config.getShopMarkupPercent(), 0, 1000);

        return errors;
    }

    /**
     * Returns true if all values pass validation.
     */
    public boolean isValid() {
        return validateAll().isEmpty();
    }

    private void validateRange(List<String> errors, String name, double value, double min, double max) {
        if (value < min || value > max) {
            errors.add(String.format("%s: %.4f is out of range [%.4f, %.4f]", name, value, min, max));
        }
    }

    private void validateEnhancementMap(List<String> errors, String name, Map<String, Double> map, double min, double max) {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            validateRange(errors, name + "." + entry.getKey(), entry.getValue(), min, max);
        }
    }
}
