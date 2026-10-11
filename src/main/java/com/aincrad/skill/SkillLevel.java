package com.aincrad.skill;

/**
 * Tracks the level and experience of an individual skill for a player.
 * <p>
 * Skills gain XP through use and level up at certain thresholds. Higher
 * levels reduce cooldown and MP cost while increasing effectiveness.
 * </p>
 */
public class SkillLevel {

    /** XP required to advance from one level to the next. */
    private static final int XP_PER_LEVEL = 100;

    /** The skill this level data belongs to. */
    private final String skillId;

    /** Current skill level (starts at 1). */
    private int level;

    /** Current accumulated XP towards the next level. */
    private int xp;

    /**
     * Creates a new skill level tracker for the given skill.
     *
     * @param skillId the skill ID
     */
    public SkillLevel(String skillId) {
        this.skillId = skillId;
        this.level = 1;
        this.xp = 0;
    }

    /**
     * Adds XP to this skill. If enough XP is accumulated, the skill levels up.
     *
     * @param amount the XP to add
     */
    public void addXp(int amount) {
        this.xp += amount;
        // TODO: check for level up
        while (this.xp >= XP_PER_LEVEL) {
            this.xp -= XP_PER_LEVEL;
            this.level++;
        }
    }

    /**
     * Returns the cooldown modifier based on skill level.
     * <p>
     * Level 1: 1.0, Level 2: 0.9, Level 3: 0.8, Level 4: 0.7, Level 5: 0.6
     * </p>
     *
     * @return the cooldown multiplier
     */
    public double getCooldownModifier() {
        // Level 1: 1.0, Level 2: 0.9, Level 3: 0.8, Level 4: 0.7, Level 5: 0.6
        return Math.max(0.5, 1.0 - (level - 1) * 0.1);
    }

    /**
     * Returns the MP cost modifier based on skill level.
     *
     * @return the MP cost multiplier
     */
    public double getMpCostModifier() {
        return Math.max(0.5, 1.0 - (level - 1) * 0.1);
    }

    /**
     * Returns the effectiveness bonus based on skill level.
     *
     * @return the effectiveness bonus
     */
    public double getEffectivenessBonus() {
        return (level - 1) * 0.05;
    }

    /**
     * Returns the current skill level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the current XP.
     *
     * @return the XP
     */
    public int getXp() {
        return xp;
    }

    /**
     * Returns the skill ID.
     *
     * @return the skill ID
     */
    public String getSkillId() {
        return skillId;
    }
}
