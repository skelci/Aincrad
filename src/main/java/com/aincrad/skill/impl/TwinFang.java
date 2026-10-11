package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * A dual strike that causes bleeding.
 */
public class TwinFang extends DualWieldSkill {

    public TwinFang() {
        super(0, 45, 3.2);
    }

    @Override
    public String getId() {
        return "twin_fang";
    }

    @Override
    public String getName() {
        return "Twin Fang";
    }

    @Override
    public String getDescription() {
        return "A dual strike that causes bleeding.";
    }

    @Override
    public int getCooldown() {
        return 28;
    }

    @Override
    public int getMpCost() {
        return 14;
    }

    @Override
    public int getRequiredLevel() {
        return 11;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage and inflict bleeding status effect
    }
}
