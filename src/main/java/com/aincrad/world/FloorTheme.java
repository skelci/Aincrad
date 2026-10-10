package com.aincrad.world;

/**
 * The 10 floor themes in Aincrad, one per tier.
 */
public enum FloorTheme {
    PLAINS("Plains", "GRASSLAND"),
    FOREST("Forest", "FOREST"),
    CAVE("Cave", "UNDERGROUND"),
    LAVA("Lava", "VOLCANIC"),
    ICE("Ice", "FROZEN"),
    DESERT("Desert", "ARID"),
    SWAMP("Swamp", "WETLAND"),
    OCEAN("Ocean", "AQUATIC"),
    SKY("Sky", "CELESTIAL"),
    VOID("Void", "ENDER");

    private final String displayName;
    private final String biomeCategory;

    FloorTheme(String displayName, String biomeCategory) {
        this.displayName = displayName;
        this.biomeCategory = biomeCategory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBiomeCategory() {
        return biomeCategory;
    }
}
