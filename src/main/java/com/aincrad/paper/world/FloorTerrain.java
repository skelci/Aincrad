package com.aincrad.paper.world;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

/**
 * Deterministic surface decoration for floor worlds outside the town wall.
 * Everything sits within the central 10x10 of each chunk so nothing bleeds
 * across chunk borders. Same seed = same world.
 */
public final class FloorTerrain {

    private static final int TOWN_SKIP = 136;

    private static final Material[][] TREE_TYPES = {
            {Material.OAK_LOG, Material.OAK_LEAVES},
            {Material.BIRCH_LOG, Material.BIRCH_LEAVES},
            {Material.SPRUCE_LOG, Material.SPRUCE_LEAVES},
            {Material.ACACIA_LOG, Material.ACACIA_LEAVES},
            {Material.JUNGLE_LOG, Material.JUNGLE_LEAVES},
            {Material.DARK_OAK_LOG, Material.DARK_OAK_LEAVES}
    };

    private static final Material[] MEADOW_FLOWERS = {
            Material.POPPY, Material.DANDELION, Material.CORNFLOWER,
            Material.OXEYE_DAISY, Material.ALLIUM, Material.AZURE_BLUET,
            Material.PINK_TULIP, Material.WHITE_TULIP, Material.ORANGE_TULIP,
            Material.RED_TULIP, Material.BLUE_ORCHID
    };

    private FloorTerrain() {
    }

    private static final org.bukkit.NamespacedKey DECORATED =
            new org.bukkit.NamespacedKey("aincrad", "terrain_decorated");

