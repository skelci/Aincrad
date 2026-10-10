package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class PartyFrames extends HudElement {
    private static final int FRAME_WIDTH = 200;
    private static final int FRAME_HEIGHT = 50;
    private static final int FRAME_SPACING = 2;
    private static final int MAX_MEMBERS = 6;
    private static final int HEADER_HEIGHT = 18;
    private static final double LERP_FACTOR = 0.15;

    private float[] displayedHp;
    private float[] displayedMp;
    private String[] names;
    private int[] levels;
    private String[] roles;
    private int partySize;
    private int targetIndex = -1;

    public PartyFrames() {
        super(10, 200, FRAME_WIDTH, HEADER_HEIGHT + MAX_MEMBERS * (FRAME_HEIGHT + FRAME_SPACING));
        this.anchor = Anchor.TOP_LEFT;
        this.displayedHp = new float[MAX_MEMBERS];
        this.displayedMp = new float[MAX_MEMBERS];
        this.names = new String[]{"Kirito", "Asuna", "Klein", "Agil", "Silica", "Lisbeth"};
        this.levels = new int[]{42, 40, 38, 39, 37, 36};
        this.roles = new String[]{"Tank", "Healer", "Tank", "DPS", "DPS", "DPS"};
        this.partySize = 6;
        for (int i = 0; i < MAX_MEMBERS; i++) {
            displayedHp[i] = 0.8f;
            displayedMp[i] = 0.6f;
        }
    }

    @Override
    public void update() {
        for (int i = 0; i < partySize; i++) {
            float targetHp = 0.5f + (i * 0.08f);
            float targetMp = 0.4f + (i * 0.1f);
            displayedHp[i] = lerp(displayedHp[i], targetHp, LERP_FACTOR);
            displayedMp[i] = lerp(displayedMp[i], targetMp, LERP_FACTOR);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!visible) return;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;

        int frameX = x;
        int frameY = y;

        extractor.text(font, "Party (" + partySize + "/" + MAX_MEMBERS + ")", frameX, frameY, HudPalette.TEXT_ACCENT);
        frameY += HEADER_HEIGHT;

        for (int i = 0; i < partySize; i++) {
            int fy = frameY + i * (FRAME_HEIGHT + FRAME_SPACING);
            boolean isTarget = (i == targetIndex);
            int borderColor = isTarget ? HudPalette.TEXT_ACCENT : HudPalette.PANEL_BORDER;

            extractor.fill(frameX, fy, frameX + FRAME_WIDTH, fy + FRAME_HEIGHT, HudPalette.PANEL_BG);
            extractor.outline(frameX, fy, frameX + FRAME_WIDTH - 1, fy + FRAME_HEIGHT - 1, borderColor);

            int nameColor = switch (roles[i]) {
                case "Tank" -> 0xFFFFAA44;
                case "Healer" -> 0xFF44FF88;
                default -> HudPalette.TEXT_PRIMARY;
            };

            extractor.text(font, names[i], frameX + 4, fy + 2, nameColor);
            String levelText = "Lv." + levels[i];
            extractor.text(font, levelText, frameX + FRAME_WIDTH - font.width(levelText) - 4, fy + 2, HudPalette.TEXT_TERTIARY);

            int barX = frameX + 4;
            int barW = FRAME_WIDTH - 8;
            int hpBarY = fy + 16;
            int mpBarY = hpBarY + 8;

            extractor.fill(barX, hpBarY, barX + barW, hpBarY + 6, HudPalette.HP_BACKGROUND);
            int hpFill = (int)(barW * clamp01(displayedHp[i]));
            if (hpFill > 0) extractor.fillGradient(barX, hpBarY, barX + hpFill, hpBarY + 6, HudPalette.HP_GRADIENT_START, HudPalette.HP_GRADIENT_END);

            extractor.fill(barX, mpBarY, barX + barW, mpBarY + 6, HudPalette.MP_BACKGROUND);
            int mpFill = (int)(barW * clamp01(displayedMp[i]));
            if (mpFill > 0) extractor.fill(barX, mpBarY, barX + mpFill, mpBarY + 6, HudPalette.MP_BAR);

            extractor.text(font, roles[i], frameX + 4, fy + FRAME_HEIGHT - 12, HudPalette.TEXT_TERTIARY);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        int relY = (int)(mouseY - y - HEADER_HEIGHT);
        int clickedIndex = relY / (FRAME_HEIGHT + FRAME_SPACING);
        if (clickedIndex >= 0 && clickedIndex < partySize) {
            targetIndex = clickedIndex;
        }
    }

    private static float lerp(float from, float to, double factor) { return (float)(from + (to - from) * factor); }
    private static float clamp01(float value) { return Math.max(0.0f, Math.min(1.0f, value)); }
}
