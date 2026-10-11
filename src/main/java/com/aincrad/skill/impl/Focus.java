package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Focus buff: +25% critical hit chance for 10 seconds.
 */
public class Focus extends ActiveSkill {

    @Override
    public String getId() {
        return "focus";
    }

    @Override
    public String getName() {
        return "Focus";
    }

    @Override
    public String getDescription() {
        return "+25% critical hit chance for 10 seconds.";
    }

    @Override
    public int getCooldown() {
        return 90;
    }

    @Override
    public int getMpCost() {
        return 18;
    }

    @Override
    public int getRequiredLevel() {
        return 6;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply +25% critical hit chance buff for 10 seconds
    }
}
