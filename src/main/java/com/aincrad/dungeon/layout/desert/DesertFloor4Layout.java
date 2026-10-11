package com.aincrad.dungeon.layout.desert;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class DesertFloor4Layout extends DungeonLayout {
    public DesertFloor4Layout() {
        super("desert_floor_4", "Desert", 4, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:canyon_1", "canyon_1:canyon_2", "canyon_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 20, 64, 10, 6, 300),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 5, 500),
                new DungeonMobPlacement("SKELETON", 60, 64, 20, 5, 400),
                new DungeonMobPlacement("SPIDER", 80, 64, 50, 4, 450),
                new DungeonMobPlacement("CAVE_SPIDER", 100, 64, 70, 4, 400)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "EPIC", 0.25),
                new DungeonLootTable(50, 65, 40, "LEGENDARY", 0.15)
            )
        );
    }
}
