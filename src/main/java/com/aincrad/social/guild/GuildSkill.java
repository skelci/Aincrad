package com.aincrad.social.guild;

/**
 * Learnable guild-wide passive skills.
 * Each skill unlocks at a fixed guild level and can be trained to
 * {@value #MAX_RANK} ranks. Effect values scale linearly per rank.
 */
public enum GuildSkill {

    /**
     * Unlocks at guild level 3.
     * Effect: +% melee damage dealt by guild members per rank.
     */
    BATTLE_TRAINING("Battle Training", 3, 0.05,
            "+5% melee damage for guild members per rank"),

    /**
     * Unlocks at guild level 5.
     * Effect: +% natural health regeneration speed per rank.
     */
    SWIFT_RECOVERY("Swift Recovery", 5, 0.04,
            "+4% natural regen speed for guild members per rank"),

    /**
     * Unlocks at guild level 8.
     * Effect: +% experience gained while in a party with guildmates per rank.
     */
    COL_RESERVE("COL Reserve", 8, 0.06,
            "+6% party XP share for guild members per rank"),

    /**
     * Unlocks at guild level 12.
     * Effect: +% damage resistance for guild members per rank.
     */
    IRON_WILL("Iron Will", 12, 0.03,
            "+3% damage resistance for guild members per rank");

    /** Highest trainable rank for every skill. */
    public static final int MAX_RANK = 3;

    private final String displayName;
    private final int unlockLevel;
    private final double effectPerRank;
    private final String description;

    GuildSkill(String displayName, int unlockLevel,
                double effectPerRank, String description) {
        this.displayName = displayName;
        this.unlockLevel = unlockLevel;
        this.effectPerRank = effectPerRank;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Guild level required before this skill can be trained. */
    public int getUnlockLevel() {
        return unlockLevel;
    }

    /** Raw multiplier added per trained rank (e.g. 0.05 = 5%). */
    public double getEffectPerRank() {
        return effectPerRank;
    }

    /** Short human-readable effect summary for GUIs and chat. */
    public String getDescription() {
        return description;
    }

    /**
     * Total effect multiplier at a given rank.
     *
     * @param rank trained rank, expected {@code 0..MAX_RANK}
     * @return cumulative multiplier (e.g. rank 2 of BATTLE_TRAINING = 0.10)
     */
    public double effectAt(int rank) {
        return effectPerRank * rank;
    }

    /**
     * Returns true if this skill is trainable at {@code guildLevel}.
     *
     * @param guildLevel current guild level
     * @return true when the skill's unlock level has been reached
     */
    public boolean isUnlockedAt(int guildLevel) {
        return guildLevel >= unlockLevel;
    }
}
