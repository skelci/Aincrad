package com.aincrad.skill;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Abstract base for dual-wield sword skills.
 * <p>
 * Extends {@link SwordSkill} with dual-wield-specific logic. A player is
 * considered dual-wielding when both the main hand and off hand contain
 * items. Skills using this base receive a damage bonus when the player is
 * dual-wielding.
 * </p>
 */
public abstract class DualWieldSkill extends SwordSkill {

    /**
     * Creates a new dual-wield skill.
     *
     * @param comboIndex        position in the combo chain
     * @param comboWindow       ticks allowed before the chain breaks
     * @param finisherMultiplier damage multiplier when this is the finisher
     */
    protected DualWieldSkill(int comboIndex, int comboWindow, double finisherMultiplier) {
        super(comboIndex, comboWindow, finisherMultiplier);
    }

    /**
     * Checks whether the player is currently dual-wielding (both hands have items).
     *
     * @param player the player to check
     * @return {@code true} if both main hand and off hand are non-empty
     */
    public boolean isDualWielding(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return !mainHand.isEmpty() && !offHand.isEmpty();
    }

    /**
     * Returns the damage multiplier when dual-wielding.
     * <p>
     * Currently returns a flat {@code 1.5} bonus. TODO: calculate based on
     * both weapon damage values for a more dynamic scaling system.
     * </p>
     *
     * @param player the player
     * @return the dual-wield damage multiplier
     */
    public double getDualWieldMultiplier(Player player) {
        // TODO: calculate based on both weapon damage values
        return 1.5;
    }
}
