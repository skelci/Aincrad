package com.aincrad.crystal.craft;

import com.aincrad.crystal.CrystalType;
import com.aincrad.crystal.CrystalTier;
import com.aincrad.craft.CraftingManager;

/**
 * Registers all crystal upgrade recipes with the CraftingManager.
 * 6 types × 4 upgrade paths = 24 recipes.
 */
public final class CrystalCraftingManager {

    private CrystalCraftingManager() {}

    /**
     * Registers all crystal upgrade recipes (24 total).
     */
    public static void registerAll() {
        for (CrystalType type : CrystalType.values()) {
            for (CrystalTier tier : CrystalTier.values()) {
                CrystalTier next = getNextTier(tier);
                if (next != null) {
                    CrystalUpgradeRecipe recipe = new CrystalUpgradeRecipe(type, tier, next);
                    CraftingManager.register(recipe.toCraftingRecipe());
                }
            }
        }
    }

    /**
     * Returns the next tier above the given tier, or null if already max.
     */
    public static CrystalTier getNextTier(CrystalTier tier) {
        return switch (tier) {
            case ROUGH -> CrystalTier.POLISHED;
            case POLISHED -> CrystalTier.FLAWLESS;
            case FLAWLESS -> CrystalTier.PERFECT;
            case PERFECT -> CrystalTier.TRANSCENDENT;
            case TRANSCENDENT -> null;
        };
    }
}
