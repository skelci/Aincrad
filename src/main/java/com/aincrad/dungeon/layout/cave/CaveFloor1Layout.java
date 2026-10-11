package com.aincrad.dungeon.layout.cave;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CaveFloor1Layout extends DungeonLayout {
    public CaveFloor1Layout() {
        super("cave_floor_1", "Cave", 1, DungeonTier.T2_NORMAL,
            0, 64, 0, 80, 70, 80,
            Arrays.asList("entrance:spawn", "spawn:tunnel_1", "tunnel_1:tunnel_2", "tunnel_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 15, 64, 10, 3, 600),
                new DungeonMobPlacement("SKELETON", 30, 64, 25, 2, 700),
                new DungeonMobPlacement("SPIDER", 50, 64, 40, 2, 550),
                new DungeonMobPlacement("BAT", 65, 64, 55, 4, 400)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.7),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.5)
            )
        );
    }
}
