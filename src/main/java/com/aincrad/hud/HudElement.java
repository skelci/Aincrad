package com.aincrad.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Base class for all HUD elements in the Aincrad UI.
 * <p>
 * Provides position, size, visibility, and anchor point, plus lifecycle
 * hooks for rendering and mouse interaction. Subclasses should override
 * {@link #render(GuiGraphicsExtractor)} and {@link #update()} to implement their
 * behaviour.
 */
public abstract class HudElement {

    /** Horizontal position (pixels from left edge of screen). */
    protected int x;

    /** Vertical position (pixels from top edge of screen). */
    protected int y;

    /** Width of the element in pixels. */
    protected int width;

    /** Height of the element in pixels. */
    protected int height;

    /** Whether the element is currently visible. */
    protected boolean visible = true;

    /** Anchor point that determines how x/y are interpreted. */
    protected Anchor anchor = Anchor.TOP_LEFT;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    public HudElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // -----------------------------------------------------------------------
    // Abstract lifecycle
    // -----------------------------------------------------------------------

    /**
     * Render the element. Called every frame while {@link #visible} is true.
     *
     * @param extractor the graphics extractor for this frame
     */
    public abstract void render(GuiGraphicsExtractor extractor);

    /**
     * Update internal state (animations, timers, etc.). Called every tick
     * before {@link #render(GuiGraphicsExtractor)}.
     */
    public abstract void update();

    // -----------------------------------------------------------------------
    // Mouse interaction hooks (no-op by default)
    // -----------------------------------------------------------------------

    public void mouseClicked(double mouseX, double mouseY, int button) { }
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) { }
    public void mouseReleased(double mouseX, double mouseY, int button) { }
    public void mouseMoved(double mouseX, double mouseY) { }

    // -----------------------------------------------------------------------
    // Anchor
    // -----------------------------------------------------------------------

    /**
     * Anchor points for positioning HUD elements.
     */
    public enum Anchor {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        CENTER_LEFT,
        CENTER,
        CENTER_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT
    }

    // -----------------------------------------------------------------------
    // Getters & setters
    // -----------------------------------------------------------------------

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
