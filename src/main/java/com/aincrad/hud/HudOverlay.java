package com.aincrad.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class HudOverlay {
    private static final Identifier AINCRAD_HUD = Identifier.fromNamespaceAndPath("aincrad", "hud");

    private HudOverlay() {}

    public static void register() {
        HudElementRegistry.addLast(AINCRAD_HUD, new DelegatingHudElement());
    }

    private static class DelegatingHudElement implements net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement {
        @Override
        public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
            HudManager manager = HudManager.getInstance();
            manager.updateAll();
            manager.renderAll(extractor);
        }
    }
}
