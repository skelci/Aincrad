package com.aincrad.hud;

/**
 * Central colour palette for the Aincrad HUD.
 * <p>
 * All colours are stored as 0xAARRGGBB integers so they can be passed
 * directly to {@link net.minecraft.client.gui.DrawContext} draw calls.
 * <p>
 * Design language: translucent blue panels, thin lighter-blue borders,
 * white text with subtle shadow, hover-highlight in cyan.
 */
public final class HudPalette {

    private HudPalette() { /* utility class */ }

    // -----------------------------------------------------------------------
    // Panel background & border
    // -----------------------------------------------------------------------

    /** Translucent blue panel background. */
    public static final int PANEL_BG = 0x590050B0;

    /** Thin lighter-blue border. */
    public static final int PANEL_BORDER = 0xCC00B4FF;

    /** Slightly darker border for depth. */
    public static final int PANEL_BORDER_DARK = 0xCC0078CC;

    // -----------------------------------------------------------------------
    // Text
    // -----------------------------------------------------------------------

    /** Primary text colour (white). */
    public static final int TEXT_PRIMARY = 0xFFFFFFFF;

    /** Secondary text colour (light grey). */
    public static final int TEXT_SECONDARY = 0xFFCCCCCC;

    /** Tertiary text colour (darker grey). */
    public static final int TEXT_TERTIARY = 0xFF999999;

    /** Accent text colour (cyan). */
    public static final int TEXT_ACCENT = 0xFF00E5FF;

    // -----------------------------------------------------------------------
    // Bar colours
    // -----------------------------------------------------------------------

    /** HP bar — red-orange gradient start (left). */
    public static final int HP_GRADIENT_START = 0xFFFF4444;

    /** HP bar — red-orange gradient end (right). */
    public static final int HP_GRADIENT_END = 0xFFFF8800;

    /** HP bar — background (dark). */
    public static final int HP_BACKGROUND = 0xFF330000;

    /** MP bar — blue. */
    public static final int MP_BAR = 0xFF3399FF;

    /** MP bar — background (dark). */
    public static final int MP_BACKGROUND = 0xFF001133;

    /** ST bar — green-yellow gradient start (left). */
    public static final int ST_GRADIENT_START = 0xFF44CC44;

    /** ST bar — green-yellow gradient end (right). */
    public static final int ST_GRADIENT_END = 0xFFCCCC00;

    /** ST bar — background (dark). */
    public static final int ST_BACKGROUND = 0xFF113300;

    // -----------------------------------------------------------------------
    // Status / feedback
    // -----------------------------------------------------------------------

    /** Flash colour when HP drops (white overlay). */
    public static final int FLASH_WHITE = 0xAAFFFFFF;

    /** Buff icon border / glow. */
    public static final int BUFF_GLOW = 0xFF00FF88;

    /** Debuff icon border / glow. */
    public static final int DEBUFF_GLOW = 0xFFFF4444;

    /** Level-up glow pulse. */
    public static final int LEVEL_UP_GLOW = 0xFFFFDD44;

    // -----------------------------------------------------------------------
    // Misc
    // -----------------------------------------------------------------------

    /** Hover highlight overlay. */
    public static final int HOVER_HIGHLIGHT = 0x3300E5FF;

    /** Shadow colour for text. */
    public static final int TEXT_SHADOW = 0xFF000000;

    /** Semi-transparent black for overlays. */
    public static final int OVERLAY_DARK = 0xAA000000;

    // -----------------------------------------------------------------------
    // Aliases matching the original spec (kept for backward compatibility)
    // -----------------------------------------------------------------------

    /** Alias for {@link #PANEL_BG}. */
    public static final int PANEL_BACKGROUND = PANEL_BG;

    /** HP bar fill (single-colour fallback). */
    public static final int HP_BAR = HP_GRADIENT_START;

    /** ST bar fill (single-colour fallback). */
    public static final int ST_BAR = ST_GRADIENT_START;

    /** Buff colour (alias for glow). */
    public static final int BUFF_COLOR = BUFF_GLOW;

    /** Debuff colour (alias for glow). */
    public static final int DEBUFF_COLOR = DEBUFF_GLOW;

    /** Critical hit colour. */
    public static final int CRIT_COLOR = 0xFFFF5522;

    /** Healing colour. */
    public static final int HEAL_COLOR = 0xFF55EE88;

    // -----------------------------------------------------------------------
    // Utility helpers
    // -----------------------------------------------------------------------

    /**
     * Packs ARGB components into a single {@code int}.
     *
     * @param a alpha {@code [0,255]}
     * @param r red   {@code [0,255]}
     * @param g green {@code [0,255]}
     * @param b blue  {@code [0,255]}
     * @return packed 0xAARRGGBB integer
     */
    public static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Returns a colour with its alpha channel replaced.
     *
     * @param colour original packed colour
     * @param a      new alpha {@code [0,255]}
     * @return new packed colour
     */
    public static int withAlpha(int colour, int a) {
        return ((a & 0xFF) << 24) | (colour & 0x00FFFFFF);
    }

    /**
     * Returns a colour with its alpha scaled by the given factor.
     *
     * @param colour original packed colour
     * @param factor multiplier {@code [0.0, 1.0]}
     * @return new packed colour with scaled alpha
     */
    public static int scaleAlpha(int colour, float factor) {
        int a = (colour >>> 24) & 0xFF;
        int newA = Math.max(0, Math.min(255, (int) (a * factor)));
        return argb(newA, (colour >> 16) & 0xFF, (colour >> 8) & 0xFF, colour & 0xFF);
    }
}
