package com.aincrad.dungeon.layout.crystal;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CrystalFloor3Layout extends DungeonLayout {
    public CrystalFloor3Layout() {
        super("crystal_floor_3", "Crystal", 3, DungeonTier.T5_MYTHIC,
            0, 64, 0, 200, 70, 200,
            Arrays.asList("entrance:spawn", "spawn:crystal_cavern", "crystal_cavern:gem_hall", "gem_hall:prism_chamber", "prism_chamber:diamond_vein", "diamond_vein:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 6, 260),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 5, 360),
                new DungeonMobPlacement("ENDERMITE", 60, 64, 20, 6, 310),
                new DungeonMobPlacement("VEX", 80, 64, 50, 4, 480),
                new DungeonMobPlacement("WITHER_SKELETON", 100, 64, 70, 3, 600)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "LEGENDARY", 0.3),
                new DungeonLootTable(50, 65, 40, "MYTHIC", 0.2),
                new DungeonLootTable(70, 65, 60, "MYTHIC", 0.12),
                new DungeonLootTable(90, 65, 80, "MYTHIC", 0.06)
            )
        );
    }
}
