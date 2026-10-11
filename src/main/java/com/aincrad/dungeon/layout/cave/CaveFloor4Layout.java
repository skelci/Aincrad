package com.aincrad.dungeon.layout.cave;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class CaveFloor4Layout extends DungeonLayout {
    public CaveFloor4Layout() {
        super("cave_floor_4", "Cave", 4, DungeonTier.T2_NORMAL,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:tunnel_1", "tunnel_1:tunnel_2", "tunnel_2:cavern_1", "cavern_1:cavern_2", "cavern_2:cavern_3", "cavern_3:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ZOMBIE", 15, 64, 10, 6, 450),
                new DungeonMobPlacement("SKELETON", 30, 64, 25, 5, 550),
                new DungeonMobPlacement("CAVE_SPIDER", 50, 64, 40, 5, 400),
                new DungeonMobPlacement("BAT", 65, 64, 55, 7, 250),
                new DungeonMobPlacement("SILVERFISH", 75, 64, 65, 5, 350),
                new DungeonMobPlacement("CREEPER", 90, 64, 80, 3, 650),
                new DungeonMobPlacement("SLIME", 105, 64, 95, 3, 500)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.85),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.65),
                new DungeonLootTable(60, 65, 55, "RARE", 0.4),
                new DungeonLootTable(80, 65, 75, "RARE", 0.25),
                new DungeonLootTable(100, 65, 95, "EPIC", 0.1)
            )
        );
    }
}
