package com.aincrad.dungeon.layout.swamp;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SwampFloor5Layout extends DungeonLayout {
    public SwampFloor5Layout() {
        super("swamp_floor_5", "Swamp", 5, DungeonTier.T4_EXPERT,
            0, 64, 0, 220, 70, 220,
            Arrays.asList("entrance:spawn", "spawn:muddy_path", "muddy_path:willow_grove", "willow_grove:bog_water", "bog_water:mangrove_thicket", "mangrove_thicket:venom_pool", "venom_pool:fungal_hollow", "fungal_hollow:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SLIME", 20, 64, 10, 7, 300),
                new DungeonMobPlacement("WITCH", 40, 64, 30, 6, 600),
                new DungeonMobPlacement("ZOMBIE", 60, 64, 20, 7, 500),
                new DungeonMobPlacement("FROG", 80, 64, 50, 6, 450),
                new DungeonMobPlacement("CREEPER", 100, 64, 70, 5, 700),
                new DungeonMobPlacement("BOGGED", 120, 64, 90, 3, 950),
                new DungeonMobPlacement("ALLAY", 140, 64, 110, 2, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "EPIC", 0.4),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.32),
                new DungeonLootTable(70, 65, 60, "LEGENDARY", 0.18),
                new DungeonLootTable(90, 65, 80, "LEGENDARY", 0.1),
                new DungeonLootTable(110, 65, 100, "MYTHIC", 0.05),
                new DungeonLootTable(130, 65, 120, "MYTHIC", 0.02)
            )
        );
    }
}
