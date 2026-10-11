package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Loot Bonus passive skill.
 */
public class LootBonus extends PassiveSkill {

    @Override
    public String getId() {
        return "loot_bonus";
    }

    @Override
    public String getName() {
        return "Loot Bonus";
    }

    @Override
    public String getDescription() {
        return "+10% item drop rate.";
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
