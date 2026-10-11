package com.aincrad.dungeon.layout.crystal;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CrystalFloor1Layout extends DungeonLayout {
    public CrystalFloor1Layout() {
        super("crystal_floor_1", "Crystal", 1, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:crystal_cavern", "crystal_cavern:gem_hall", "gem_hall:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 4, 300),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 3, 400),
                new DungeonMobPlacement("ENDERMITE", 60, 64, 20, 4, 350)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "LEGENDARY", 0.2),
                new DungeonLootTable(50, 65, 40, "MYTHIC", 0.1)
            )
        );
    }
}
