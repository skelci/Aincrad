package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Dark Resist passive skill.
 */
public class DarkResist extends PassiveSkill {

    @Override
    public String getId() {
        return "dark_resist";
    }

    @Override
    public String getName() {
        return "Dark Resist";
    }

    @Override
    public String getDescription() {
        return "Reduce dark damage by 20%.";
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
