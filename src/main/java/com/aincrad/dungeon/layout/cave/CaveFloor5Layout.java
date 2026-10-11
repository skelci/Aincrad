package com.aincrad.dungeon.layout.cave;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CaveFloor5Layout extends DungeonLayout {
    public CaveFloor5Layout() {
        super("cave_floor_5", "Cave", 5, DungeonTier.T2_NORMAL,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:tunnel_1", "tunnel_1:tunnel_2", "tunnel_2:cavern_1", "cavern_1:cavern_2", "cavern_2:cavern_3", "cavern_3:cavern_4", "cavern_4:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 15, 64, 10, 7, 400),
                new DungeonMobPlacement("SKELETON", 30, 64, 25, 6, 500),
                new DungeonMobPlacement("CAVE_SPIDER", 50, 64, 40, 6, 350),
                new DungeonMobPlacement("BAT", 65, 64, 55, 8, 200),
                new DungeonMobPlacement("SILVERFISH", 75, 64, 65, 6, 300),
                new DungeonMobPlacement("CREEPER", 90, 64, 80, 4, 600),
                new DungeonMobPlacement("SLIME", 105, 64, 95, 4, 450),
                new DungeonMobPlacement("WITCH", 120, 64, 110, 2, 800)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.9),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.7),
                new DungeonLootTable(60, 65, 55, "RARE", 0.45),
                new DungeonLootTable(80, 65, 75, "RARE", 0.3),
                new DungeonLootTable(100, 65, 95, "EPIC", 0.15),
                new DungeonLootTable(120, 65, 115, "EPIC", 0.05)
            )
        );
    }
}
