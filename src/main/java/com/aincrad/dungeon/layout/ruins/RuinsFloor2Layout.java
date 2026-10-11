package com.aincrad.dungeon.layout.ruins;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class RuinsFloor2Layout extends DungeonLayout {
    public RuinsFloor2Layout() {
        super("ruins_floor_2", "Ruins", 2, DungeonTier.T3_HARD,
            0, 64, 0, 130, 70, 130,
            Arrays.asList("entrance:spawn", "spawn:hallway_1", "hallway_1:hallway_2", "hallway_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 25, 64, 15, 4, 700),
                new DungeonMobPlacement("ZOMBIE", 45, 64, 35, 3, 800),
                new DungeonMobPlacement("SPIDER", 65, 64, 25, 3, 700),
                new DungeonMobPlacement("CREEPER", 80, 64, 50, 2, 900)
            ),
            Arrays.asList(
                new DungeonLootTable(35, 65, 20, "RARE", 0.38),
                new DungeonLootTable(55, 65, 45, "EPIC", 0.20)
            )
        );
    }
}
