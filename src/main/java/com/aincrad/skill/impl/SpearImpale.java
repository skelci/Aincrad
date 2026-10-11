package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Spear Impale. An impaling thrust that pierces through enemies.
 */
public class SpearImpale extends SwordSkill {

    public SpearImpale() {
        super(0, 40, 2.6);
    }

    @Override
    public String getId() {
        return "spear_impale";
    }

    @Override
    public String getName() {
        return "Spear Impale";
    }

    @Override
    public String getDescription() {
        return "An impaling thrust that pierces through enemies.";
    }

    @Override
    public int getCooldown() {
        return 22;
    }

    @Override
    public int getMpCost() {
        return 7;
    }

    @Override
    public int getRequiredLevel() {
        return 5;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
