package com.aincrad.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class HudElement {
    protected int x, y, width, height;
    protected boolean visible = true;
    protected Anchor anchor = Anchor.TOP_LEFT;

    public HudElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphicsExtractor extractor);
    public abstract void update();

    public void mouseClicked(double mouseX, double mouseY, int button) {}
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {}
    public void mouseReleased(double mouseX, double mouseY, int button) {}
    public void mouseMoved(double mouseX, double mouseY) {}

    public enum Anchor {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        CENTER_LEFT, CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public Anchor getAnchor() { return anchor; }
    public void setAnchor(Anchor anchor) { this.anchor = anchor; }
}
