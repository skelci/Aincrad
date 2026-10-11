package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Water Resist passive skill.
 */
public class WaterResist extends PassiveSkill {

    @Override
    public String getId() {
        return "water_resist";
    }

    @Override
    public String getName() {
        return "Water Resist";
    }

    @Override
    public String getDescription() {
        return "Reduce water damage by 20%.";
    }

    @Override
    public int getRequiredLevel() {
        return 4;
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
