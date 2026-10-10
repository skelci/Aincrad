package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Rapid multi-hit attack. 5-hit chain, finisher x3.5.
 */
public class BladeDance extends SwordSkill {

    public BladeDance() {
        super(0, 60, 3.5);
    }

    @Override
    public String getId() {
        return "blade_dance";
    }

    @Override
    public String getName() {
        return "Blade Dance";
    }

    @Override
    public String getDescription() {
        return "A rapid multi-hit attack. Chains into a 5-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 45;
    }

    @Override
    public int getMpCost() {
        return 15;
    }

    @Override
    public int getRequiredLevel() {
        return 12;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply rapid multi-hit damage
    }
}
