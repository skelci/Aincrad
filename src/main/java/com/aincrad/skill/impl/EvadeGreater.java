package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Evade Greater passive skill.
 */
public class EvadeGreater extends PassiveSkill {

    @Override
    public String getId() {
        return "evade_greater";
    }

    @Override
    public String getName() {
        return "Evade Greater";
    }

    @Override
    public String getDescription() {
        return "+12% dodge chance.";
    }

    @Override
    public int getRequiredLevel() {
        return 9;
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
