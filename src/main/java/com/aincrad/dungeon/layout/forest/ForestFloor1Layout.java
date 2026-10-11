package com.aincrad.dungeon.layout.forest;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.DungeonLayout;
import com.aincrad.dungeon.layout.DungeonMobPlacement;
import com.aincrad.dungeon.layout.DungeonLootTable;

import java.util.Arrays;
import java.util.List;

public final class ForestFloor1Layout extends DungeonLayout {

    public ForestFloor1Layout() {
        super(
            "forest_floor_1",
            "Forest",
            1,
            DungeonTier.T1_EASY,
            0, 64, 0,
            100, 70, 100,
            Arrays.asList(
                "entrance:spawn",
                "spawn:clearing_1",
                "clearing_1:clearing_2",
                "clearing_2:boss_room"
            ),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 20, 64, 10, 3, 600),
                new DungeonMobPlacement("SKELETON", 40, 64, 30, 2, 800),
                new DungeonMobPlacement("SPIDER", 60, 64, 20, 4, 400)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "COMMON", 0.8),
                new DungeonLootTable(50, 65, 40, "UNCOMMON", 0.5)
            )
        );
    }
}
