package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SkillsMenu extends MenuWindow {
    private static final int CARD_WIDTH = 90;
    private static final int CARD_HEIGHT = 60;
    private static final int CARD_SPACING = 4;
    private static final int COLS = 4;
    private static final int DETAIL_HEIGHT = 80;

    private String[] tabs = {"Active", "Passive", "Weapon", "Learned"};
    private int selectedTab = 0;
    private String[] skillNames = {"Starburst", "Heal", "Dash", "Fireball", "Guard", "Buff", "Debuff", "Swap"};
    private int[] cooldowns = {12, 8, 15, 20, 10, 30, 25, 5};
    private int[] mpCosts = {40, 60, 25, 80, 30, 50, 45, 0};
    private int selectedSkill = -1;

    public SkillsMenu() {
        super(100, 50, 450, 400, "Skills");
        MenuManager.getInstance().register(this);
        MenuManager.getInstance().registerShortcut(86, this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        for (int i = 0; i < tabs.length; i++) {
            int tabX = contentX + i * 80;
            boolean selected = (i == selectedTab);
            extractor.fill(tabX, contentY, tabX + 76, contentY + 18, selected ? HudPalette.PANEL_BORDER_DARK : HudPalette.PANEL_BG);
            extractor.text(font, tabs[i], tabX + 4, contentY + 4, selected ? HudPalette.TEXT_ACCENT : HudPalette.TEXT_SECONDARY);
        }
        contentY += 24;

        for (int i = 0; i < skillNames.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int cardX = contentX + col * (CARD_WIDTH + CARD_SPACING);
            int cardY = contentY + row * (CARD_HEIGHT + CARD_SPACING);
            boolean selected = (i == selectedSkill);
            int borderColor = selected ? HudPalette.TEXT_ACCENT : HudPalette.PANEL_BORDER;
            extractor.fill(cardX, cardY, cardX + CARD_WIDTH, cardY + CARD_HEIGHT, HudPalette.PANEL_BG);
            extractor.outline(cardX, cardY, cardX + CARD_WIDTH - 1, cardY + CARD_HEIGHT - 1, borderColor);
            extractor.text(font, skillNames[i], cardX + 4, cardY + 4, HudPalette.TEXT_PRIMARY);
            extractor.text(font, "CD: " + cooldowns[i] + "s", cardX + 4, cardY + 18, HudPalette.TEXT_SECONDARY);
            extractor.text(font, "MP: " + mpCosts[i], cardX + 4, cardY + 30, HudPalette.MP_BAR);
        }

        int detailY = contentY + 2 * (CARD_HEIGHT + CARD_SPACING) + 8;
        extractor.fill(contentX, detailY, contentX + width - 16, detailY + DETAIL_HEIGHT, HudPalette.PANEL_BG);
        extractor.outline(contentX, detailY, contentX + width - 16, detailY + DETAIL_HEIGHT, HudPalette.PANEL_BORDER);

        if (selectedSkill >= 0) {
            extractor.text(font, skillNames[selectedSkill], contentX + 4, detailY + 4, HudPalette.TEXT_PRIMARY);
            extractor.text(font, "Deal 300% weapon damage to target.", contentX + 4, detailY + 16, HudPalette.TEXT_SECONDARY);
            extractor.text(font, "5-hit combo. Each hit reduces cooldown by 0.5s.", contentX + 4, detailY + 28, HudPalette.TEXT_TERTIARY);
            extractor.text(font, "[Assign to Slot _]", contentX + 4, detailY + 44, HudPalette.TEXT_ACCENT);
        }
    }

    @Override
    protected void contentMouseClicked(double mx, double my, int button) {
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;
        int relY = (int)(my - contentY);
        if (relY < 24) {
            int relX = (int)(mx - contentX);
            int tabIdx = relX / 80;
            if (tabIdx >= 0 && tabIdx < tabs.length) selectedTab = tabIdx;
            return;
        }
        int cardY = contentY + 24;
        int relX = (int)(mx - contentX);
        int relCardY = (int)(my - cardY);
        int col = relX / (CARD_WIDTH + CARD_SPACING);
        int row = relCardY / (CARD_HEIGHT + CARD_SPACING);
        int idx = row * COLS + col;
        if (idx >= 0 && idx < skillNames.length) selectedSkill = idx;
    }
}
