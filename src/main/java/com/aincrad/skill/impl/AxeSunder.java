package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Axe Sunder. A sundering blow that breaks armor.
 */
public class AxeSunder extends SwordSkill {

    public AxeSunder() {
        super(1, 45, 1.0);
    }

    @Override
    public String getId() {
        return "axe_sunder";
    }

    @Override
    public String getName() {
        return "Axe Sunder";
    }

    @Override
    public String getDescription() {
        return "A sundering blow that breaks armor.";
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
        return 6;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
