package com.aincrad.dungeon.layout.snow;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SnowFloor3Layout extends DungeonLayout {
    public SnowFloor3Layout() {
        super("snow_floor_3", "Snow", 3, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:clearing_4", "clearing_4:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 10, 64, 8, 5, 400),
                new DungeonMobPlacement("POLAR_BEAR", 30, 64, 20, 3, 800),
                new DungeonMobPlacement("VINDICATOR", 50, 64, 12, 4, 650),
                new DungeonMobPlacement("STRAY", 65, 64, 35, 4, 450),
                new DungeonMobPlacement("POLAR_BEAR", 80, 64, 55, 2, 900)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "EPIC", 0.3),
                new DungeonLootTable(40, 65, 30, "EPIC", 0.25),
                new DungeonLootTable(60, 65, 45, "LEGENDARY", 0.15),
                new DungeonLootTable(80, 65, 60, "LEGENDARY", 0.1)
            )
        );
    }
}
