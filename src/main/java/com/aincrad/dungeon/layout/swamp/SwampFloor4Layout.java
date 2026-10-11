package com.aincrad.dungeon.layout.swamp;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SwampFloor4Layout extends DungeonLayout {
    public SwampFloor4Layout() {
        super("swamp_floor_4", "Swamp", 4, DungeonTier.T4_EXPERT,
            0, 64, 0, 200, 70, 200,
            Arrays.asList("entrance:spawn", "spawn:muddy_path", "muddy_path:willow_grove", "willow_grove:bog_water", "bog_water:mangrove_thicket", "mangrove_thicket:venom_pool", "venom_pool:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SLIME", 20, 64, 10, 6, 350),
                new DungeonMobPlacement("WITCH", 40, 64, 30, 5, 650),
                new DungeonMobPlacement("ZOMBIE", 60, 64, 20, 6, 550),
                new DungeonMobPlacement("FROG", 80, 64, 50, 5, 500),
                new DungeonMobPlacement("CREEPER", 100, 64, 70, 4, 750),
                new DungeonMobPlacement("BOGGED", 120, 64, 90, 2, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "EPIC", 0.35),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.28),
                new DungeonLootTable(70, 65, 60, "LEGENDARY", 0.12),
                new DungeonLootTable(90, 65, 80, "LEGENDARY", 0.07),
                new DungeonLootTable(110, 65, 100, "MYTHIC", 0.03)
            )
        );
    }
}
