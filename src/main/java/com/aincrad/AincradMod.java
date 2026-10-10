package com.aincrad;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AincradMod implements ModInitializer {
    public static final String MOD_ID = "aincrad";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Aincrad mod initialized");
    }
}
