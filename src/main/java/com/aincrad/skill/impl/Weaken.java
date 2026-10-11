package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Weaken debuff: Target deals 25% less damage for 10 seconds.
 */
public class Weaken extends ActiveSkill {

    @Override
    public String getId() {
        return "weaken";
    }

    @Override
    public String getName() {
        return "Weaken";
    }

    @Override
    public String getDescription() {
        return "Target deals 25% less damage for 10 seconds.";
    }

    @Override
    public int getCooldown() {
        return 60;
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
        // TODO: apply -25% damage debuff to target for 10 seconds
    }
}
