package com.aincrad.skill.impl;

import com.aincrad.skill.PassiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Attack Up Greater passive skill.
 */
public class AttackUpGreater extends PassiveSkill {

    @Override
    public String getId() {
        return "attack_up_greater";
    }

    @Override
    public String getName() {
        return "Attack Up Greater";
    }

    @Override
    public String getDescription() {
        return "+20% attack damage.";
    }

    @Override
    public int getRequiredLevel() {
        return 13;
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
