package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * HP Boost passive skill.
 */
public class HpBoost extends PassiveSkill {

    @Override
    public String getId() {
        return "hp_boost";
    }

    @Override
    public String getName() {
        return "HP Boost";
    }

    @Override
    public String getDescription() {
        return "+20 maximum HP.";
    }

    @Override
    public int getRequiredLevel() {
        return 1;
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
