package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Lightning Resist passive skill.
 */
public class LightningResist extends PassiveSkill {

    @Override
    public String getId() {
        return "lightning_resist";
    }

    @Override
    public String getName() {
        return "Lightning Resist";
    }

    @Override
    public String getDescription() {
        return "Reduce lightning damage by 20%.";
    }

    @Override
    public int getRequiredLevel() {
        return 5;
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
