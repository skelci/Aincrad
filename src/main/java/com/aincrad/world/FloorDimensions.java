package com.aincrad.world;

import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * Central registry for all 100 Aincrad floor dimensions.
 * Each floor is a 1024x1024 block area with a 20-block wall height.
 */
public class FloorDimensions {

    public static final int FLOOR_COUNT = 100;
    public static final int FLOOR_SIZE = 1024;
    public static final int HALF_SIZE = 512;
    public static final int WALL_HEIGHT = 20;
    public static final int GROUND_LEVEL = 64;

    private static final String FLOOR_PREFIX = "aincrad:floor_";

    /**
     * Registers all 100 floor dimensions.
     * In a full implementation this would be called during mod initialization
     * to register each floor's DimensionOptions with the game's registry.
     */
    public static void registerAll() {
        for (int floor = 1; floor <= FLOOR_COUNT; floor++) {
            ResourceKey<Level> worldKey = getFloorKey(floor);
            ResourceKey<DimensionType> dimensionKey = getDimensionKey(floor);

            // In a full implementation, DimensionOptions would be created and registered:
            // DimensionOptions options = new DimensionOptions(dimensionType, chunkGenerator);
            // Registry.register(BuiltinRegistries.DIMENSION, worldKey.getValue(), options);
            // For now, we validate the keys are properly constructed.
            if (worldKey == null || dimensionKey == null) {
                throw new IllegalStateException("Failed to create registry keys for floor " + floor);
            }
        }
    }

