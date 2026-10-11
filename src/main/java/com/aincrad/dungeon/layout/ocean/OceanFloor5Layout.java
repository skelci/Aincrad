package com.aincrad.dungeon.layout.ocean;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OceanFloor5Layout extends DungeonLayout {
    public OceanFloor5Layout() {
        super("ocean_floor_5", "Ocean", 5, DungeonTier.T2_NORMAL,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:water_tunnel", "water_tunnel:coral_hall", "coral_hall:kelp_grove", "kelp_grove:sunken_ruins", "sunken_ruins:deep_trench", "deep_trench:abyssal_depths", "abyssal_depths:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("DROWNED", 15, 64, 10, 7, 400),
                new DungeonMobPlacement("COD", 30, 64, 25, 8, 300),
                new DungeonMobPlacement("SALMON", 50, 64, 40, 7, 350),
                new DungeonMobPlacement("PUFFERFISH", 65, 64, 55, 5, 550),
                new DungeonMobPlacement("GUARDIAN", 80, 64, 70, 4, 800),
                new DungeonMobPlacement("DOLPHIN", 95, 64, 85, 3, 950),
                new DungeonMobPlacement("ELDER_GUARDIAN", 110, 64, 100, 1, 1500)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "RARE", 0.55),
                new DungeonLootTable(40, 65, 35, "RARE", 0.45),
                new DungeonLootTable(60, 65, 55, "EPIC", 0.25),
                new DungeonLootTable(80, 65, 75, "EPIC", 0.18),
                new DungeonLootTable(100, 65, 95, "LEGENDARY", 0.08),
                new DungeonLootTable(120, 65, 115, "LEGENDARY", 0.04)
            )
        );
    }
}
