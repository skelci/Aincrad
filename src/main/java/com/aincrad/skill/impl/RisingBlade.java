package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Upward slash that launches enemies. 2-hit chain.
 */
public class RisingBlade extends SwordSkill {

    public RisingBlade() {
        super(0, 40, 2.5);
    }

    @Override
    public String getId() {
        return "rising_blade";
    }

    @Override
    public String getName() {
        return "Rising Blade";
    }

    @Override
    public String getDescription() {
        return "An upward slash that launches enemies into the air. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 30;
    }

    @Override
    public int getMpCost() {
        return 8;
    }

    @Override
    public int getRequiredLevel() {
        return 6;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage and launch enemies upward
    }
}
