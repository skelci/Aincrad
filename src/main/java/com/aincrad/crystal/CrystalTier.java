package com.aincrad.crystal;

public enum CrystalTier {
    ROUGH("Rough", 1, 0.1),
    POLISHED("Polished", 2, 0.25),
    FLAWLESS("Flawless", 3, 0.5),
    PERFECT("Perfect", 4, 1.0),
    TRANSCENDENT("Transcendent", 5, 2.0);

    private final String displayName;
    private final int level;
    private final double multiplier;

    CrystalTier(String displayName, int level, double multiplier) {
        this.displayName = displayName;
        this.level = level;
        this.multiplier = multiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
