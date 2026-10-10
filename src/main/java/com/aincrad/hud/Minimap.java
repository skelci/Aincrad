package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class Minimap extends HudElement {
    private static final int MAP_SIZE = 128;
    private static final int RADIUS = MAP_SIZE / 2;
    private static final int COMPASS_RADIUS = RADIUS - 6;
    private static final double ZOOM_LEVEL = 1.0;

    private int playerX = 1234;
    private int playerZ = -567;
    private String floorName = "Floor 15 — The Crystal Caverns";
    private int mapSize = MAP_SIZE;
    private boolean bossOffScreen = false;
    private float bossAngle = 0;

    public Minimap() {
        super(-MAP_SIZE - 10, 10, MAP_SIZE, MAP_SIZE + 30);
        this.anchor = Anchor.TOP_RIGHT;
    }

    @Override
    public void update() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            playerX = (int) client.player.getX();
            playerZ = (int) client.player.getZ();
        }
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!visible) return;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;

        int mapX = x;
        int mapY = y;

        extractor.text(font, floorName, mapX + RADIUS - font.width(floorName) / 2, mapY, HudPalette.TEXT_ACCENT);
        mapY += 14;

        extractor.fill(mapX, mapY, mapX + mapSize, mapY + mapSize, 0xFF111111);
        extractor.outline(mapX, mapY, mapX + mapSize - 1, mapY + mapSize - 1, HudPalette.PANEL_BORDER);

        int centerX = mapX + RADIUS;
        int centerY = mapY + RADIUS;

        for (int dy = -RADIUS + 2; dy < RADIUS - 2; dy += 4) {
            for (int dx = -RADIUS + 2; dx < RADIUS - 2; dx += 4) {
                if (dx * dx + dy * dy < (RADIUS - 2) * (RADIUS - 2)) {
                    int worldDx = (int)(dx / ZOOM_LEVEL);
                    int worldDz = (int)(dy / ZOOM_LEVEL);
                    int terrainColor = getTerrainColor(playerX + worldDx, playerZ + worldDz);
                    if (worldDx * worldDx + worldDz * worldDz < 36) {
                        extractor.fill(centerX + dx, centerY + dy, centerX + dx + 3, centerY + dy + 3, terrainColor);
                    }
                }
            }
        }

        extractor.fill(centerX - 1, centerY - 1, centerX + 1, centerY + 1, 0xFFFF0000);
        extractor.fill(centerX - 1, centerY - 4, centerX, centerY - 1, 0xFFFF0000);
        extractor.fill(centerX, centerY - 4, centerX + 1, centerY - 1, 0xFFFF0000);

        extractor.fill(centerX + 20, centerY - 15, centerX + 22, centerY - 13, 0xFF3399FF);
        extractor.fill(centerX - 25, centerY + 10, centerX - 23, centerY + 12, 0xFFFF0000);
        extractor.fill(centerX + 15, centerY + 20, centerX + 17, centerY + 22, 0xFF44FF44);
        extractor.fill(centerX - 10, centerY - 25, centerX - 8, centerY - 23, 0xFFFFFF00);

        if (bossOffScreen) {
            int bossX = centerX + (int)(COMPASS_RADIUS * Math.cos(bossAngle));
            int bossY = centerY + (int)(COMPASS_RADIUS * Math.sin(bossAngle));
            extractor.fill(bossX - 3, bossY - 3, bossX + 3, bossY + 3, 0xFFFF0000);
            extractor.outline(bossX - 3, bossY - 3, bossX + 3, bossY + 3, 0xFF000000);
        }

        extractor.text(font, "N", centerX - 3, mapY + 2, HudPalette.TEXT_PRIMARY);
        extractor.text(font, "S", centerX - 3, mapY + mapSize - 10, HudPalette.TEXT_PRIMARY);
        extractor.text(font, "E", mapX + mapSize - 8, centerY - 3, HudPalette.TEXT_PRIMARY);
        extractor.text(font, "W", mapX + 2, centerY - 3, HudPalette.TEXT_PRIMARY);

        String coords = "X: " + playerX + "  Z: " + playerZ;
        extractor.text(font, coords, mapX + RADIUS - font.width(coords) / 2, mapY + mapSize + 2, HudPalette.TEXT_SECONDARY);
    }

    private static int getTerrainColor(int x, int z) {
        int hash = (x * 31 + z * 17) & 0xFF;
        if (hash < 50) return 0xFF228B22;
        if (hash < 100) return 0xFF8B4513;
        if (hash < 150) return 0xFF444444;
        if (hash < 200) return 0xFF2E8B57;
        return 0xFF696969;
    }

    public void toggleSize() {
        if (mapSize == 96) mapSize = 128;
        else if (mapSize == 128) mapSize = 192;
        else mapSize = 96;
    }

    public void setBossOffScreen(boolean offScreen, float angle) {
        this.bossOffScreen = offScreen;
        this.bossAngle = angle;
    }
}
