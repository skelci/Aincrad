package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Iron Will buff: +30% defense for 15 seconds.
 */
public class IronWill extends ActiveSkill {

    @Override
    public String getId() {
        return "iron_will";
    }

    @Override
    public String getName() {
        return "Iron Will";
    }

    @Override
    public String getDescription() {
        return "+30% defense for 15 seconds.";
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    @Override
    public int getMpCost() {
        return 15;
    }

    @Override
    public int getRequiredLevel() {
        return 4;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply +30% defense buff for 15 seconds
    }
}
