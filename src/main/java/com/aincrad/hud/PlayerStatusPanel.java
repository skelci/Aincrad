package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;

public class PlayerStatusPanel extends HudElement {
    private static final int BAR_WIDTH = 180;
    private static final int BAR_HEIGHT = 8;
    private static final int BAR_SPACING = 4;
    private static final int PADDING = 8;
    private static final int NAME_HEIGHT = 14;
    private static final int LEVEL_HEIGHT = 12;
    private static final int BUFF_ROW_HEIGHT = 36;
    private static final int BUFF_ICON_SIZE = 32;
    private static final int BUFF_SPACING = 4;
    private static final double LERP_FACTOR = 0.15;
    private static final int FLASH_DURATION = 10;

    private float displayedHp, displayedMp, displayedSt;
    private int flashTimer;
    private int level;

    public PlayerStatusPanel() {
        super(10, 10, BAR_WIDTH + PADDING * 2,
              NAME_HEIGHT + LEVEL_HEIGHT + (BAR_HEIGHT + BAR_SPACING) * 3 + PADDING * 2);
        this.anchor = Anchor.TOP_LEFT;
        this.displayedHp = 1.0f;
        this.displayedMp = 1.0f;
        this.displayedSt = 1.0f;
        this.flashTimer = 0;
        this.level = 1;
    }

    @Override
    public void update() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        float targetHp, targetMp, targetSt;
        if (player != null) {
            float maxHp = player.getMaxHealth();
            targetHp = maxHp > 0 ? player.getHealth() / maxHp : 0.0f;
            targetMp = (float) com.aincrad.client.HudStats.manaRatio();
            targetSt = player.getFoodData().getFoodLevel() / 20.0f;
        } else {
            targetHp = 0.0f; targetMp = 0.0f; targetSt = 0.0f;
        }
        if (targetHp < displayedHp - 0.001f) flashTimer = FLASH_DURATION;
        displayedHp = lerp(displayedHp, targetHp, LERP_FACTOR);
        displayedMp = lerp(displayedMp, targetMp, LERP_FACTOR);
        displayedSt = lerp(displayedSt, targetSt, LERP_FACTOR);
        displayedHp = clamp01(displayedHp);
        displayedMp = clamp01(displayedMp);
        displayedSt = clamp01(displayedSt);
        if (flashTimer > 0) flashTimer--;
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!visible) return;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        LocalPlayer player = client.player;
        String playerName = player != null ? player.getName().getString() : "Player";
        level = Math.max(1, com.aincrad.client.HudStats.level);

        int panelX = x, panelY = y, panelW = width, panelH = height;
        extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, HudPalette.PANEL_BG);
        drawBorder(extractor, panelX, panelY, panelW, panelH);

        int contentX = panelX + PADDING;
        int contentY = panelY + PADDING;

        extractor.text(font, playerName, contentX, contentY, HudPalette.TEXT_PRIMARY);
        contentY += NAME_HEIGHT;

        String levelText = "Lv. " + level;
        int levelTextWidth = font.width(levelText);
        extractor.text(font, levelText, panelX + panelW - PADDING - levelTextWidth, contentY, HudPalette.TEXT_ACCENT);
        contentY += LEVEL_HEIGHT;

        String hpLabel = "HP";
        extractor.text(font, hpLabel, contentX, contentY, HudPalette.TEXT_SECONDARY);
        int hpBarX = contentX + font.width(hpLabel) + 4;
        int hpBarW = panelX + panelW - PADDING - hpBarX;
        drawBar(extractor, hpBarX, contentY, hpBarW, BAR_HEIGHT, displayedHp, HudPalette.HP_GRADIENT_START, HudPalette.HP_GRADIENT_END, HudPalette.HP_BACKGROUND);
        String hpText = player != null ? String.format("%.0f/%.0f", player.getHealth(), player.getMaxHealth()) : "0/0";
        extractor.text(font, hpText, hpBarX + hpBarW - font.width(hpText) - 2, contentY + 1, HudPalette.TEXT_PRIMARY);
        contentY += BAR_HEIGHT + BAR_SPACING;

        String mpLabel = "MP";
        extractor.text(font, mpLabel, contentX, contentY, HudPalette.TEXT_SECONDARY);
        int mpBarX = contentX + font.width(mpLabel) + 4;
        int mpBarW = panelX + panelW - PADDING - mpBarX;
        drawBar(extractor, mpBarX, contentY, mpBarW, BAR_HEIGHT, displayedMp, HudPalette.MP_BAR, HudPalette.MP_BAR, HudPalette.MP_BACKGROUND);
        String mpText = com.aincrad.client.HudStats.manaText();
        extractor.text(font, mpText, mpBarX + mpBarW - font.width(mpText) - 2, contentY + 1, HudPalette.TEXT_PRIMARY);
        contentY += BAR_HEIGHT + BAR_SPACING;

        String stLabel = "ST";
        extractor.text(font, stLabel, contentX, contentY, HudPalette.TEXT_SECONDARY);
        int stBarX = contentX + font.width(stLabel) + 4;
        int stBarW = panelX + panelW - PADDING - stBarX;
        drawBar(extractor, stBarX, contentY, stBarW, BAR_HEIGHT, displayedSt, HudPalette.ST_GRADIENT_START, HudPalette.ST_GRADIENT_END, HudPalette.ST_BACKGROUND);
        String stText = player != null ? String.format("%d/%d", player.getFoodData().getFoodLevel(), 20) : "0/20";
        extractor.text(font, stText, stBarX + stBarW - font.width(stText) - 2, contentY + 1, HudPalette.TEXT_PRIMARY);
        contentY += BAR_HEIGHT + BAR_SPACING;

        if (flashTimer > 0) {
            float flashAlpha = (float) flashTimer / FLASH_DURATION;
            int flashColor = ((int)(0xAA * flashAlpha) << 24) | 0xFFFFFF;
            extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, flashColor);
        }
    }

    private void drawBar(GuiGraphicsExtractor extractor, int barX, int barY, int barW, int barH, float fill, int colorStart, int colorEnd, int background) {
        extractor.fill(barX, barY, barX + barW, barY + barH, background);
        int fillW = (int)(barW * clamp01(fill));
        if (fillW > 0) extractor.fillGradient(barX, barY, barX + fillW, barY + barH, colorStart, colorEnd);
        extractor.fill(barX, barY, barX + barW, barY + 1, 0x33FFFFFF);
    }

    private void drawBorder(GuiGraphicsExtractor extractor, int px, int py, int pw, int ph) {
        // Only draw bottom border if there would be effects below bars
        extractor.horizontalLine(px, px + pw - 1, py, 0x44FFFFFF);
        extractor.horizontalLine(px, px + pw - 1, py + ph - 1, 0x22000000);
        extractor.verticalLine(px, py, py + ph - 1, 0x44FFFFFF);
        extractor.verticalLine(px + pw - 1, py, py + ph - 1, 0x22000000);
    }

    private static float lerp(float from, float to, double factor) { return (float)(from + (to - from) * factor); }
    private static float clamp01(float value) { return Math.max(0.0f, Math.min(1.0f, value)); }
}
