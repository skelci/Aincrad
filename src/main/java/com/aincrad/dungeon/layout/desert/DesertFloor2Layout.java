package com.aincrad.dungeon.layout.desert;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class DesertFloor2Layout extends DungeonLayout {
    public DesertFloor2Layout() {
        super("desert_floor_2", "Desert", 2, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:canyon_1", "canyon_1:canyon_2", "canyon_2:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 20, 64, 10, 5, 300),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 4, 500),
                new DungeonMobPlacement("SKELETON", 60, 64, 20, 4, 400)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "UNCOMMON", 0.6),
                new DungeonLootTable(50, 65, 40, "RARE", 0.3)
            )
        );
    }
}
