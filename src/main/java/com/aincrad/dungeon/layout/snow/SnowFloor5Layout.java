package com.aincrad.dungeon.layout.snow;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SnowFloor5Layout extends DungeonLayout {
    public SnowFloor5Layout() {
        super("snow_floor_5", "Snow", 5, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:clearing_4", "clearing_4:clearing_5", "clearing_5:clearing_6", "clearing_6:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 5, 64, 5, 7, 300),
                new DungeonMobPlacement("POLAR_BEAR", 20, 64, 12, 5, 700),
                new DungeonMobPlacement("VINDICATOR", 40, 64, 8, 6, 550),
                new DungeonMobPlacement("STRAY", 55, 64, 25, 6, 350),
                new DungeonMobPlacement("POLAR_BEAR", 70, 64, 45, 4, 800),
                new DungeonMobPlacement("VINDICATOR", 85, 64, 60, 5, 600),
                new DungeonMobPlacement("STRAY", 95, 64, 70, 5, 400)
            ),
            Arrays.asList(
                new DungeonLootTable(12, 65, 10, "EPIC", 0.4),
                new DungeonLootTable(30, 65, 22, "LEGENDARY", 0.25),
                new DungeonLootTable(50, 65, 38, "LEGENDARY", 0.22),
                new DungeonLootTable(70, 65, 52, "MYTHIC", 0.1),
                new DungeonLootTable(90, 65, 68, "MYTHIC", 0.07)
            )
        );
    }
}
