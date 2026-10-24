package com.aincrad;

import com.aincrad.client.HudStats;

import com.aincrad.client.SkillKeys;
import com.aincrad.client.BossFx;
import com.aincrad.hud.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.world.entity.EntityTypes;

public class AincradClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AincradMod.LOGGER.info("Aincrad client initialized");
        SkillKeys.register();
        BossFx.register();
        HudStats.register();
        EntityRendererRegistry.register(EntityTypes.RAVAGER, com.aincrad.client.IlfangRenderer::new);
        HudOverlay.register();
        HudManager hudManager = HudManager.getInstance();
        hudManager.register(new PlayerStatusPanel());
        hudManager.register(new SkillCooldownBar());
        hudManager.register(new BossHealthBar());
        hudManager.register(new Minimap());
        AincradMod.LOGGER.info("Aincrad HUD initialized");
    }
}
