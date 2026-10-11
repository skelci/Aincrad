package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Full Restore: Fully restore HP and cure all debuffs.
 */
public class FullRestore extends ActiveSkill {

    @Override
    public String getId() {
        return "full_restore";
    }

    @Override
    public String getName() {
        return "Full Restore";
    }

    @Override
    public String getDescription() {
        return "Fully restore HP and cure all debuffs.";
    }

    @Override
    public int getCooldown() {
        return 300;
    }

    @Override
    public int getMpCost() {
        return 60;
    }

    @Override
    public int getRequiredLevel() {
        return 15;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: fully restore player's HP and remove all debuffs
    }
}
