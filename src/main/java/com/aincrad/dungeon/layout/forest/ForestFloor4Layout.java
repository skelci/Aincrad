package com.aincrad.dungeon.layout.forest;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.DungeonLayout;
import com.aincrad.dungeon.layout.DungeonMobPlacement;
import com.aincrad.dungeon.layout.DungeonLootTable;

import java.util.Arrays;
import java.util.List;

public final class ForestFloor4Layout extends DungeonLayout {

    public ForestFloor4Layout() {
        super(
            "forest_floor_4",
            "Forest",
            4,
            DungeonTier.T1_EASY,
            0, 64, 0,
            160, 74, 160,
            Arrays.asList(
                "entrance:spawn",
                "spawn:clearing_1",
                "spawn:clearing_2",
                "clearing_1:clearing_3",
                "clearing_2:clearing_4",
                "clearing_3:boss_room",
                "clearing_4:boss_room"
            ),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 10, 64, 10, 6, 400),
                new DungeonMobPlacement("SKELETON", 25, 64, 30, 5, 550),
                new DungeonMobPlacement("SPIDER", 40, 64, 45, 7, 280),
                new DungeonMobPlacement("CREEPER", 60, 64, 55, 4, 700),
                new DungeonMobPlacement("WITCH", 80, 64, 75, 3, 900),
                new DungeonMobPlacement("ENDERMAN", 100, 64, 100, 2, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(15, 65, 20, "RARE", 0.45),
                new DungeonLootTable(35, 65, 40, "EPIC", 0.3),
                new DungeonLootTable(60, 65, 65, "LEGENDARY", 0.15)
            )
        );
    }
}
