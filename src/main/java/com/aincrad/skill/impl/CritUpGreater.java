package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Crit Up Greater passive skill.
 */
public class CritUpGreater extends PassiveSkill {

    @Override
    public String getId() {
        return "crit_up_greater";
    }

    @Override
    public String getName() {
        return "Crit Up Greater";
    }

    @Override
    public String getDescription() {
        return "+12% critical hit chance.";
    }

    @Override
    public int getRequiredLevel() {
        return 12;
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
