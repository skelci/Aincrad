package com.aincrad.dungeon.layout.crystal;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CrystalFloor5Layout extends DungeonLayout {
    public CrystalFloor5Layout() {
        super("crystal_floor_5", "Crystal", 5, DungeonTier.T5_MYTHIC,
            0, 64, 0, 240, 70, 240,
            Arrays.asList("entrance:spawn", "spawn:crystal_cavern", "crystal_cavern:gem_hall", "gem_hall:prism_chamber", "prism_chamber:diamond_vein", "diamond_vein:obsidian_core", "obsidian_core:ender_depths", "ender_depths:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 8, 220),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 7, 320),
                new DungeonMobPlacement("ENDERMITE", 60, 64, 20, 8, 270),
                new DungeonMobPlacement("VEX", 80, 64, 50, 6, 440),
                new DungeonMobPlacement("WITHER_SKELETON", 100, 64, 70, 5, 560),
                new DungeonMobPlacement("SHULKER", 120, 64, 90, 4, 780),
                new DungeonMobPlacement("ENDERMAN", 140, 64, 110, 3, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "LEGENDARY", 0.4),
                new DungeonLootTable(50, 65, 40, "MYTHIC", 0.3),
                new DungeonLootTable(70, 65, 60, "MYTHIC", 0.22),
                new DungeonLootTable(90, 65, 80, "MYTHIC", 0.15),
                new DungeonLootTable(110, 65, 100, "MYTHIC", 0.08),
                new DungeonLootTable(130, 65, 120, "MYTHIC", 0.04)
            )
        );
    }
}
