package com.aincrad.dungeon.layout.cave;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CaveFloor3Layout extends DungeonLayout {
    public CaveFloor3Layout() {
        super("cave_floor_3", "Cave", 3, DungeonTier.T2_NORMAL,
            0, 64, 0, 120, 70, 120,
            Arrays.asList("entrance:spawn", "spawn:tunnel_1", "tunnel_1:tunnel_2", "tunnel_2:cavern_1", "cavern_1:cavern_2", "cavern_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 15, 64, 10, 5, 500),
                new DungeonMobPlacement("SKELETON", 30, 64, 25, 4, 600),
                new DungeonMobPlacement("CAVE_SPIDER", 50, 64, 40, 4, 450),
                new DungeonMobPlacement("BAT", 65, 64, 55, 6, 300),
                new DungeonMobPlacement("SILVERFISH", 75, 64, 65, 4, 400),
                new DungeonMobPlacement("CREEPER", 90, 64, 80, 2, 700)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.8),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.6),
                new DungeonLootTable(60, 65, 55, "RARE", 0.35),
                new DungeonLootTable(80, 65, 75, "RARE", 0.2)
            )
        );
    }
}
