package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Regen passive skill.
 */
public class Regen extends PassiveSkill {

    @Override
    public String getId() {
        return "regen";
    }

    @Override
    public String getName() {
        return "Regen";
    }

    @Override
    public String getDescription() {
        return "Regenerate 1% HP per second.";
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
