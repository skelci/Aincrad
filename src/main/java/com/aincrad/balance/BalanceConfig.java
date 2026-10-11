package com.aincrad.balance;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads and provides access to all balance values from {@code balance.yml}.
 * <p>
 * Values are loaded once and cached. The config file is resolved in this order:
 * <ol>
 *   <li>Plugin data folder (for overrides)</li>
 *   <li>Classpath resource (bundled default)</li>
 * </ol>
 * </p>
 */
public final class BalanceConfig {

    private static final String CONFIG_NAME = "balance.yml";
    private static final Logger LOGGER = Logger.getLogger(BalanceConfig.class.getSimpleName());

    // ---- XP Curve ----
    private int xpBase;
    private double xpExponent;
    private int maxLevel;

    // ---- Stat Points ----
    private int statPointsPerLevel;
    private int statPointsTotal;

    // ---- Skill Points ----
    private int skillPointsPerLevel;
    private int skillPointsTotal;

    // ---- HP / MP Per Level ----
    private int hpPerLevel;
    private double hpVitMultiplier;
    private int mpPerLevel;
    private double mpIntMultiplier;

    // ---- Damage Formula ----
    private double damageStrMultiplier;
    private double damageWeaponAtkMultiplier;

    // ---- Critical Hits ----
    private double critDexMultiplier;
    private double critMaxChance;
    private double critBaseDamage;
    private double critStrDamageMultiplier;

    // ---- Evasion ----
    private double evasionAgiMultiplier;
    private double evasionMaxChance;

    // ---- Regeneration ----
    private double regenHpBase;
    private double regenHpVitMultiplier;
    private double regenMpBase;
    private double regenMpIntMultiplier;

    // ---- Weight Limit ----
    private int weightBase;
    private double weightStrMultiplier;

    // ---- Boss Scaling ----
    private double bossHpBase;
    private double bossHpExponent;
    private double bossDamageBase;
    private double bossDamageExponent;

    // ---- Mob Scaling ----
    private double mobHpBase;
    private double mobHpExponent;
    private double mobDamageBase;
    private double mobDamageExponent;

    // ---- XP Scaling ----
    private double xpScalingBase;
    private double xpScalingExponent;

    // ---- Loot Drop Rates ----
    private double lootCommon;
    private double lootUncommon;
    private double lootRare;
    private double lootEpic;
    private double lootLegendary;
    private double lootMythic;

    // ---- Enhancement ----
    private Map<String, Double> enhancementSuccess;
    private Map<String, Double> enhancementBreakChance;

    // ---- Crystal Drop Rates ----
    private double crystalTier1;
    private double crystalTier2;
    private double crystalTier3;
    private double crystalTier4;
    private double crystalTier5;

    // ---- Armor Set Bonus ----
    private int armorSetTwoPieces;
    private int armorSetFourPieces;
    private int armorSetSixPieces;

    // ---- Party ----
    private int partyXpBonusPerMember;

    // ---- Guild ----
    private double guildXpBase;
    private double guildXpExponent;
    private int guildMaxLevel;

    // ---- Dungeon ----
    private int dungeonLockoutDays;

    // ---- Economy ----
    private double auctionHouseFeePercent;
    private double shopMarkupPercent;

    /**
     * Creates a BalanceConfig from a YamlConfiguration.
     */
    public BalanceConfig(YamlConfiguration config) {
        loadFromConfig(config);
    }

    /**
     * Creates a BalanceConfig with all default values (no file loading).
     * Useful for testing or when no plugin instance is available.
     */
    public BalanceConfig() {
        setDefaults();
    }

    // ==================== LOAD ====================

