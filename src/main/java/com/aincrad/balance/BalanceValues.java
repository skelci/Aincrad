package com.aincrad.balance;

import java.util.Collections;
import java.util.Map;

/**
 * Static access point for all balance values.
 * <p>
 * All fields are populated from a {@link BalanceConfig} instance. Use
 * {@link #init(BalanceConfig)} to load values, then access them statically.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *   BalanceValues.init(pluginBalanceConfig);
 *   double hpPerLevel = BalanceValues.HP_PER_LEVEL;
 * </pre>
 * </p>
 */
public final class BalanceValues {

    private BalanceValues() {}

    // ---- XP Curve ----
    public static int XP_BASE;
    public static double XP_EXPONENT;
    public static int MAX_LEVEL;

    // ---- Stat Points ----
    public static int STAT_POINTS_PER_LEVEL;
    public static int STAT_POINTS_TOTAL;

    // ---- Skill Points ----
    public static int SKILL_POINTS_PER_LEVEL;
    public static int SKILL_POINTS_TOTAL;

    // ---- HP / MP Per Level ----
    public static int HP_PER_LEVEL;
    public static double HP_VIT_MULTIPLIER;
    public static int MP_PER_LEVEL;
    public static double MP_INT_MULTIPLIER;

    // ---- Damage Formula ----
    public static double DAMAGE_STR_MULTIPLIER;
    public static double DAMAGE_WEAPON_ATK_MULTIPLIER;

    // ---- Critical Hits ----
    public static double CRIT_DEX_MULTIPLIER;
    public static double CRIT_MAX_CHANCE;
    public static double CRIT_BASE_DAMAGE;
    public static double CRIT_STR_DAMAGE_MULTIPLIER;

    // ---- Evasion ----
    public static double EVASION_AGI_MULTIPLIER;
    public static double EVASION_MAX_CHANCE;

    // ---- Regeneration ----
    public static double REGEN_HP_BASE;
    public static double REGEN_HP_VIT_MULTIPLIER;
    public static double REGEN_MP_BASE;
    public static double REGEN_MP_INT_MULTIPLIER;

    // ---- Weight Limit ----
    public static int WEIGHT_BASE;
    public static double WEIGHT_STR_MULTIPLIER;

    // ---- Boss Scaling ----
    public static double BOSS_HP_BASE;
    public static double BOSS_HP_EXPONENT;
    public static double BOSS_DAMAGE_BASE;
    public static double BOSS_DAMAGE_EXPONENT;

    // ---- Mob Scaling ----
    public static double MOB_HP_BASE;
    public static double MOB_HP_EXPONENT;
    public static double MOB_DAMAGE_BASE;
    public static double MOB_DAMAGE_EXPONENT;

    // ---- XP Scaling ----
    public static double XP_SCALING_BASE;
    public static double XP_SCALING_EXPONENT;

    // ---- Loot Drop Rates ----
    public static double LOOT_COMMON;
    public static double LOOT_UNCOMMON;
    public static double LOOT_RARE;
    public static double LOOT_EPIC;
    public static double LOOT_LEGENDARY;
    public static double LOOT_MYTHIC;

    // ---- Enhancement ----
    public static Map<String, Double> ENHANCEMENT_SUCCESS;
    public static Map<String, Double> ENHANCEMENT_BREAK_CHANCE;

    // ---- Crystal Drop Rates ----
    public static double CRYSTAL_TIER1;
    public static double CRYSTAL_TIER2;
    public static double CRYSTAL_TIER3;
    public static double CRYSTAL_TIER4;
    public static double CRYSTAL_TIER5;

    // ---- Armor Set Bonus ----
    public static int ARMOR_SET_TWO_PIECES;
    public static int ARMOR_SET_FOUR_PIECES;
    public static int ARMOR_SET_SIX_PIECES;

    // ---- Party ----
    public static int PARTY_XP_BONUS_PER_MEMBER;

    // ---- Guild ----
    public static double GUILD_XP_BASE;
    public static double GUILD_XP_EXPONENT;
    public static int GUILD_MAX_LEVEL;

    // ---- Dungeon ----
    public static int DUNGEON_LOCKOUT_DAYS;

    // ---- Economy ----
    public static double AUCTION_HOUSE_FEE_PERCENT;
    public static double SHOP_MARKUP_PERCENT;

