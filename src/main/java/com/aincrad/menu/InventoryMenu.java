package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class InventoryMenu extends MenuWindow {
    private static final int SLOT_SIZE = 40;
    private static final int SLOT_SPACING = 2;
    private static final int COLS = 8;
    private static final int ROWS = 5;
    private static final int STATS_HEIGHT = 80;
    private static final int CURRENCY_HEIGHT = 20;

    private String[] itemNames = {"Crystal Sword", "Crystal Shield", "Health Potion", "Mana Potion", "Iron Ingot", "Gold Ingot", "Crystal Shard", "Dragon Scale"};
    private int[] itemCounts = {1, 1, 15, 10, 32, 5, 7, 2};
    private int selectedSlot = -1;

    public InventoryMenu() {
        super(100, 50, 500, 500, "Inventory");
        MenuManager.getInstance().register(this);
        MenuManager.getInstance().registerShortcut(66, this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = row * COLS + col;
                int slotX = contentX + col * (SLOT_SIZE + SLOT_SPACING);
                int slotY = contentY + row * (SLOT_SIZE + SLOT_SPACING);
                boolean selected = (idx == selectedSlot);
                int borderColor = selected ? HudPalette.TEXT_ACCENT : HudPalette.PANEL_BORDER;
                extractor.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, HudPalette.PANEL_BG);
                extractor.outline(slotX, slotY, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, borderColor);

                if (idx < itemNames.length) {
                    extractor.text(font, itemNames[idx], slotX + 2, slotY + 2, HudPalette.TEXT_PRIMARY);
                    extractor.text(font, "x" + itemCounts[idx], slotX + 2, slotY + SLOT_SIZE - 10, HudPalette.TEXT_TERTIARY);
                }
            }
        }

        int statsY = contentY + ROWS * (SLOT_SIZE + SLOT_SPACING) + 8;
        extractor.fill(contentX, statsY, contentX + width - 16, statsY + STATS_HEIGHT, HudPalette.PANEL_BG);
        extractor.outline(contentX, statsY, contentX + width - 16, statsY + STATS_HEIGHT, HudPalette.PANEL_BORDER);
        extractor.text(font, "Character Stats", contentX + 4, statsY + 4, HudPalette.TEXT_ACCENT);
        extractor.text(font, "STR: 42  AGI: 38  VIT: 35", contentX + 4, statsY + 18, HudPalette.TEXT_SECONDARY);
        extractor.text(font, "INT: 30  DEX: 40  LUK: 25", contentX + 4, statsY + 30, HudPalette.TEXT_SECONDARY);

        int currencyY = statsY + STATS_HEIGHT + 4;
        extractor.text(font, "Col: 1,250  Gold: 50,000  Tokens: 340", contentX, currencyY, HudPalette.TEXT_ACCENT);
    }

    @Override
    protected void contentMouseClicked(double mx, double my, int button) {
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;
        int relX = (int)(mx - contentX);
        int relY = (int)(my - contentY);
        int col = relX / (SLOT_SIZE + SLOT_SPACING);
        int row = relY / (SLOT_SIZE + SLOT_SPACING);
        int idx = row * COLS + col;
        if (idx >= 0 && idx < COLS * ROWS) selectedSlot = idx;
    }
}
