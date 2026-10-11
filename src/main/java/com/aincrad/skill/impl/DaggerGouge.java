package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Dagger Gouge. A gouging strike that wounds the target.
 */
public class DaggerGouge extends SwordSkill {

    public DaggerGouge() {
        super(1, 25, 1.0);
    }

    @Override
    public String getId() {
        return "dagger_gouge";
    }

    @Override
    public String getName() {
        return "Dagger Gouge";
    }

    @Override
    public String getDescription() {
        return "A gouging strike that wounds the target.";
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
