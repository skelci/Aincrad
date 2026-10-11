package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Dagger Backstab. A sneaky strike from behind.
 */
public class DaggerBackstab extends SwordSkill {

    public DaggerBackstab() {
        super(1, 20, 1.0);
    }

    @Override
    public String getId() {
        return "dagger_backstab";
    }

    @Override
    public String getName() {
        return "Dagger Backstab";
    }

    @Override
    public String getDescription() {
        return "A sneaky strike from behind.";
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
