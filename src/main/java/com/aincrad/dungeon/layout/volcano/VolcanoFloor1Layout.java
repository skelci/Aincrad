package com.aincrad.dungeon.layout.volcano;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VolcanoFloor1Layout extends DungeonLayout {
    public VolcanoFloor1Layout() {
        super("volcano_floor_1", "Volcano", 1, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:lava_channels", "lava_channels:magma_chamber", "magma_chamber:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("BLAZE", 20, 64, 10, 4, 350),
                new DungeonMobPlacement("MAGMA_CUBE", 40, 64, 30, 3, 500),
                new DungeonMobPlacement("WITHER_SKELETON", 60, 64, 20, 2, 800)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "LEGENDARY", 0.2),
                new DungeonLootTable(50, 65, 40, "MYTHIC", 0.1)
            )
        );
    }
}
