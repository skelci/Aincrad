package com.aincrad.dungeon.layout.ice;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class IceFloor4Layout extends DungeonLayout {
    public IceFloor4Layout() {
        super("ice_floor_4", "Ice", 4, DungeonTier.T3_HARD,
            0, 64, 0, 150, 70, 150,
            Arrays.asList("entrance:spawn", "spawn:hallway_1", "hallway_1:hallway_2", "hallway_2:hallway_3", "hallway_3:hallway_4", "hallway_4:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 35, 64, 25, 5, 500),
                new DungeonMobPlacement("POLAR_BEAR", 55, 64, 45, 4, 600),
                new DungeonMobPlacement("VINDICATOR", 75, 64, 35, 4, 500),
                new DungeonMobPlacement("VEX", 90, 64, 60, 3, 700),
                new DungeonMobPlacement("PILLAGER", 105, 64, 75, 2, 900),
                new DungeonMobPlacement("EVOKER", 120, 64, 90, 2, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(45, 65, 30, "RARE", 0.42),
                new DungeonLootTable(65, 65, 55, "EPIC", 0.28),
                new DungeonLootTable(95, 65, 65, "EPIC", 0.22),
                new DungeonLootTable(115, 65, 85, "LEGENDARY", 0.10)
            )
        );
    }
}
