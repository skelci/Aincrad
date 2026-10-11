package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Spear Thrust. A long-reach spear thrust. Chains into a 2-hit combo.
 */
public class SpearThrust extends SwordSkill {

    public SpearThrust() {
        super(0, 35, 2.3);
    }

    @Override
    public String getId() {
        return "spear_thrust";
    }

    @Override
    public String getName() {
        return "Spear Thrust";
    }

    @Override
    public String getDescription() {
        return "A long-reach spear thrust. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 18;
    }

    @Override
    public int getMpCost() {
        return 5;
    }

    @Override
    public int getRequiredLevel() {
        return 2;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
