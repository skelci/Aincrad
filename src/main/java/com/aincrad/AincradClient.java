package com.aincrad;

import net.fabricmc.api.ClientModInitializer;

public class AincradClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AincradMod.LOGGER.info("Aincrad client initialized");
    }
}
