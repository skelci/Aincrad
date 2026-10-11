package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Rapier Lunge. A lunging thrust with extended reach.
 */
public class RapierLunge extends SwordSkill {

    public RapierLunge() {
        super(0, 35, 2.4);
    }

    @Override
    public String getId() {
        return "rapier_lunge";
    }

    @Override
    public String getName() {
        return "Rapier Lunge";
    }

    @Override
    public String getDescription() {
        return "A lunging thrust with extended reach.";
    }

    @Override
    public int getCooldown() {
        return 18;
    }

    @Override
    public int getMpCost() {
        return 6;
    }

    @Override
    public int getRequiredLevel() {
        return 4;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
