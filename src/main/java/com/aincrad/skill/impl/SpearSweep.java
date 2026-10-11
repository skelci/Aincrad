package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Spear Sweep. A sweeping strike that knocks back enemies.
 */
public class SpearSweep extends SwordSkill {

    public SpearSweep() {
        super(1, 35, 1.0);
    }

    @Override
    public String getId() {
        return "spear_sweep";
    }

    @Override
    public String getName() {
        return "Spear Sweep";
    }

    @Override
    public String getDescription() {
        return "A sweeping strike that knocks back enemies.";
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
