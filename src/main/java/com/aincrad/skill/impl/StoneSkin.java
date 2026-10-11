package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Stone Skin passive skill.
 */
public class StoneSkin extends PassiveSkill {

    @Override
    public String getId() {
        return "stone_skin";
    }

    @Override
    public String getName() {
        return "Stone Skin";
    }

    @Override
    public String getDescription() {
        return "Reduce physical damage taken by 30%.";
    }

    @Override
    public int getRequiredLevel() {
        return 14;
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
