package com.aincrad.dungeon.layout.mountain;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class MountainFloor4Layout extends DungeonLayout {
    public MountainFloor4Layout() {
        super("mountain_floor_4", "Mountain", 4, DungeonTier.T3_HARD,
            0, 64, 0, 180, 70, 180,
            Arrays.asList("entrance:spawn", "spawn:rocky_path", "rocky_path:cliffside", "cliffside:windy_ridge", "windy_ridge:stone_spire", "stone_spire:cloud_passage", "cloud_passage:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 6, 650),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 5, 750),
                new DungeonMobPlacement("SPIDER", 60, 64, 20, 5, 650),
                new DungeonMobPlacement("VEX", 80, 64, 50, 4, 800),
                new DungeonMobPlacement("PILLAGER", 100, 64, 70, 3, 950),
                new DungeonMobPlacement("VINDICATOR", 120, 64, 90, 2, 1100)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "RARE", 0.5),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.3),
                new DungeonLootTable(70, 65, 60, "EPIC", 0.22),
                new DungeonLootTable(90, 65, 80, "LEGENDARY", 0.08),
                new DungeonLootTable(110, 65, 100, "LEGENDARY", 0.04)
            )
        );
    }
}
