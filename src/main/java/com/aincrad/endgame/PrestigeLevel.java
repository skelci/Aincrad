package com.aincrad.endgame;

/**
 * Prestige ranks for the endgame prestige system.
 * <p>
 * Players earn prestige levels by meeting endgame requirements such as
 * clearing high floors, completing NG+ cycles, and accumulating Col.
 * Each rank grants exclusive titles and benefits.
 * </p>
 */
public enum PrestigeLevel {

    /** Entry prestige rank — no requirements. */
    NONE(0, "None", "No prestige rank", 0),

    /** Bronze prestige — requires Floor 50 clear. */
    BRONZE(1, "Bronze", "Prestige rank for clearing Floor 50", 50),

    /** Silver prestige — requires Floor 75 clear. */
    SILVER(2, "Silver", "Prestige rank for clearing Floor 75", 75),

    /** Gold prestige — requires Floor 100 clear. */
    GOLD(3, "Gold", "Prestige rank for clearing Floor 100", 100),

    /** Platinum prestige — requires NG+ cycle 1. */
    PLATINUM(4, "Platinum", "Prestige rank for completing NG+ cycle 1", 100),

    /** Diamond prestige — requires NG+ cycle 3. */
    DIAMOND(5, "Diamond", "Prestige rank for completing NG+ cycle 3", 100),

    /** Aincrad prestige — requires NG+ cycle 5. */
    AINCRAD(6, "Aincrad", "The ultimate prestige rank — NG+ cycle 5", 100);

    private final int tier;
    private final String displayName;
    private final String description;
    private final int requiredFloor;

    PrestigeLevel(int tier, String displayName, String description, int requiredFloor) {
        this.tier = tier;
        this.displayName = displayName;
        this.description = description;
        this.requiredFloor = requiredFloor;
    }

    /**
     * Returns the numeric tier of this prestige level.
     *
     * @return the tier (0-6)
     */
    public int getTier() {
        return tier;
    }

    /**
     * Returns the display name of this prestige level.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the description of this prestige level.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the floor required to achieve this prestige level.
     *
     * @return the required floor
     */
    public int getRequiredFloor() {
    return requiredFloor;
    }

    /**
     * Returns the title granted at this prestige level.
     *
     * @return the title string
     */
    public String getTitle() {
        return switch (this) {
            case NONE -> "";
            case BRONZE -> "§7[Bronze]";
            case SILVER -> "§f[Silver]";
            case GOLD -> "§6[Gold]";
            case PLATINUM -> "§b[Platinum]";
            case DIAMOND -> "§3[Diamond]";
            case AINCRAD -> "§5§l[Aincrad]";
        };
    }

    /**
     * Returns the Col bonus percentage at this prestige level.
     *
     * @return the bonus percentage (e.g., 10 = 10%)
     */
    public int getColBonusPercent() {
        return switch (this) {
            case NONE -> 0;
            case BRONZE -> 5;
            case SILVER -> 10;
            case GOLD -> 15;
            case PLATINUM -> 20;
            case DIAMOND -> 30;
            case AINCRAD -> 50;
        };
    }

    /**
     * Returns the next prestige level, or {@code null} if at max.
     *
     * @return the next PrestigeLevel, or {@code null}
     */
    public PrestigeLevel next() {
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal >= values().length) {
            return null;
        }
        return values()[nextOrdinal];
    }

    /**
     * Returns the previous prestige level, or {@code null} if at min.
     *
     * @return the previous PrestigeLevel, or {@code null}
     */
    public PrestigeLevel previous() {
        int prevOrdinal = this.ordinal() - 1;
        if (prevOrdinal < 0) {
            return null;
        }
        return values()[prevOrdinal];
    }

    /**
     * Checks whether this level is higher than another.
     *
     * @param other the other level
     * @return {@code true} if this level is higher
     */
    public boolean isHigherThan(PrestigeLevel other) {
        return this.tier > other.tier;
    }
}
