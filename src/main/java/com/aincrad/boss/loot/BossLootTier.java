package com.aincrad.boss.loot;

/**
 * Loot tier for boss drops, based on floor range.
 * Each tier determines weapon rarity, material quality, and bonus drop rates.
 */
public enum BossLootTier {
    TIER_1(1, 25, "Purple", 0.05f),
    TIER_2(26, 50, "Orange", 0.08f),
    TIER_3(51, 75, "Red", 0.12f),
    TIER_4(76, 100, "Legendary", 0.15f);

    private final int minFloor;
    private final int maxFloor;
    private final String weaponRarity;
    private final float bonusDropChance;

    BossLootTier(int minFloor, int maxFloor, String weaponRarity, float bonusDropChance) {
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.weaponRarity = weaponRarity;
        this.bonusDropChance = bonusDropChance;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public String getWeaponRarity() {
        return weaponRarity;
    }

    public float getBonusDropChance() {
        return bonusDropChance;
    }

    /**
     * Resolves the loot tier for a given floor.
     */
    public static BossLootTier fromFloor(int floor) {
        for (BossLootTier tier : values()) {
            if (floor >= tier.minFloor && floor <= tier.maxFloor) {
                return tier;
            }
        }
        return TIER_1;
    }
}
