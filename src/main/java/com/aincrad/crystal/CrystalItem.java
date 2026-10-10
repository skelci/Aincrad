package com.aincrad.crystal;

import com.aincrad.item.AincradItem;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

/**
 * A crystal item that can be slotted into the Crystal equipment slot.
 * 6 types × 5 tiers = 30 crystal variants.
 */
public class CrystalItem extends AincradItem {

    private final CrystalType crystalType;
    private final CrystalTier crystalTier;

    public CrystalItem(CrystalType crystalType, CrystalTier crystalTier) {
        super(crystalTier.getDisplayName() + " " + crystalType.getDisplayName() + " Crystal",
              getRarityForTier(crystalTier), ItemType.CRYSTAL, 5, 50 * crystalTier.getLevel(),
              "A " + crystalTier.getDisplayName().toLowerCase() + " crystal of "
              + crystalType.getDisplayName().toLowerCase() + ".");
        this.crystalType = crystalType;
        this.crystalTier = crystalTier;
    }

    public CrystalType getCrystalType() { return crystalType; }
    public CrystalTier getCrystalTier() { return crystalTier; }

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