    /**
     * Returns the ResourceKey<Level> for the given floor number.
     * @param floor Floor number (1-100)
     * @return RegistryKey identifying this floor's Level
     */
    public static ResourceKey<Level> getFloorKey(int floor) {
        validateFloor(floor);
        return ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("aincrad", "floor_" + floor));
    }

    /**
     * Returns the ResourceKey<DimensionType> for the given floor number.
     * @param floor Floor number (1-100)
     * @return RegistryKey identifying this floor's DimensionType
     */
    public static ResourceKey<DimensionType> getDimensionKey(int floor) {
        validateFloor(floor);
        return ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor_" + floor));
    }

    /**
     * Returns the boss name for the given floor.
     * @param floor Floor number (1-100)
     * @return Boss name string
     */
    public static String getBossName(int floor) {
        validateFloor(floor);
        return switch (floor) {
            case 1 -> "The Gleam Eyes";
            case 2 -> "The Red-Nosed Reindeer";
            case 3 -> "The Nannygoat";
            case 4 -> "The Whiskered Thunder";
            case 5 -> "The Crimson Killing Intent";
            case 6 -> "The Coil of the Deep";
            case 7 -> "The Imp of the Abyss";
            case 8 -> "The Golem of the Caves";
            case 9 -> "The Serpent of the Maze";
            case 10 -> "The Minotaur of the Labyrinth";
            case 11 -> "The Arachne of the Web";
            case 12 -> "The Gargoyle of the Tower";
            case 13 -> "The Sphinx of the Sands";
            case 14 -> "The Chimera of the Flames";
            case 15 -> "The Hydra of the Lake";
            case 16 -> "The Cyclops of the Forge";
            case 17 -> "The Medusa of the Marsh";
            case 18 -> "The Kraken of the Deep";
            case 19 -> "The Phoenix of the Sun";
            case 20 -> "The Dragon of the Mountain";
            case 21 -> "The Basilisk of the Swamp";
            case 22 -> "The Manticore of the Desert";
            case 23 -> "The Siren of the Sea";
            case 24 -> "The Werewolf of the Forest";
            case 25 -> "The Vampire of the Night";
            case 26 -> "The Lich of the Crypt";
            case 27 -> "The Banshee of the Moors";
            case 28 -> "The Djinn of the Lamp";
            case 29 -> "The Efreet of the Flame";
            case 30 -> "The Marid of the Waters";
            case 31 -> "The Ghoul of the Graveyard";
            case 32 -> "The Wraith of the Shadows";
            case 33 -> "The Revenant of the Ruins";
            case 34 -> "The Specter of the Spire";
            case 35 -> "The Phantom of the Palace";
            case 36 -> "The Apparition of the Abyss";
            case 37 -> "The Shade of the Shroud";
            case 38 -> "The Wisp of the Willows";
            case 39 -> "The Sprite of the Spring";
            case 40 -> "The Nymph of the Narrows";
            case 41 -> "The Dryad of the Dell";
            case 42 -> "The Naiad of the Nether";
            case 43 -> "The Oread of the Ores";
            case 44 -> "The Limoniad of the Limestone";
            case 45 -> "The Muse of the Mists";
            case 46 -> "The Fate of the Falls";
            case 47 -> "The Sphinx of the Stars";
            case 48 -> "The Oracle of the Oasis";
            case 49 -> "The Prophet of the Peak";
            case 50 -> "The Seer of the Sands";
            case 51 -> "The Sage of the Summit";
            case 52 -> "The Mystic of the Marsh";
            case 53 -> "The Warlock of the Wastes";
            case 54 -> "The Witch of the Woods";
            case 55 -> "The Sorcerer of the Swamp";
            case 56 -> "The Necromancer of the Nether";
            case 57 -> "The Enchanter of the Ember";
            case 58 -> "The Conjurer of the Caverns";
            case 59 -> "The Summoner of the Spire";
            case 60 -> "The Evoker of the Ember";
            case 61 -> "The Alchemist of the Abyss";
            case 62 -> "The Transmuter of the Tides";
            case 63 -> "The Diviner of the Deep";
            case 64 -> "The Augur of the Ash";
            case 65 -> "The Haruspex of the Hollow";
            case 66 -> "The Hierophant of the Heights";
            case 67 -> "The Pontiff of the Pit";
            case 68 -> "The Cardinal of the Crags";
            case 69 -> "The Bishop of the Bog";
            case 70 -> "The Archbishop of the Abyss";
            case 71 -> "The Paladin of the Peak";
            case 72 -> "The Crusader of the Crags";
            case 73 -> "The Templar of the Tides";
            case 74 -> "The Knight of the Nether";
            case 75 -> "The Cavalier of the Caverns";
            case 76 -> "The Dragoon of the Deep";
            case 77 -> "The Lancer of the Limestone";
            case 78 -> "The Halberdier of the Hollow";
            case 79 -> "The Pikeman of the Pit";
            case 80 -> "The Swordsman of the Spire";
            case 81 -> "The Berserker of the Bog";
            case 82 -> "The Brawler of the Brine";
            case 83 -> "The Gladiator of the Gulf";
            case 84 -> "The Champion of the Chasm";
            case 85 -> "The Warlord of the Wastes";
            case 86 -> "The Overlord of the Ores";
            case 87 -> "The Emperor of the Ember";
            case 88 -> "The King of the Keep";
            case 89 -> "The Queen of the Quarry";
            case 90 -> "The Regent of the Ruins";
            case 91 -> "The Viceroy of the Vale";
            case 92 -> "The Governor of the Gulf";
            case 93 -> "The Magistrate of the Marsh";
            case 94 -> "The Warden of the Woods";
            case 95 -> "The Keeper of the Keep";
            case 96 -> "The Guardian of the Gulf";
            case 97 -> "The Protector of the Peak";
            case 98 -> "The Defender of the Deep";
            case 99 -> "The Sentinel of the Spire";
            case 100 -> "The Final Boss of Aincrad";
            default -> "Unknown Boss";
        };
    }

    /**
     * Validates that the floor number is within the valid range.
     */
    private static void validateFloor(int floor) {
        if (floor < 1 || floor > FLOOR_COUNT) {
            throw new IllegalArgumentException("Floor must be between 1 and " + FLOOR_COUNT + ", got: " + floor);
        }
    }
}
