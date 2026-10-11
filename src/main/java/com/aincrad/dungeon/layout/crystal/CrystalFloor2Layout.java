package com.aincrad.dungeon.layout.crystal;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CrystalFloor2Layout extends DungeonLayout {
    public CrystalFloor2Layout() {
        super("crystal_floor_2", "Crystal", 2, DungeonTier.T5_MYTHIC,
            0, 64, 0, 180, 70, 180,
            Arrays.asList("entrance:spawn", "spawn:crystal_cavern", "crystal_cavern:gem_hall", "gem_hall:prism_chamber", "prism_chamber:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 5, 280),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 4, 380),
                new DungeonMobPlacement("ENDERMITE", 60, 64, 20, 5, 330),
                new DungeonMobPlacement("VEX", 80, 64, 50, 3, 500)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "LEGENDARY", 0.25),
                new DungeonLootTable(50, 65, 40, "MYTHIC", 0.15),
                new DungeonLootTable(70, 65, 60, "MYTHIC", 0.08)
            )
        );
    }
}
