package com.aincrad.dungeon.layout.ruins;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class RuinsFloor5Layout extends DungeonLayout {
    public RuinsFloor5Layout() {
        super("ruins_floor_5", "Ruins", 5, DungeonTier.T3_HARD,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:hallway_1", "hallway_1:hallway_2", "hallway_2:hallway_3", "hallway_3:hallway_4", "hallway_4:hallway_5", "hallway_5:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 40, 64, 30, 5, 400),
                new DungeonMobPlacement("ZOMBIE", 60, 64, 50, 5, 500),
                new DungeonMobPlacement("SPIDER", 80, 64, 40, 4, 400),
                new DungeonMobPlacement("CREEPER", 95, 64, 65, 4, 600),
                new DungeonMobPlacement("WITCH", 110, 64, 80, 3, 800),
                new DungeonMobPlacement("ENDERMAN", 130, 64, 100, 3, 1000),
                new DungeonMobPlacement("BLAZE", 145, 64, 115, 2, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(50, 65, 35, "EPIC", 0.35),
                new DungeonLootTable(70, 65, 60, "EPIC", 0.30),
                new DungeonLootTable(100, 65, 70, "LEGENDARY", 0.15),
                new DungeonLootTable(120, 65, 90, "LEGENDARY", 0.12)
            )
        );
    }
}
