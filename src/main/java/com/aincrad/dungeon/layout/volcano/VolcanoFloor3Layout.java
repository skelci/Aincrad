package com.aincrad.dungeon.layout.volcano;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VolcanoFloor3Layout extends DungeonLayout {
    public VolcanoFloor3Layout() {
        super("volcano_floor_3", "Volcano", 3, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:lava_channels", "lava_channels:magma_chamber", "magma_chamber:obsidian_hall", "obsidian_hall:basalt_delta", "basalt_delta:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("BLAZE", 10, 64, 15, 6, 280),
                new DungeonMobPlacement("MAGMA_CUBE", 30, 64, 35, 5, 420),
                new DungeonMobPlacement("WITHER_SKELETON", 50, 64, 30, 4, 650),
                new DungeonMobPlacement("PIGLIN_BRUTE", 65, 64, 45, 3, 550),
                new DungeonMobPlacement("ZOMBIFIED_PIGLIN", 45, 64, 60, 6, 900)
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
