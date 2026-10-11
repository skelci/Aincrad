package com.aincrad.boss.loot;

/**
 * Represents a single boss loot drop entry.
 *
 * @param itemName    the registry name of the item
 * @param minQty      minimum quantity dropped
 * @param maxQty      maximum quantity dropped
 * @param dropChance  probability of this drop (0.0–1.0)
 * @param isWeapon    {@code true} if this is the boss weapon drop
 */
public record BossDrop(
    String itemName,
    int minQty,
    int maxQty,
    float dropChance,
    boolean isWeapon
) {
    /**
     * Creates a standard (non-weapon) drop.
     */
    public static BossDrop standard(String itemName, int minQty, int maxQty, float dropChance) {
        return new BossDrop(itemName, minQty, maxQty, dropChance, false);
    }

    /**
     * Creates a boss weapon drop (always quantity 1, always 100% chance).
     */
    public static BossDrop weapon(String itemName) {
        return new BossDrop(itemName, 1, 1, 1.0f, true);
    }

    /**
     * Creates a bonus drop (low chance, e.g. reset stone, mount).
     */
    public static BossDrop bonus(String itemName, float dropChance) {
        return new BossDrop(itemName, 1, 1, dropChance, false);
    }
}
