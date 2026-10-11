package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Stun debuff: Target is unable to move for 3 seconds.
 */
public class Stun extends ActiveSkill {

    @Override
    public String getId() {
        return "stun";
    }

    @Override
    public String getName() {
        return "Stun";
    }

    @Override
    public String getDescription() {
        return "Target is unable to move for 3 seconds.";
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    @Override
    public int getMpCost() {
        return 25;
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
        // TODO: stun target, preventing movement for 3 seconds
    }
}
