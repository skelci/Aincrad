package com.aincrad.dungeon.layout.volcano;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VolcanoFloor5Layout extends DungeonLayout {
    public VolcanoFloor5Layout() {
        super("volcano_floor_5", "Volcano", 5, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:lava_channels", "lava_channels:magma_chamber", "magma_chamber:obsidian_hall", "obsidian_hall:basalt_delta", "basalt_delta:sulfur_caverns", "sulfur_caverns:inferno_core", "inferno_core:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("BLAZE", 0, 64, 5, 9, 200),
                new DungeonMobPlacement("MAGMA_CUBE", 20, 64, 25, 7, 340),
                new DungeonMobPlacement("WITHER_SKELETON", 40, 64, 20, 6, 550),
                new DungeonMobPlacement("PIGLIN_BRUTE", 55, 64, 35, 5, 480),
                new DungeonMobPlacement("ZOMBIFIED_PIGLIN", 35, 64, 50, 10, 750),
                new DungeonMobPlacement("HOGLIN", 70, 64, 60, 4, 1000),
                new DungeonMobPlacement("WITHER", 85, 64, 75, 1, 1800)
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
