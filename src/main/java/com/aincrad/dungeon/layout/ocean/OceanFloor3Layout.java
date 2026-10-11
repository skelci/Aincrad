package com.aincrad.dungeon.layout.ocean;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OceanFloor3Layout extends DungeonLayout {
    public OceanFloor3Layout() {
        super("ocean_floor_3", "Ocean", 3, DungeonTier.T2_NORMAL,
            0, 64, 0, 120, 70, 120,
            Arrays.asList("entrance:spawn", "spawn:water_tunnel", "water_tunnel:coral_hall", "coral_hall:kelp_grove", "kelp_grove:sunken_ruins", "sunken_ruins:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("DROWNED", 15, 64, 10, 5, 500),
                new DungeonMobPlacement("COD", 30, 64, 25, 6, 400),
                new DungeonMobPlacement("SALMON", 50, 64, 40, 5, 450),
                new DungeonMobPlacement("PUFFERFISH", 65, 64, 55, 3, 650),
                new DungeonMobPlacement("GUARDIAN", 80, 64, 70, 2, 900)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "UNCOMMON", 0.7),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.6),
                new DungeonLootTable(60, 65, 55, "RARE", 0.35),
                new DungeonLootTable(80, 65, 75, "RARE", 0.2)
            )
        );
    }
}
