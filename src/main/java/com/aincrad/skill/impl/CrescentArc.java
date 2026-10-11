package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Wide arc slash. 2-hit chain, finisher x2.8.
 */
public class CrescentArc extends SwordSkill {

    public CrescentArc() {
        super(0, 40, 2.8);
    }

    @Override
    public String getId() {
        return "crescent_arc";
    }

    @Override
    public String getName() {
        return "Crescent Arc";
    }

    @Override
    public String getDescription() {
        return "A wide arc slash hitting enemies in a broad area. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 25;
    }

    @Override
    public int getMpCost() {
        return 7;
    }

    @Override
    public int getRequiredLevel() {
        return 3;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply wide arc damage
    }
}
