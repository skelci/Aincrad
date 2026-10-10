package com.aincrad.menu;

import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class AuctionHouseMenu extends MenuWindow {
    private static final int SEARCH_HEIGHT = 24;
    private static final int LISTING_HEIGHT = 30;
    private static final int LISTING_SPACING = 2;
    private static final int TAB_HEIGHT = 20;

    private String[] tabs = {"Browse", "My Auctions", "Create Listing"};
    private int selectedTab = 0;
    private String searchText = "";
    private String[] itemNames = {"Crystal Sword +7", "Dragon Scale", "Health Potion x15", "Iron Ingot x32", "Crystal Shard", "Gold Ingot x5"};
    private String[] sellers = {"Kirito", "Asuna", "Klein", "Agil", "Silica", "Lisbeth"};
    private int[] prices = {2500, 1800, 150, 80, 300, 500};
    private String[] timeRemaining = {"2:30:00", "1:45:00", "0:30:00", "5:00:00", "3:15:00", "0:45:00"};

    public AuctionHouseMenu() {
        super(100, 50, 500, 450, "Auction House");
        MenuManager.getInstance().register(this);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor extractor) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8;

        extractor.fill(contentX, contentY, contentX + width - 16, contentY + SEARCH_HEIGHT, HudPalette.PANEL_BG);
        extractor.outline(contentX, contentY, contentX + width - 16, contentY + SEARCH_HEIGHT, HudPalette.PANEL_BORDER);
        extractor.text(font, searchText.isEmpty() ? "Search items..." : searchText, contentX + 4, contentY + 6, searchText.isEmpty() ? HudPalette.TEXT_TERTIARY : HudPalette.TEXT_PRIMARY);
        contentY += SEARCH_HEIGHT + 4;

        for (int i = 0; i < itemNames.length; i++) {
            int listingY = contentY + i * (LISTING_HEIGHT + LISTING_SPACING);
            extractor.fill(contentX, listingY, contentX + width - 16, listingY + LISTING_HEIGHT, HudPalette.PANEL_BG);
            extractor.outline(contentX, listingY, contentX + width - 16, listingY + LISTING_HEIGHT, HudPalette.PANEL_BORDER);

            extractor.fill(contentX + 4, listingY + 4, contentX + 24, listingY + 24, 0xFF66AAFF);
            extractor.text(font, itemNames[i], contentX + 30, listingY + 4, HudPalette.TEXT_PRIMARY);
            extractor.text(font, "Seller: " + sellers[i], contentX + 30, listingY + 16, HudPalette.TEXT_SECONDARY);
            extractor.text(font, prices[i] + "c", contentX + width - 16 - 60, listingY + 4, HudPalette.TEXT_ACCENT);
            extractor.text(font, timeRemaining[i], contentX + width - 16 - 60, listingY + 16, HudPalette.TEXT_TERTIARY);
        }

        int tabY = contentY + itemNames.length * (LISTING_HEIGHT + LISTING_SPACING) + 8;
        for (int i = 0; i < tabs.length; i++) {
            int tabX = contentX + i * 100;
            boolean selected = (i == selectedTab);
            extractor.fill(tabX, tabY, tabX + 96, tabY + TAB_HEIGHT, selected ? HudPalette.PANEL_BORDER_DARK : HudPalette.PANEL_BG);
            extractor.text(font, tabs[i], tabX + 4, tabY + 4, selected ? HudPalette.TEXT_ACCENT : HudPalette.TEXT_SECONDARY);
        }
    }

    @Override
    protected void contentMouseClicked(double mx, double my, int button) {
        int contentX = x + 8;
        int contentY = y + HEADER_HEIGHT + 8 + SEARCH_HEIGHT + 4;
        int tabY = contentY + itemNames.length * (LISTING_HEIGHT + LISTING_SPACING) + 8;
        int relY = (int)(my - tabY);
        if (relY >= 0 && relY < TAB_HEIGHT) {
            int relX = (int)(mx - contentX);
            int tabIdx = relX / 100;
            if (tabIdx >= 0 && tabIdx < tabs.length) selectedTab = tabIdx;
        }
    }
}
