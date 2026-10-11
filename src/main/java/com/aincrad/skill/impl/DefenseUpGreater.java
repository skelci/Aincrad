package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Defense Up Greater passive skill.
 */
public class DefenseUpGreater extends PassiveSkill {

    @Override
    public String getId() {
        return "defense_up_greater";
    }

    @Override
    public String getName() {
        return "Defense Up Greater";
    }

    @Override
    public String getDescription() {
        return "+25% defense.";
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
