package com.aincrad.dungeon.layout.crystal;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CrystalFloor4Layout extends DungeonLayout {
    public CrystalFloor4Layout() {
        super("crystal_floor_4", "Crystal", 4, DungeonTier.T5_MYTHIC,
            0, 64, 0, 220, 70, 220,
            Arrays.asList("entrance:spawn", "spawn:crystal_cavern", "crystal_cavern:gem_hall", "gem_hall:prism_chamber", "prism_chamber:diamond_vein", "diamond_vein:obsidian_core", "obsidian_core:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 7, 240),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 6, 340),
                new DungeonMobPlacement("ENDERMITE", 60, 64, 20, 7, 290),
                new DungeonMobPlacement("VEX", 80, 64, 50, 5, 460),
                new DungeonMobPlacement("WITHER_SKELETON", 100, 64, 70, 4, 580),
                new DungeonMobPlacement("SHULKER", 120, 64, 90, 3, 800)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "LEGENDARY", 0.35),
                new DungeonLootTable(50, 65, 40, "MYTHIC", 0.25),
                new DungeonLootTable(70, 65, 60, "MYTHIC", 0.18),
                new DungeonLootTable(90, 65, 80, "MYTHIC", 0.1),
                new DungeonLootTable(110, 65, 100, "MYTHIC", 0.05)
            )
        );
    }
}
