package com.aincrad.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * Fabric HUD overlay entry point.
 * <p>
 * Registers a delegating Fabric HUD element with {@link HudElementRegistry},
 * hooking into Minecraft's HUD rendering pipeline. All rendering and animation
 * updates are delegated to the shared {@link HudManager} singleton.
 * <p>
 * Register this class from {@code AincradClient.onInitializeClient()}:
 * <pre>{@code
 * HudOverlay.register();
 * }</pre>
 */
public class HudOverlay {

    private static final Identifier AINCRAD_HUD =
            Identifier.fromNamespaceAndPath("aincrad", "hud");

    private HudOverlay() {
        // Utility class — use {@link #register()}.
    }

    /**
     * Registers the Aincrad HUD with Fabric's {@link HudElementRegistry}.
     * <p>
     * This should be called once during client initialization.
     */
    public static void register() {
        HudElementRegistry.addLast(AINCRAD_HUD, new DelegatingHudElement());
    }

    /**
     * Internal adapter that bridges Fabric's HUD element interface
     * to our {@link HudManager}.
     */
    private static class DelegatingHudElement
            implements net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement {

        @Override
        public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
            HudManager manager = HudManager.getInstance();

            // Update animations before rendering.
            manager.updateAll();

            // Render all registered HUD elements.
            manager.renderAll(extractor);
        }
    }
}
