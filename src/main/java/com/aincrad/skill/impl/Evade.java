package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Evade passive skill.
 */
public class Evade extends PassiveSkill {

    @Override
    public String getId() {
        return "evade";
    }

    @Override
    public String getName() {
        return "Evade";
    }

    @Override
    public String getDescription() {
        return "+5% dodge chance.";
    }

    @Override
    public int getRequiredLevel() {
        return 2;
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
