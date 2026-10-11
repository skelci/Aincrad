package com.aincrad.dungeon.layout.voidlayout;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VoidFloor4Layout extends DungeonLayout {
    public VoidFloor4Layout() {
        super("void_floor_4", "Void", 4, DungeonTier.T1_EASY,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:void_passage", "void_passage:shadow_hall", "shadow_hall:dark_bridge", "dark_bridge:obsidian_chamber", "obsidian_chamber:ender_gallery", "ender_gallery:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ENDERMAN", 15, 64, 10, 6, 450),
                new DungeonMobPlacement("ENDERMITE", 30, 64, 25, 7, 350),
                new DungeonMobPlacement("SILVERFISH", 50, 64, 40, 6, 400),
                new DungeonMobPlacement("VEX", 65, 64, 55, 4, 600),
                new DungeonMobPlacement("GHAST", 80, 64, 70, 3, 850),
                new DungeonMobPlacement("WITHER_SKELETON", 95, 64, 85, 2, 1000)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "UNCOMMON", 0.75),
                new DungeonLootTable(40, 65, 35, "RARE", 0.45),
                new DungeonLootTable(60, 65, 55, "RARE", 0.35),
                new DungeonLootTable(80, 65, 75, "EPIC", 0.15),
                new DungeonLootTable(100, 65, 95, "EPIC", 0.08)
            )
        );
    }
}
