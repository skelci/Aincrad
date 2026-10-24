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
        this.names = new String[MAX_MEMBERS];
        this.levels = new int[MAX_MEMBERS];
        this.roles = new String[MAX_MEMBERS];
        this.partySize = 1;
        names[0] = "";
        levels[0] = 1;
        roles[0] = "DPS";
        for (int i = 0; i < MAX_MEMBERS; i++) {
            displayedHp[i] = 1.0f;
            displayedMp[i] = 1.0f;
        }
    }

    @Override
    public void update() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            names[0] = client.player.getName().getString();
            levels[0] = client.player.experienceLevel;
            displayedHp[0] = lerp(displayedHp[0], client.player.getMaxHealth() > 0 ? client.player.getHealth() / client.player.getMaxHealth() : 0, LERP_FACTOR);
            displayedMp[0] = lerp(displayedMp[0], 0.45f, LERP_FACTOR);
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
