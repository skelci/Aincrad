package com.aincrad.client;

import com.aincrad.hud.MapTracker;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class WorldMapScreen extends Screen {
    private static final double SHOP_X = -42.5;
    private static final double SHOP_Z = 1.5;
    private static final double QUEST_X = 38.5;
    private static final double QUEST_Z = 5.5;

    public WorldMapScreen() {
        super(Component.literal("Aincrad World Map"));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        extractor.fill(0, 0, width, height, 0xE0000000);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        MapTracker.tick(mc);
        Player player = mc.player;
        if (player == null) return;

        extractor.fill(0, 0, width, height, 0xE0000000);

        int mapSize = Math.min(width - 80, height - 80);
        if (mapSize < 80) mapSize = Math.max(80, Math.min(width, height) - 20);
        int cell = Math.max(4, mapSize / 100);
        int span = Math.min(100, mapSize / cell);
        int half = span / 2;
        int mapX = (width - span * cell) / 2;
        int mapY = (height - span * cell) / 2;

        int pcx = player.getBlockX() >> 4;
        int pcz = player.getBlockZ() >> 4;

        extractor.fill(mapX - 2, mapY - 2, mapX + span * cell + 2, mapY + span * cell + 2, 0xFF0E0F14);
        extractor.outline(mapX - 2, mapY - 2, mapX + span * cell + 1, mapY + span * cell + 1, 0xFF8C7A58);

        for (int i = 0; i < span; i++) {
            int cx = pcx - half + i;
            for (int j = 0; j < span; j++) {
                int cz = pcz - half + j;
                long key = MapTracker.key(cx, cz);
                int color = MapTracker.has(key) ? MapTracker.color(key) : 0xFF15161C;
                int px = mapX + i * cell;
                int py = mapY + j * cell;
                extractor.fill(px, py, px + cell, py + cell, color);
            }
        }

        for (int i = 0; i <= span; i++) {
            int p = mapX + i * cell;
            extractor.verticalLine(p, mapY, mapY + span * cell - 1, 0x22333333);
            extractor.horizontalLine(mapY + i * cell, mapX, mapX + span * cell - 1, 0x22333333);
        }

        int centerX = mapX + span * cell / 2;
        int centerY = mapY + span * cell / 2;
        extractor.fill(centerX - 3, centerY - 3, centerX + 3, centerY + 3, 0xFFFFEE44);
        extractor.fill(centerX - 1, centerY - 6, centerX + 1, centerY - 3, 0xFFFF6A3D);
        extractor.fill(centerX - 1, centerY + 3, centerX + 1, centerY + 6, 0xFFFF6A3D);

        if (MapTracker.dimension().contains("floor_1")) {
            drawMarker(extractor, mc, mapX, mapY, span, cell, pcx, pcz, SHOP_X, SHOP_Z, 0xFFE8C33A, "S");
            drawMarker(extractor, mc, mapX, mapY, span, cell, pcx, pcz, QUEST_X, QUEST_Z, 0xFF44DDFF, "!");
        }

        String title = "Aincrad World Map";
        extractor.text(mc.font, title, width / 2 - mc.font.width(title) / 2, mapY - 30, 0xFFE8C33A);
        String floor = MapTracker.floorName() + "  |  explored: " + MapTracker.count() + " chunks";
        extractor.text(mc.font, floor, width / 2 - mc.font.width(floor) / 2, mapY - 16, 0xFFBFBEC2);

        String legend = "S shop    ! quest    M / Esc closes";
        extractor.text(mc.font, legend, width / 2 - mc.font.width(legend) / 2,
                mapY + span * cell + 12, 0xFF9A9A9E);
    }

    private static void drawMarker(GuiGraphicsExtractor extractor, Minecraft mc, int mapX, int mapY,
                                   int span, int cell, int pcx, int pcz,
                                   double wx, double wz, int color, String glyph) {
        int cx = (int) Math.floor(wx) >> 4;
        int cz = (int) Math.floor(wz) >> 4;
        int dxChunk = cx - (pcx - span / 2);
        int dzChunk = cz - (pcz - span / 2);
        if (dxChunk < 0 || dzChunk < 0 || dxChunk >= span || dzChunk >= span) return;
        int px = mapX + dxChunk * cell + cell / 2;
        int py = mapY + dzChunk * cell + cell / 2;
        extractor.fill(px - 4, py - 4, px + 4, py + 4, 0xCC000000);
        extractor.fill(px - 3, py - 3, px + 3, py + 3, color);
        extractor.text(mc.font, glyph, px - 2, py - 5, 0xFFFFFFFF);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_M || event.key() == InputConstants.KEY_ESCAPE) {
            minecraft.gui.setScreen(null);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
