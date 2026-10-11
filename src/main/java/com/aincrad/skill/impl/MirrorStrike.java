package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Copies the target's last attack against them.
 */
public class MirrorStrike extends DualWieldSkill {

    public MirrorStrike() {
        super(0, 50, 3.0);
    }

    @Override
    public String getId() {
        return "mirror_strike";
    }

    @Override
    public String getName() {
        return "Mirror Strike";
    }

    @Override
    public String getDescription() {
        return "Copies the target's last attack against them.";
    }

    @Override
    public int getCooldown() {
        return 30;
    }

    @Override
    public int getMpCost() {
        return 16;
    }

    @Override
    public int getRequiredLevel() {
        return 9;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: replicate the target's last used skill against them
    }
}
