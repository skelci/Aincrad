package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Slow debuff: Target moves 30% slower for 8 seconds.
 */
public class Slow extends ActiveSkill {

    @Override
    public String getId() {
        return "slow";
    }

    @Override
    public String getName() {
        return "Slow";
    }

    @Override
    public String getDescription() {
        return "Target moves 30% slower for 8 seconds.";
    }

    @Override
    public int getCooldown() {
        return 50;
    }

    @Override
    public int getMpCost() {
        return 12;
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply -30% move speed debuff to target for 8 seconds
    }
}
