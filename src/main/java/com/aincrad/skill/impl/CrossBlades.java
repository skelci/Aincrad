package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * An X-pattern strike with both weapons.
 */
public class CrossBlades extends DualWieldSkill {

    public CrossBlades() {
        super(0, 45, 2.8);
    }

    @Override
    public String getId() {
        return "cross_blades";
    }

    @Override
    public String getName() {
        return "Cross Blades";
    }

    @Override
    public String getDescription() {
        return "An X-pattern strike with both weapons.";
    }

    @Override
    public int getCooldown() {
        return 25;
    }

    @Override
    public int getMpCost() {
        return 12;
    }

    @Override
    public int getRequiredLevel() {
        return 8;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply X-pattern strike damage
    }
}
