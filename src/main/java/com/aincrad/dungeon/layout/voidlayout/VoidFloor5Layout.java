package com.aincrad.dungeon.layout.voidlayout;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class VoidFloor5Layout extends DungeonLayout {
    public VoidFloor5Layout() {
        super("void_floor_5", "Void", 5, DungeonTier.T1_EASY,
            0, 64, 0, 160, 70, 160,
            Arrays.asList("entrance:spawn", "spawn:void_passage", "void_passage:shadow_hall", "shadow_hall:dark_bridge", "dark_bridge:obsidian_chamber", "obsidian_chamber:ender_gallery", "ender_gallery:shulker_nest", "shulker_nest:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("ENDERMAN", 15, 64, 10, 7, 400),
                new DungeonMobPlacement("ENDERMITE", 30, 64, 25, 8, 300),
                new DungeonMobPlacement("SILVERFISH", 50, 64, 40, 7, 350),
                new DungeonMobPlacement("VEX", 65, 64, 55, 5, 550),
                new DungeonMobPlacement("GHAST", 80, 64, 70, 4, 800),
                new DungeonMobPlacement("WITHER_SKELETON", 95, 64, 85, 3, 950),
                new DungeonMobPlacement("SHULKER", 110, 64, 100, 2, 1200)
            ),
            Arrays.asList(
                new DungeonLootTable(20, 65, 15, "RARE", 0.55),
                new DungeonLootTable(40, 65, 35, "RARE", 0.45),
                new DungeonLootTable(60, 65, 55, "EPIC", 0.25),
                new DungeonLootTable(80, 65, 75, "EPIC", 0.18),
                new DungeonLootTable(100, 65, 95, "LEGENDARY", 0.08),
                new DungeonLootTable(120, 65, 115, "LEGENDARY", 0.04)
            )
        );
    }
}
