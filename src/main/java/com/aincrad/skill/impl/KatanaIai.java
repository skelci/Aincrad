package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Katana Iai. A precise iaido technique.
 */
public class KatanaIai extends SwordSkill {

    public KatanaIai() {
        super(1, 30, 1.0);
    }

    @Override
    public String getId() {
        return "katana_iai";
    }

    @Override
    public String getName() {
        return "Katana Iai";
    }

    @Override
    public String getDescription() {
        return "A precise iaido technique.";
    }

    @Override
    public int getCooldown() {
        return 14;
    }

    @Override
    public int getMpCost() {
        return 4;
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
