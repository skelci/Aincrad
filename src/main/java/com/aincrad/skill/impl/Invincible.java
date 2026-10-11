package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Invincible shield: Become immune to all damage for 3 seconds.
 */
public class Invincible extends ActiveSkill {

    @Override
    public String getId() {
        return "invincible";
    }

    @Override
    public String getName() {
        return "Invincible";
    }

    @Override
    public String getDescription() {
        return "Become immune to all damage for 3 seconds.";
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public int getMpCost() {
        return 40;
    }

    @Override
    public int getRequiredLevel() {
        return 12;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: grant complete damage immunity for 3 seconds
    }
}
