package com.aincrad.world;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Theme-based terrain generator for Aincrad floors.
 *
 * Generates 1024x1024 block floors enclosed by bedrock walls.
 * Terrain height varies by theme using simple hash-based noise.
 *
 * This is a skeleton implementation — full noise generation,
 * ore placement, and structure generation will be added later.
 */
public class FloorChunkGenerator extends ChunkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-floor-gen");

    private final FloorTheme theme;
    private final int floorNumber;

    public FloorChunkGenerator(BiomeSource biomeSource, FloorTheme theme, int floorNumber) {
        super(biomeSource);
        this.theme = theme;
        this.floorNumber = floorNumber;
        LOGGER.info("FloorChunkGenerator created for floor {} with theme {}", floorNumber, theme.getDisplayName());
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor world, RandomState randomState) {
        int groundHeight = computeTerrainHeight(x, z);
        return Math.max(1, Math.min(world.getHeight(), groundHeight));
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
        int columnHeight = computeTerrainHeight(x, z);
        BlockState[] states = new BlockState[columnHeight + 1];
        for (int y = 0; y < columnHeight; y++) {
            if (y < columnHeight - 4) {
                states[y] = Blocks.STONE.defaultBlockState();
            } else {
                states[y] = Blocks.DIRT.defaultBlockState();
            }
        }
        states[columnHeight] = getSurfaceBlock();
        return new NoiseColumn(columnHeight, states);
    }

    /**
     * Compute terrain height using a simple hash-based noise function.
     * Varies by theme to create distinct terrain profiles.
     */
    private int computeTerrainHeight(int x, int z) {
        double noise = hashNoise(x, z);
        int baseHeight = FloorDimension.GROUND_LEVEL;
        int amplitude = getThemeAmplitude();
        return (int) (baseHeight + (noise - 0.5) * 2 * amplitude);
    }

    /**
     * Returns the terrain amplitude (height variation) for this theme.
     */
    private int getThemeAmplitude() {
        return switch (theme) {
            case PLAINS -> 6;
            case FOREST -> 12;
            case CAVE -> 20;
            case LAVA -> 16;
            case ICE -> 8;
            case DESERT -> 10;
            case SWAMP -> 4;
            case OCEAN -> 14;
            case SKY -> 24;
            case VOID -> 30;
        };
    }

    /**
     * Returns the surface block type for this theme.
     */
    private BlockState getSurfaceBlock() {
        return switch (theme) {
            case PLAINS -> Blocks.GRASS_BLOCK.defaultBlockState();
            case FOREST -> Blocks.GRASS_BLOCK.defaultBlockState();
            case CAVE -> Blocks.STONE.defaultBlockState();
            case LAVA -> Blocks.BLACKSTONE.defaultBlockState();
            case ICE -> Blocks.SNOW_BLOCK.defaultBlockState();
            case DESERT -> Blocks.SAND.defaultBlockState();
            case SWAMP -> Blocks.MUD.defaultBlockState();
            case OCEAN -> Blocks.WATER.defaultBlockState();
            case SKY -> Blocks.SNOW_BLOCK.defaultBlockState();
            case VOID -> Blocks.OBSIDIAN.defaultBlockState();
        };
    }

    /**
     * Simple hash-based noise function.
     * Returns a value in [0, 1).
     */
    private double hashNoise(int x, int z) {
        long seed = (long) x * 374761393L + (long) z * 668265263L + floorNumber * 1442695040888963407L;
        seed = (seed ^ (seed >> 13)) * 1274126177L;
        seed = seed ^ (seed >> 16);
        return ((double) (seed & 0xFFFFFF) / 0xFFFFFF);
    }

    /**
     * Place bedrock walls at the 1024-block boundary.
     */
    public void buildBoundaryWalls(Level world, int chunkX, int chunkZ) {
        int minX = -FloorDimension.HALF_SIZE;
        int maxX = FloorDimension.HALF_SIZE - 1;
        int minZ = -FloorDimension.HALF_SIZE;
        int maxZ = FloorDimension.HALF_SIZE - 1;
        int wallTop = FloorDimension.GROUND_LEVEL + FloorDimension.WALL_HEIGHT;

        int blockX = chunkX * 16;
        int blockZ = chunkZ * 16;

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int wx = blockX + lx;
                int wz = blockZ + lz;
                if (wx == minX || wx == maxX || wz == minZ || wz == maxZ) {
                    for (int y = 0; y <= wallTop; y++) {
                        world.setBlock(new BlockPos(wx, y, wz), Blocks.BEDROCK.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    @Override
    public int getSeaLevel() {
        return FloorDimension.GROUND_LEVEL - 1;
    }

    @Override
    public void addDebugScreenInfo(java.util.List<String> info, net.minecraft.world.level.levelgen.RandomState randomState, net.minecraft.core.BlockPos pos, net.minecraft.world.level.levelgen.densityfunction.SamplerContext context) {
        // Debug info for floor chunk generator
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
    protected com.mojang.serialization.MapCodec<? extends ChunkGenerator> codec() {
        return com.mojang.serialization.MapCodec.unit(this);
    }

    @Override
    public void spawnOriginalMobs(net.minecraft.server.level.WorldGenRegion region) {
        // No mobs spawned by this generator
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }
}
