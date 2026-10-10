package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Overhead vertical slash. 2-hit chain, finisher x2.7.
 */
public class VerticalSlash extends SwordSkill {

    public VerticalSlash() {
        super(0, 40, 2.7);
    }

    @Override
    public String getId() {
        return "vertical_slash";
    }

    @Override
    public String getName() {
        return "Vertical Slash";
    }

    @Override
    public String getDescription() {
        return "An overhead vertical slash. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public int getMpCost() {
        return 5;
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
