package com.aincrad.dungeon.layout.snow;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.*;

import java.util.Arrays;
import java.util.List;

public final class SnowFloor4Layout extends DungeonLayout {
    public SnowFloor4Layout() {
        super("snow_floor_4", "Snow", 4, DungeonTier.T4_EXPERT,
            0, 64, 0, 140, 70, 140,
            Arrays.asList("entrance:spawn", "spawn:clearing_1", "clearing_1:clearing_2", "clearing_2:clearing_3", "clearing_3:clearing_4", "clearing_4:clearing_5", "clearing_5:boss_room"),
            Arrays.asList(
                new DungeonMobPlacement("STRAY", 8, 64, 5, 6, 350),
                new DungeonMobPlacement("POLAR_BEAR", 25, 64, 15, 4, 750),
                new DungeonMobPlacement("VINDICATOR", 45, 64, 10, 5, 600),
                new DungeonMobPlacement("STRAY", 60, 64, 30, 5, 400),
                new DungeonMobPlacement("POLAR_BEAR", 75, 64, 50, 3, 850),
                new DungeonMobPlacement("VINDICATOR", 90, 64, 65, 4, 650)
            ),
            Arrays.asList(
                new DungeonLootTable(15, 65, 12, "EPIC", 0.35),
                new DungeonLootTable(35, 65, 25, "LEGENDARY", 0.2),
                new DungeonLootTable(55, 65, 40, "LEGENDARY", 0.18),
                new DungeonLootTable(75, 65, 55, "MYTHIC", 0.08)
            )
        );
    }
}
