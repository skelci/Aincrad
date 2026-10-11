package com.aincrad.skill.impl;

import com.aincrad.skill.ActiveSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Regenerate: Regenerate 5% HP per second for 10 seconds.
 */
public class Regenerate extends ActiveSkill {

    @Override
    public String getId() {
        return "regenerate";
    }

    @Override
    public String getName() {
        return "Regenerate";
    }

    @Override
    public String getDescription() {
        return "Regenerate 5% HP per second for 10 seconds.";
    }

    @Override
    public int getCooldown() {
        return 120;
    }

    @Override
    public int getMpCost() {
        return 25;
    }

    @Override
    public int getRequiredLevel() {
        return 6;
    }

    @Override
    public void cast(Player player) {
        tryActivate(player);
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply regeneration effect, restoring 5% max HP per second for 10 seconds
    }
}
