package com.aincrad.skill;

import net.minecraft.world.entity.player.Player;

/**
 * Abstract base for passive skills that provide a continuous modifier.
 * <p>
 * Passive skills have no cooldown or MP cost. Subclasses override
 * {@link #apply(Player)} when the effect is applied (e.g. on equip) and
 * {@link #remove(Player)} when it is removed (e.g. on unequip or death).
 * </p>
 */
public abstract class PassiveSkill implements Skill {

    @Override
    public SkillType getSkillType() {
        return SkillType.PASSIVE_SKILL;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public int getMpCost() {
        return 0;
    }

    /**
     * Applies the passive effect to the player.
     *
     * @param player the player receiving the effect
     */
    public abstract void apply(Player player);

    /**
     * Removes the passive effect from the player.
     *
     * @param player the player losing the effect
     */
    public abstract void remove(Player player);
}
