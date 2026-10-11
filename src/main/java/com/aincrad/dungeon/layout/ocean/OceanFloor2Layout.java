package com.aincrad.dungeon.layout.ocean;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OceanFloor2Layout extends DungeonLayout {
    public OceanFloor2Layout() {
        super("ocean_floor_2", "Ocean", 2, DungeonTier.T2_NORMAL,
            0, 64, 0, 100, 70, 100,
            Arrays.asList("entrance:spawn", "spawn:water_tunnel", "water_tunnel:coral_hall", "coral_hall:kelp_grove", "kelp_grove:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("DROWNED", 15, 64, 10, 4, 550),
                new DungeonMobPlacement("COD", 30, 64, 25, 5, 450),
                new DungeonMobPlacement("SALMON", 50, 64, 40, 4, 500),
                new DungeonMobPlacement("PUFFERFISH", 65, 64, 55, 2, 700)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.85),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.55),
                new DungeonLootTable(60, 65, 55, "RARE", 0.3)
            )
        );
    }
}
