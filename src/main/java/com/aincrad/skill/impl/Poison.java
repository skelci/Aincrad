package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Poison debuff: Target takes poison damage over 12 seconds.
 */
public class Poison extends ActiveSkill {

    @Override
    public String getId() {
        return "poison";
    }

    @Override
    public String getName() {
        return "Poison";
    }

    @Override
    public String getDescription() {
        return "Target takes poison damage over 12 seconds.";
    }

    @Override
    public int getCooldown() {
        return 70;
    }

    @Override
    public int getMpCost() {
        return 18;
    }

    @Override
    public int getRequiredLevel() {
        return 5;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply poison damage over time to target for 12 seconds
    }
}
