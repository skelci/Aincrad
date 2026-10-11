package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Axe Smash. A devastating smash that crushes defenses.
 */
public class AxeSmash extends SwordSkill {

    public AxeSmash() {
        super(0, 45, 3.0);
    }

    @Override
    public String getId() {
        return "axe_smash";
    }

    @Override
    public String getName() {
        return "Axe Smash";
    }

    @Override
    public String getDescription() {
        return "A devastating smash that crushes defenses.";
    }

    @Override
    public int getCooldown() {
        return 30;
    }

    @Override
    public int getMpCost() {
        return 10;
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
