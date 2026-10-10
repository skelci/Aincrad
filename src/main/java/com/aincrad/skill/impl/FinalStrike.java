package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Ultimate skill. Requires full combo, x4.0 damage.
 */
public class FinalStrike extends SwordSkill {

    public FinalStrike() {
        super(0, 80, 4.0);
    }

    @Override
    public String getId() {
        return "final_strike";
    }

    @Override
    public String getName() {
        return "Final Strike";
    }

    @Override
    public String getDescription() {
        return "An ultimate skill requiring a full combo. Deals massive damage.";
    }

    @Override
    public int getCooldown() {
        return 60;
    }

    @Override
    public int getMpCost() {
        return 20;
    }

    @Override
    public int getRequiredLevel() {
        return 15;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply massive damage to all nearby enemies
    }
}
