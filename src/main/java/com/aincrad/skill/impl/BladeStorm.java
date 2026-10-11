package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * A rapid multi-hit attack with both weapons.
 */
public class BladeStorm extends DualWieldSkill {

    public BladeStorm() {
        super(0, 55, 3.5);
    }

    @Override
    public String getId() {
        return "blade_storm";
    }

    @Override
    public String getName() {
        return "Blade Storm";
    }

    @Override
    public String getDescription() {
        return "A rapid multi-hit attack with both weapons.";
    }

    @Override
    public int getCooldown() {
        return 35;
    }

    @Override
    public int getMpCost() {
        return 18;
    }

    @Override
    public int getRequiredLevel() {
        return 13;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply rapid multi-hit damage to target
    }
}
