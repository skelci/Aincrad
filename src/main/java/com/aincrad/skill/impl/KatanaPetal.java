package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Katana Petal. A falling petal strike that confuses the target.
 */
public class KatanaPetal extends SwordSkill {

    public KatanaPetal() {
        super(1, 35, 1.0);
    }

    @Override
    public String getId() {
        return "katana_petal";
    }

    @Override
    public String getName() {
        return "Katana Petal";
    }

    @Override
    public String getDescription() {
        return "A falling petal strike that confuses the target.";
    }

    @Override
    public int getCooldown() {
        return 16;
    }

    @Override
    public int getMpCost() {
        return 6;
    }

    @Override
    public int getRequiredLevel() {
        return 7;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
