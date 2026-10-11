package com.aincrad.dungeon.layout.frozenlake;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class FrozenLakeFloor5Layout extends DungeonLayout {
    public FrozenLakeFloor5Layout() {
        super("frozenlake_floor_5", "FrozenLake", 5, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:clearing_4", "clearing_4:clearing_5", "clearing_5:clearing_6", "clearing_6:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 5, 64, 5, 7, 300),
                new DungeonMobPlacement("DROWNED", 20, 63, 12, 7, 400),
                new DungeonMobPlacement("GUARDIAN", 40, 62, 8, 5, 900),
                new DungeonMobPlacement("DROWNED", 55, 63, 25, 6, 450),
                new DungeonMobPlacement("GUARDIAN", 70, 62, 45, 4, 1000),
                new DungeonMobPlacement("STRAY", 85, 64, 60, 6, 350),
                new DungeonMobPlacement("GUARDIAN", 95, 62, 70, 3, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(12, 63, 10, "EPIC", 0.4),
                new DungeonLootTable(30, 63, 22, "LEGENDARY", 0.25),
                new DungeonLootTable(50, 63, 38, "LEGENDARY", 0.22),
                new DungeonLootTable(70, 63, 52, "MYTHIC", 0.1),
                new DungeonLootTable(90, 63, 68, "MYTHIC", 0.07)
            )
        );
    }
}
