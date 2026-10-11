package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Reflect shield: Reflect 30% of damage back to attacker for 8 seconds.
 */
public class Reflect extends ActiveSkill {

    @Override
    public String getId() {
        return "reflect";
    }

    @Override
    public String getName() {
        return "Reflect";
    }

    @Override
    public String getDescription() {
        return "Reflect 30% of damage back to attacker for 8 seconds.";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public int getMpCost() {
        return 25;
    }

    @Override
    public int getRequiredLevel() {
        return 7;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage reflection (30%) for 8 seconds
    }
}
