package com.aincrad.dungeon.layout.ruins;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class RuinsFloor3Layout extends DungeonLayout {
    public RuinsFloor3Layout() {
        super("ruins_floor_3", "Ruins", 3, DungeonTier.T3_HARD,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:hallway_1", "hallway_1:hallway_2", "hallway_2:hallway_3", "hallway_3:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 30, 64, 20, 4, 600),
                new DungeonMobPlacement("ZOMBIE", 50, 64, 40, 4, 700),
                new DungeonMobPlacement("SPIDER", 70, 64, 30, 3, 600),
                new DungeonMobPlacement("CREEPER", 85, 64, 55, 3, 800),
                new DungeonMobPlacement("WITCH", 100, 64, 70, 2, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(40, 65, 25, "RARE", 0.40),
                new DungeonLootTable(60, 65, 50, "EPIC", 0.25),
                new DungeonLootTable(90, 65, 60, "EPIC", 0.20)
            )
        );
    }
}
