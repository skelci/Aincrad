package com.aincrad.skill.impl;

import com.aincrad.skill.DualWieldSkill;
import net.minecraft.world.entity.player.Player;

/**
 * An elemental dual strike with fire and ice.
 */
public class TwinDragons extends DualWieldSkill {

    public TwinDragons() {
        super(0, 60, 4.0);
    }

    @Override
    public String getId() {
        return "twin_dragons";
    }

    @Override
    public String getName() {
        return "Twin Dragons";
    }

    @Override
    public String getDescription() {
        return "An elemental dual strike with fire and ice.";
    }

    @Override
    public int getCooldown() {
        return 45;
    }

    @Override
    public int getMpCost() {
        return 25;
    }

    @Override
    public int getRequiredLevel() {
        return 18;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply fire and ice elemental damage to target
    }
}
