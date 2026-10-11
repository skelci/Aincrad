package com.aincrad.dungeon.layout.snow;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SnowFloor1Layout extends DungeonLayout {
    public SnowFloor1Layout() {
        super("snow_floor_1", "Snow", 1, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 20, 64, 10, 3, 500),
                new DungeonMobPlacement("POLAR_BEAR", 40, 64, 30, 1, 1000),
                new DungeonMobPlacement("VINDICATOR", 60, 64, 20, 2, 800)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.4),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.2)
            )
        );
    }
}
