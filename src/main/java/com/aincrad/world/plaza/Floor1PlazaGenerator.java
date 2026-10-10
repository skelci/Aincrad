package com.aincrad.world.plaza;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chunk generator for the Floor 1 safe zone plaza.
 *
 * Generates a flat grass terrain in the central 256x256 block area,
 * surrounded by themed buildings (simple rectangular structures).
 *
 * Plaza layout (256x256, centered on 0,0):
 *   - Spawn point at center (0, 64, 0)
 *   - NPC positions marked with gold blocks
 *   - Market district: SE quadrant
 *   - Residential district: SW quadrant
 *   - Training district: NW quadrant
 *   - Portal district: NE quadrant (8 portal pads in a 2x4 grid)
 *
 * All buildings use simple rectangular shapes for skeleton implementation.
 */
public class Floor1PlazaGenerator extends ChunkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-plaza");

    // Plaza dimensions
    public static final int PLAZA_SIZE = 256;
    public static final int HALF_PLAZA = PLAZA_SIZE / 2; // 128
    public static final int GROUND_LEVEL = 64;
    public static final int WALL_HEIGHT = 8;

    // Portal pad configuration
    public static final int PORTAL_PADS_PER_ROW = 2;
    public static final int PORTAL_PADS_PER_COL = 4;
    public static final int PORTAL_SPACING = 12;

    // District quadrant bounds (inset from plaza edge)
    private static final int DISTRICT_INSET = 8;

    public Floor1PlazaGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        LOGGER.info("Floor1PlazaGenerator created with biome source: {}",
                biomeSource != null ? biomeSource.getClass().getSimpleName() : "null");
    }

    /**
     * Returns the base terrain height for the plaza area.
     * The entire plaza is flat at GROUND_LEVEL.
     */
    @Override
    public int getBaseHeight(int x, int z, net.minecraft.world.level.levelgen.Heightmap.Types type,
                             LevelHeightAccessor world,
                             net.minecraft.world.level.levelgen.RandomState randomState) {
        // Flat plaza — everything at ground level
        if (isInsidePlaza(x, z)) {
            return GROUND_LEVEL;
        }
        // Outside plaza: let normal generation handle it (return sea level as fallback)
        return GROUND_LEVEL;
    }

    /**
     * Returns a column of blocks for the given x,z position.
     * Inside the plaza, fills with grass on top and stone below.
     */
    @Override
    public NoiseColumn getBaseColumn(int x, int z,
                                     LevelHeightAccessor world,
                                     net.minecraft.world.level.levelgen.RandomState randomState) {
        if (isInsidePlaza(x, z)) {
            // Flat grass terrain: grass at GROUND_LEVEL, stone below
            BlockState[] column = new BlockState[GROUND_LEVEL + 1];
            for (int y = 0; y < GROUND_LEVEL; y++) {
                column[y] = Blocks.STONE.defaultBlockState();
            }
            column[GROUND_LEVEL] = Blocks.GRASS_BLOCK.defaultBlockState();
            return new NoiseColumn(GROUND_LEVEL, column);
        }
        return new NoiseColumn(GROUND_LEVEL, new BlockState[GROUND_LEVEL + 1]);
    }

    /**
     * Build the plaza structures into the given world at the specified chunk origin.
     * Called during chunk population.
     */
    public void buildPlaza(Level world, int chunkX, int chunkZ) {
        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;

        // Only generate plaza structures once (at chunk 0,0)
        if (chunkX == 0 && chunkZ == 0) {
            LOGGER.info("Building Floor 1 plaza at chunk origin ({}, {})", chunkX, chunkZ);

            // Ground leveling for entire plaza
            levelPlazaGround(world);

            // Spawn point marker at center
            buildSpawnMarker(world);

            // NPC positions (gold block markers)
            buildNPCMarkers(world);

            // District markers
            buildDistrictMarkers(world);

            // Market district buildings
            buildMarketDistrict(world);

            // Residential district buildings
            buildResidentialDistrict(world);

            // Training district structures
            buildTrainingDistrict(world);

            // Portal district with 8 portal pads
            buildPortalDistrict(world);

            // Border walls around plaza
            buildBorderWalls(world);

            LOGGER.info("Floor 1 plaza generation complete");
        }
    }

    /**
     * Fill the entire 256x256 plaza area with flat grass terrain.
     */
    private void levelPlazaGround(Level world) {
        for (int x = -HALF_PLAZA; x < HALF_PLAZA; x++) {
            for (int z = -HALF_PLAZA; z < HALF_PLAZA; z++) {
                // Clear above ground
                for (int y = GROUND_LEVEL + 1; y < GROUND_LEVEL + 32; y++) {
                    world.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
                // Grass surface
                world.setBlock(new BlockPos(x, GROUND_LEVEL, z),
                        Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                // Stone below
                for (int y = GROUND_LEVEL - 4; y < GROUND_LEVEL; y++) {
                    world.setBlock(new BlockPos(x, y, z), Blocks.STONE.defaultBlockState(), 3);
                }
            }
        }
    }

    /**
     * Build a marker at the spawn point (center of plaza).
     * Uses a small cobblestone platform with a beacon-like marker.
     */
    private void buildSpawnMarker(Level world) {
        BlockPos center = new BlockPos(0, GROUND_LEVEL, 0);

        // 3x3 cobblestone platform
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                world.setBlock(center.offset(dx, 1, dz),
                        Blocks.COBBLESTONE.defaultBlockState(), 3);
            }
        }

        // Center marker block
        world.setBlock(center.offset(0, 2, 0),
                Blocks.BEACON.defaultBlockState(), 3);

        LOGGER.info("Spawn marker placed at {}", center);
    }

    /**
     * Place NPC marker positions throughout the plaza.
     * Each marker is a gold block at ground level with a nameplate-like structure.
     */
    private void buildNPCMarkers(Level world) {
        int[][] npcPositions = {
                {10, 0, 0},    // East of spawn
                {-10, 0, 0},   // West of spawn
                {0, 0, 10},    // South of spawn
                {0, 0, -10},   // North of spawn
                {20, 0, 20},   // SE
                {-20, 0, 20},  // SW
                {20, 0, -20},  // NW
                {-20, 0, -20}, // NE
                {40, 0, 0},    // Far east
                {-40, 0, 0},   // Far west
                {0, 0, 40},    // Far south
                {0, 0, -40},   // Far north
        };

        for (int[] pos : npcPositions) {
            BlockPos npcPos = new BlockPos(pos[0], GROUND_LEVEL + 1, pos[2]);

            // Gold block marker
            world.setBlock(npcPos, Blocks.GOLD_BLOCK.defaultBlockState(), 3);

            // Simple frame above (iron posts)
            world.setBlock(npcPos.offset(0, 1, 0), Blocks.IRON_BLOCK.defaultBlockState(), 3);
            world.setBlock(npcPos.offset(0, 2, 0), Blocks.GOLD_BLOCK.defaultBlockState(), 3);

            LOGGER.debug("NPC marker placed at {}", npcPos);
        }
    }

    /**
     * Build colored marker blocks for each district at the quadrant corners.
     */
    private void buildDistrictMarkers(Level world) {
        int offset = HALF_PLAZA - DISTRICT_INSET;

        // SE: Market (emerald)
        placeDistrictMarker(world, offset, offset, Blocks.EMERALD_BLOCK, "Market District");

        // SW: Residential (lapis)
        placeDistrictMarker(world, -offset, offset, Blocks.LAPIS_BLOCK, "Residential District");

        // NW: Training (redstone)
        placeDistrictMarker(world, -offset, -offset, Blocks.REDSTONE_BLOCK, "Training District");

        // NE: Portal (diamond)
        placeDistrictMarker(world, offset, -offset, Blocks.DIAMOND_BLOCK, "Portal District");
    }

    private void placeDistrictMarker(Level world, int x, int z, Block block, String name) {
        world.setBlock(new BlockPos(x, GROUND_LEVEL + 1, z), block.defaultBlockState(), 3);
        LOGGER.debug("District marker '{}' at ({}, {})", name, x, z);
    }

    /**
     * Build simple rectangular buildings in the Market district (SE quadrant).
     */
    private void buildMarketDistrict(Level world) {
        int startX = DISTRICT_INSET;
        int endX = HALF_PLAZA - DISTRICT_INSET;
        int startZ = DISTRICT_INSET;
        int endZ = HALF_PLAZA - DISTRICT_INSET;

        // 4 market stalls (small 3x3 buildings)
        buildRectangularBuilding(world, startX, startZ, 3, 3, WALL_HEIGHT, Blocks.OAK_PLANKS);
        buildRectangularBuilding(world, startX + 10, startZ, 3, 3, WALL_HEIGHT, Blocks.OAK_PLANKS);
        buildRectangularBuilding(world, startX, startZ + 10, 3, 3, WALL_HEIGHT, Blocks.OAK_PLANKS);
        buildRectangularBuilding(world, startX + 10, startZ + 10, 3, 3, WALL_HEIGHT, Blocks.OAK_PLANKS);

        // 1 large market hall
        buildRectangularBuilding(world, startX + 20, startZ + 5, 8, 6, WALL_HEIGHT + 4, Blocks.BRICKS);

        LOGGER.info("Market district built in SE quadrant");
    }

    /**
     * Build simple rectangular buildings in the Residential district (SW quadrant).
     */
    private void buildResidentialDistrict(Level world) {
        int startX = -HALF_PLAZA + DISTRICT_INSET;
        int endZ = HALF_PLAZA - DISTRICT_INSET;

        // 6 houses in a 2x3 grid
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                int bx = startX + i * 15;
                int bz = DISTRICT_INSET + j * 20;
                buildRectangularBuilding(world, bx, bz, 5, 7, WALL_HEIGHT, Blocks.COBBLESTONE);
                // Roof
                buildFlatRoof(world, bx, bz, 5, 7, WALL_HEIGHT + 1, Blocks.SPRUCE_PLANKS);
            }
        }

        LOGGER.info("Residential district built in SW quadrant");
    }

    /**
     * Build training structures in the Training district (NW quadrant).
     */
    private void buildTrainingDistrict(Level world) {
        // Large open training arena (walls only, no roof)
        int arenaX = -HALF_PLAZA + DISTRICT_INSET + 5;
        int arenaZ = -HALF_PLAZA + DISTRICT_INSET + 5;
        buildRectangularWalls(world, arenaX, arenaZ, 12, 12, WALL_HEIGHT + 2, Blocks.STONE_BRICKS);

        // Training dummies (small posts)
        for (int i = 0; i < 4; i++) {
            world.setBlock(new BlockPos(arenaX + 3 + i * 2, GROUND_LEVEL + 1, arenaZ + 6),
                    Blocks.HAY_BLOCK.defaultBlockState(), 3);
        }

        LOGGER.info("Training district built in NW quadrant");
    }

    /**
     * Build the Portal district (NE quadrant) with 8 portal pads.
     *
     * Layout: 2x4 grid of portal pads, each with a distinct color.
     * Each pad is a 5x5 platform with a portal frame marker.
     */
    private void buildPortalDistrict(Level world) {
        int baseX = HALF_PLAZA - DISTRICT_INSET - 30;
        int baseZ = -HALF_PLAZA + DISTRICT_INSET + 5;

        // 8 portal pads in a 2x4 grid
        int padIndex = 0;
        for (int row = 0; row < PORTAL_PADS_PER_COL; row++) {
            for (int col = 0; col < PORTAL_PADS_PER_ROW; col++) {
                int padX = baseX + col * PORTAL_SPACING;
                int padZ = baseZ + row * PORTAL_SPACING;

                buildPortalPad(world, padX, padZ, padIndex + 2); // Floor 2-9
                padIndex++;
            }
        }

        LOGGER.info("Portal district built with 8 portal pads in NE quadrant");
    }

    /**
     * Build a single portal pad: 5x5 platform with a frame marker.
     */
    private void buildPortalPad(Level world, int x, int z, int targetFloor) {
        // Stone platform (5x5)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                world.setBlock(new BlockPos(x + dx, GROUND_LEVEL, z + dz),
                        Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }

        // Corner posts (obsidian)
        int h = 3;
        world.setBlock(new BlockPos(x - 2, GROUND_LEVEL + 1, z - 2), Blocks.OBSIDIAN.defaultBlockState(), 3);
        world.setBlock(new BlockPos(x - 2, GROUND_LEVEL + 1, z - 2).above(h - 1), Blocks.OBSIDIAN.defaultBlockState(), 3);
        world.setBlock(new BlockPos(x + 2, GROUND_LEVEL + 1, z - 2), Blocks.OBSIDIAN.defaultBlockState(), 3);
        world.setBlock(new BlockPos(x + 2, GROUND_LEVEL + 1, z - 2).above(h - 1), Blocks.OBSIDIAN.defaultBlockState(), 3);
        world.setBlock(new BlockPos(x - 2, GROUND_LEVEL + 1, z + 2), Blocks.OBSIDIAN.defaultBlockState(), 3);
        world.setBlock(new BlockPos(x - 2, GROUND_LEVEL + 1, z + 2).above(h - 1), Blocks.OBSIDIAN.defaultBlockState(), 3);
        world.setBlock(new BlockPos(x + 2, GROUND_LEVEL + 1, z + 2), Blocks.OBSIDIAN.defaultBlockState(), 3);
        world.setBlock(new BlockPos(x + 2, GROUND_LEVEL + 1, z + 2).above(h - 1), Blocks.OBSIDIAN.defaultBlockState(), 3);

        // Floor indicator at center
        world.setBlock(new BlockPos(x, GROUND_LEVEL + 1, z),
                Blocks.GOLD_BLOCK.defaultBlockState(), 3);

        LOGGER.debug("Portal pad for floor {} built at ({}, {})", targetFloor, x, z);
    }

    /**
     * Build border walls around the entire plaza perimeter.
     */
    private void buildBorderWalls(Level world) {
        int h = 12;
        for (int x = -HALF_PLAZA; x <= HALF_PLAZA; x++) {
            // North and South walls
            for (int y = GROUND_LEVEL + 1; y <= GROUND_LEVEL + h; y++) {
                world.setBlock(new BlockPos(x, y, -HALF_PLAZA),
                        Blocks.STONE_BRICKS.defaultBlockState(), 3);
                world.setBlock(new BlockPos(x, y, HALF_PLAZA),
                        Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }
        for (int z = -HALF_PLAZA; z <= HALF_PLAZA; z++) {
            // East and West walls
            for (int y = GROUND_LEVEL + 1; y <= GROUND_LEVEL + h; y++) {
                world.setBlock(new BlockPos(-HALF_PLAZA, y, z),
                        Blocks.STONE_BRICKS.defaultBlockState(), 3);
                world.setBlock(new BlockPos(HALF_PLAZA, y, z),
                        Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }

        LOGGER.info("Border walls built around plaza perimeter");
    }

    // --- Helper methods for building structures ---

    /**
     * Build a solid rectangular building of the given dimensions.
     */
    private void buildRectangularBuilding(Level world, int originX, int originZ,
                                          int width, int depth, int height, Block material) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // Floor
                world.setBlock(new BlockPos(originX + x, GROUND_LEVEL, originZ + z),
                        Blocks.STONE.defaultBlockState(), 3);
                // Walls
                for (int y = 1; y <= height; y++) {
                    boolean isWall = (x == 0 || x == width - 1 || z == 0 || z == depth - 1);
                    if (isWall) {
                        world.setBlock(new BlockPos(originX + x, GROUND_LEVEL + y, originZ + z),
                                material.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    /**
     * Build rectangular walls only (no floor or ceiling).
     */
    private void buildRectangularWalls(Level world, int originX, int originZ,
                                       int width, int depth, int height, Block material) {
        for (int y = 1; y <= height; y++) {
            for (int x = 0; x < width; x++) {
                world.setBlock(new BlockPos(originX + x, GROUND_LEVEL + y, originZ),
                        material.defaultBlockState(), 3);
                world.setBlock(new BlockPos(originX + x, GROUND_LEVEL + y, originZ + depth - 1),
                        material.defaultBlockState(), 3);
            }
            for (int z = 1; z < depth - 1; z++) {
                world.setBlock(new BlockPos(originX, GROUND_LEVEL + y, originZ + z),
                        material.defaultBlockState(), 3);
                world.setBlock(new BlockPos(originX + width - 1, GROUND_LEVEL + y, originZ + z),
                        material.defaultBlockState(), 3);
            }
        }
    }

    /**
     * Build a flat roof on top of a rectangular building.
     */
    private void buildFlatRoof(Level world, int originX, int originZ,
                               int width, int depth, int y, Block material) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                world.setBlock(new BlockPos(originX + x, y, originZ + z),
                        material.defaultBlockState(), 3);
            }
        }
    }

    /**
     * Check if a given block position is inside the plaza bounds.
     */
    public static boolean isInsidePlaza(int x, int z) {
        return x >= -HALF_PLAZA && x < HALF_PLAZA &&
               z >= -HALF_PLAZA && z < HALF_PLAZA;
    }

    /**
     * Get the spawn position for the plaza (center point).
     */
    public static BlockPos getSpawnPos() {
        return new BlockPos(0, GROUND_LEVEL + 1, 0);
    }

    // --- ChunkGenerator abstract method stubs ---

    @Override
    protected com.mojang.serialization.MapCodec<? extends ChunkGenerator> codec() {
        // Return a dummy codec — full implementation requires proper serialization
        return com.mojang.serialization.MapCodec.unit(this);
    }

    @Override
    public void spawnOriginalMobs(net.minecraft.server.level.WorldGenRegion region) {
        // No mobs in the plaza safe zone
    }

    @Override
    public int getGenDepth() {
        return 384; // Standard world depth
    }

    @Override
    public int getSeaLevel() {
        return GROUND_LEVEL - 1;
    }

    @Override
    public void addDebugScreenInfo(java.util.List<String> info, net.minecraft.world.level.levelgen.RandomState randomState, net.minecraft.core.BlockPos pos, net.minecraft.world.level.levelgen.densityfunction.SamplerContext context) {
        // Debug info for plaza generator
    }

    @Override
    public java.util.concurrent.CompletableFuture<net.minecraft.world.level.chunk.ChunkAccess> buildTerrain(
            net.minecraft.world.level.chunk.ChunkAccess chunk,
            net.minecraft.world.level.levelgen.blending.Blender blender,
            net.minecraft.world.level.levelgen.RandomState randomState,
            net.minecraft.world.level.StructureManager structureManager,
            net.minecraft.world.level.biome.BiomeManager biomeManager,
            net.minecraft.server.level.WorldGenRegion region,
            java.util.Set<net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome>> biomes) {
        return java.util.concurrent.CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getMinY() {
        return 0;
    }
}
