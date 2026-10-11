package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Leap mobility: Perform a high jump with AoE landing damage.
 */
public class Leap extends ActiveSkill {

    @Override
    public String getId() {
        return "leap";
    }

    @Override
    public String getName() {
        return "Leap";
    }

    @Override
    public String getDescription() {
        return "Perform a high jump with AoE landing damage.";
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
        return 4;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: launch player into the air and deal AoE damage on landing
    }
}
