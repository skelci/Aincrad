package com.aincrad.dungeon.layout.oasis;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OasisFloor4Layout extends DungeonLayout {
    public OasisFloor4Layout() {
        super("oasis_floor_4", "Oasis", 4, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:palm_grove", "palm_grove:water_pool", "water_pool:sunken_ruins", "sunken_ruins:coral_depths", "coral_depths:kelp_forest", "kelp_forest:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 5, 64, 10, 8, 220),
                new DungeonMobPlacement("DROWNED", 25, 64, 30, 6, 380),
                new DungeonMobPlacement("GUARDIAN", 45, 64, 25, 5, 600),
                new DungeonMobPlacement("CREEPER", 60, 64, 40, 4, 500),
                new DungeonMobPlacement("TROPICAL_FISH", 40, 64, 55, 10, 900),
                new DungeonMobPlacement("DOLPHIN", 75, 64, 65, 3, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(15, 68, 10, "LEGENDARY", 0.26),
                new DungeonLootTable(35, 68, 25, "MYTHIC", 0.16),
                new DungeonLootTable(55, 68, 45, "MYTHIC", 0.12),
                new DungeonLootTable(75, 68, 65, "MYTHIC", 0.08),
                new DungeonLootTable(95, 68, 85, "MYTHIC", 0.05)
            )
        );
    }
}
