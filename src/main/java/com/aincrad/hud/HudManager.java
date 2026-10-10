package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import java.util.ArrayList;
import java.util.List;

public final class HudManager {
    private static HudManager instance;
    private final List<HudElement> elements = new ArrayList<>();
    private boolean mouseEventsEnabled = true;

    public static synchronized HudManager getInstance() {
        if (instance == null) instance = new HudManager();
        return instance;
    }

    public void register(HudElement element) {
        if (element == null) throw new IllegalArgumentException("Cannot register null HudElement");
        elements.add(element);
    }

    public void unregister(HudElement element) { elements.remove(element); }
    public void clear() { elements.clear(); }
    public List<HudElement> getElements() { return List.copyOf(elements); }

    public void renderAll(GuiGraphicsExtractor extractor) {
        for (HudElement element : elements) {
            if (element.isVisible()) element.render(extractor);
        }
    }

    public void updateAll() {
        for (HudElement element : elements) element.update();
    }

    public void handleMouseClicked(double mouseX, double mouseY, int button) {
        if (!mouseEventsEnabled) return;
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement e = elements.get(i);
            if (e.isVisible() && contains(e, mouseX, mouseY)) e.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!mouseEventsEnabled) return;
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement e = elements.get(i);
            if (e.isVisible()) e.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public void handleMouseReleased(double mouseX, double mouseY, int button) {
        if (!mouseEventsEnabled) return;
        for (int i = elements.size() - 1; i >= 0; i--) {
            HudElement e = elements.get(i);
            if (e.isVisible()) e.mouseReleased(mouseX, mouseY, button);
        }
    }

    public void handleMouseMoved(double mouseX, double mouseY) {
        if (!mouseEventsEnabled) return;
        for (HudElement e : elements) {
            if (e.isVisible()) e.mouseMoved(mouseX, mouseY);
        }
    }

    private static boolean contains(HudElement element, double mouseX, double mouseY) {
        int[] b = getBounds(element);
        return mouseX >= b[0] && mouseX < b[2] && mouseY >= b[1] && mouseY < b[3];
    }

    private static int[] getBounds(HudElement element) {
        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        int absX = getAbsoluteX(element, screenW);
        int absY = getAbsoluteY(element, screenH);
        return new int[]{absX, absY, absX + element.getWidth(), absY + element.getHeight()};
    }

    private static int getAbsoluteX(HudElement element, int screenWidth) {
        return switch (element.getAnchor()) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> element.getX();
            case TOP_CENTER, CENTER, BOTTOM_CENTER -> screenWidth / 2 + element.getX();
            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> screenWidth - element.getX() - element.getWidth();
        };
    }

    private static int getAbsoluteY(HudElement element, int screenHeight) {
        return switch (element.getAnchor()) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> element.getY();
            case CENTER_LEFT, CENTER, CENTER_RIGHT -> screenHeight / 2 + element.getY();
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> screenHeight - element.getY() - element.getHeight();
        };
    }

    public void setMouseEventsEnabled(boolean enabled) { this.mouseEventsEnabled = enabled; }
    public boolean isMouseEventsEnabled() { return mouseEventsEnabled; }
}
