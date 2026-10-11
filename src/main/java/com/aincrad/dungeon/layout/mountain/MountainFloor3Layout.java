package com.aincrad.dungeon.layout.mountain;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class MountainFloor3Layout extends DungeonLayout {
    public MountainFloor3Layout() {
        super("mountain_floor_3", "Mountain", 3, DungeonTier.T3_HARD,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:rocky_path", "rocky_path:cliffside", "cliffside:windy_ridge", "windy_ridge:stone_spire", "stone_spire:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 5, 700),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 4, 800),
                new DungeonMobPlacement("SPIDER", 60, 64, 20, 4, 700),
                new DungeonMobPlacement("VEX", 80, 64, 50, 3, 850),
                new DungeonMobPlacement("PILLAGER", 100, 64, 70, 2, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.45),
                new DungeonLootTable(50, 65, 40, "RARE", 0.4),
                new DungeonLootTable(70, 65, 60, "EPIC", 0.2),
                new DungeonLootTable(90, 65, 80, "EPIC", 0.1)
            )
        );
    }
}
