package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class WorldMapMenu extends MenuWindow {
    private static final int MAP_PADDING = 40;
    private String floorName = "Floor 15 — The Crystal Caverns";
    private int playerX = 1234, playerZ = -567;

    public WorldMapMenu() {
        super(50, 30, 720, 480, "World Map");
        MenuManager.getInstance().register(this);
        MenuManager.getInstance().registerShortcut(77, this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int mapX = x + MAP_PADDING;
        int mapY = y + HEADER_HEIGHT + MAP_PADDING;
        int mapW = width - MAP_PADDING * 2;
        int mapH = height - MAP_PADDING * 2 - 20;

        extractor.fill(mapX, mapY, mapX + mapW, mapY + mapH, 0xFF111111);
        extractor.outline(mapX, mapY, mapX + mapW - 1, mapY + mapH - 1, HudPalette.PANEL_BORDER);

        for (int gy = 0; gy < mapH; gy += 20) {
            extractor.horizontalLine(mapX, mapX + mapW - 1, mapY + gy, 0xFF333333);
        }
        for (int gx = 0; gx < mapW; gx += 20) {
            extractor.verticalLine(mapX + gx, mapY, mapY + mapH - 1, 0xFF333333);
        }

        extractor.fill(mapX + 100, mapY + 80, mapX + 108, mapY + 88, 0xFF44FF44);
        extractor.fill(mapX + 200, mapY + 150, mapX + 208, mapY + 158, 0xFFFF4444);
        extractor.fill(mapX + 300, mapY + 200, mapX + 308, mapY + 208, 0xFF3399FF);
        extractor.fill(mapX + 150, mapY + 250, mapX + 158, mapY + 258, 0xFFFFFF00);

        int playerMapX = mapX + mapW / 2;
        int playerMapY = mapY + mapH / 2;
        extractor.fill(playerMapX - 2, playerMapY - 2, playerMapX + 2, playerMapY + 2, 0xFFFFFFFF);
        extractor.fill(playerMapX - 2, playerMapY - 6, playerMapX, playerMapY - 2, 0xFFFFFFFF);
        extractor.fill(playerMapX, playerMapY - 6, playerMapX + 2, playerMapY - 2, 0xFFFFFFFF);

        extractor.text(font, floorName, mapX + mapW / 2 - font.width(floorName) / 2, mapY + 4, HudPalette.TEXT_ACCENT);
        extractor.text(font, "X: " + playerX + "  Z: " + playerZ, mapX + mapW / 2 - 40, mapY + mapH + 4, HudPalette.TEXT_SECONDARY);
    }
}
