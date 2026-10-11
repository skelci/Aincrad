package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * EXP Bonus passive skill.
 */
public class ExpBonus extends PassiveSkill {

    @Override
    public String getId() {
        return "exp_bonus";
    }

    @Override
    public String getName() {
        return "EXP Bonus";
    }

    @Override
    public String getDescription() {
        return "+15% experience gained.";
    }

    @Override
    public int getRequiredLevel() {
        return 5;
    }

    @Override
    public void apply(Player player) {
        // TODO: implement passive effect
    }

    @Override
    public void remove(Player player) {
        // TODO: implement passive effect removal
    }

    @Override
    public void cast(Player player) {
        // Passive skills are always active; no cast action needed.
    }
}
