package com.aincrad.dungeon.layout.voidlayout;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VoidFloor2Layout extends DungeonLayout {
    public VoidFloor2Layout() {
        super("void_floor_2", "Void", 2, DungeonTier.T1_EASY,
            0, 64, 0, 100, 70, 100,
            Arrays.asList("entrance:spawn", "spawn:void_passage", "void_passage:shadow_hall", "shadow_hall:dark_bridge", "dark_bridge:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ENDERMAN", 15, 64, 10, 4, 550),
                new DungeonMobPlacement("ENDERMITE", 30, 64, 25, 5, 450),
                new DungeonMobPlacement("SILVERFISH", 50, 64, 40, 4, 500),
                new DungeonMobPlacement("VEX", 65, 64, 55, 2, 700)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "COMMON", 0.85),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.55),
                new DungeonLootTable(60, 65, 55, "RARE", 0.3)
            )
        );
    }
}
