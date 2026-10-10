package com.aincrad;

import com.aincrad.hud.HudManager;
import com.aincrad.hud.HudOverlay;
import com.aincrad.hud.PlayerStatusPanel;
import net.fabricmc.api.ClientModInitializer;

public class AincradClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AincradMod.LOGGER.info("Aincrad client initialized");

        // Register HUD overlay with Fabric
        HudOverlay.register();

        // Create and register HUD elements
        HudManager hudManager = HudManager.getInstance();
        hudManager.register(new PlayerStatusPanel());

        AincradMod.LOGGER.info("Aincrad HUD initialized");
    }
}
