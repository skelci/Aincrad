package com.aincrad.dungeon.layout.swamp;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SwampFloor2Layout extends DungeonLayout {
    public SwampFloor2Layout() {
        super("swamp_floor_2", "Swamp", 2, DungeonTier.T4_EXPERT,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:muddy_path", "muddy_path:willow_grove", "willow_grove:bog_water", "bog_water:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SLIME", 20, 64, 10, 4, 450),
                new DungeonMobPlacement("WITCH", 40, 64, 30, 3, 750),
                new DungeonMobPlacement("ZOMBIE", 60, 64, 20, 4, 650),
                new DungeonMobPlacement("FROG", 80, 64, 50, 3, 600)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.45),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.25),
                new DungeonLootTable(70, 65, 60, "EPIC", 0.12)
            )
        );
    }
}
