package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Central registry and dispatcher for all {@link HudElement}s.
 * <p>
 * The manager is a singleton accessed via {@link #getInstance()}. It owns the
 * canonical element list, delegates rendering and mouse events, and is driven
 * by {@link HudOverlay} which hooks into the Fabric HUD rendering pipeline.
 */
public final class HudManager {

    /* ------------------------------------------------------------------ */
    /* Singleton                                                          */
    /* ------------------------------------------------------------------ */

    private static HudManager instance;

    /**
     * Returns the shared {@code HudManager} instance, creating it if needed.
     */
    public static synchronized HudManager getInstance() {
        if (instance == null) {
            instance = new HudManager();
        }
        return instance;
    }

    /* ------------------------------------------------------------------ */
    /* State                                                              */
    /* ------------------------------------------------------------------ */

    /** All registered HUD elements, in render order. */
    private final List<HudElement> elements = new ArrayList<>();

    /** Whether mouse events are forwarded to elements. */
    private boolean mouseEventsEnabled = true;

    /* ------------------------------------------------------------------ */
    /* Registration                                                       */
    /* ------------------------------------------------------------------ */

    /**
     * Registers a HUD element for rendering and mouse-event dispatch.
     *
     * @param element the element to add
     */
    public void register(HudElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot register null HudElement");
        }
        elements.add(element);
    }

    /**
     * Removes a previously registered element.
     *
     * @param element the element to remove
     */
    public void unregister(HudElement element) {
        elements.remove(element);
    }

    /**
     * Removes all registered elements.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Returns an unmodifiable view of the current element list.
     */
    public List<HudElement> getElements() {
        return List.copyOf(elements);
    }

    /* ------------------------------------------------------------------ */
    /* Rendering                                                          */
    /* ------------------------------------------------------------------ */

    /**
     * Renders all visible elements.
     *
     * @param extractor the graphics extractor for the current frame
     */
    public void renderAll(GuiGraphicsExtractor extractor) {
        for (HudElement element : elements) {
            if (element.isVisible()) {
                element.render(extractor);
            }
        }
    }

    /**
     * Calls {@link HudElement#update()} on every registered element.
     */
    public void updateAll() {
        for (HudElement element : elements) {
            element.update();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Mouse event dispatch                                               */
    /* ------------------------------------------------------------------ */

    /**
     * Forwards a mouse-click event to all visible elements.
     * <p>
     * Elements are tested in reverse render order (top-most first) so that
     * overlapping elements receive events correctly.
     *
     * @param mouseX screen X coordinate
     * @param mouseY screen Y coordinate
     * @param button mouse button index
     */
    public void handleMouseClicked(double mouseX, double mouseY, int button) {
        if (!mouseEventsEnabled) {
            return;
        }
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement e = elements.get(i);
            if (e.isVisible() && contains(e, mouseX, mouseY)) {
                e.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    /**
     * Forwards a mouse-drag event.
     *
     * @param mouseX screen X coordinate
     * @param mouseY screen Y coordinate
     * @param button the currently held mouse button
     * @param deltaX change in X since last frame
     * @param deltaY change in Y since last frame
     */
    public void handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!mouseEventsEnabled) {
            return;
        }
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement e = elements.get(i);
            if (e.isVisible()) {
                e.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
    }

    /**
     * Forwards a mouse-release event.
     *
     * @param mouseX screen X coordinate
     * @param mouseY screen Y coordinate
     * @param button the released mouse button
     */
    public void handleMouseReleased(double mouseX, double mouseY, int button) {
        if (!mouseEventsEnabled) {
            return;
        }
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement e = elements.get(i);
            if (e.isVisible()) {
                e.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    /**
     * Forwards a mouse-move (hover) event.
     *
     * @param mouseX screen X coordinate
     * @param mouseY screen Y coordinate
     */
    public void handleMouseMoved(double mouseX, double mouseY) {
        if (!mouseEventsEnabled) {
            return;
        }
        for (HudElement e : elements) {
            if (e.isVisible()) {
                e.mouseMoved(mouseX, mouseY);
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /* Hit-testing                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Checks whether the given screen point falls inside the element's bounds.
     *
     * @param element the HUD element
     * @param mouseX  screen X coordinate
     * @param mouseY  screen Y coordinate
     * @return {@code true} if the point is inside the element
     */
    private static boolean contains(HudElement element, double mouseX, double mouseY) {
        int[] b = getBounds(element);
        return mouseX >= b[0] && mouseX < b[2] && mouseY >= b[1] && mouseY < b[3];
    }

    /**
     * Computes the absolute bounding box for an element.
     *
     * @param element the HUD element
     * @return {@code int[]{left, top, right, bottom}} in screen coordinates
     */
    private static int[] getBounds(HudElement element) {
        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        int absX = getAbsoluteX(element, screenW);
        int absY = getAbsoluteY(element, screenH);
        return new int[]{absX, absY, absX + element.getWidth(), absY + element.getHeight()};
    }

    /**
     * Computes the absolute screen X position given the element's anchor.
     */
    private static int getAbsoluteX(HudElement element, int screenWidth) {
        return switch (element.getAnchor()) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> element.getX();
            case TOP_CENTER, CENTER, BOTTOM_CENTER -> screenWidth / 2 + element.getX();
            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> screenWidth - element.getX() - element.getWidth();
        };
    }

    /**
     * Computes the absolute screen Y position given the element's anchor.
     */
    private static int getAbsoluteY(HudElement element, int screenHeight) {
        return switch (element.getAnchor()) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> element.getY();
            case CENTER_LEFT, CENTER, CENTER_RIGHT -> screenHeight / 2 + element.getY();
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> screenHeight - element.getY() - element.getHeight();
        };
    }

    /* ------------------------------------------------------------------ */
    /* Settings                                                           */
    /* ------------------------------------------------------------------ */

    /**
     * Enables or disables mouse-event forwarding to elements.
     */
    public void setMouseEventsEnabled(boolean enabled) {
        this.mouseEventsEnabled = enabled;
    }

    public boolean isMouseEventsEnabled() {
        return mouseEventsEnabled;
    }
}
