package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Defense Up passive skill.
 */
public class DefenseUp extends PassiveSkill {

    @Override
    public String getId() {
        return "defense_up";
    }

    @Override
    public String getName() {
        return "Defense Up";
    }

    @Override
    public String getDescription() {
        return "+10% defense.";
    }

    @Override
    public int getRequiredLevel() {
        return 3;
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
