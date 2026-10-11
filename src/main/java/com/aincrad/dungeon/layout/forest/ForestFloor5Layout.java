package com.aincrad.dungeon.layout.forest;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.DungeonLayout;
import com.aincrad.dungeon.layout.DungeonMobPlacement;
import com.aincrad.dungeon.layout.DungeonLootTable;

import java.util.Arrays;
import java.util.List;

public final class ForestFloor5Layout extends DungeonLayout {

    public ForestFloor5Layout() {
        super(
            "forest_floor_5",
            "Forest",
            5,
            DungeonTier.T1_EASY,
            0, 64, 0,
            180, 76, 180,
            Arrays.asList(
                "entrance:spawn",
                "spawn:clearing_1",
                "spawn:clearing_2",
                "clearing_1:clearing_3",
                "clearing_2:clearing_3",
                "clearing_3:clearing_4",
                "clearing_4:clearing_5",
                "clearing_5:boss_room"
            ),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 5, 64, 15, 7, 350),
                new DungeonMobPlacement("SKELETON", 20, 64, 35, 6, 500),
                new DungeonMobPlacement("SPIDER", 35, 64, 50, 8, 250),
                new DungeonMobPlacement("CREEPER", 50, 64, 60, 5, 600),
                new DungeonMobPlacement("WITCH", 70, 64, 80, 4, 800),
                new DungeonMobPlacement("ENDERMAN", 90, 64, 95, 3, 1000),
                new DungeonMobPlacement("BLAZE", 110, 64, 110, 2, 1500)
            ),
            Arrays.asList(
                new DungeonLootTable(10, 65, 15, "EPIC", 0.35),
                new DungeonLootTable(30, 65, 35, "LEGENDARY", 0.2),
                new DungeonLootTable(55, 65, 60, "MYTHIC", 0.08)
            )
        );
    }
}
