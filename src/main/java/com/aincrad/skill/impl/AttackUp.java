package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Attack Up passive skill.
 */
public class AttackUp extends PassiveSkill {

    @Override
    public String getId() {
        return "attack_up";
    }

    @Override
    public String getName() {
        return "Attack Up";
    }

    @Override
    public String getDescription() {
        return "+10% attack damage.";
    }

    @Override
    public int getRequiredLevel() {
        return 6;
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
