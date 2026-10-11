package com.aincrad;

import com.aincrad.boss.BossRegistration;
import com.aincrad.skill.SkillRegistration;
import com.aincrad.dungeon.layout.DungeonLayoutRegistration;
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
        BossRegistration.registerAll();
        LOGGER.info("Bosses registered: {}", com.aincrad.boss.BossRegistry.size());

        // Register all skills
        SkillRegistration.registerAll();
        LOGGER.info("Skills registered: {}", com.aincrad.skill.SkillRegistry.size());

        // Register all dungeon layouts
        DungeonLayoutRegistration.registerAll();
        LOGGER.info("Dungeon layouts registered: {}", com.aincrad.dungeon.layout.DungeonLayoutRegistry.size());
    }
}
