package com.aincrad.dungeon.layout.swamp;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SwampFloor1Layout extends DungeonLayout {
    public SwampFloor1Layout() {
        super("swamp_floor_1", "Swamp", 1, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:muddy_path", "muddy_path:willow_grove", "willow_grove:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SLIME", 20, 64, 10, 3, 500),
                new DungeonMobPlacement("WITCH", 40, 64, 30, 2, 800),
                new DungeonMobPlacement("ZOMBIE", 60, 64, 20, 3, 700)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.4),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.2)
            )
        );
    }
}
