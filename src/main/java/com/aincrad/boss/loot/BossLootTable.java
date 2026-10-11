package com.aincrad.boss.loot;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the loot table for a given floor tier.
 * Each tier has standard drops (weapon, materials, WP, XP, coins) and
 * bonus drops (protection scroll, reset stone, mount for raids).
 */
public final class BossLootTable {

    private BossLootTable() {
    }

    /**
     * Returns the list of drops for a given floor.
     *
     * @param floor the floor number (1–100)
     * @return list of {@link BossDrop} entries
     */
    public static List<BossDrop> getDropsForFloor(int floor) {
        BossLootTier tier = BossLootTier.fromFloor(floor);
        List<BossDrop> drops = new ArrayList<>();

        // Standard drops (100%)
        drops.add(BossDrop.weapon("aincrad:floor_" + floor + "_boss_weapon"));
        drops.add(BossDrop.standard("aincrad:boss_material", 3 + tier.ordinal() * 2, 5 + tier.ordinal() * 3, 1.0f));
        drops.add(BossDrop.standard("aincrad:wp", 10 + floor * 2, 50 + floor * 5, 1.0f));
        drops.add(BossDrop.standard("aincrad:xp_orb", 1, 1, 1.0f));
        drops.add(BossDrop.standard("aincrad:coin", 100 * floor, 1000 * floor, 1.0f));

        // Bonus drops
        drops.add(BossDrop.bonus("aincrad:protection_scroll", 0.15f));
        drops.add(BossDrop.bonus("aincrad:reset_stone", 0.05f));

        // Raid bosses (25, 50, 75, 100) get mount drops
        if (floor % 25 == 0) {
            drops.add(BossDrop.bonus("aincrad:mount_" + floor, 0.10f));
        }

        return drops;
    }

    /**
     * Returns the loot tier for a given floor.
     */
    public static BossLootTier getTierForFloor(int floor) {
        return BossLootTier.fromFloor(floor);
    }
}
