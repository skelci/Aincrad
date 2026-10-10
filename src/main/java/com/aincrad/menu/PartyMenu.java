package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class PartyMenu extends MenuWindow {
    private static final int MEMBER_HEIGHT = 40;
    private static final int MEMBER_SPACING = 4;
    private static final int SETTINGS_HEIGHT = 80;

    private String partyName = "Party A";
    private int partySize = 4;
    private String[] memberNames = {"Kirito", "Asuna", "Klein", "Agil"};
    private int[] memberLevels = {42, 40, 38, 39};
    private String[] memberRoles = {"Tank", "Healer", "Tank", "DPS"};
    private float[] memberHp = {0.87f, 0.78f, 0.55f, 1.0f};
    private float[] memberMp = {0.45f, 0.92f, 0.40f, 0.70f};
    private String lootRule = "Round Robin";

    public PartyMenu() {
        super(100, 50, 400, 400, "Party");
        MenuManager.getInstance().register(this);
        MenuManager.getInstance().registerShortcut(80, this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        extractor.text(font, partyName, contentX, contentY, HudPalette.TEXT_PRIMARY);
        extractor.text(font, "(" + partySize + "/6)", contentX + font.width(partyName) + 8, contentY, HudPalette.TEXT_TERTIARY);
        contentY += 16;

        for (int i = 0; i < partySize; i++) {
            int memberY = contentY + i * (MEMBER_HEIGHT + MEMBER_SPACING);
            extractor.fill(contentX, memberY, contentX + width - 16, memberY + MEMBER_HEIGHT, HudPalette.PANEL_BG);
            extractor.outline(contentX, memberY, contentX + width - 16, memberY + MEMBER_HEIGHT, HudPalette.PANEL_BORDER);

            int nameColor = switch (memberRoles[i]) {
                case "Tank" -> 0xFFFFAA44;
                case "Healer" -> 0xFF44FF88;
                default -> HudPalette.TEXT_PRIMARY;
            };
            extractor.text(font, memberNames[i], contentX + 4, memberY + 2, nameColor);
            extractor.text(font, "Lv." + memberLevels[i], contentX + width - 16 - 40, memberY + 2, HudPalette.TEXT_TERTIARY);

            int barX = contentX + 4;
            int barW = width - 24;
            int hpBarY = memberY + 16;
            int mpBarY = hpBarY + 8;

            extractor.fill(barX, hpBarY, barX + barW, hpBarY + 6, HudPalette.HP_BACKGROUND);
            int hpFill = (int)(barW * memberHp[i]);
            if (hpFill > 0) extractor.fillGradient(barX, hpBarY, barX + hpFill, hpBarY + 6, HudPalette.HP_GRADIENT_START, HudPalette.HP_GRADIENT_END);

            extractor.fill(barX, mpBarY, barX + barW, mpBarY + 6, HudPalette.MP_BACKGROUND);
            int mpFill = (int)(barW * memberMp[i]);
            if (mpFill > 0) extractor.fill(barX, mpBarY, barX + mpFill, mpBarY + 6, HudPalette.MP_BAR);

            extractor.text(font, memberRoles[i], contentX + 4, memberY + MEMBER_HEIGHT - 10, HudPalette.TEXT_TERTIARY);
        }

        int settingsY = contentY + partySize * (MEMBER_HEIGHT + MEMBER_SPACING) + 8;
        extractor.fill(contentX, settingsY, contentX + width - 16, settingsY + SETTINGS_HEIGHT, HudPalette.PANEL_BG);
        extractor.outline(contentX, settingsY, contentX + width - 16, settingsY + SETTINGS_HEIGHT, HudPalette.PANEL_BORDER);
        extractor.text(font, "Settings", contentX + 4, settingsY + 4, HudPalette.TEXT_ACCENT);
        extractor.text(font, "Loot Rule: " + lootRule, contentX + 4, settingsY + 18, HudPalette.TEXT_SECONDARY);
        extractor.text(font, "[Invite] [Leave]", contentX + 4, settingsY + 34, HudPalette.TEXT_ACCENT);
    }
}
