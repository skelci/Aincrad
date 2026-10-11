package com.aincrad.paper.guild;

import java.util.EnumMap;
import java.util.Map;

/**
 * Learnable guild-wide passive skills.
 * Each has an unlock level and effect per rank (max rank 3).
 */
public enum GuildSkill {
    BATTLE_TRAINING("Battle Training", 3, 0.05, "+5% melee damage for guild members per rank"),
    SWIFT_RECOVERY("Swift Recovery", 5, 0.04, "+4% natural regen speed for guild members per rank"),
    COL_RESERVE("Col Reserve", 8, 0.06, "+6% party XP share for guild members per rank"),
    IRON_WILL("Iron Will", 12, 0.03, "+3% damage resistance for guild members per rank");

    public static final int MAX_RANK = 3;

    private final String displayName;
    private final int unlockLevel;
    private final double effectPerRank;
    private final String description;

    GuildSkill(String displayName, int unlockLevel, double effectPerRank, String description) {
        this.displayName = displayName;
        this.unlockLevel = unlockLevel;
        this.effectPerRank = effectPerRank;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public int getUnlockLevel() { return unlockLevel; }
    public String getDescription() { return description; }

    public double effectAt(int rank) {
        return effectPerRank * rank;
    }

    public boolean isUnlockedAt(int guildLevel) {
        return guildLevel >= unlockLevel;
    }
}
