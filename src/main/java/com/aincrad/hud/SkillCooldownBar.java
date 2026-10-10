package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SkillCooldownBar extends HudElement {
    private static final int SLOT_SIZE = 48;
    private static final int SLOT_SPACING = 4;
    private static final int NUM_SLOTS = 8;
    private static final int LABEL_HEIGHT = 10;
    private static final double LERP_FACTOR = 0.12;

    private float[] displayedCooldowns;
    private float[] cooldowns;
    private float[] cooldownTotals;
    private String[] skillNames;

    public SkillCooldownBar() {
        super(-(NUM_SLOTS * (SLOT_SIZE + SLOT_SPACING)) / 2, -60,
              NUM_SLOTS * (SLOT_SIZE + SLOT_SPACING), SLOT_SIZE + LABEL_HEIGHT);
        this.anchor = Anchor.BOTTOM_CENTER;
        this.displayedCooldowns = new float[NUM_SLOTS];
        this.cooldowns = new float[NUM_SLOTS];
        this.cooldownTotals = new float[NUM_SLOTS];
        this.skillNames = new String[]{"Sword", "Heal", "Dash", "Fire", "Guard", "Buff", "Debuff", "Swap"};
        for (int i = 0; i < NUM_SLOTS; i++) cooldownTotals[i] = 20.0f;
    }

    @Override
    public void update() {
        for (int i = 0; i < NUM_SLOTS; i++) {
            displayedCooldowns[i] = lerp(displayedCooldowns[i], cooldowns[i], LERP_FACTOR);
            if (cooldowns[i] > 0) cooldowns[i] = Math.max(0, cooldowns[i] - 0.05f);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!visible) return;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        int barWidth = NUM_SLOTS * (SLOT_SIZE + SLOT_SPACING) - SLOT_SPACING;
        int startX = x;

        for (int i = 0; i < NUM_SLOTS; i++) {
            int slotX = startX + i * (SLOT_SIZE + SLOT_SPACING);
            int slotY = y;

            extractor.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, HudPalette.PANEL_BG);
            extractor.outline(slotX, slotY, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, HudPalette.PANEL_BORDER);

            int iconColor = (i == 7) ? 0xFFAA66FF : 0xFF66AAFF;
            extractor.fill(slotX + 4, slotY + 4, slotX + SLOT_SIZE - 4, slotY + SLOT_SIZE - 4, iconColor);

            String hotkey = String.valueOf(i + 1);
            extractor.text(font, hotkey, slotX + 2, slotY + 2, HudPalette.TEXT_PRIMARY, true);

            float cd = displayedCooldowns[i];
            if (cd > 0) {
                float frac = cd / cooldownTotals[i];
                int overlayH = (int)(SLOT_SIZE * frac);
                extractor.fill(slotX, slotY + SLOT_SIZE - overlayH, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0xAA000000);
                if (cd > 2) {
                    String secs = String.format("%.1fs", cd);
                    extractor.text(font, secs, slotX + SLOT_SIZE / 2 - font.width(secs) / 2, slotY + SLOT_SIZE / 2 - 4, HudPalette.TEXT_PRIMARY, true);
                }
            } else {
                extractor.outline(slotX, slotY, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, HudPalette.BUFF_GLOW);
            }

            extractor.text(font, skillNames[i], slotX + SLOT_SIZE / 2 - font.width(skillNames[i]) / 2, slotY + SLOT_SIZE + 1, HudPalette.TEXT_SECONDARY);
        }
    }

    public void setCooldown(int slot, float remaining, float total) {
        if (slot < 0 || slot >= NUM_SLOTS) return;
        cooldowns[slot] = remaining;
        cooldownTotals[slot] = total;
    }

    public void clearCooldowns() {
        for (int i = 0; i < NUM_SLOTS; i++) cooldowns[i] = 0;
    }

    private static float lerp(float from, float to, double factor) { return (float)(from + (to - from) * factor); }
}
