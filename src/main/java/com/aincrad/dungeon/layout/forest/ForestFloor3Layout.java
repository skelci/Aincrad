package com.aincrad.dungeon.layout.forest;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.DungeonLayout;
import com.aincrad.dungeon.layout.DungeonMobPlacement;
import com.aincrad.dungeon.layout.DungeonLootTable;

import java.util.Arrays;
import java.util.List;

public final class ForestFloor3Layout extends DungeonLayout {

    public ForestFloor3Layout() {
        super(
            "forest_floor_3",
            "Forest",
            3,
            DungeonTier.T1_EASY,
            0, 64, 0,
            140, 72, 140,
            Arrays.asList(
                "entrance:spawn",
                "spawn:clearing_1",
                "spawn:clearing_2",
                "clearing_1:clearing_3",
                "clearing_2:clearing_3",
                "clearing_3:boss_room"
            ),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 10, 64, 20, 5, 450),
                new DungeonMobPlacement("SKELETON", 30, 64, 40, 4, 600),
                new DungeonMobPlacement("SPIDER", 50, 64, 50, 6, 300),
                new DungeonMobPlacement("CREEPER", 70, 64, 70, 3, 800),
                new DungeonMobPlacement("WITCH", 90, 64, 90, 2, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 25, "UNCOMMON", 0.6),
                new DungeonLootTable(40, 65, 45, "RARE", 0.4),
                new DungeonLootTable(65, 65, 70, "EPIC", 0.2)
            )
        );
    }
}
