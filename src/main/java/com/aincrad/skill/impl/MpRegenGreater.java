package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * MP Regen Greater passive skill.
 */
public class MpRegenGreater extends PassiveSkill {

    @Override
    public String getId() {
        return "mp_regen_greater";
    }

    @Override
    public String getName() {
        return "MP Regen Greater";
    }

    @Override
    public String getDescription() {
        return "Regenerate 5 MP per second.";
    }

    @Override
    public int getRequiredLevel() {
        return 11;
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
