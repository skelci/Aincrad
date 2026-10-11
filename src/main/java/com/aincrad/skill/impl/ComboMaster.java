package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Combo Master passive skill.
 */
public class ComboMaster extends PassiveSkill {

    @Override
    public String getId() {
        return "combo_master";
    }

    @Override
    public String getName() {
        return "Combo Master";
    }

    @Override
    public String getDescription() {
        return "+10% combo window duration.";
    }

    @Override
    public int getRequiredLevel() {
        return 8;
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
