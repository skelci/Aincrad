package com.aincrad.crystal;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import net.minecraft.world.item.Item;

/**
 * A crystal item that can be slotted into the Crystal equipment slot.
 * 6 types × 5 tiers = 30 crystal variants.
 */
public class CrystalItem extends Item {

    private final CrystalType crystalType;
    private final CrystalTier crystalTier;
    private final String name;
    private final ItemRarity rarity;
    private final ItemType type;
    private final int weight;
    private final int baseValue;
    private final String description;

    public CrystalItem(CrystalType crystalType, CrystalTier crystalTier) {
        super(new Properties());
        this.crystalType = crystalType;
        this.crystalTier = crystalTier;
        this.name = crystalTier.getDisplayName() + " " + crystalType.getDisplayName() + " Crystal";
        this.rarity = getRarityForTier(crystalTier);
        this.type = ItemType.CRYSTAL;
        this.weight = 5;
        this.baseValue = 50 * crystalTier.getLevel();
        this.description = "A " + crystalTier.getDisplayName().toLowerCase() + " crystal of "
                + crystalType.getDisplayName().toLowerCase() + ".";
    }

    public CrystalType getCrystalType() { return crystalType; }
    public CrystalTier getCrystalTier() { return crystalTier; }
    public String getName() { return name; }
    public ItemRarity getRarity() { return rarity; }
    public ItemType getType() { return type; }
    public int getWeight() { return weight; }
    public int getBaseValue() { return baseValue; }
    public String getDescription() { return description; }

    /**
     * Returns the rarity based on crystal tier.
     */
    private static ItemRarity getRarityForTier(CrystalTier tier) {
        return switch (tier) {
            case ROUGH -> ItemRarity.COMMON;
            case POLISHED -> ItemRarity.UNCOMMON;
            case FLAWLESS -> ItemRarity.RARE;
            case PERFECT -> ItemRarity.EPIC;
            case TRANSCENDENT -> ItemRarity.LEGENDARY;
        };
    }

    /**
     * Calculates the stat bonus provided by this crystal.
     */
    public double getStatBonus(double baseStat) {
        return baseStat * crystalTier.getMultiplier();
    }
}
