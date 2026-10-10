package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class BuffIconRenderer extends HudElement {
    private static final int ICON_SIZE = 32;
    private static final int ICON_SPACING = 4;
    private static final int TIMER_HEIGHT = 10;
    private static final int MAX_BUFFS = 8;

    private String[] buffNames;
    private float[] remainingTimes;
    private float[] durations;
    private boolean[] isBuff;
    private int activeBuffs;

    public BuffIconRenderer() {
        super(10, 120, MAX_BUFFS * (ICON_SIZE + ICON_SPACING), ICON_SIZE + TIMER_HEIGHT);
        this.anchor = Anchor.TOP_LEFT;
        this.buffNames = new String[]{"Regen", "Strength", "Haste", "Poison", "Weakness"};
        this.remainingTimes = new float[]{83, 45, 60, 30, 20};
        this.durations = new float[]{120, 60, 90, 45, 30};
        this.isBuff = new boolean[]{true, true, true, false, false};
        this.activeBuffs = 5;
    }

    @Override
    public void update() {
        for (int i = 0; i < activeBuffs; i++) {
            if (remainingTimes[i] > 0) remainingTimes[i] = Math.max(0, remainingTimes[i] - 0.05f);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!visible) return;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;

        for (int i = 0; i < activeBuffs; i++) {
            int iconX = x + i * (ICON_SIZE + ICON_SPACING);
            int iconY = y;

            int borderColor = isBuff[i] ? HudPalette.BUFF_GLOW : HudPalette.DEBUFF_GLOW;
            extractor.fill(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, 0x44000000);
            extractor.outline(iconX, iconY, iconX + ICON_SIZE - 1, iconY + ICON_SIZE - 1, borderColor);

            int innerColor = isBuff[i] ? 0xFF00FF88 : 0xFFFF4444;
            extractor.fill(iconX + 8, iconY + 8, iconX + ICON_SIZE - 8, iconY + ICON_SIZE - 8, innerColor);

            if (remainingTimes[i] < 5) {
                float alpha = 0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() * 0.01);
                int fadeColor = ((int)(0xAA * alpha) << 24) | 0xFFFFFF;
                extractor.fill(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, fadeColor);
            }

            int mins = (int)(remainingTimes[i] / 60);
            int secs = (int)(remainingTimes[i] % 60);
            String timer = String.format("%d:%02d", mins, secs);
            extractor.text(font, timer, iconX + ICON_SIZE / 2 - font.width(timer) / 2, iconY + ICON_SIZE + 1, HudPalette.TEXT_SECONDARY);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        int relX = (int)(mouseX - x);
        int clickedIndex = relX / (ICON_SIZE + ICON_SPACING);
        if (clickedIndex >= 0 && clickedIndex < activeBuffs && !isBuff[clickedIndex]) {
            remainingTimes[clickedIndex] = 0;
        }
    }
}
