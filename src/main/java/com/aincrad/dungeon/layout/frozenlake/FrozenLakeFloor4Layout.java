package com.aincrad.dungeon.layout.frozenlake;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class FrozenLakeFloor4Layout extends DungeonLayout {
    public FrozenLakeFloor4Layout() {
        super("frozenlake_floor_4", "FrozenLake", 4, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:clearing_4", "clearing_4:clearing_5", "clearing_5:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 8, 64, 5, 6, 350),
                new DungeonMobPlacement("DROWNED", 25, 63, 15, 6, 450),
                new DungeonMobPlacement("GUARDIAN", 45, 62, 10, 4, 950),
                new DungeonMobPlacement("DROWNED", 60, 63, 30, 5, 500),
                new DungeonMobPlacement("GUARDIAN", 75, 62, 50, 3, 1100),
                new DungeonMobPlacement("STRAY", 90, 64, 65, 5, 400)
            ),
            Arrays.asList(
                new DungeonLootTable(15, 63, 12, "EPIC", 0.35),
                new DungeonLootTable(35, 63, 25, "LEGENDARY", 0.2),
                new DungeonLootTable(55, 63, 40, "LEGENDARY", 0.18),
                new DungeonLootTable(75, 63, 55, "MYTHIC", 0.08)
            )
        );
    }
}
