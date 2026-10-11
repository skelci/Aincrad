package com.aincrad.boss.theme;

import java.util.Locale;

/**
 * Theme assigned to each floor range, derived from {@code docs/world/FLOORS.md}.
 * Themes drive arena templates, mob choices, particle effects, and weapon
 * flavours for the bosses in that range.
 */
public enum BossTheme {
    GRASSLAND("Grassland", 0x7EC850, "aincrad:grassland_ambient"),
    FOREST("Forest", 0x3B8C3B, "aincrad:forest_ambient"),
    CAVE("Cave", 0x4A4A4A, "aincrad:cave_ambient"),
    LAVA("Lava", 0xFF5A1F, "aincrad:lava_ambient"),
    ICE("Ice", 0x9BDCFF, "aincrad:ice_ambient"),
    DESERT("Desert", 0xE3C878, "aincrad:desert_ambient"),
    SWAMP("Swamp", 0x5A7A3B, "aincrad:swamp_ambient"),
    OCEAN("Ocean", 0x2B6CB0, "aincrad:ocean_ambient"),
    SKY("Sky", 0xB8D4F0, "aincrad:sky_ambient"),
    VOID("Void", 0x7B2FBE, "aincrad:void_ambient");

    private final String displayName;
    private final int primaryColor;
    private final String ambientParticle;

    BossTheme(String displayName, int primaryColor, String ambientParticle) {
        this.displayName = displayName;
        this.primaryColor = primaryColor;
        this.ambientParticle = ambientParticle;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public String getAmbientParticle() {
        return ambientParticle;
    }

    /**
     * Resolves the theme for a given floor (1–100) using the tier boundaries
     * defined in {@code docs/world/FLOORS.md}.
     */
    public static BossTheme fromFloor(int floor) {
        if (floor >= 91) return VOID;
        if (floor >= 81) return SKY;
        if (floor >= 71) return OCEAN;
        if (floor >= 61) return SWAMP;
        if (floor >= 51) return DESERT;
        if (floor >= 41) return ICE;
        if (floor >= 31) return LAVA;
        if (floor >= 21) return CAVE;
        if (floor >= 11) return FOREST;
        return GRASSLAND;
    }

    /**
     * @return the theme key in lowercase, used for entity ids and config keys.
     */
    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }
}
