package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Wind Walker passive skill.
 */
public class WindWalker extends PassiveSkill {

    @Override
    public String getId() {
        return "wind_walker";
    }

    @Override
    public String getName() {
        return "Wind Walker";
    }

    @Override
    public String getDescription() {
        return "+15% movement speed and +5% dodge chance.";
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
