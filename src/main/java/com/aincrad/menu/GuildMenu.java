package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class GuildMenu extends MenuWindow {
    private static final int TAB_HEIGHT = 20;
    private static final int MEMBER_HEIGHT = 24;
    private static final int MEMBER_SPACING = 2;

    private String[] tabs = {"Roster", "Bank", "Management"};
    private int selectedTab = 0;
    private String guildName = "Knights of the Blood";
    private int guildLevel = 15;
    private int guildXp = 65;
    private int memberCount = 8;
    private String[] memberNames = {"Kirito", "Asuna", "Klein", "Agil", "Silica", "Lisbeth", "Heathcliff", "Yui"};
    private int[] memberLevels = {42, 40, 38, 39, 37, 36, 41, 35};
    private String[] memberRoles = {"Leader", "Sub-Leader", "Member", "Member", "Member", "Member", "Member", "Member"};
    private boolean[] memberOnline = {true, true, true, false, true, true, true, false};

    public GuildMenu() {
        super(100, 50, 500, 400, "Guild");
        MenuManager.getInstance().register(this);
        MenuManager.getInstance().registerShortcut(71, this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        extractor.text(font, guildName, contentX, contentY, HudPalette.TEXT_PRIMARY);
        extractor.text(font, "Lv." + guildLevel, contentX + font.width(guildName) + 8, contentY, HudPalette.TEXT_ACCENT);
        extractor.text(font, "(" + memberCount + " members)", contentX + font.width(guildName) + 60, contentY, HudPalette.TEXT_TERTIARY);
        contentY += 16;

        extractor.fill(contentX, contentY, contentX + 100, contentY + 6, HudPalette.PANEL_BG);
        extractor.fillGradient(contentX, contentY, contentX + (int)(100 * guildXp / 100.0), contentY + 6, HudPalette.HP_GRADIENT_START, HudPalette.HP_GRADIENT_END);
        contentY += 12;

        for (int i = 0; i < tabs.length; i++) {
            int tabX = contentX + i * 80;
            boolean selected = (i == selectedTab);
            extractor.fill(tabX, contentY, tabX + 76, contentY + TAB_HEIGHT, selected ? HudPalette.PANEL_BORDER_DARK : HudPalette.PANEL_BG);
            extractor.text(font, tabs[i], tabX + 4, contentY + 4, selected ? HudPalette.TEXT_ACCENT : HudPalette.TEXT_SECONDARY);
        }
        contentY += TAB_HEIGHT + 4;

        if (selectedTab == 0) {
            for (int i = 0; i < memberCount; i++) {
                int memberY = contentY + i * (MEMBER_HEIGHT + MEMBER_SPACING);
                extractor.fill(contentX, memberY, contentX + width - 16, memberY + MEMBER_HEIGHT, HudPalette.PANEL_BG);
                extractor.outline(contentX, memberY, contentX + width - 16, memberY + MEMBER_HEIGHT, HudPalette.PANEL_BORDER);

                int onlineColor = memberOnline[i] ? 0xFF44FF44 : 0xFFFF4444;
                extractor.fill(contentX + 4, memberY + 4, contentX + 8, memberY + 8, onlineColor);
                extractor.text(font, memberNames[i], contentX + 14, memberY + 4, HudPalette.TEXT_PRIMARY);
                extractor.text(font, "Lv." + memberLevels[i], contentX + 100, memberY + 4, HudPalette.TEXT_TERTIARY);
                extractor.text(font, memberRoles[i], contentX + width - 16 - font.width(memberRoles[i]) - 4, memberY + 4, HudPalette.TEXT_SECONDARY);
            }
        } else if (selectedTab == 1) {
            extractor.text(font, "Guild Bank", contentX, contentY, HudPalette.TEXT_PRIMARY);
            extractor.text(font, "Items: 47", contentX, contentY + 14, HudPalette.TEXT_SECONDARY);
        } else {
            extractor.text(font, "Management", contentX, contentY, HudPalette.TEXT_PRIMARY);
            extractor.text(font, "[Create/Disband] [Invite] [Promote]", contentX, contentY + 14, HudPalette.TEXT_ACCENT);
        }
    }

    @Override
    protected void contentMouseClicked(double mx, double my, int button) {
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8 + 16 + 12;
        int relX = (int)(mx - contentX);
        int relY = (int)(my - contentY);
        if (relY < TAB_HEIGHT) {
            int tabIdx = relX / 80;
            if (tabIdx >= 0 && tabIdx < tabs.length) selectedTab = tabIdx;
        }
    }
}
