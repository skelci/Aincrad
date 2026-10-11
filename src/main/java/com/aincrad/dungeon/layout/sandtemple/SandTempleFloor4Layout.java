package com.aincrad.dungeon.layout.sandtemple;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SandTempleFloor4Layout extends DungeonLayout {
    public SandTempleFloor4Layout() {
        super("sandtemple_floor_4", "SandTemple", 4, DungeonTier.T5_MYTHIC,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:temple_hall", "temple_hall:temple_inner", "temple_inner:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("HUSK", 20, 64, 10, 6, 300),
                new DungeonMobPlacement("PILLAGER", 40, 64, 20, 5, 400),
                new DungeonMobPlacement("EVOKER", 60, 64, 30, 3, 600),
                new DungeonMobPlacement("SKELETON", 80, 64, 50, 4, 450),
                new DungeonMobPlacement("VINDICATOR", 100, 64, 70, 4, 400)
            ),
            Arrays.asList(
                new DungeonLootTable(30, 65, 15, "EPIC", 0.25),
                new DungeonLootTable(50, 65, 40, "LEGENDARY", 0.15)
            )
        );
    }
}
