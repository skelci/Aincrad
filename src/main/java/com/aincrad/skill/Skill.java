package com.aincrad.skill;

import net.minecraft.world.entity.player.Player;

/**
 * Common interface for all Aincrad skills.
 * <p>
 * Skills are registered in {@link SkillRegistry} and come in three categories:
 * {@link SkillType#SWORD_SKILL}, {@link SkillType#ACTIVE_SKILL}, and
 * {@link SkillType#PASSIVE_SKILL}.
 * </p>
 */
public interface Skill {

    /**
     * Returns the unique identifier for this skill (e.g. "horizontal").
     *
     * @return the skill ID
     */
    String getId();

    /**
     * Returns the human-readable display name.
     *
     * @return the display name
     */
    String getName();

    /**
     * Returns a short description of what the skill does.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Returns the category of this skill.
     *
     * @return the skill type
     */
    SkillType getSkillType();

    /**
     * Returns the base cooldown time in ticks.
     *
     * @return cooldown in ticks
     */
    int getCooldown();

    /**
     * Returns the MP cost to use this skill.
     *
     * @return the MP cost
     */
    int getMpCost();

    /**
     * Returns the minimum player level required to learn this skill.
     *
     * @return the required level
     */
    int getRequiredLevel();

    /**
     * Executes the skill for the given player.
     *
     * @param player the player using the skill
     */
    void cast(Player player);
}
