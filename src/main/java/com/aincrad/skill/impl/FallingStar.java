package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Diving attack from above. AoE damage x2.5.
 */
public class FallingStar extends SwordSkill {

    public FallingStar() {
        super(0, 50, 2.5);
    }

    @Override
    public String getId() {
        return "falling_star";
    }

    @Override
    public String getName() {
        return "Falling Star";
    }

    @Override
    public String getDescription() {
        return "A diving attack from above dealing AoE damage.";
    }

    @Override
    public int getCooldown() {
        return 40;
    }

    @Override
    public int getMpCost() {
        return 12;
    }

    @Override
    public int getRequiredLevel() {
        return 10;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply AoE damage below player
    }
}
