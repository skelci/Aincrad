package com.aincrad.dungeon.layout.oasis;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class OasisFloor5Layout extends DungeonLayout {
    public OasisFloor5Layout() {
        super("oasis_floor_5", "Oasis", 5, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:palm_grove", "palm_grove:water_pool", "water_pool:sunken_ruins", "sunken_ruins:coral_depths", "coral_depths:kelp_forest", "kelp_forest:ancient_wreck", "ancient_wreck:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 0, 64, 5, 10, 200),
                new DungeonMobPlacement("DROWNED", 20, 64, 25, 7, 350),
                new DungeonMobPlacement("GUARDIAN", 40, 64, 20, 6, 550),
                new DungeonMobPlacement("CREEPER", 55, 64, 35, 5, 480),
                new DungeonMobPlacement("TROPICAL_FISH", 35, 64, 50, 12, 800),
                new DungeonMobPlacement("DOLPHIN", 70, 64, 60, 4, 1000),
                new DungeonMobPlacement("ELDER_GUARDIAN", 85, 64, 75, 1, 1500)
            ),
            Arrays.asList(
                new DungeonLootTable(10, 69, 5, "LEGENDARY", 0.28),
                new DungeonLootTable(30, 69, 20, "MYTHIC", 0.18),
                new DungeonLootTable(50, 69, 40, "MYTHIC", 0.14),
                new DungeonLootTable(70, 69, 60, "MYTHIC", 0.10),
                new DungeonLootTable(90, 69, 80, "MYTHIC", 0.07),
                new DungeonLootTable(110, 69, 100, "MYTHIC", 0.04)
            )
        );
    }
}
