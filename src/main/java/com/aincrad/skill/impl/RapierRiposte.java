package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Rapier Riposte. A counter-attack that punishes enemy aggression.
 */
public class RapierRiposte extends SwordSkill {

    public RapierRiposte() {
        super(1, 35, 1.0);
    }

    @Override
    public String getId() {
        return "rapier_riposte";
    }

    @Override
    public String getName() {
        return "Rapier Riposte";
    }

    @Override
    public String getDescription() {
        return "A counter-attack that punishes enemy aggression.";
    }

    @Override
    public int getCooldown() {
        return 14;
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
