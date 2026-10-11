package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Holy Resist passive skill.
 */
public class HolyResist extends PassiveSkill {

    @Override
    public String getId() {
        return "holy_resist";
    }

    @Override
    public String getName() {
        return "Holy Resist";
    }

    @Override
    public String getDescription() {
        return "Reduce holy damage by 20%.";
    }

    @Override
    public int getRequiredLevel() {
        return 7;
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
