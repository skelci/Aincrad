package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * War Cry buff: Party members gain +15% damage for 20 seconds.
 */
public class WarCry extends ActiveSkill {

    @Override
    public String getId() {
        return "war_cry";
    }

    @Override
    public String getName() {
        return "War Cry";
    }

    @Override
    public String getDescription() {
        return "Party members gain +15% damage for 20 seconds.";
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public int getMpCost() {
        return 30;
    }

    @Override
    public int getRequiredLevel() {
        return 10;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply +15% damage buff to all party members for 20 seconds
    }
}
