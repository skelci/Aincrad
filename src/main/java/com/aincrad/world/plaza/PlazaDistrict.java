package com.aincrad.world.plaza;

/**
 * Enum representing the districts within the Floor 1 plaza.
 *
 * Each district has a display name and a relative position within the
 * 256x256 plaza area. Positions are quadrant-based:
 *   - Positive X = East, Positive Z = South
 *   - Quadrant centers are at roughly ±64, ±64
 */
public enum PlazaDistrict {

    /** Spawn district — center of plaza, safe zone entry point. */
    SPAWN("Spawn District", 0, 0),

    /** Market district — SE quadrant, shops and trading. */
    MARKET("Market District", 64, 64),

    /** Residential district — SW quadrant, player housing. */
    RESIDENTIAL("Residential District", -64, 64),

    /** Training district — NW quadrant, combat practice area. */
    TRAINING("Training District", -64, -64),

    /** Portal district — NE quadrant, floor teleportation hub. */
    PORTAL("Portal District", 64, -64);

    private final String displayName;
    private final int relativeX;
    private final int relativeZ;

    PlazaDistrict(String displayName, int relativeX, int relativeZ) {
        this.displayName = displayName;
        this.relativeX = relativeX;
        this.relativeZ = relativeZ;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return relative X position within the plaza (East = positive)
     */
    public int getRelativeX() {
        return relativeX;
    }

    /**
     * @return relative Z position within the plaza (South = positive)
     */
    public int getRelativeZ() {
        return relativeZ;
    }

    /**
     * Returns the color associated with this district for map markers.
     */
    public int getMapColor() {
        return switch (this) {
            case SPAWN -> 0xFFFFFF;     // White
            case MARKET -> 0x00FF00;    // Green
            case RESIDENTIAL -> 0x0000FF; // Blue
            case TRAINING -> 0xFF0000;   // Red
            case PORTAL -> 0xFF00FF;     // Magenta
        };
    }

    @Override
    public String toString() {
        return displayName + " (" + relativeX + ", " + relativeZ + ")";
    }
}
