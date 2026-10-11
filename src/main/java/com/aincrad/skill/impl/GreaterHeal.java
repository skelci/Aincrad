package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Greater Heal: Restore 60% of maximum HP.
 */
public class GreaterHeal extends ActiveSkill {

    @Override
    public String getId() {
        return "greater_heal";
    }

    @Override
    public String getName() {
        return "Greater Heal";
    }

    @Override
    public String getDescription() {
        return "Restore 60% of maximum HP.";
    }

    @Override
    public int getCooldown() {
        return 150;
    }

    @Override
    public int getMpCost() {
        return 40;
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
        // TODO: restore 60% of player's maximum HP
    }
}
