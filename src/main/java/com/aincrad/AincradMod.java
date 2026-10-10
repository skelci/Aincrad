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
    }
}
