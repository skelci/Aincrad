package com.aincrad.enhance;

/**
 * Enhancement levels from +0 to +15.
 * Each level defines the success rate, break chance, and stat bonus multiplier
 * when attempting to enhance TO that level (i.e., from level-1 to level).
 */
public enum EnhanceLevel {

    PLUS_0(0, 1.00, 0.00, 1.00),
    PLUS_1(1, 0.95, 0.00, 1.05),
    PLUS_2(2, 0.90, 0.00, 1.10),
    PLUS_3(3, 0.85, 0.00, 1.15),
    PLUS_4(4, 0.80, 0.00, 1.20),
    PLUS_5(5, 0.75, 0.05, 1.30),
    PLUS_6(6, 0.70, 0.10, 1.40),
    PLUS_7(7, 0.65, 0.15, 1.50),
    PLUS_8(8, 0.60, 0.20, 1.60),
    PLUS_9(9, 0.55, 0.25, 1.70),
    PLUS_10(10, 0.50, 0.30, 1.80),
    PLUS_11(11, 0.45, 0.35, 1.90),
    PLUS_12(12, 0.40, 0.40, 2.00),
    PLUS_13(13, 0.35, 0.45, 2.10),
    PLUS_14(14, 0.30, 0.50, 2.20),
    PLUS_15(15, 0.25, 0.55, 2.30);

    private final int level;
    private final double successRate;
    private final double breakChance;
    private final double statBonusMultiplier;

    EnhanceLevel(int level, double successRate, double breakChance, double statBonusMultiplier) {
        this.level = level;
        this.successRate = successRate;
        this.breakChance = breakChance;
        this.statBonusMultiplier = statBonusMultiplier;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Probability (0.0–1.0) that an attempt to reach this level succeeds.
     */
    public double getSuccessRate() {
        return successRate;
    }

    /**
     * Probability (0.0–1.0) that an attempt to reach this level destroys the item.
     */
    public double getBreakChance() {
        return breakChance;
    }

    /**
     * Multiplier applied to base stats when the item is at this level.
     */
    public double getStatBonusMultiplier() {
        return statBonusMultiplier;
    }

    /**
     * Get the EnhanceLevel for a given numeric level, clamped to [0, 15].
     */
    public static EnhanceLevel fromInt(int level) {
        int clamped = Math.max(0, Math.min(15, level));
        return values()[clamped];
    }
}
