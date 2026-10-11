package com.aincrad.dungeon.layout.frozenlake;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class FrozenLakeFloor1Layout extends DungeonLayout {
    public FrozenLakeFloor1Layout() {
        super("frozenlake_floor_1", "FrozenLake", 1, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 20, 64, 10, 3, 500),
                new DungeonMobPlacement("DROWNED", 40, 63, 30, 3, 600),
                new DungeonMobPlacement("GUARDIAN", 60, 62, 20, 1, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 63, 15, "RARE", 0.4),
                new DungeonLootTable(50, 63, 40, "EPIC", 0.2)
            )
        );
    }
}
