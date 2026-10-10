package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class EquipmentMenu extends MenuWindow {
    private static final int SLOT_SIZE = 48;
    private static final int SLOT_SPACING = 4;
    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 3;
    private static final int DETAIL_HEIGHT = 100;

    private String[] slotNames = {"Helmet", "Chest", "Legs", "Boots", "Main Hand", "Off Hand", "Ring 1", "Ring 2", "Necklace", "Earring"};
    private String[] itemNames = {"Crystal Helm", "Crystal Plate", "Crystal Legs", "Crystal Boots", "Crystal Sword", "Crystal Shield", "Power Ring", "Magic Ring", "Crystal Necklace", "Crystal Earring"};
    private int selectedSlot = -1;

    public EquipmentMenu() {
        super(100, 50, 400, 350, "Equipment");
        MenuManager.getInstance().register(this);
        MenuManager.getInstance().registerShortcut(67, this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int idx = row * GRID_COLS + col;
                if (idx >= slotNames.length) break;
                int slotX = contentX + col * (SLOT_SIZE + SLOT_SPACING);
                int slotY = contentY + row * (SLOT_SIZE + SLOT_SPACING);
                boolean selected = (idx == selectedSlot);
                int borderColor = selected ? HudPalette.TEXT_ACCENT : HudPalette.PANEL_BORDER;
                extractor.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, HudPalette.PANEL_BG);
                extractor.outline(slotX, slotY, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, borderColor);
                extractor.text(font, slotNames[idx], slotX + 2, slotY + 2, HudPalette.TEXT_TERTIARY);
                extractor.text(font, itemNames[idx], slotX + 2, slotY + SLOT_SIZE - 10, HudPalette.TEXT_SECONDARY);
            }
        }

        int detailY = contentY + GRID_ROWS * (SLOT_SIZE + SLOT_SPACING) + 8;
        extractor.fill(contentX, detailY, contentX + width - 16, detailY + DETAIL_HEIGHT, HudPalette.PANEL_BG);
        extractor.outline(contentX, detailY, contentX + width - 16, detailY + DETAIL_HEIGHT, HudPalette.PANEL_BORDER);

        if (selectedSlot >= 0) {
            extractor.text(font, itemNames[selectedSlot], contentX + 4, detailY + 4, HudPalette.TEXT_PRIMARY);
            extractor.text(font, "Attack: 342 (+85)", contentX + 4, detailY + 16, HudPalette.TEXT_SECONDARY);
            extractor.text(font, "Sharpness V, Unbreaking III", contentX + 4, detailY + 28, HudPalette.TEXT_TERTIARY);
            extractor.text(font, "[Equip] [Unequip] [Repair] [Enhance]", contentX + 4, detailY + 44, HudPalette.TEXT_ACCENT);
        } else {
            extractor.text(font, "Select an item", contentX + 4, detailY + 4, HudPalette.TEXT_TERTIARY);
        }
    }

    @Override
    protected void contentMouseClicked(double mx, double my, int button) {
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;
        int relX = (int)(mx - contentX);
        int relY = (int)(my - contentY);
        int col = relX / (SLOT_SIZE + SLOT_SPACING);
        int row = relY / (SLOT_SIZE + SLOT_SPACING);
        int idx = row * GRID_COLS + col;
        if (idx >= 0 && idx < slotNames.length) selectedSlot = idx;
    }
}
