package com.aincrad.enhance;

import net.minecraft.world.item.ItemStack;

import java.util.Random;

/**
 * Core enhancement framework for Aincrad items.
 * Handles enhancement rolls, success/break rates, and stat bonus calculation.
 */
public final class EnhancementManager {

    private static final Random RANDOM = new Random();

    private EnhancementManager() {
        // Utility class
    }

    /**
     * Attempt to enhance an ItemStack to the given target level.
     *
     * @param stack       the item to enhance
     * @param targetLevel the desired enhancement level (0–15)
     * @return the result of the attempt
     */
    public static EnhancementResult enhance(ItemStack stack, int targetLevel) {
        EnhanceLevel level = EnhanceLevel.fromInt(targetLevel);

        double roll = RANDOM.nextDouble();

        if (roll < level.getSuccessRate()) {
            return EnhancementResult.SUCCESS;
        }

        double breakRoll = RANDOM.nextDouble();
        if (breakRoll < level.getBreakChance()) {
            return EnhancementResult.BREAK;
        }

        return EnhancementResult.FAIL;
    }

    /**
     * Get the success rate for enhancing to the given level.
     *
     * @param level the target enhancement level (0–15)
     * @return probability 0.0–1.0
     */
    public static double getSuccessRate(int level) {
        return EnhanceLevel.fromInt(level).getSuccessRate();
    }

    /**
     * Get the break chance for enhancing to the given level.
     *
     * @param level the target enhancement level (0–15)
     * @return probability 0.0–1.0
     */
    public static double getBreakChance(int level) {
        return EnhanceLevel.fromInt(level).getBreakChance();
    }

    /**
     * Calculate the stat bonus multiplier for an item at the given level.
     *
     * @param stack the item (reserved for future per-item logic)
     * @param level the current enhancement level (0–15)
     * @return the stat bonus multiplier
     */
    public static double applyBonus(ItemStack stack, int level) {
        return EnhanceLevel.fromInt(level).getStatBonusMultiplier();
    }
}
