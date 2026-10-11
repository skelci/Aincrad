package com.aincrad.dungeon.layout.oasis;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OasisFloor3Layout extends DungeonLayout {
    public OasisFloor3Layout() {
        super("oasis_floor_3", "Oasis", 3, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:palm_grove", "palm_grove:water_pool", "water_pool:sunken_ruins", "sunken_ruins:coral_depths", "coral_depths:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 10, 64, 15, 7, 250),
                new DungeonMobPlacement("DROWNED", 30, 64, 35, 5, 400),
                new DungeonMobPlacement("GUARDIAN", 50, 64, 30, 4, 650),
                new DungeonMobPlacement("CREEPER", 65, 64, 45, 3, 550),
                new DungeonMobPlacement("TROPICAL_FISH", 45, 64, 60, 8, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 67, 10, "LEGENDARY", 0.24),
                new DungeonLootTable(40, 67, 30, "MYTHIC", 0.14),
                new DungeonLootTable(60, 67, 50, "MYTHIC", 0.10),
                new DungeonLootTable(80, 67, 70, "MYTHIC", 0.06)
            )
        );
    }
}
