package com.aincrad.dungeon.layout.voidlayout;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VoidFloor1Layout extends DungeonLayout {
    public VoidFloor1Layout() {
        super("void_floor_1", "Void", 1, DungeonTier.T1_EASY,
            0, 64, 0, 80, 70, 80,
            Arrays.asList("entrance:spawn", "spawn:void_passage", "void_passage:shadow_hall", "shadow_hall:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ENDERMAN", 15, 64, 10, 3, 600),
                new DungeonMobPlacement("ENDERMITE", 30, 64, 25, 4, 500),
                new DungeonMobPlacement("SILVERFISH", 50, 64, 40, 3, 550)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.8),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.5)
            )
        );
    }
}
