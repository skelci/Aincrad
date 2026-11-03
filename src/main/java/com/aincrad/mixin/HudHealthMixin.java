package com.aincrad.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Aincrad's exponential health can exceed vanilla's 20-heart HUD limit.
 * Draw a single row of 10 hearts scaled to the player's full HP instead.
 */
@Mixin(Hud.class)
public class HudHealthMixin {

    private static final int HEARTS = 10;
    private static final int HEART_SIZE = 9;
    private static final int HEART_SEPARATION = 8;

    @Inject(method = "extractHearts", at = @At("HEAD"), cancellable = true)
    private void aincrad$oneRowHearts(GuiGraphicsExtractor extractor,
                                      Player player,
                                      int x, int y,
                                      int arg5, int arg6,
                                      float maxHealth,
                                      int currentHealth,
                                      int displayHealth,
                                      int absorption,
                                      boolean blink,
                                      CallbackInfo ci) {
        ci.cancel();
        if (player == null) return;

        double max = player.getMaxHealth();
        double hp = player.getHealth();
        int units = max <= 0 ? 0 : (int) Math.round(Math.min(20.0, Math.max(0.0, hp / max * 20.0)));

        Identifier container = Identifier.withDefaultNamespace("hud/heart/container");
        Identifier full = Identifier.withDefaultNamespace("hud/heart/full");
        Identifier half = Identifier.withDefaultNamespace("hud/heart/half");

        for (int i = 0; i < HEARTS; i++) {
            int hx = x + i * HEART_SEPARATION;
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, container, hx, y, HEART_SIZE, HEART_SIZE);
            int heartHp = units - i * 2;
            if (heartHp >= 2) {
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, full, hx, y, HEART_SIZE, HEART_SIZE);
            } else if (heartHp == 1) {
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, half, hx, y, HEART_SIZE, HEART_SIZE);
            }
        }
    }
}
