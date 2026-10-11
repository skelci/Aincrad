package com.aincrad.dungeon.layout.volcano;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VolcanoFloor4Layout extends DungeonLayout {
    public VolcanoFloor4Layout() {
        super("volcano_floor_4", "Volcano", 4, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:lava_channels", "lava_channels:magma_chamber", "magma_chamber:obsidian_hall", "obsidian_hall:basalt_delta", "basalt_delta:sulfur_caverns", "sulfur_caverns:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("BLAZE", 5, 64, 10, 7, 240),
                new DungeonMobPlacement("MAGMA_CUBE", 25, 64, 30, 6, 380),
                new DungeonMobPlacement("WITHER_SKELETON", 45, 64, 25, 5, 600),
                new DungeonMobPlacement("PIGLIN_BRUTE", 60, 64, 40, 4, 500),
                new DungeonMobPlacement("ZOMBIFIED_PIGLIN", 40, 64, 55, 8, 800),
                new DungeonMobPlacement("HOGLIN", 75, 64, 65, 3, 1100)
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
