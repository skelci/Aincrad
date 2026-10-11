package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * A 360 degree spinning attack hitting all nearby enemies.
 */
public class SpinningBlades extends DualWieldSkill {

    public SpinningBlades() {
        super(0, 50, 3.0);
    }

    @Override
    public String getId() {
        return "spinning_blades";
    }

    @Override
    public String getName() {
        return "Spinning Blades";
    }

    @Override
    public String getDescription() {
        return "A 360 degree spinning attack hitting all nearby enemies.";
    }

    @Override
    public int getCooldown() {
        return 30;
    }

    @Override
    public int getMpCost() {
        return 15;
    }

    @Override
    public int getRequiredLevel() {
        return 10;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply 360-degree AoE damage to all nearby enemies
    }
}
