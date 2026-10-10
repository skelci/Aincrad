package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SettingsMenu extends MenuWindow {
    private static final int SECTION_HEADER_HEIGHT = 20;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_SPACING = 4;

    private boolean damageNumbersEnabled = true;
    private boolean minimapEnabled = true;
    private boolean hudEnabled = true;
    private float minimapSize = 1.0f;
    private float hudScale = 1.0f;
    private String language = "English";

    public SettingsMenu() {
        super(100, 50, 500, 450, "Aincrad Settings");
        MenuManager.getInstance().register(this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        extractor.text(font, "HUD", contentX, contentY, HudPalette.TEXT_ACCENT);
        contentY += SECTION_HEADER_HEIGHT;

        extractor.text(font, "Damage Numbers: " + (damageNumbersEnabled ? "ON" : "OFF"), contentX, contentY, HudPalette.TEXT_SECONDARY);
        contentY += ROW_HEIGHT + ROW_SPACING;
        extractor.text(font, "Minimap: " + (minimapEnabled ? "ON" : "OFF"), contentX, contentY, HudPalette.TEXT_SECONDARY);
        contentY += ROW_HEIGHT + ROW_SPACING;
        extractor.text(font, "HUD Visibility: " + (hudEnabled ? "ON" : "OFF"), contentX, contentY, HudPalette.TEXT_SECONDARY);
        contentY += ROW_HEIGHT + ROW_SPACING + 8;

        extractor.text(font, "Keybinds", contentX, contentY, HudPalette.TEXT_ACCENT);
        contentY += SECTION_HEADER_HEIGHT;
        extractor.text(font, "[Inventory] [Skills] [Map] [Party]", contentX, contentY, HudPalette.TEXT_SECONDARY);
        contentY += ROW_HEIGHT + ROW_SPACING + 8;

        extractor.text(font, "Graphics", contentX, contentY, HudPalette.TEXT_ACCENT);
        contentY += SECTION_HEADER_HEIGHT;
        extractor.text(font, "Minimap Size: " + (int)(minimapSize * 100) + "%", contentX, contentY, HudPalette.TEXT_SECONDARY);
        contentY += ROW_HEIGHT + ROW_SPACING;
        extractor.text(font, "HUD Scale: " + (int)(hudScale * 100) + "%", contentX, contentY, HudPalette.TEXT_SECONDARY);
        contentY += ROW_HEIGHT + ROW_SPACING + 8;

        extractor.text(font, "Language", contentX, contentY, HudPalette.TEXT_ACCENT);
        contentY += SECTION_HEADER_HEIGHT;
        extractor.text(font, "Language: " + language, contentX, contentY, HudPalette.TEXT_SECONDARY);
    }

    @Override
    protected void contentMouseClicked(double mx, double my, int button) {
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8 + SECTION_HEADER_HEIGHT;
        int relY = (int)(my - contentY);
        if (relY < ROW_HEIGHT) damageNumbersEnabled = !damageNumbersEnabled;
        else if (relY < ROW_HEIGHT * 2 + ROW_SPACING) minimapEnabled = !minimapEnabled;
        else if (relY < ROW_HEIGHT * 3 + ROW_SPACING * 2) hudEnabled = !hudEnabled;
    }
}
