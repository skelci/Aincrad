package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Spear Skewer. A skewering strike that pins the target.
 */
public class SpearSkewer extends SwordSkill {

    public SpearSkewer() {
        super(1, 40, 1.0);
    }

    @Override
    public String getId() {
        return "spear_skewer";
    }

    @Override
    public String getName() {
        return "Spear Skewer";
    }

    @Override
    public String getDescription() {
        return "A skewering strike that pins the target.";
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
        return 5;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
