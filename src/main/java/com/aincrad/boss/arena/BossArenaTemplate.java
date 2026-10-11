package com.aincrad.boss.arena;

import com.aincrad.boss.theme.BossTheme;

/**
 * Pre-built arena configuration for a given floor.
 * Each template defines arena size, floor/wall blocks, ambient effects,
 * and whether it is a raid arena.
 */
public record BossArenaTemplate(
    String name,
    int size,
    String floorBlock,
    String wallBlock,
    String ambientEffect,
    boolean isRaid
) {
    /**
     * Returns the arena template for a given floor.
     *
     * @param floor the floor number (1–100)
     * @return the appropriate {@link BossArenaTemplate}
     */
    public static BossArenaTemplate getTemplateForFloor(int floor) {
        BossTheme theme = BossTheme.fromFloor(floor);
        boolean isRaid = (floor % 25 == 0);

        int size = isRaid ? 40 + (floor / 25) * 10 : 30;
        String floorBlock = getFloorBlock(theme);
        String wallBlock = getWallBlock(theme);

        return new BossArenaTemplate(
            theme.getDisplayName() + " Arena",
            size,
            floorBlock,
            wallBlock,
            theme.getAmbientParticle(),
            isRaid
        );
    }

    private static String getFloorBlock(BossTheme theme) {
        return switch (theme) {
            case GRASSLAND -> "minecraft:grass_block";
            case FOREST -> "minecraft:podzol";
            case CAVE -> "minecraft:deepslate";
            case LAVA -> "minecraft:obsidian";
            case ICE -> "minecraft:packed_ice";
            case DESERT -> "minecraft:sandstone";
            case SWAMP -> "minecraft:mud";
            case OCEAN -> "minecraft:prismarine";
            case SKY -> "minecraft:end_stone";
            case VOID -> "minecraft:blackstone";
        };
    }

    private static String getWallBlock(BossTheme theme) {
        return switch (theme) {
            case GRASSLAND -> "minecraft:oak_log";
            case FOREST -> "minecraft:dark_oak_log";
            case CAVE -> "minecraft:stone";
            case LAVA -> "minecraft:netherrack";
            case ICE -> "minecraft:blue_ice";
            case DESERT -> "minecraft:chiseled_sandstone";
            case SWAMP -> "minecraft:mangrove_log";
            case OCEAN -> "minecraft:dark_prismarine";
            case SKY -> "minecraft:purpur_block";
            case VOID -> "minecraft:obsidian";
        };
    }
}
