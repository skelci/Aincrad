package com.aincrad.hud;

public final class HudPalette {
    private HudPalette() {}

    public static final int PANEL_BG = 0x590050B0;
    public static final int PANEL_BORDER = 0xCC00B4FF;
    public static final int PANEL_BORDER_DARK = 0xCC0078CC;
    public static final int TEXT_PRIMARY = 0xFFFFFFFF;
    public static final int TEXT_SECONDARY = 0xFFCCCCCC;
    public static final int TEXT_TERTIARY = 0xFF999999;
    public static final int TEXT_ACCENT = 0xFF00E5FF;
    public static final int HP_GRADIENT_START = 0xFFFF4444;
    public static final int HP_GRADIENT_END = 0xFFFF8800;
    public static final int HP_BACKGROUND = 0xFF330000;
    public static final int MP_BAR = 0xFF3399FF;
    public static final int MP_BACKGROUND = 0xFF001133;
    public static final int ST_GRADIENT_START = 0xFF44CC44;
    public static final int ST_GRADIENT_END = 0xFFCCCC00;
    public static final int ST_BACKGROUND = 0xFF113300;
    public static final int FLASH_WHITE = 0xAAFFFFFF;
    public static final int BUFF_GLOW = 0xFF00FF88;
    public static final int DEBUFF_GLOW = 0xFFFF4444;
    public static final int LEVEL_UP_GLOW = 0xFFFFDD44;
    public static final int HOVER_HIGHLIGHT = 0x3300E5FF;
    public static final int TEXT_SHADOW = 0xFF000000;
    public static final int OVERLAY_DARK = 0xAA000000;
    public static final int PANEL_BACKGROUND = PANEL_BG;
    public static final int HP_BAR = HP_GRADIENT_START;
    public static final int ST_BAR = ST_GRADIENT_START;
    public static final int BUFF_COLOR = BUFF_GLOW;
    public static final int DEBUFF_COLOR = DEBUFF_GLOW;
    public static final int CRIT_COLOR = 0xFFFF5522;
    public static final int HEAL_COLOR = 0xFF55EE88;

    public static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int withAlpha(int colour, int a) {
        return ((a & 0xFF) << 24) | (colour & 0x00FFFFFF);
    }

    public static int scaleAlpha(int colour, float factor) {
        int a = (colour >>> 24) & 0xFF;
        int newA = Math.max(0, Math.min(255, (int)(a * factor)));
        return argb(newA, (colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF);
    }
}
