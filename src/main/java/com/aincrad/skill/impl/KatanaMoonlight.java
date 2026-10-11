package com.aincrad.skill.impl;

import com.aincrad.skill.SwordSkill;
import net.minecraft.world.entity.player.Player;

/**
 * Katana Moonlight. A graceful slash like moonlight on water.
 */
public class KatanaMoonlight extends SwordSkill {

    public KatanaMoonlight() {
        super(0, 35, 2.8);
    }

    @Override
    public String getId() {
        return "katana_moonlight";
    }

    @Override
    public String getName() {
        return "Katana Moonlight";
    }

    @Override
    public String getDescription() {
        return "A graceful slash like moonlight on water.";
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    @Override
    public int getMpCost() {
        return 7;
    }

    @Override
    public int getRequiredLevel() {
        return 7;
    }

    @Override
    protected void onActivate(Player player) {
        // TODO: apply damage to entities in front of player
    }
}
