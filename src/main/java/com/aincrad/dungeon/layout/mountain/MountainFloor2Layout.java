package com.aincrad.dungeon.layout.mountain;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class MountainFloor2Layout extends DungeonLayout {
    public MountainFloor2Layout() {
        super("mountain_floor_2", "Mountain", 2, DungeonTier.T3_HARD,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:rocky_path", "rocky_path:cliffside", "cliffside:windy_ridge", "windy_ridge:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 4, 750),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 3, 850),
                new DungeonMobPlacement("SPIDER", 60, 64, 20, 3, 750),
                new DungeonMobPlacement("VEX", 80, 64, 50, 2, 900)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.4),
                new DungeonLootTable(50, 65, 40, "RARE", 0.35),
                new DungeonLootTable(70, 65, 60, "EPIC", 0.15)
            )
        );
    }
}
