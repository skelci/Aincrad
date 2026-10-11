package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Heal: Restore 30% of maximum HP.
 */
public class Heal extends ActiveSkill {

    @Override
    public String getId() {
        return "heal";
    }

    @Override
    public String getName() {
        return "Heal";
    }

    @Override
    public String getDescription() {
        return "Restore 30% of maximum HP.";
    }

    @Override
    public int getCooldown() {
        return 80;
    }

    @Override
    public int getMpCost() {
        return 20;
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
        // TODO: restore 30% of player's maximum HP
    }
}
