package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Dagger Shank. A vicious shank that causes bleeding.
 */
public class DaggerShank extends SwordSkill {

    public DaggerShank() {
        super(0, 25, 2.2);
    }

    @Override
    public String getId() {
        return "dagger_shank";
    }

    @Override
    public String getName() {
        return "Dagger Shank";
    }

    @Override
    public String getDescription() {
        return "A vicious shank that causes bleeding.";
    }

    @Override
    public int getCooldown() {
        return 12;
    }

    @Override
    public int getMpCost() {
        return 4;
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
