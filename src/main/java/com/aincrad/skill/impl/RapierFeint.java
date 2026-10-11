package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Rapier Feint. A feinting strike that sets up the next attack.
 */
public class RapierFeint extends SwordSkill {

    public RapierFeint() {
        super(1, 30, 1.0);
    }

    @Override
    public String getId() {
        return "rapier_feint";
    }

    @Override
    public String getName() {
        return "Rapier Feint";
    }

    @Override
    public String getDescription() {
        return "A feinting strike that sets up the next attack.";
    }

    @Override
    public int getCooldown() {
        return 12;
    }

    @Override
    public int getMpCost() {
        return 3;
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
