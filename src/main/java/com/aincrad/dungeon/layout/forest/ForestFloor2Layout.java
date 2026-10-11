package com.aincrad.dungeon.layout.forest;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.DungeonLayout;
import com.aincrad.dungeon.layout.DungeonMobPlacement;
import com.aincrad.dungeon.layout.DungeonLootTable;

import java.util.Arrays;
import java.util.List;

public final class ForestFloor2Layout extends DungeonLayout {

    public ForestFloor2Layout() {
        super(
            "forest_floor_2",
            "Forest",
            2,
            DungeonTier.T1_EASY,
            0, 64, 0,
            120, 70, 120,
            Arrays.asList(
                "entrance:spawn",
                "spawn:clearing_1",
                "clearing_1:clearing_2",
                "clearing_2:clearing_3",
                "clearing_3:boss_room"
            ),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 15, 64, 15, 4, 500),
                new DungeonMobPlacement("SKELETON", 35, 64, 25, 3, 700),
                new DungeonMobPlacement("SPIDER", 55, 64, 35, 5, 350),
                new DungeonMobPlacement("CREEPER", 80, 64, 60, 2, 900)
            ),
            Arrays.asList(
                new DungeonLootTable(25, 65, 20, "COMMON", 0.75),
                new DungeonLootTable(45, 65, 35, "UNCOMMON", 0.55),
                new DungeonLootTable(70, 65, 55, "RARE", 0.3)
            )
        );
    }
}
