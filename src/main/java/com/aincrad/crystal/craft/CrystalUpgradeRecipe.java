package com.aincrad.crystal.craft;

import com.aincrad.crystal.CrystalItem;
import com.aincrad.crystal.CrystalType;
import com.aincrad.crystal.CrystalTier;
import com.aincrad.craft.CraftingRecipe;
import com.aincrad.craft.CraftingStation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A recipe that upgrades a crystal from one tier to the next.
 * Requires 3 crystals of the lower tier to produce 1 of the next tier.
 */
public class CrystalUpgradeRecipe {
    private final CrystalType type;
    private final CrystalTier fromTier;
    private final CrystalTier toTier;

    public CrystalUpgradeRecipe(CrystalType type, CrystalTier fromTier, CrystalTier toTier) {
        this.type = type;
        this.fromTier = fromTier;
        this.toTier = toTier;
    }

    public CrystalType getType() { return type; }
    public CrystalTier getFromTier() { return fromTier; }
    public CrystalTier getToTier() { return toTier; }

    /**
     * Converts this upgrade recipe into a standard CraftingRecipe.
     * Uses FORGE as the crafting station since no CRYSTAL_FORGE exists.
     */
    public CraftingRecipe toCraftingRecipe() {
        CrystalItem inputCrystal = new CrystalItem(type, fromTier);
        CrystalItem outputCrystal = new CrystalItem(type, toTier);
        List<ItemStack> inputs = List.of(
            new ItemStack(inputCrystal),
            new ItemStack(inputCrystal),
            new ItemStack(inputCrystal)
        );
        return new CraftingRecipe(
            new ItemStack(outputCrystal),
            inputs,
            CraftingStation.FORGE,
            1.0f
        );
    }
}
