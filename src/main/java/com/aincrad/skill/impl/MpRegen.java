package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * MP Regen passive skill.
 */
public class MpRegen extends PassiveSkill {

    @Override
    public String getId() {
        return "mp_regen";
    }

    @Override
    public String getName() {
        return "MP Regen";
    }

    @Override
    public String getDescription() {
        return "Regenerate 2 MP per second.";
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
