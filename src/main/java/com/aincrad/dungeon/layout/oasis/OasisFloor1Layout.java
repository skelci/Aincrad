package com.aincrad.dungeon.layout.oasis;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OasisFloor1Layout extends DungeonLayout {
    public OasisFloor1Layout() {
        super("oasis_floor_1", "Oasis", 1, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:palm_grove", "palm_grove:water_pool", "water_pool:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 20, 64, 10, 5, 300),
                new DungeonMobPlacement("DROWNED", 40, 64, 30, 3, 500),
                new DungeonMobPlacement("GUARDIAN", 60, 64, 20, 2, 800)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "LEGENDARY", 0.2),
                new DungeonLootTable(50, 65, 40, "MYTHIC", 0.1)
            )
        );
    }
}
