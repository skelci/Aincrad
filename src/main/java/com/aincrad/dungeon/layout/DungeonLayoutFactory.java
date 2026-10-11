package com.aincrad.dungeon.layout;

import com.aincrad.dungeon.DungeonTier;
import com.aincrad.dungeon.layout.cave.*;
import com.aincrad.dungeon.layout.crystal.*;
import com.aincrad.dungeon.layout.desert.*;
import com.aincrad.dungeon.layout.forest.*;
import com.aincrad.dungeon.layout.frozenlake.*;
import com.aincrad.dungeon.layout.ice.*;
import com.aincrad.dungeon.layout.mountain.*;
import com.aincrad.dungeon.layout.oasis.*;
import com.aincrad.dungeon.layout.ocean.*;
import com.aincrad.dungeon.layout.ruins.*;
import com.aincrad.dungeon.layout.sandtemple.*;
import com.aincrad.dungeon.layout.snow.*;
import com.aincrad.dungeon.layout.swamp.*;
import com.aincrad.dungeon.layout.voidlayout.*;
import com.aincrad.dungeon.layout.volcano.*;

/**
 * Factory that creates DungeonLayout instances by floor number.
 * Each floor number (1-5) maps to a specific theme and tier combination.
 *
 * Floor ranges:
 *   1-5:   Forest (T1_EASY), Cave (T2_NORMAL), Ice (T3_HARD), Snow (T4_EXPERT), Volcano (T5_MYTHIC)
 *   6-10:  Void (T1_EASY), Ocean (T2_NORMAL), Mountain (T3_HARD), Swamp (T4_EXPERT), Crystal (T5_MYTHIC)
 */
public final class DungeonLayoutFactory {

    private DungeonLayoutFactory() {
        // Utility class
    }

    /**
     * Creates a DungeonLayout instance for the given floor number.
     *
     * @param floor the floor number (1-10)
     * @return a new DungeonLayout instance
     * @throws IllegalArgumentException if floor is not in range 1-10
     */
    public static DungeonLayout createLayout(int floor) {
        if (floor < 1 || floor > 10) {
            throw new IllegalArgumentException("Floor must be between 1 and 10, got: " + floor);
        }

        int themeIndex = (floor - 1) / 5;  // 0 or 1
        int floorInTheme = ((floor - 1) % 5) + 1;  // 1-5

        if (themeIndex == 0) {
            return createTheme1Layout(floorInTheme);
        } else {
            return createTheme2Layout(floorInTheme);
        }
    }

    private static DungeonLayout createTheme1Layout(int floor) {
        return switch (floor) {
            case 1 -> new ForestFloor1Layout();
            case 2 -> new CaveFloor1Layout();
            case 3 -> new IceFloor1Layout();
            case 4 -> new SnowFloor1Layout();
            case 5 -> new VolcanoFloor1Layout();
            default -> throw new IllegalArgumentException("Invalid floor: " + floor);
        };
    }

    private static DungeonLayout createTheme2Layout(int floor) {
        return switch (floor) {
            case 1 -> new VoidFloor1Layout();
            case 2 -> new OceanFloor1Layout();
            case 3 -> new MountainFloor1Layout();
            case 4 -> new SwampFloor1Layout();
            case 5 -> new CrystalFloor1Layout();
            default -> throw new IllegalArgumentException("Invalid floor: " + floor);
        };
    }

    /**
     * Returns the tier for the given floor number.
     */
    public static DungeonTier getTierForFloor(int floor) {
        if (floor < 1 || floor > 10) {
            throw new IllegalArgumentException("Floor must be between 1 and 10, got: " + floor);
        }

        int tierIndex = (floor - 1) % 5;  // 0-4
        return switch (tierIndex) {
            case 0 -> DungeonTier.T1_EASY;
            case 1 -> DungeonTier.T2_NORMAL;
            case 2 -> DungeonTier.T3_HARD;
            case 3 -> DungeonTier.T4_EXPERT;
            case 4 -> DungeonTier.T5_MYTHIC;
            default -> throw new IllegalStateException("Unexpected tier index: " + tierIndex);
        };
    }

    /**
     * Returns the theme name for the given floor number.
     */
    public static String getThemeForFloor(int floor) {
        if (floor < 1 || floor > 10) {
            throw new IllegalArgumentException("Floor must be between 1 and 10, got: " + floor);
        }

        int themeIndex = (floor - 1) / 5;
        int tierIndex = (floor - 1) % 5;

        if (themeIndex == 0) {
            return switch (tierIndex) {
                case 0 -> "Forest";
                case 1 -> "Cave";
                case 2 -> "Ice";
                case 3 -> "Snow";
                case 4 -> "Volcano";
                default -> throw new IllegalStateException("Unexpected tier index: " + tierIndex);
            };
        } else {
            return switch (tierIndex) {
                case 0 -> "Void";
                case 1 -> "Ocean";
                case 2 -> "Mountain";
                case 3 -> "Swamp";
                case 4 -> "Crystal";
                default -> throw new IllegalStateException("Unexpected tier index: " + tierIndex);
            };
        }
    }
}
