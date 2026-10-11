package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Axe Chop. A heavy axe chop. Chains into a 2-hit combo.
 */
public class AxeChop extends SwordSkill {

    public AxeChop() {
        super(0, 40, 2.8);
    }

    @Override
    public String getId() {
        return "axe_chop";
    }

    @Override
    public String getName() {
        return "Axe Chop";
    }

    @Override
    public String getDescription() {
        return "A heavy axe chop. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 25;
    }

    @Override
    public int getMpCost() {
        return 8;
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
