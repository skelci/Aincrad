package com.aincrad.dungeon.layout.volcano;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VolcanoFloor2Layout extends DungeonLayout {
    public VolcanoFloor2Layout() {
        super("volcano_floor_2", "Volcano", 2, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:lava_channels", "lava_channels:magma_chamber", "magma_chamber:obsidian_hall", "obsidian_hall:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("BLAZE", 15, 64, 20, 5, 320),
                new DungeonMobPlacement("MAGMA_CUBE", 35, 64, 40, 4, 460),
                new DungeonMobPlacement("WITHER_SKELETON", 55, 64, 25, 3, 700),
                new DungeonMobPlacement("PIGLIN_BRUTE", 70, 64, 50, 2, 600)
            ),
            Arrays.asList(
                new DungeonLootTable(25, 66, 20, "LEGENDARY", 0.22),
                new DungeonLootTable(45, 66, 35, "MYTHIC", 0.12),
                new DungeonLootTable(65, 66, 55, "MYTHIC", 0.08)
            )
        );
    }
}
