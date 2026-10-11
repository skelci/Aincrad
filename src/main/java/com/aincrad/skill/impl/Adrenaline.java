package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Adrenaline buff: +30% attack speed for 8 seconds.
 */
public class Adrenaline extends ActiveSkill {

    @Override
    public String getId() {
        return "adrenaline";
    }

    @Override
    public String getName() {
        return "Adrenaline";
    }

    @Override
    public String getDescription() {
        return "+30% attack speed for 8 seconds.";
    }

    @Override
    public int getCooldown() {
        return 110;
    }

    @Override
    public int getMpCost() {
        return 22;
    }

    @Override
    public int getRequiredLevel() {
        return 8;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply +30% attack speed buff for 8 seconds
    }
}
