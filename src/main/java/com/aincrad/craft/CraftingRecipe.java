package com.aincrad.craft;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A crafting recipe that defines inputs, output, required station, and success rate.
 */
public class CraftingRecipe {

    private final ItemStack output;
    private final List<ItemStack> input;
    private final CraftingStation requiredStation;
    private final float successRate;

    public CraftingRecipe(ItemStack output, List<ItemStack> input,
                          CraftingStation requiredStation, float successRate) {
        this.output = output;
        this.input = List.copyOf(input);
        this.requiredStation = requiredStation;
        this.successRate = successRate;
    }

    public ItemStack getOutput() { return output; }
    public List<ItemStack> getInput() { return input; }
    public CraftingStation getRequiredStation() { return requiredStation; }
    public float getSuccessRate() { return successRate; }
}
