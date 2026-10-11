package com.aincrad.dungeon.layout.sandtemple;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SandTempleFloor2Layout extends DungeonLayout {
    public SandTempleFloor2Layout() {
        super("sandtemple_floor_2", "SandTemple", 2, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:temple_hall", "temple_hall:temple_inner", "temple_inner:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 20, 64, 10, 5, 300),
                new DungeonMobPlacement("PILLAGER", 40, 64, 20, 4, 400),
                new DungeonMobPlacement("EVOKER", 60, 64, 30, 2, 600)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "UNCOMMON", 0.6),
                new DungeonLootTable(50, 65, 40, "RARE", 0.3)
            )
        );
    }
}
