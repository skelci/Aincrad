package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Gold Find passive skill.
 */
public class GoldFind extends PassiveSkill {

    @Override
    public String getId() {
        return "gold_find";
    }

    @Override
    public String getName() {
        return "Gold Find";
    }

    @Override
    public String getDescription() {
        return "+20% col dropped from enemies.";
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
