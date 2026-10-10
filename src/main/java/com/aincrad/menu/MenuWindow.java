package com.aincrad.menu;

import com.aincrad.hud.HudElement;
import com.aincrad.hud.HudPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class MenuWindow extends HudElement {
    protected static final int HEADER_HEIGHT = 24;
    protected static final int CLOSE_BUTTON_SIZE = 16;
    protected static final int SNAP_THRESHOLD = 16;

    protected String title;
    protected boolean isOpen;
    protected boolean isDragging;
    protected int dragOffsetX, dragOffsetY;
    protected boolean active;
    protected double openProgress;

    public MenuWindow(int x, int y, int width, int height, String title) {
        super(x, y, width, height);
        this.title = title;
        this.isOpen = false;
        this.isDragging = false;
        this.dragOffsetX = 0;
        this.dragOffsetY = 0;
        this.active = false;
        this.openProgress = 0.0;
    }

    public void open() { isOpen = true; active = true; }
    public void close() { isOpen = false; active = false; }
    public void toggle() { if (isOpen) close(); else open(); }
    public boolean isOpen() { return isOpen; }
    public double getOpenProgress() { return openProgress; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }
    public String getTitle() { return title; }

    @Override
    public void update() {
        double target = isOpen ? 1.0 : 0.0;
        openProgress += (target - openProgress) * 0.2;
        if (Math.abs(target - openProgress) < 0.01) openProgress = target;
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!isOpen && openProgress < 0.01) return;
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int alpha = (int)(openProgress * 255);

        extractor.fill(x, y, x + width, y + height, withAlpha(HudPalette.PANEL_BG, alpha));
        extractor.outline(x, y, x + width - 1, y + height - 1, withAlpha(HudPalette.PANEL_BORDER, alpha));
        extractor.fill(x, y, x + width, y + HEADER_HEIGHT, withAlpha(HudPalette.PANEL_BORDER_DARK, alpha));
        extractor.text(font, title, x + 8, y + 6, withAlpha(HudPalette.TEXT_PRIMARY, alpha));

        int closeX = x + width - CLOSE_BUTTON_SIZE - 4;
        int closeY = y + 4;
        extractor.fill(closeX, closeY, closeX + CLOSE_BUTTON_SIZE, closeY + CLOSE_BUTTON_SIZE, withAlpha(HudPalette.OVERLAY_DARK, alpha));
        extractor.text(font, "X", closeX + 4, closeY + 4, withAlpha(HudPalette.TEXT_PRIMARY, alpha));

        int contentY = y + HEADER_HEIGHT;
        extractor.fill(x + 1, contentY, x + width - 1, y + height - 1, withAlpha(HudPalette.PANEL_BG, alpha));
        renderContent(extractor);
    }

    protected abstract void renderContent(GuiGraphicsExtractor extractor);

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (!isOpen) return;
        int closeX = x + width - CLOSE_BUTTON_SIZE - 4;
        int closeY = y + 4;
        if (mouseX >= closeX && mouseX <= closeX + CLOSE_BUTTON_SIZE && mouseY >= closeY && mouseY <= closeY + CLOSE_BUTTON_SIZE) {
            close();
            return;
        }
        if (mouseY >= y && mouseY <= y + HEADER_HEIGHT) {
            isDragging = true;
            dragOffsetX = (int)(mouseX - x);
            dragOffsetY = (int)(mouseY - y);
        }
        if (mouseX >= x && mouseX <= x + width && mouseY >= y + HEADER_HEIGHT && mouseY <= y + height) {
            contentMouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isOpen || !isDragging) return;
        x = (int)(mouseX - dragOffsetX);
        y = (int)(mouseY - dragOffsetY);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) { isDragging = false; snapToEdge(); }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (!isOpen) return;
        if (mouseX >= x && mouseX <= x + width && mouseY >= y + HEADER_HEIGHT && mouseY <= y + height) {
            contentMouseMoved(mouseX, mouseY);
        }
    }

    private void snapToEdge() {
        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        if (x < SNAP_THRESHOLD) x = 0;
        if (y < SNAP_THRESHOLD) y = 0;
        if (x + width > screenW - SNAP_THRESHOLD) x = screenW - width;
        if (y + height > screenH - SNAP_THRESHOLD) y = screenH - height;
    }

    protected void contentMouseClicked(double mx, double my, int button) {}
    protected void contentMouseDragged(double mx, double my, int button, double dx, double dy) {}
    protected void contentMouseReleased(double mx, double my, int button) {}
    protected void contentMouseMoved(double mx, double my) {}

    protected static int withAlpha(int colour, int alpha) {
        return (alpha << 24) | (colour & 0x00FFFFFF);
    }
    protected static int withAlpha(int colour, float factor) {
        int alpha = Math.round(factor * 255);
        return (alpha << 24) | (colour & 0x00FFFFFF);
    }
}
