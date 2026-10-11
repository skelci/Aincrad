package com.aincrad.dungeon.layout.cave;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CaveFloor2Layout extends DungeonLayout {
    public CaveFloor2Layout() {
        super("cave_floor_2", "Cave", 2, DungeonTier.T2_NORMAL,
            0, 64, 0, 100, 70, 100,
            Arrays.asList("entrance:spawn", "spawn:tunnel_1", "tunnel_1:tunnel_2", "tunnel_2:cavern_1", "cavern_1:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 15, 64, 10, 4, 550),
                new DungeonMobPlacement("SKELETON", 30, 64, 25, 3, 650),
                new DungeonMobPlacement("CAVE_SPIDER", 50, 64, 40, 3, 500),
                new DungeonMobPlacement("BAT", 65, 64, 55, 5, 350),
                new DungeonMobPlacement("SILVERFISH", 75, 64, 65, 3, 450)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.75),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.55),
                new DungeonLootTable(60, 65, 55, "RARE", 0.3)
            )
        );
    }
}
