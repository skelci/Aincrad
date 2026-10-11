package com.aincrad.endgame;

/**
 * Special rewards granted for completing NG+ cycles.
 * <p>
 * Each NG+ cycle unlocks exclusive rewards including titles, cosmetics,
 * and bonus items that are only available to players who have entered NG+.
 * </p>
 */
public enum NGReward {

    /** Cycle 1 reward: "NG+ Veteran" title. */
    CYCLE_1_TITLE("NG+ Veteran", RewardType.TITLE, "Title for completing your first NG+ cycle"),

    /** Cycle 1 reward: +10% Col bonus cosmetic aura. */
    CYCLE_1_AURA("NG+ Glow", RewardType.COSMETIC, "A subtle glow effect for NG+ players"),

    /** Cycle 2 reward: "NG+ Champion" title. */
    CYCLE_2_TITLE("NG+ Champion", RewardType.TITLE, "Title for completing two NG+ cycles"),

    /** Cycle 3 reward: Exclusive NG+ weapon skin. */
    CYCLE_3_WEAPON_SKIN("NG+ Blade", RewardType.COSMETIC, "Exclusive weapon appearance for NG+ cycle 3"),

    /** Cycle 5 reward: "NG+ Master" title. */
    CYCLE_5_TITLE("NG+ Master", RewardType.TITLE, "Title for completing five NG+ cycles"),

    /** Cycle 5 reward: NG+ exclusive pet. */
    CYCLE_5_PET("NG+ Phoenix Pet", RewardType.PET, "A loyal phoenix companion"),

    /** Cycle 10 reward: "NG+ Legend" title. */
    CYCLE_10_TITLE("NG+ Legend", RewardType.TITLE, "Title for completing ten NG+ cycles"),

    /** Cycle 10 reward: NG+ exclusive mount. */
    CYCLE_10_MOUNT("NG+ Shadow Drake", RewardType.MOUNT, "A fearsome drake mount");

    private final String name;
    private final RewardType type;
    private final String description;

    NGReward(String name, RewardType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    /**
     * Returns the reward display name.
     *
     * @return the reward name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the reward type.
     *
     * @return the RewardType
     */
    public RewardType getType() {
        return type;
    }

    /**
     * Returns the reward description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the NG+ cycle required to unlock this reward.
     *
     * @return the cycle count required
     */
    public int getRequiredCycle() {
        return switch (this) {
            case CYCLE_1_TITLE, CYCLE_1_AURA -> 1;
            case CYCLE_2_TITLE -> 2;
            case CYCLE_3_WEAPON_SKIN -> 3;
            case CYCLE_5_TITLE, CYCLE_5_PET -> 5;
            case CYCLE_10_TITLE, CYCLE_10_MOUNT -> 10;
        };
    }

    /**
     * Returns all rewards available for a given NG+ cycle.
     *
     * @param cycle the NG+ cycle count
     * @return array of rewards unlocked at or below this cycle
     */
    public static NGReward[] getRewardsForCycle(int cycle) {
        return java.util.Arrays.stream(values())
                .filter(r -> r.getRequiredCycle() <= cycle)
                .toArray(NGReward[]::new);
    }

    /**
     * Returns the type of reward.
     */
    public enum RewardType {
        TITLE,
        COSMETIC,
        PET,
        MOUNT
    }
}
