package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * A wide arc strike with both weapons.
 */
public class TwinCleave extends DualWieldSkill {

    public TwinCleave() {
        super(1, 40, 1.0);
    }

    @Override
    public String getId() {
        return "twin_cleave";
    }

    @Override
    public String getName() {
        return "Twin Cleave";
    }

    @Override
    public String getDescription() {
        return "A wide arc strike with both weapons.";
    }

    @Override
    public int getCooldown() {
        return 18;
    }

    @Override
    public int getMpCost() {
        return 8;
    }

    @Override
    public int getRequiredLevel() {
        return 5;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply wide arc damage to entities around player
    }
}
