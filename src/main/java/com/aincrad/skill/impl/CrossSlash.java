package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * X-shaped slash. 2-hit chain, finisher x2.9.
 */
public class CrossSlash extends SwordSkill {

    public CrossSlash() {
        super(0, 40, 2.9);
    }

    @Override
    public String getId() {
        return "cross_slash";
    }

    @Override
    public String getName() {
        return "Cross Slash";
    }

    @Override
    public String getDescription() {
        return "An X-shaped slash hitting enemies in a cross pattern. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 25;
    }

    @Override
    public int getMpCost() {
        return 8;
    }

    @Override
    public int getRequiredLevel() {
        return 4;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply X-shaped damage pattern
    }
}