    public static void decorate(Chunk chunk) {
        World w = chunk.getWorld();
        var pdc = chunk.getPersistentDataContainer();
        if (pdc.has(DECORATED, org.bukkit.persistence.PersistentDataType.BYTE)) return;
        int cx = chunk.getX() * 16 + 8;
        int cz = chunk.getZ() * 16 + 8;
        if (Math.hypot(cx, cz) < TOWN_SKIP) return;
        if (FloorDungeons.nearDungeon(w, cx, cz, 40)) return;

        terraform(chunk);

        long seed = w.getSeed();
        Random r = seeded(seed, chunk.getX(), chunk.getZ());
        int zone = zoneAt(seed, cx, cz);
        int floor = parseFloor(w.getName());
        double harshness = Math.min(1.0, floor / 50.0);

        switch (zone) {
            case 0 -> forest(r, w, cx, cz, harshness);
            case 1 -> meadow(r, w, cx, cz);
            case 2 -> rocky(r, w, cx, cz, harshness);
            default -> wetland(r, w, cx, cz);
        }

        if (Math.hypot(cx, cz) > 160 && r.nextInt(90) == 0) {
            ruin(r, w, cx, cz);
        }
        pdc.set(DECORATED, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
    }

    private static int surf(World w, int x, int z) {
        int y = w.getHighestBlockYAt(x, z);
        return y < 60 ? 65 : y;
    }

    private static int zoneAt(long seed, int x, int z) {
        long h = mix(seed ^ 0x9E3779B97F4A7C15L, x >> 5, z >> 5);
        return (int) (Math.abs(h) % 4);
    }

    private static void forest(Random r, World w, int cx, int cz, double harsh) {
        int trees = 3 + r.nextInt(4) - (int) (harsh * 2);
        for (int i = 0; i < trees; i++) {
            int x = cx + r.nextInt(9) - 4;
            int z = cz + r.nextInt(9) - 4;
            if (treeAlreadyAt(w, x, z)) continue;
            tree(w, x, z, r);
        }
        scatterMixed(w, r, cx, cz, 6, MEADOW_FLOWERS);
        scatter(w, r, cx, cz, 4, Material.RED_MUSHROOM);
        if (r.nextInt(4) == 0) logPile(w, r, cx, cz);
    }

    private static void meadow(Random r, World w, int cx, int cz) {
        scatter(w, r, cx, cz, 12, Material.TALL_GRASS);
        scatterMixed(w, r, cx, cz, 14, MEADOW_FLOWERS);
        if (r.nextInt(6) == 0) {
            int x = cx + r.nextInt(6) - 3;
            int z = cz + r.nextInt(6) - 3;
            if (!treeAlreadyAt(w, x, z)) tree(w, x, z, r);
        }
    }

    private static void rocky(Random r, World w, int cx, int cz, double harsh) {
        int boulders = 2 + r.nextInt(3) + (int) (harsh * 3);
        for (int i = 0; i < boulders; i++) {
            int x = cx + r.nextInt(9) - 4, z = cz + r.nextInt(9) - 4;
            int s0 = surf(w, x, z);
            int size = 1 + r.nextInt(3);
            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > size) continue;
                    Material m = r.nextInt(4) == 0 ? Material.MOSSY_COBBLESTONE
                            : (r.nextBoolean() ? Material.STONE : Material.ANDESITE);
                    set(w, x + dx, s0 + 1, z + dz, m);
                }
            }
            set(w, x, s0 + 2, z, r.nextInt(6) == 0 ? Material.COAL_ORE : Material.STONE);
        }
        scatter(w, r, cx, cz, 4, Material.STONE);
        scatter(w, r, cx, cz, 3, Material.DEAD_BUSH);
        if (r.nextInt(3) == 0) {
            int x = cx + r.nextInt(6) - 3, z = cz + r.nextInt(6) - 3;
            int s0 = surf(w, x, z);
            for (int y = 1; y <= 3; y++) set(w, x, s0 + y, z, Material.ANDESITE_WALL);
        }
    }

    private static void wetland(Random r, World w, int cx, int cz) {
        scatter(w, r, cx, cz, 6, Material.DEAD_BUSH);
        scatter(w, r, cx, cz, 5, Material.FERN);
        scatterMixed(w, r, cx, cz, 6, new Material[]{Material.BLUE_ORCHID,
                Material.ALLIUM, Material.TALL_GRASS});
        if (r.nextInt(5) != 0) return;
        int x = cx + r.nextInt(4) - 2, z = cz + r.nextInt(4) - 2;
        int s0 = surf(w, x, z);
        int rad = 2 + r.nextInt(2);
        for (int dx = -rad - 1; dx <= rad + 1; dx++) {
            for (int dz = -rad - 1; dz <= rad + 1; dz++) {
                int d = Math.abs(dx) + Math.abs(dz);
                if (d <= rad) {
                    set(w, x + dx, s0, z + dz, Material.WATER);
                } else if (d == rad + 1) {
                    set(w, x + dx, s0, z + dz, Material.SAND);
                    if (r.nextInt(3) == 0) set(w, x + dx, s0 + 1, z + dz, Material.DEAD_BUSH);
                }
            }
        }
    }

    private static void tree(World w, int x, int z, Random r) {
        int s0 = surf(w, x, z);
        int type = r.nextInt(TREE_TYPES.length);
        Material log = TREE_TYPES[type][0];
        Material leaves = TREE_TYPES[type][1];
        int h = switch (type) {
            case 1, 5 -> 5 + r.nextInt(2);
            case 2 -> 6 + r.nextInt(3);
            case 3 -> 4 + r.nextInt(2);
            case 4 -> 6 + r.nextInt(3);
            default -> 4 + r.nextInt(3);
        };
        for (int y = 1; y <= h; y++) set(w, x, s0 + y, z, log);
        for (int dy = -2; dy <= 0; dy++) {
            int rad = switch (type) {
                case 2 -> dy == 0 ? 1 : 2;
                case 3, 4 -> 3;
                default -> 2;
            };
            for (int dx = -rad; dx <= rad; dx++) {
                for (int dz = -rad; dz <= rad; dz++) {
                    if (Math.abs(dx) == rad && Math.abs(dz) == rad && r.nextInt(2) == 0) continue;
                    if (w.getBlockAt(x + dx, s0 + h + 1 + dy, z + dz).getType() == Material.AIR) {
                        set(w, x + dx, s0 + h + 1 + dy, z + dz, leaves);
                    }
                }
            }
        }
    }

    private static boolean treeAlreadyAt(World w, int x, int z) {
        int top = surf(w, x, z);
        for (int y = top; y >= 60; y--) {
            String n = w.getBlockAt(x, y, z).getType().name();
            if (n.endsWith("_LOG") || n.endsWith("_LEAVES")) return true;
        }
        return false;
    }

    private static void logPile(World w, Random r, int cx, int cz) {
        int x = cx + r.nextInt(7) - 3, z = cz + r.nextInt(7) - 3;
        if (treeAlreadyAt(w, x, z)) return;
        int s0 = surf(w, x, z);
        int len = 2 + r.nextInt(3);
        boolean ns = r.nextBoolean();
        Material log = TREE_TYPES[r.nextInt(TREE_TYPES.length)][0];
        for (int i = 0; i < len; i++) {
            set(w, ns ? x : x + i, s0 + 1, ns ? z + i : z, log);
        }
    }

    private static void ruin(Random r, World w, int cx, int cz) {
        int s0 = surf(w, cx, cz);
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                Material floor = r.nextInt(4) == 0 ? Material.CRACKED_STONE_BRICKS : Material.MOSSY_STONE_BRICKS;
                set(w, cx + dx, s0, cz + dz, floor);
                boolean wall = Math.abs(dx) == 3 || Math.abs(dz) == 3;
                if (!wall) continue;
                boolean doorway = dz == 3 && Math.abs(dx) <= 1;
                int h = 1 + r.nextInt(2);
                for (int y = 1; y <= h; y++) {
                    if (doorway && y == 1) continue;
                    set(w, cx + dx, s0 + y, cz + dz,
                            r.nextInt(3) == 0 ? Material.MOSSY_STONE_BRICKS : Material.STONE_BRICKS);
                }
                if (Math.abs(dx) == 3 && Math.abs(dz) == 3) {
                    set(w, cx + dx, s0 + 3, cz + dz, Material.STONE_BRICK_WALL);
                }
            }
        }
        set(w, cx, s0 + 1, cz, Material.LANTERN);
        set(w, cx, s0 + 2, cz, TownKit.chain());
        if (r.nextBoolean()) set(w, cx + 1, s0 + 1, cz - 1, Material.SKELETON_SKULL);
        if (r.nextInt(3) == 0) set(w, cx - 1, s0 + 1, cz + 1, Material.BONE_BLOCK);
    }

    private static void scatter(World w, Random r, int cx, int cz, int count, Material m) {
        for (int i = 0; i < count; i++) {
            int x = cx + r.nextInt(9) - 4, z = cz + r.nextInt(9) - 4;
            int s0 = surf(w, x, z);
            Material ground = w.getBlockAt(x, s0, z).getType();
            if (ground != Material.GRASS_BLOCK && ground != Material.DIRT
                    && ground != Material.COARSE_DIRT && ground != Material.PODZOL
                    && ground != Material.MOSS_BLOCK && ground != Material.SAND) continue;
            if (w.getBlockAt(x, s0 + 1, z).getType() != Material.AIR) continue;
            set(w, x, s0 + 1, z, m);
        }
    }


    private static void scatterMixed(World w, Random r, int cx, int cz, int count, Material[] pool) {
        for (int i = 0; i < count; i++) {
            scatter(w, r, cx, cz, 1, pool[r.nextInt(pool.length)]);
        }
    }

    private static final int[][] EXCLUDED = {
            {0, -420}, {380, 180}, {-360, 260}, {0, 460}, {-440, -220},
            {com.aincrad.paper.world.FloorBoss.ARENA_X, com.aincrad.paper.world.FloorBoss.ARENA_Z}
    };

    private static boolean firstTerraLog = true;

    private static void terraform(Chunk chunk) {
        World w = chunk.getWorld();
        int min = 0, max = 0;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = chunk.getX() * 16 + dx;
                int z = chunk.getZ() * 16 + dz;
                int d = offsetAt(w.getSeed(), x, z);
                min = Math.min(min, d);
                max = Math.max(max, d);
                reshapeColumn(w, x, z, d);
            }
        }
        if (firstTerraLog && (min < 0 || max > 0)) {
            firstTerraLog = false;
            org.bukkit.Bukkit.getLogger().info("[Aincrad] Hills active, sample column range "
                    + min + ".." + max);
        }
    }

    private static int offsetAt(long seed, int x, int z) {
        double amp = smooth(Math.hypot(x, z) - (TOWN_SKIP + 20), 40);
        for (int[] p : EXCLUDED) {
            double d = Math.hypot(x - p[0], z - p[1]);
            boolean arena = p[0] == FloorBoss.ARENA_X && p[1] == FloorBoss.ARENA_Z;
            amp = Math.min(amp, smooth(d - (arena ? 90 : 40), 30));
        }
        if (amp <= 0.01) {
            return 0;
        }
        double hills = valueNoise(seed, x / 46.0, z / 46.0) * 5.0
                + valueNoise(seed ^ 0xBEEFL, x / 17.0, z / 17.0) * 1.4
                - 1.2;
        int d = (int) Math.round(hills * amp);
        return Math.max(-3, Math.min(6, d));
    }

    private static double smooth(double v, double range) {
        if (v <= 0) return 0;
        if (v >= range) return 1;
        double t = v / range;
        return t * t * (3 - 2 * t);
    }

    private static double valueNoise(long seed, double x, double z) {
        int x0 = (int) Math.floor(x), z0 = (int) Math.floor(z);
        double fx = x - x0, fz = z - z0;
        double a = lattice(seed, x0, z0), b = lattice(seed, x0 + 1, z0);
        double c = lattice(seed, x0, z0 + 1), d = lattice(seed, x0 + 1, z0 + 1);
        double sx = fx * fx * (3 - 2 * fx), sz = fz * fz * (3 - 2 * fz);
        return (a + (b - a) * sx) * (1 - sz) + (c + (d - c) * sx) * sz;
    }

    private static double lattice(long seed, int x, int z) {
        long h = mix(seed, x, z);
        return ((h >>> 40) & 0xFFFFFL) / 1048576.0 * 2.0 - 1.0;
    }

    private static void reshapeColumn(World w, int x, int z, int d) {
        if (d == 0) return;
        int base = 65;
        if (d > 0) {
            for (int y = base; y <= base + d; y++) {
                Material m = y == base + d ? Material.GRASS_BLOCK
                        : (y > base + d - 4 ? Material.DIRT : Material.STONE);
                set(w, x, y, z, m);
            }
        } else {
            for (int y = base; y > base + d; y--) {
                set(w, x, y, z, Material.AIR);
            }
            int top = base + d;
            set(w, x, top, z, Material.GRASS_BLOCK);
            set(w, x, top - 1, z, Material.DIRT);
            set(w, x, top - 2, z, Material.DIRT);
        }
    }

    static Random seeded(long seed, int x, int z) {
        return new Random(mix(seed, x, z));
    }

    private static long mix(long seed, int x, int z) {
        long h = seed ^ (x * 0x9E3779B97F4A7C15L) ^ ((long) z * 0xC2B2AE3D27D4EB4FL);
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return h;
    }

    static int parseFloor(String name) {
        String n = name.toLowerCase();
        int i = n.lastIndexOf("floor_");
        if (i < 0) return 1;
        int v = 0, j = i + 6;
        while (j < n.length() && Character.isDigit(n.charAt(j))) {
            v = v * 10 + (n.charAt(j) - '0');
            j++;
        }
        return j > i + 6 ? v : 1;
    }

    private static void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m, false);
    }
}
