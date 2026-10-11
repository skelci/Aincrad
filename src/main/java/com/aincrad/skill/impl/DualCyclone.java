package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * A pulling vortex that damages all enemies.
 */
public class DualCyclone extends DualWieldSkill {

    public DualCyclone() {
        super(0, 55, 3.8);
    }

    @Override
    public String getId() {
        return "dual_cyclone";
    }

    @Override
    public String getName() {
        return "Dual Cyclone";
    }

    @Override
    public String getDescription() {
        return "A pulling vortex that damages all enemies.";
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public int getMpCost() {
        return 20;
    }

    @Override
    public int getRequiredLevel() {
        return 15;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: create a vortex that pulls enemies in and deals damage
    }
}
