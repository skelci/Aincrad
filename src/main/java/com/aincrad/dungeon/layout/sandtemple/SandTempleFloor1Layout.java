package com.aincrad.dungeon.layout.sandtemple;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SandTempleFloor1Layout extends DungeonLayout {
    public SandTempleFloor1Layout() {
        super("sandtemple_floor_1", "SandTemple", 1, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:temple_hall", "temple_hall:temple_inner", "temple_inner:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 20, 64, 10, 4, 300),
                new DungeonMobPlacement("PILLAGER", 40, 64, 20, 3, 400),
                new DungeonMobPlacement("SKELETON", 60, 64, 30, 3, 500)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "COMMON", 0.8),
                new DungeonLootTable(50, 65, 40, "UNCOMMON", 0.5)
            )
        );
    }
}
