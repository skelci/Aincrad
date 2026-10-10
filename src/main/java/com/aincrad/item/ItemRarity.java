package com.aincrad.item;

/**
 * Six item rarities with stat multipliers and drop rates.
 */
public enum ItemRarity {
    COMMON("Common", 1.0, 0.60, 0.0),
    UNCOMMON("Uncommon", 1.3, 0.25, 0.15),
    RARE("Rare", 1.7, 0.10, 0.30),
    EPIC("Epic", 2.2, 0.04, 0.30),
    LEGENDARY("Legendary", 3.0, 0.01, 0.20),
    MYTHIC("Mythic", 4.0, 0.001, 0.05);

    private final String displayName;
    private final double statMultiplier;
    private final double standardDropRate;
    private final double bossDropRate;

    ItemRarity(String displayName, double statMultiplier,
               double standardDropRate, double bossDropRate) {
        this.displayName = displayName;
        this.statMultiplier = statMultiplier;
        this.standardDropRate = standardDropRate;
        this.bossDropRate = bossDropRate;
    }

    public String getDisplayName() { return displayName; }
    public double getStatMultiplier() { return statMultiplier; }
    public double getStandardDropRate() { return standardDropRate; }
    public double getBossDropRate() { return bossDropRate; }
}
