package com.aincrad.craft;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static registry and manager for crafting recipes.
 */
public final class CraftingManager {

    private static final List<CraftingRecipe> RECIPES = new ArrayList<>();

    private CraftingManager() {}

    /**
     * Registers a crafting recipe.
     */
    public static void register(CraftingRecipe recipe) {
        RECIPES.add(recipe);
    }

    /**
     * Attempts to craft using the given inputs and station.
     * Returns the output ItemStack on success, or ItemStack.EMPTY on failure.
     */
    public static ItemStack craft(List<ItemStack> inputs, CraftingStation station) {
        for (CraftingRecipe recipe : RECIPES) {
            if (recipe.getRequiredStation() == station && matches(inputs, recipe.getInput())) {
                if (Math.random() < recipe.getSuccessRate()) {
                    return recipe.getOutput().copy();
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns an unmodifiable view of all registered recipes.
     */
    public static List<CraftingRecipe> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    private static boolean matches(List<ItemStack> inputs, List<ItemStack> recipeInputs) {
        if (inputs.size() != recipeInputs.size()) return false;
        for (int i = 0; i < inputs.size(); i++) {
            if (!ItemStack.isSameItemSameComponents(inputs.get(i), recipeInputs.get(i))) {
                return false;
            }
        }
        return true;
    }
}
