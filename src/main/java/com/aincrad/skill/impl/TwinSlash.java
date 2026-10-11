package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Basic dual-wield attack striking with both weapons.
 */
public class TwinSlash extends DualWieldSkill {

    public TwinSlash() {
        super(0, 40, 2.5);
    }

    @Override
    public String getId() {
        return "twin_slash";
    }

    @Override
    public String getName() {
        return "Twin Slash";
    }

    @Override
    public String getDescription() {
        return "A basic dual-wield attack striking with both weapons.";
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public int getMpCost() {
        return 10;
    }

    @Override
    public int getRequiredLevel() {
        return 5;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player with both weapons
    }
}
