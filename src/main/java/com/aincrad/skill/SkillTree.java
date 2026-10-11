package com.aincrad.skill;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages skill unlock prerequisites and costs for players.
 * <p>
 * Skills can have prerequisite skills that must be unlocked first, and
 * each skill has an associated unlock cost (e.g. skill points).
 * </p>
 */
public class SkillTree {

    /** Maps a skill ID to the prerequisite skill ID required to unlock it. */
    private final Map<String, String> prerequisites = new HashMap<>();

    /** Maps a skill ID to its unlock cost in skill points. */
    private final Map<String, Integer> skillCosts = new HashMap<>();

    /**
     * Creates a new skill tree and initializes default prerequisites and costs.
     */
    public SkillTree() {
        // Initialize prerequisites and costs
        // Example: put("rapier_lunge", "rapier_thrust") means rapier_lunge requires rapier_thrust
    }

    /**
     * Checks whether a player can unlock the given skill.
     * <p>
     * Verifies that the player meets the level requirement and has already
     * unlocked any prerequisite skill.
     * </p>
     *
     * @param player the player attempting to unlock
     * @param skillId the skill to unlock
     * @return {@code true} if the skill can be unlocked
     */
    public boolean canUnlock(Player player, String skillId) {
        // TODO: check level requirement and prerequisites
        return true;
    }

    /**
     * Unlocks the given skill for the player.
     *
     * @param player the player unlocking the skill
     * @param skillId the skill to unlock
     * @return {@code true} if the skill was successfully unlocked
     */
    public boolean unlock(Player player, String skillId) {
        // TODO: unlock the skill for the player
        return true;
    }

    /**
     * Returns a list of skill IDs that the player can currently unlock.
     *
     * @param player the player
     * @return list of unlockable skill IDs
     */
    public List<String> getAvailableSkills(Player player) {
        // TODO: return list of skills the player can currently unlock
        return new ArrayList<>();
    }
}
