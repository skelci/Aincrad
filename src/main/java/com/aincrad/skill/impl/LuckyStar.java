package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Lucky Star passive skill.
 */
public class LuckyStar extends PassiveSkill {

    @Override
    public String getId() {
        return "lucky_star";
    }

    @Override
    public String getName() {
        return "Lucky Star";
    }

    @Override
    public String getDescription() {
        return "+5% to all stats.";
    }

    @Override
    public int getRequiredLevel() {
        return 15;
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
