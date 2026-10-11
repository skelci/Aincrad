package com.aincrad.dungeon.layout.swamp;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SwampFloor3Layout extends DungeonLayout {
    public SwampFloor3Layout() {
        super("swamp_floor_3", "Swamp", 3, DungeonTier.T4_EXPERT,
            0, 64, 0, 180, 70, 180,
            Arrays.asList("entrance:spawn", "spawn:muddy_path", "muddy_path:willow_grove", "willow_grove:bog_water", "bog_water:mangrove_thicket", "mangrove_thicket:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SLIME", 20, 64, 10, 5, 400),
                new DungeonMobPlacement("WITCH", 40, 64, 30, 4, 700),
                new DungeonMobPlacement("ZOMBIE", 60, 64, 20, 5, 600),
                new DungeonMobPlacement("FROG", 80, 64, 50, 4, 550),
                new DungeonMobPlacement("CREEPER", 100, 64, 70, 3, 800)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.5),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.3),
                new DungeonLootTable(70, 65, 60, "EPIC", 0.18),
                new DungeonLootTable(90, 65, 80, "LEGENDARY", 0.06)
            )
        );
    }
}
