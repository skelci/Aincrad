package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Axe Cleave. A cleaving strike that splits armor.
 */
public class AxeCleave extends SwordSkill {

    public AxeCleave() {
        super(1, 40, 1.0);
    }

    @Override
    public String getId() {
        return "axe_cleave";
    }

    @Override
    public String getName() {
        return "Axe Cleave";
    }

    @Override
    public String getDescription() {
        return "A cleaving strike that splits armor.";
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public int getMpCost() {
        return 6;
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
