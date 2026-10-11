package com.aincrad.dungeon.layout.ocean;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OceanFloor1Layout extends DungeonLayout {
    public OceanFloor1Layout() {
        super("ocean_floor_1", "Ocean", 1, DungeonTier.T2_NORMAL,
            0, 64, 0, 80, 70, 80,
            Arrays.asList("entrance:spawn", "spawn:water_tunnel", "water_tunnel:coral_hall", "coral_hall:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("DROWNED", 15, 64, 10, 3, 600),
                new DungeonMobPlacement("COD", 30, 64, 25, 4, 500),
                new DungeonMobPlacement("SALMON", 50, 64, 40, 3, 550)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.8),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.5)
            )
        );
    }
}
