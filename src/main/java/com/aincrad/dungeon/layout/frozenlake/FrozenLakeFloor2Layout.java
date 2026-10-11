package com.aincrad.dungeon.layout.frozenlake;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class FrozenLakeFloor2Layout extends DungeonLayout {
    public FrozenLakeFloor2Layout() {
        super("frozenlake_floor_2", "FrozenLake", 2, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 15, 64, 10, 4, 450),
                new DungeonMobPlacement("DROWNED", 35, 63, 25, 4, 550),
                new DungeonMobPlacement("GUARDIAN", 55, 62, 15, 2, 1100),
                new DungeonMobPlacement("DROWNED", 70, 63, 45, 3, 600)
            ),
            Arrays.asList(
                new DungeonLootTable(25, 63, 20, "RARE", 0.35),
                new DungeonLootTable(45, 63, 35, "EPIC", 0.25),
                new DungeonLootTable(65, 63, 50, "LEGENDARY", 0.12)
            )
        );
    }
}
