package com.aincrad;

import com.aincrad.world.FloorDimensions;
import com.aincrad.world.FloorRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AincradMod implements ModInitializer {
    public static final String MOD_ID = "aincrad";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Aincrad mod initialized");

        // Register all 100 floor dimensions
        FloorDimensions.registerAll();
        FloorRegistry registry = FloorRegistry.getInstance();
        LOGGER.info("Floor dimensions registered: {} floors", registry.getTotalFloors());

        // Register all boss entities
        registerBosses();
    }

    private void registerBosses() {
        com.aincrad.boss.BossRegistry.register("floor1_goblin_king",
            com.aincrad.boss.impl.Floor1Boss.class, 1);
        com.aincrad.boss.BossRegistry.register("floor5_stone_golem",
            com.aincrad.boss.impl.Floor5Boss.class, 5);
        com.aincrad.boss.BossRegistry.register("floor10_dark_knight",
            com.aincrad.boss.impl.Floor10Boss.class, 10);
        com.aincrad.boss.BossRegistry.register("floor25_dragon_lord",
            com.aincrad.boss.impl.Floor25RaidBoss.class, 25);
        com.aincrad.boss.BossRegistry.register("floor50_void_emperor",
            com.aincrad.boss.impl.Floor50RaidBoss.class, 50);
        LOGGER.info("Bosses registered: {}", com.aincrad.boss.BossRegistry.size());
    }
}
