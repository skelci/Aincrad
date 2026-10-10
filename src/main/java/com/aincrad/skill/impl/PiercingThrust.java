package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Forward thrust. 1-hit, high single-target damage x3.2.
 */
public class PiercingThrust extends SwordSkill {

    public PiercingThrust() {
        super(0, 30, 3.2);
    }

    @Override
    public String getId() {
        return "piercing_thrust";
    }

    @Override
    public String getName() {
        return "Piercing Thrust";
    }

    @Override
    public String getDescription() {
        return "A powerful forward thrust dealing high single-target damage.";
    }

    @Override
    public int getCooldown() {
        return 35;
    }

    @Override
    public int getMpCost() {
        return 10;
    }

    @Override
    public int getRequiredLevel() {
        return 8;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply high single-target damage in front
    }
}