    /**
     * Loads all balance values from the given config into static fields.
     * Must be called before accessing any static field.
     *
     * @param config the balance configuration to load from
     */
    public static void init(BalanceConfig config) {
        // XP Curve
        XP_BASE = config.getXpBase();
        XP_EXPONENT = config.getXpExponent();
        MAX_LEVEL = config.getMaxLevel();

        // Stat Points
        STAT_POINTS_PER_LEVEL = config.getStatPointsPerLevel();
        STAT_POINTS_TOTAL = config.getStatPointsTotal();

        // Skill Points
        SKILL_POINTS_PER_LEVEL = config.getSkillPointsPerLevel();
        SKILL_POINTS_TOTAL = config.getSkillPointsTotal();

        // HP / MP
        HP_PER_LEVEL = config.getHpPerLevel();
        HP_VIT_MULTIPLIER = config.getHpVitMultiplier();
        MP_PER_LEVEL = config.getMpPerLevel();
        MP_INT_MULTIPLIER = config.getMpIntMultiplier();

        // Damage
        DAMAGE_STR_MULTIPLIER = config.getDamageStrMultiplier();
        DAMAGE_WEAPON_ATK_MULTIPLIER = config.getDamageWeaponAtkMultiplier();

        // Crit
        CRIT_DEX_MULTIPLIER = config.getCritDexMultiplier();
        CRIT_MAX_CHANCE = config.getCritMaxChance();
        CRIT_BASE_DAMAGE = config.getCritBaseDamage();
        CRIT_STR_DAMAGE_MULTIPLIER = config.getCritStrDamageMultiplier();

        // Evasion
        EVASION_AGI_MULTIPLIER = config.getEvasionAgiMultiplier();
        EVASION_MAX_CHANCE = config.getEvasionMaxChance();

        // Regen
        REGEN_HP_BASE = config.getRegenHpBase();
        REGEN_HP_VIT_MULTIPLIER = config.getRegenHpVitMultiplier();
        REGEN_MP_BASE = config.getRegenMpBase();
        REGEN_MP_INT_MULTIPLIER = config.getRegenMpIntMultiplier();

        // Weight
        WEIGHT_BASE = config.getWeightBase();
        WEIGHT_STR_MULTIPLIER = config.getWeightStrMultiplier();

        // Boss
        BOSS_HP_BASE = config.getBossHpBase();
        BOSS_HP_EXPONENT = config.getBossHpExponent();
        BOSS_DAMAGE_BASE = config.getBossDamageBase();
        BOSS_DAMAGE_EXPONENT = config.getBossDamageExponent();

        // Mob
        MOB_HP_BASE = config.getMobHpBase();
        MOB_HP_EXPONENT = config.getMobHpExponent();
        MOB_DAMAGE_BASE = config.getMobDamageBase();
        MOB_DAMAGE_EXPONENT = config.getMobDamageExponent();

        // XP Scaling
        XP_SCALING_BASE = config.getXpScalingBase();
        XP_SCALING_EXPONENT = config.getXpScalingExponent();

        // Loot
        LOOT_COMMON = config.getLootCommon();
        LOOT_UNCOMMON = config.getLootUncommon();
        LOOT_RARE = config.getLootRare();
        LOOT_EPIC = config.getLootEpic();
        LOOT_LEGENDARY = config.getLootLegendary();
        LOOT_MYTHIC = config.getLootMythic();

        // Enhancement
        ENHANCEMENT_SUCCESS = Collections.unmodifiableMap(config.getEnhancementSuccess());
        ENHANCEMENT_BREAK_CHANCE = Collections.unmodifiableMap(config.getEnhancementBreakChance());

        // Crystal
        CRYSTAL_TIER1 = config.getCrystalTier1();
        CRYSTAL_TIER2 = config.getCrystalTier2();
        CRYSTAL_TIER3 = config.getCrystalTier3();
        CRYSTAL_TIER4 = config.getCrystalTier4();
        CRYSTAL_TIER5 = config.getCrystalTier5();

        // Armor Set
        ARMOR_SET_TWO_PIECES = config.getArmorSetTwoPieces();
        ARMOR_SET_FOUR_PIECES = config.getArmorSetFourPieces();
        ARMOR_SET_SIX_PIECES = config.getArmorSetSixPieces();

        // Party
        PARTY_XP_BONUS_PER_MEMBER = config.getPartyXpBonusPerMember();

        // Guild
        GUILD_XP_BASE = config.getGuildXpBase();
        GUILD_XP_EXPONENT = config.getGuildXpExponent();
        GUILD_MAX_LEVEL = config.getGuildMaxLevel();

        // Dungeon
        DUNGEON_LOCKOUT_DAYS = config.getDungeonLockoutDays();

        // Economy
        AUCTION_HOUSE_FEE_PERCENT = config.getAuctionHouseFeePercent();
        SHOP_MARKUP_PERCENT = config.getShopMarkupPercent();
    }
}
