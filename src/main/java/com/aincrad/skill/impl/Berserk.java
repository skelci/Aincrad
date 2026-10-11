package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Berserk buff: +50% damage, -20% defense for 10 seconds.
 */
public class Berserk extends ActiveSkill {

    @Override
    public String getId() {
        return "berserk";
    }

    @Override
    public String getName() {
        return "Berserk";
    }

    @Override
    public String getDescription() {
        return "+50% damage, -20% defense for 10 seconds.";
    }

    @Override
    public int getCooldown() {
        return 120;
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
        // TODO: apply +50% damage buff and -20% defense debuff for 10 seconds
    }
}
