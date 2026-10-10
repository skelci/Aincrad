package com.aincrad.world;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton registry that creates and manages all 100 FloorDimension instances.
 *
 * Floor-to-theme mapping:
 *   Floors 1-10:   PLAINS
 *   Floors 11-20:  FOREST
 *   Floors 21-30:  CAVE
 *   Floors 31-40:  LAVA
 *   Floors 41-50:  ICE
 *   Floors 51-60:  DESERT
 *   Floors 61-70:  SWAMP
 *   Floors 71-80:  OCEAN
 *   Floors 81-90:  SKY
 *   Floors 91-100: VOID
 */
public class FloorRegistry {

    private static FloorRegistry instance;
    private final Map<Integer, FloorDimension> floors;

    private FloorRegistry() {
        this.floors = new HashMap<>();
        init();
    }

    public static synchronized FloorRegistry getInstance() {
        if (instance == null) {
            instance = new FloorRegistry();
        }
        return instance;
    }

    /**
     * Initialize all 100 floor dimensions.
     */
    private void init() {
        for (int i = 1; i <= 100; i++) {
            int tier = getTierForFloor(i);
            FloorTheme theme = getThemeForFloor(i);
            String bossName = FloorDimensions.getBossName(i);
            boolean safeZone = (i == 1);
            floors.put(i, new FloorDimension(i, tier, theme, bossName, safeZone));
        }
    }

    /**
     * Returns the tier (1-10) for a given floor.
     */
    public static int getTierForFloor(int floor) {
        if (floor < 1 || floor > 100) {
            throw new IllegalArgumentException("Floor must be 1-100, got: " + floor);
        }
        return ((floor - 1) / 10) + 1;
    }

    /**
     * Returns the theme for a given floor.
     */
    public static FloorTheme getThemeForFloor(int floor) {
        int tier = getTierForFloor(floor);
        return switch (tier) {
            case 1 -> FloorTheme.PLAINS;
            case 2 -> FloorTheme.FOREST;
            case 3 -> FloorTheme.CAVE;
            case 4 -> FloorTheme.LAVA;
            case 5 -> FloorTheme.ICE;
            case 6 -> FloorTheme.DESERT;
            case 7 -> FloorTheme.SWAMP;
            case 8 -> FloorTheme.OCEAN;
            case 9 -> FloorTheme.SKY;
            case 10 -> FloorTheme.VOID;
            default -> FloorTheme.PLAINS;
        };
    }

    /**
     * Returns the FloorDimension for the given floor number.
     */
    public FloorDimension getFloor(int floorNumber) {
        return floors.get(floorNumber);
    }

    /**
     * Returns the total number of floors.
     */
    public int getTotalFloors() {
        return floors.size();
    }

    /**
     * Returns true if the given floor number is valid (1-100).
     */
    public static boolean isValidFloor(int floor) {
        return floor >= 1 && floor <= 100;
    }
}
