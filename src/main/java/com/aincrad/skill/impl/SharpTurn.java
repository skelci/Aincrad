package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Spinning slash. 3-hit chain, finisher x3.0.
 */
public class SharpTurn extends SwordSkill {

    public SharpTurn() {
        super(0, 50, 3.0);
    }

    @Override
    public String getId() {
        return "sharp_turn";
    }

    @Override
    public String getName() {
        return "Sharp Turn";
    }

    @Override
    public String getDescription() {
        return "A spinning slash that hits all nearby enemies. Chains into a 3-hit combo.";
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
        return 5;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply AoE damage around player
    }
}
