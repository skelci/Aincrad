package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Barrier shield: Absorb 50% of incoming damage for 10 seconds.
 */
public class Barrier extends ActiveSkill {

    @Override
    public String getId() {
        return "barrier";
    }

    @Override
    public String getName() {
        return "Barrier";
    }

    @Override
    public String getDescription() {
        return "Absorb 50% of incoming damage for 10 seconds.";
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    @Override
    public int getMpCost() {
        return 20;
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
        // TODO: apply damage absorption shield (50% reduction) for 10 seconds
    }
}
