package com.aincrad.dungeon.layout.frozenlake;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class FrozenLakeFloor3Layout extends DungeonLayout {
    public FrozenLakeFloor3Layout() {
        super("frozenlake_floor_3", "FrozenLake", 3, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:clearing_4", "clearing_4:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 10, 64, 8, 5, 400),
                new DungeonMobPlacement("DROWNED", 30, 63, 20, 5, 500),
                new DungeonMobPlacement("GUARDIAN", 50, 62, 12, 3, 1000),
                new DungeonMobPlacement("DROWNED", 65, 63, 35, 4, 550),
                new DungeonMobPlacement("GUARDIAN", 80, 62, 55, 2, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 63, 15, "EPIC", 0.3),
                new DungeonLootTable(40, 63, 30, "EPIC", 0.25),
                new DungeonLootTable(60, 63, 45, "LEGENDARY", 0.15),
                new DungeonLootTable(80, 63, 60, "LEGENDARY", 0.1)
            )
        );
    }
}
