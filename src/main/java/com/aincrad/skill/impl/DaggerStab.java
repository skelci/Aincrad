package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Dagger Stab. A quick dagger stab. Chains into a 2-hit combo.
 */
public class DaggerStab extends SwordSkill {

    public DaggerStab() {
        super(0, 20, 2.0);
    }

    @Override
    public String getId() {
        return "dagger_stab";
    }

    @Override
    public String getName() {
        return "Dagger Stab";
    }

    @Override
    public String getDescription() {
        return "A quick dagger stab. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public int getMpCost() {
        return 3;
    }

    @Override
    public int getRequiredLevel() {
        return 1;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