    private void loadFromConfig(YamlConfiguration c) {
        // XP Curve
        this.xpBase = c.getInt("xp.base", 50);
        this.xpExponent = c.getDouble("xp.exponent", 1.8);
        this.maxLevel = c.getInt("xp.maxLevel", 100);

        // Stat Points
        this.statPointsPerLevel = c.getInt("statPoints.perLevel", 5);
        this.statPointsTotal = c.getInt("statPoints.total", 500);

        // Skill Points
        this.skillPointsPerLevel = c.getInt("skillPoints.perLevel", 3);
        this.skillPointsTotal = c.getInt("skillPoints.total", 299);

        // HP / MP Per Level
        this.hpPerLevel = c.getInt("hp.perLevel", 10);
        this.hpVitMultiplier = c.getDouble("hp.vitMultiplier", 2);
        this.mpPerLevel = c.getInt("mp.perLevel", 5);
        this.mpIntMultiplier = c.getDouble("mp.intMultiplier", 1.5);

        // Damage Formula
        this.damageStrMultiplier = c.getDouble("damage.strMultiplier", 2);
        this.damageWeaponAtkMultiplier = c.getDouble("damage.weaponAtkMultiplier", 1);

        // Critical Hits
        this.critDexMultiplier = c.getDouble("crit.dexMultiplier", 0.05);
        this.critMaxChance = c.getDouble("crit.maxChance", 50);
        this.critBaseDamage = c.getDouble("crit.baseDamage", 150);
        this.critStrDamageMultiplier = c.getDouble("crit.strDamageMultiplier", 0.5);

        // Evasion
        this.evasionAgiMultiplier = c.getDouble("evasion.agiMultiplier", 0.03);
        this.evasionMaxChance = c.getDouble("evasion.maxChance", 30);

        // Regeneration
        this.regenHpBase = c.getDouble("regen.hpBase", 1);
        this.regenHpVitMultiplier = c.getDouble("regen.hpVitMultiplier", 0.5);
        this.regenMpBase = c.getDouble("regen.mpBase", 0.5);
        this.regenMpIntMultiplier = c.getDouble("regen.mpIntMultiplier", 0.3);

        // Weight Limit
        this.weightBase = c.getInt("weight.base", 100);
        this.weightStrMultiplier = c.getDouble("weight.strMultiplier", 5);

        // Boss Scaling
        this.bossHpBase = c.getDouble("boss.hpBase", 500);
        this.bossHpExponent = c.getDouble("boss.hpExponent", 1.6);
        this.bossDamageBase = c.getDouble("boss.damageBase", 20);
        this.bossDamageExponent = c.getDouble("boss.damageExponent", 1.3);

        // Mob Scaling
        this.mobHpBase = c.getDouble("mob.hpBase", 30);
        this.mobHpExponent = c.getDouble("mob.hpExponent", 1.5);
        this.mobDamageBase = c.getDouble("mob.damageBase", 4);
        this.mobDamageExponent = c.getDouble("mob.damageExponent", 1.3);

        // XP Scaling
        this.xpScalingBase = c.getDouble("xpScaling.base", 8);
        this.xpScalingExponent = c.getDouble("xpScaling.exponent", 1.4);

        // Loot Drop Rates
        this.lootCommon = c.getDouble("loot.common", 60);
        this.lootUncommon = c.getDouble("loot.uncommon", 25);
        this.lootRare = c.getDouble("loot.rare", 10);
        this.lootEpic = c.getDouble("loot.epic", 4);
        this.lootLegendary = c.getDouble("loot.legendary", 0.9);
        this.lootMythic = c.getDouble("loot.mythic", 0.1);

        // Enhancement
        this.enhancementSuccess = loadEnhancementMap(c, "enhancement.success");
        this.enhancementBreakChance = loadEnhancementMap(c, "enhancement.breakChance");

        // Crystal Drop Rates
        this.crystalTier1 = c.getDouble("crystal.tier1", 50);
        this.crystalTier2 = c.getDouble("crystal.tier2", 30);
        this.crystalTier3 = c.getDouble("crystal.tier3", 15);
        this.crystalTier4 = c.getDouble("crystal.tier4", 4);
        this.crystalTier5 = c.getDouble("crystal.tier5", 1);

        // Armor Set Bonus
        this.armorSetTwoPieces = c.getInt("armorSet.twoPieces", 10);
        this.armorSetFourPieces = c.getInt("armorSet.fourPieces", 25);
        this.armorSetSixPieces = c.getInt("armorSet.sixPieces", 50);

        // Party
        this.partyXpBonusPerMember = c.getInt("party.xpBonusPerMember", 2);

        // Guild
        this.guildXpBase = c.getDouble("guild.xpBase", 250);
        this.guildXpExponent = c.getDouble("guild.xpExponent", 1.5);
        this.guildMaxLevel = c.getInt("guild.maxLevel", 20);

        // Dungeon
        this.dungeonLockoutDays = c.getInt("dungeon.lockoutDays", 7);

        // Economy
        this.auctionHouseFeePercent = c.getDouble("auctionHouse.feePercent", 5);
        this.shopMarkupPercent = c.getDouble("shop.markupPercent", 20);
    }

