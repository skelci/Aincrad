package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * The ultimate dual-wield skill. Unleashes infinite blade strikes.
 */
public class InfinityBlades extends DualWieldSkill {

    public InfinityBlades() {
        super(0, 80, 5.0);
    }

    @Override
    public String getId() {
        return "infinity_blades";
    }

    @Override
    public String getName() {
        return "Infinity Blades";
    }

    @Override
    public String getDescription() {
        return "The ultimate dual-wield skill. Unleashes infinite blade strikes.";
    }

    @Override
    public int getCooldown() {
        return 60;
    }

    @Override
    public int getMpCost() {
        return 35;
    }

    @Override
    public int getRequiredLevel() {
        return 25;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: unleash a barrage of infinite blade strikes on all nearby enemies
    }
}
