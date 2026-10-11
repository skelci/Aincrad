package com.aincrad.dungeon.layout.oasis;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OasisFloor2Layout extends DungeonLayout {
    public OasisFloor2Layout() {
        super("oasis_floor_2", "Oasis", 2, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:palm_grove", "palm_grove:water_pool", "water_pool:sunken_ruins", "sunken_ruins:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 15, 64, 20, 6, 280),
                new DungeonMobPlacement("DROWNED", 35, 64, 40, 4, 450),
                new DungeonMobPlacement("GUARDIAN", 55, 64, 25, 3, 700),
                new DungeonMobPlacement("CREEPER", 70, 64, 50, 2, 600)
            ),
            Arrays.asList(
                new DungeonLootTable(25, 66, 20, "LEGENDARY", 0.22),
                new DungeonLootTable(45, 66, 35, "MYTHIC", 0.12),
                new DungeonLootTable(65, 66, 55, "MYTHIC", 0.08)
            )
        );
    }
}