    private Map<String, Double> loadEnhancementMap(YamlConfiguration c, String path) {
        Map<String, Double> map = new java.util.LinkedHashMap<>();
        if (c.isConfigurationSection(path)) {
            for (String key : c.getConfigurationSection(path).getKeys(false)) {
                map.put(key, c.getDouble(path + "." + key));
            }
        }
        return map;
    }

    private void setDefaults() {
        // Defaults match balance.yml values
        this.xpBase = 50;
        this.xpExponent = 1.8;
        this.maxLevel = 100;
        this.statPointsPerLevel = 5;
        this.statPointsTotal = 500;
        this.skillPointsPerLevel = 3;
        this.skillPointsTotal = 299;
        this.hpPerLevel = 10;
        this.hpVitMultiplier = 2;
        this.mpPerLevel = 5;
        this.mpIntMultiplier = 1.5;
        this.damageStrMultiplier = 2;
        this.damageWeaponAtkMultiplier = 1;
        this.critDexMultiplier = 0.05;
        this.critMaxChance = 50;
        this.critBaseDamage = 150;
        this.critStrDamageMultiplier = 0.5;
        this.evasionAgiMultiplier = 0.03;
        this.evasionMaxChance = 30;
        this.regenHpBase = 1;
        this.regenHpVitMultiplier = 0.5;
        this.regenMpBase = 0.5;
        this.regenMpIntMultiplier = 0.3;
        this.weightBase = 100;
        this.weightStrMultiplier = 5;
        this.bossHpBase = 500;
        this.bossHpExponent = 1.6;
        this.bossDamageBase = 20;
        this.bossDamageExponent = 1.3;
        this.mobHpBase = 30;
        this.mobHpExponent = 1.5;
        this.mobDamageBase = 4;
        this.mobDamageExponent = 1.3;
        this.xpScalingBase = 8;
        this.xpScalingExponent = 1.4;
        this.lootCommon = 60;
        this.lootUncommon = 25;
        this.lootRare = 10;
        this.lootEpic = 4;
        this.lootLegendary = 0.9;
        this.lootMythic = 0.1;
        this.enhancementSuccess = new java.util.LinkedHashMap<>();
        this.enhancementBreakChance = new java.util.LinkedHashMap<>();
        this.crystalTier1 = 50;
        this.crystalTier2 = 30;
        this.crystalTier3 = 15;
        this.crystalTier4 = 4;
        this.crystalTier5 = 1;
        this.armorSetTwoPieces = 10;
        this.armorSetFourPieces = 25;
        this.armorSetSixPieces = 50;
        this.partyXpBonusPerMember = 2;
        this.guildXpBase = 250;
        this.guildXpExponent = 1.5;
        this.guildMaxLevel = 20;
        this.dungeonLockoutDays = 7;
        this.auctionHouseFeePercent = 5;
        this.shopMarkupPercent = 20;
    }

    // ==================== GETTERS ====================

    // XP Curve
    public int getXpBase() { return xpBase; }
    public double getXpExponent() { return xpExponent; }
    public int getMaxLevel() { return maxLevel; }

