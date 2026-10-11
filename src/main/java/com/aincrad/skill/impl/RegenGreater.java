package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Regen Greater passive skill.
 */
public class RegenGreater extends PassiveSkill {

    @Override
    public String getId() {
        return "regen_greater";
    }

    @Override
    public String getName() {
        return "Regen Greater";
    }

    @Override
    public String getDescription() {
        return "Regenerate 2% HP per second.";
    }

    @Override
    public int getRequiredLevel() {
        return 10;
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
