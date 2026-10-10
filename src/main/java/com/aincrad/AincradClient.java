package com.aincrad;

import com.aincrad.hud.*;
import net.fabricmc.api.ClientModInitializer;

public class AincradClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AincradMod.LOGGER.info("Aincrad client initialized");
        HudOverlay.register();
        HudManager hudManager = HudManager.getInstance();
        hudManager.register(new PlayerStatusPanel());
        hudManager.register(new BuffIconRenderer());
        hudManager.register(new SkillCooldownBar());
        hudManager.register(new PartyFrames());
        hudManager.register(new BossHealthBar());
        hudManager.register(new Minimap());
        AincradMod.LOGGER.info("Aincrad HUD initialized");
    }
}
