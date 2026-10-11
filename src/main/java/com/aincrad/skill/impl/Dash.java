package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Dash mobility: Quickly dash forward 5 blocks.
 */
public class Dash extends ActiveSkill {

    @Override
    public String getId() {
        return "dash";
    }

    @Override
    public String getName() {
        return "Dash";
    }

    @Override
    public String getDescription() {
        return "Quickly dash forward 5 blocks.";
    }

    @Override
    public int getCooldown() {
        return 30;
    }

    @Override
    public int getMpCost() {
        return 8;
    }

    @Override
    public int getRequiredLevel() {
        return 2;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: dash player forward 5 blocks in look direction
    }
}
