package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class QuestLogMenu extends MenuWindow {
    private static final int FILTER_HEIGHT = 20;
    private static final int QUEST_HEIGHT = 50;
    private static final int QUEST_SPACING = 4;

    private String[] filters = {"All", "Main", "Daily", "Side", "Guild"};
    private int selectedFilter = 0;
    private String[] questNames = {"The Front Line", "Crystal Harvest", "Klein's Request"};
    private String[] questDescs = {"Defeat the Floor 1 boss, True Dragon.", "Collect 10 Crystal Shards.", "Gather 5 Iron Ore for Klein."};
    private String[] questProgress = {"1/1", "7/10", "5/5"};
    private String[] questRewards = {"2,500c, Sword of the Beginning", "500c", "150c, Iron Ingot x20"};
    private boolean[] questComplete = {true, false, true};

    public QuestLogMenu() {
        super(100, 50, 400, 450, "Quest Log");
        MenuManager.getInstance().register(this);
        MenuManager.getInstance().registerShortcut(74, this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        for (int i = 0; i < filters.length; i++) {
            int filterX = contentX + i * 70;
            boolean selected = (i == selectedFilter);
            extractor.fill(filterX, contentY, filterX + 66, contentY + 18, selected ? HudPalette.PANEL_BORDER_DARK : HudPalette.PANEL_BG);
            extractor.text(font, filters[i], filterX + 4, contentY + 4, selected ? HudPalette.TEXT_ACCENT : HudPalette.TEXT_SECONDARY);
        }
        contentY += FILTER_HEIGHT + 4;

        for (int i = 0; i < questNames.length; i++) {
            int questY = contentY + i * (QUEST_HEIGHT + QUEST_SPACING);
            extractor.fill(contentX, questY, contentX + width - 16, questY + QUEST_HEIGHT, HudPalette.PANEL_BG);
            extractor.outline(contentX, questY, contentX + width - 16, questY + QUEST_HEIGHT, HudPalette.PANEL_BORDER);

            int nameColor = questComplete[i] ? HudPalette.HEAL_COLOR : HudPalette.TEXT_PRIMARY;
            extractor.text(font, (questComplete[i] ? "✓ " : "► ") + questNames[i], contentX + 4, questY + 4, nameColor);
            extractor.text(font, questDescs[i], contentX + 4, questY + 16, HudPalette.TEXT_SECONDARY);
            extractor.text(font, "Progress: " + questProgress[i], contentX + 4, questY + 28, HudPalette.TEXT_TERTIARY);
            extractor.text(font, "Reward: " + questRewards[i], contentX + 4, questY + 40, HudPalette.TEXT_ACCENT);
        }
    }
}
