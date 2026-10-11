package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Katana Draw. A quick iaido draw strike. Chains into a 2-hit combo.
 */
public class KatanaDraw extends SwordSkill {

    public KatanaDraw() {
        super(0, 30, 2.5);
    }

    @Override
    public String getId() {
        return "katana_draw";
    }

    @Override
    public String getName() {
        return "Katana Draw";
    }

    @Override
    public String getDescription() {
        return "A quick iaido draw strike. Chains into a 2-hit combo.";
    }

    @Override
    public int getCooldown() {
        return 16;
    }

    @Override
    public int getMpCost() {
        return 5;
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