    // Stat Points
    public int getStatPointsPerLevel() { return statPointsPerLevel; }
    public int getStatPointsTotal() { return statPointsTotal; }

    // Skill Points
    public int getSkillPointsPerLevel() { return skillPointsPerLevel; }
    public int getSkillPointsTotal() { return skillPointsTotal; }

    // HP / MP
    public int getHpPerLevel() { return hpPerLevel; }
    public double getHpVitMultiplier() { return hpVitMultiplier; }
    public int getMpPerLevel() { return mpPerLevel; }
    public double getMpIntMultiplier() { return mpIntMultiplier; }

    // Damage
    public double getDamageStrMultiplier() { return damageStrMultiplier; }
    public double getDamageWeaponAtkMultiplier() { return damageWeaponAtkMultiplier; }

    // Crit
    public double getCritDexMultiplier() { return critDexMultiplier; }
    public double getCritMaxChance() { return critMaxChance; }
    public double getCritBaseDamage() { return critBaseDamage; }
    public double getCritStrDamageMultiplier() { return critStrDamageMultiplier; }

    // Evasion
    public double getEvasionAgiMultiplier() { return evasionAgiMultiplier; }
    public double getEvasionMaxChance() { return evasionMaxChance; }

    // Regen
    public double getRegenHpBase() { return regenHpBase; }
    public double getRegenHpVitMultiplier() { return regenHpVitMultiplier; }
    public double getRegenMpBase() { return regenMpBase; }
    public double getRegenMpIntMultiplier() { return regenMpIntMultiplier; }

    // Weight
    public int getWeightBase() { return weightBase; }
    public double getWeightStrMultiplier() { return weightStrMultiplier; }

    // Boss
    public double getBossHpBase() { return bossHpBase; }
    public double getBossHpExponent() { return bossHpExponent; }
    public double getBossDamageBase() { return bossDamageBase; }
    public double getBossDamageExponent() { return bossDamageExponent; }

    // Mob
    public double getMobHpBase() { return mobHpBase; }
    public double getMobHpExponent() { return mobHpExponent; }
    public double getMobDamageBase() { return mobDamageBase; }
    public double getMobDamageExponent() { return mobDamageExponent; }

    // XP Scaling
    public double getXpScalingBase() { return xpScalingBase; }
    public double getXpScalingExponent() { return xpScalingExponent; }

    // Loot
    public double getLootCommon() { return lootCommon; }
    public double getLootUncommon() { return lootUncommon; }
    public double getLootRare() { return lootRare; }
    public double getLootEpic() { return lootEpic; }
    public double getLootLegendary() { return lootLegendary; }
    public double getLootMythic() { return lootMythic; }

    // Enhancement
    public Map<String, Double> getEnhancementSuccess() { return enhancementSuccess; }
    public Map<String, Double> getEnhancementBreakChance() { return enhancementBreakChance; }

    // Crystal
    public double getCrystalTier1() { return crystalTier1; }
    public double getCrystalTier2() { return crystalTier2; }
    public double getCrystalTier3() { return crystalTier3; }
    public double getCrystalTier4() { return crystalTier4; }
    public double getCrystalTier5() { return crystalTier5; }

    // Armor Set
    public int getArmorSetTwoPieces() { return armorSetTwoPieces; }
    public int getArmorSetFourPieces() { return armorSetFourPieces; }
    public int getArmorSetSixPieces() { return armorSetSixPieces; }

    // Party
    public int getPartyXpBonusPerMember() { return partyXpBonusPerMember; }

    // Guild
    public double getGuildXpBase() { return guildXpBase; }
    public double getGuildXpExponent() { return guildXpExponent; }
    public int getGuildMaxLevel() { return guildMaxLevel; }

    // Dungeon
    public int getDungeonLockoutDays() { return dungeonLockoutDays; }

    // Economy
    public double getAuctionHouseFeePercent() { return auctionHouseFeePercent; }
    public double getShopMarkupPercent() { return shopMarkupPercent; }
}
