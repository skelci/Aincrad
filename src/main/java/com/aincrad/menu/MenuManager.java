package com.aincrad.menu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MenuManager {
    private static MenuManager instance;
    private final List<MenuWindow> menus = new ArrayList<>();
    private final Map<Integer, MenuWindow> shortcuts = new HashMap<>();
    private MenuWindow activeMenu;

    private MenuManager() {}

    public static synchronized MenuManager getInstance() {
        if (instance == null) instance = new MenuManager();
        return instance;
    }

    public void register(MenuWindow menu) {
        if (menu == null) throw new IllegalArgumentException("Cannot register null MenuWindow");
        menus.add(menu);
    }

    public void unregister(MenuWindow menu) { menus.remove(menu); }
    public void clear() { menus.clear(); activeMenu = null; }

    public void openMenu(MenuWindow menu) {
        if (!menus.contains(menu)) register(menu);
        closeActiveMenu();
        menu.open();
        activeMenu = menu;
    }

    public void closeMenu(MenuWindow menu) {
        menu.close();
        if (activeMenu == menu) activeMenu = null;
    }

    public void toggleMenu(MenuWindow menu) {
        if (menu.isOpen()) closeMenu(menu);
        else openMenu(menu);
    }

    public void closeActiveMenu() {
        if (activeMenu != null) { activeMenu.close(); activeMenu = null; }
    }

    public MenuWindow getActiveMenu() { return activeMenu; }

    public void registerShortcut(int keyCode, MenuWindow menu) {
        shortcuts.put(keyCode, menu);
    }

    public void updateAll() {
        for (MenuWindow menu : menus) menu.update();
    }

    public void renderAll(GuiGraphicsExtractor extractor) {
        for (MenuWindow menu : menus) {
            if (menu != activeMenu) menu.render(extractor);
        }
        if (activeMenu != null) activeMenu.render(extractor);
    }

    public void handleMouseClicked(double mouseX, double mouseY, int button) {
        boolean insideAny = false;
        for (int i = menus.size() - 1; i >= 0; i--) {
            MenuWindow menu = menus.get(i);
            if (menu.isOpen() && contains(menu, mouseX, mouseY)) {
                insideAny = true;
                menu.mouseClicked(mouseX, mouseY, button);
                if (menu.isActive()) break;
            }
        }
        if (!insideAny && activeMenu != null) closeActiveMenu();
    }

    public void handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (activeMenu != null) activeMenu.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void handleMouseReleased(double mouseX, double mouseY, int button) {
        if (activeMenu != null) activeMenu.mouseReleased(mouseX, mouseY, button);
    }

    public void handleMouseMoved(double mouseX, double mouseY) {
        for (MenuWindow menu : menus) {
            if (menu.isOpen()) menu.mouseMoved(mouseX, mouseY);
        }
    }

    private static boolean contains(MenuWindow menu, double mouseX, double mouseY) {
        return mouseX >= menu.getX() && mouseX <= menu.getX() + menu.getWidth()
                && mouseY >= menu.getY() && mouseY <= menu.getY() + menu.getHeight();
    }
}
