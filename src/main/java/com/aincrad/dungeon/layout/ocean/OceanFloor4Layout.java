package com.aincrad.dungeon.layout.ocean;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OceanFloor4Layout extends DungeonLayout {
    public OceanFloor4Layout() {
        super("ocean_floor_4", "Ocean", 4, DungeonTier.T2_NORMAL,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:water_tunnel", "water_tunnel:coral_hall", "coral_hall:kelp_grove", "kelp_grove:sunken_ruins", "sunken_ruins:deep_trench", "deep_trench:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("DROWNED", 15, 64, 10, 6, 450),
                new DungeonMobPlacement("COD", 30, 64, 25, 7, 350),
                new DungeonMobPlacement("SALMON", 50, 64, 40, 6, 400),
                new DungeonMobPlacement("PUFFERFISH", 65, 64, 55, 4, 600),
                new DungeonMobPlacement("GUARDIAN", 80, 64, 70, 3, 850),
                new DungeonMobPlacement("DOLPHIN", 95, 64, 85, 2, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "UNCOMMON", 0.75),
                new DungeonLootTable(40, 65, 35, "RARE", 0.45),
                new DungeonLootTable(60, 65, 55, "RARE", 0.35),
                new DungeonLootTable(80, 65, 75, "EPIC", 0.15),
                new DungeonLootTable(100, 65, 95, "EPIC", 0.08)
            )
        );
    }
}
