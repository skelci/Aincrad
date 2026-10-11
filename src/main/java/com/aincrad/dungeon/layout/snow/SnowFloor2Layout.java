package com.aincrad.dungeon.layout.snow;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SnowFloor2Layout extends DungeonLayout {
    public SnowFloor2Layout() {
        super("snow_floor_2", "Snow", 2, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 15, 64, 10, 4, 450),
                new DungeonMobPlacement("POLAR_BEAR", 35, 64, 25, 2, 900),
                new DungeonMobPlacement("VINDICATOR", 55, 64, 15, 3, 700),
                new DungeonMobPlacement("STRAY", 70, 64, 45, 3, 500)
            ),
            Arrays.asList(
                new DungeonLootTable(25, 65, 20, "RARE", 0.35),
                new DungeonLootTable(45, 65, 35, "EPIC", 0.25),
                new DungeonLootTable(65, 65, 50, "LEGENDARY", 0.12)
            )
        );
    }
}
