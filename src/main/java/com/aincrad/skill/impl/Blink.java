package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Blink mobility: Teleport 10 blocks forward.
 */
public class Blink extends ActiveSkill {

    @Override
    public String getId() {
        return "blink";
    }

    @Override
    public String getName() {
        return "Blink";
    }

    @Override
    public String getDescription() {
        return "Teleport 10 blocks forward.";
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public int getMpCost() {
        return 15;
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
        // TODO: teleport player 10 blocks forward in look direction
    }
}
