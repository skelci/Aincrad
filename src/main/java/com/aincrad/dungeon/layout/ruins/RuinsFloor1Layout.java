package com.aincrad.dungeon.layout.ruins;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class RuinsFloor1Layout extends DungeonLayout {
    public RuinsFloor1Layout() {
        super("ruins_floor_1", "Ruins", 1, DungeonTier.T3_HARD,
            0, 64, 0, 120, 70, 120,
            Arrays.asList("entrance:spawn", "spawn:hallway_1", "hallway_1:hallway_2", "hallway_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 3, 800),
                new DungeonMobPlacement("ZOMBIE", 40, 64, 30, 2, 900),
                new DungeonMobPlacement("SPIDER", 60, 64, 20, 2, 800)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.35),
                new DungeonLootTable(50, 65, 40, "RARE", 0.30)
            )
        );
    }
}
