package com.aincrad.dungeon.layout.voidlayout;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VoidFloor3Layout extends DungeonLayout {
    public VoidFloor3Layout() {
        super("void_floor_3", "Void", 3, DungeonTier.T1_EASY,
            0, 64, 0, 120, 70, 120,
            Arrays.asList("entrance:spawn", "spawn:void_passage", "void_passage:shadow_hall", "shadow_hall:dark_bridge", "dark_bridge:obsidian_chamber", "obsidian_chamber:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ENDERMAN", 15, 64, 10, 5, 500),
                new DungeonMobPlacement("ENDERMITE", 30, 64, 25, 6, 400),
                new DungeonMobPlacement("SILVERFISH", 50, 64, 40, 5, 450),
                new DungeonMobPlacement("VEX", 65, 64, 55, 3, 650),
                new DungeonMobPlacement("GHAST", 80, 64, 70, 2, 900)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "UNCOMMON", 0.7),
                new DungeonLootTable(40, 65, 35, "UNCOMMON", 0.6),
                new DungeonLootTable(60, 65, 55, "RARE", 0.35),
                new DungeonLootTable(80, 65, 75, "RARE", 0.2)
            )
        );
    }
}
