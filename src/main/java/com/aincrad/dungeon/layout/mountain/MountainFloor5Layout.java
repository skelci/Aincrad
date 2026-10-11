package com.aincrad.dungeon.layout.mountain;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class MountainFloor5Layout extends DungeonLayout {
    public MountainFloor5Layout() {
        super("mountain_floor_5", "Mountain", 5, DungeonTier.T3_HARD,
            0, 64, 0, 200, 70, 200,
            Arrays.asList("entrance:spawn", "spawn:rocky_path", "rocky_path:cliffside", "cliffside:windy_ridge", "windy_ridge:stone_spire", "stone_spire:cloud_passage", "cloud_passage:sky_temple", "sky_temple:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("SKELETON", 20, 64, 10, 7, 600),
                new DungeonMobPlacement("CREEPER", 40, 64, 30, 6, 700),
                new DungeonMobPlacement("SPIDER", 60, 64, 20, 6, 600),
                new DungeonMobPlacement("VEX", 80, 64, 50, 5, 750),
                new DungeonMobPlacement("PILLAGER", 100, 64, 70, 4, 900),
                new DungeonMobPlacement("VINDICATOR", 120, 64, 90, 3, 1050),
                new DungeonMobPlacement("EVOKER", 140, 64, 110, 2, 1300)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "EPIC", 0.35),
                new DungeonLootTable(50, 65, 40, "EPIC", 0.28),
                new DungeonLootTable(70, 65, 60, "LEGENDARY", 0.15),
                new DungeonLootTable(90, 65, 80, "LEGENDARY", 0.1),
                new DungeonLootTable(110, 65, 100, "MYTHIC", 0.04),
                new DungeonLootTable(130, 65, 120, "MYTHIC", 0.02)
            )
        );
    }
}
