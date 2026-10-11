package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Rapier Thrust. A precise rapier thrust. Chains into a 2-hit combo.
 */
public class RapierThrust extends SwordSkill {

    public RapierThrust() {
        super(0, 30, 2.2);
    }

    @Override
    public String getId() {
        return "rapier_thrust";
    }

    @Override
    public String getName() {
        return "Rapier Thrust";
    }

    @Override
    public String getDescription() {
        return "A precise rapier thrust. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 15;
    }

    @Override
    public int getMpCost() {
        return 4;
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
