package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Swiftness buff: +40% move speed for 12 seconds.
 */
public class Swiftness extends ActiveSkill {

    @Override
    public String getId() {
        return "swiftness";
    }

    @Override
    public String getName() {
        return "Swiftness";
    }

    @Override
    public String getDescription() {
        return "+40% move speed for 12 seconds.";
    }

    @Override
    public int getCooldown() {
        return 80;
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
        // TODO: apply +40% move speed buff for 12 seconds
    }
}
