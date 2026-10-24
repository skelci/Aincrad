package com.aincrad.paper.world;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import static com.aincrad.paper.world.TownKit.G;
public final class TownDistricts {
    private TownDistricts() {}
    public static void build(World w) {
        tavern(w);
        blacksmith(w);
        trainingYard(w);
        TownKit.refreshPanes(w);
    }
    private static void beds(World w, int x, int y, int[] zs, BlockFace f, Material[] mats) {
        for (int i = 0; i < zs.length; i++) {
            Bed b = (Bed) Bukkit.createBlockData(mats[i]);
            b.setFacing(f);
            b.setPart(Bed.Part.FOOT);
            w.getBlockAt(x, y, zs[i]).setBlockData(b);
            Bed h = (Bed) Bukkit.createBlockData(mats[i]);
            h.setFacing(f);
            h.setPart(Bed.Part.HEAD);
            int dx = f == BlockFace.EAST ? 1 : f == BlockFace.WEST ? -1 : 0;
            int dz = f == BlockFace.SOUTH ? 1 : f == BlockFace.NORTH ? -1 : 0;
            w.getBlockAt(x + dx, y, zs[i] + dz).setBlockData(h);
        }
    }
    private static void ladderCol(World w, int x, int z, int y1, int y2, BlockFace f) {
        for (int y = y1; y <= y2; y++) {
            Directional l = (Directional) Bukkit.createBlockData(Material.LADDER);
            l.setFacing(f);
            w.getBlockAt(x, y, z).setBlockData(l);
        }
    }
    private static void waterCauldron(World w, int x, int y, int z) {
        Levelled c = (Levelled) Bukkit.createBlockData(Material.WATER_CAULDRON);
        c.setLevel(3);
        w.getBlockAt(x, y, z).setBlockData(c);
    }
    private static void sets(World w, int y, int[][] cells, Material m) {
        for (int[] c : cells) TownKit.set(w, c[0], y + c[2], c[1], m);
    }
    private static void poleBanner(World w, int x, int z, Material banner) {
        for (int y = 1; y <= 4; y++) TownKit.set(w, x, G + y, z, Material.SPRUCE_FENCE);
        TownKit.set(w, x, G + 5, z, banner);
    }
    private static void table(World w, int x, int z) {
        TownKit.set(w, x, G + 1, z, Material.OAK_FENCE);
        TownKit.set(w, x + 1, G + 1, z, Material.OAK_FENCE);
        TownKit.set(w, x, G + 2, z, Material.OAK_PLANKS);
        TownKit.set(w, x + 1, G + 2, z, Material.OAK_PLANKS);
        TownKit.stair(w, x - 1, G + 1, z, BlockFace.EAST);
        TownKit.stair(w, x + 2, G + 1, z, BlockFace.WEST);
        TownKit.stair(w, x, G + 1, z - 1, BlockFace.SOUTH);
        TownKit.stair(w, x + 1, G + 1, z - 1, BlockFace.SOUTH);
        TownKit.stair(w, x, G + 1, z + 1, BlockFace.NORTH);
        TownKit.stair(w, x + 1, G + 1, z + 1, BlockFace.NORTH);
    }
    private static void dummy(World w, int x, int z) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                if (!(dx == 0 && dz == 0)) TownKit.set(w, x + dx, G, z + dz, Material.SAND);
        TownKit.set(w, x, G + 1, z, Material.DARK_OAK_FENCE);
        TownKit.set(w, x, G + 2, z, Material.HAY_BLOCK);
        TownKit.set(w, x, G + 3, z, Material.HAY_BLOCK);
        TownKit.set(w, x, G + 4, z, Material.RED_BANNER);
    }
    private static void tavern(World w) {
        TownKit.shell(w, 35, 34, 47, 46, 5, Material.WHITE_TERRACOTTA);
        TownKit.box(w, 35, 34, 47, 46, G, G, Material.SPRUCE_PLANKS);
        for (int y = 1; y <= 5; y++)
            for (int[] p : new int[][]{{35, 34}, {47, 34}, {35, 46}, {47, 46}, {38, 34}, {44, 34}, {38, 46}, {44, 46}, {35, 38}, {35, 42}, {47, 38}, {47, 42}})
                TownKit.set(w, p[0], G + y, p[1], Material.DARK_OAK_LOG);
        for (int x = 35; x <= 47; x++) {
            TownKit.set(w, x, G + 3, 34, Material.DARK_OAK_LOG);
            TownKit.set(w, x, G + 3, 46, Material.DARK_OAK_LOG);
        }
        for (int z = 34; z <= 46; z++) {
            TownKit.set(w, 35, G + 3, z, Material.DARK_OAK_LOG);
            TownKit.set(w, 47, G + 3, z, Material.DARK_OAK_LOG);
        }
        for (int x = 36; x <= 46; x++)
            for (int z : new int[]{34, 46})
                if (TownKit.hash(x, z) % 3 == 0) TownKit.set(w, x, G + 2, z, Material.STRIPPED_SPRUCE_WOOD);
        for (int z = 35; z <= 45; z++)
            for (int x : new int[]{35, 47})
                if (TownKit.hash(x, z) % 3 == 0) TownKit.set(w, x, G + 2, z, Material.STRIPPED_SPRUCE_WOOD);
        TownKit.doorWood(w, 41, 34, BlockFace.NORTH, Material.SPRUCE_DOOR);
        TownKit.doorWood(w, 42, 34, BlockFace.NORTH, Material.SPRUCE_DOOR);
        TownKit.doorWood(w, 41, 46, BlockFace.SOUTH, Material.SPRUCE_DOOR);
        TownKit.win2Sill(w, 36, G + 2, 34, BlockFace.NORTH, Material.DARK_OAK_PLANKS);
        TownKit.win2Sill(w, 44, G + 2, 34, BlockFace.NORTH, Material.DARK_OAK_PLANKS);
        TownKit.win2(w, 37, G + 2, 46, BlockFace.SOUTH);
        TownKit.win2(w, 43, G + 2, 46, BlockFace.SOUTH);
        TownKit.win2(w, 35, G + 2, 37, BlockFace.WEST);
        TownKit.win2(w, 35, G + 2, 42, BlockFace.WEST);
        TownKit.win2(w, 47, G + 2, 43, BlockFace.EAST);
        TownKit.box(w, 35, 33, 48, 46, G + 6, G + 6, Material.SPRUCE_PLANKS);
        for (int y = 7; y <= 10; y++) {
            for (int x = 35; x <= 48; x++) {
                TownKit.set(w, x, G + y, 33, Material.WHITE_TERRACOTTA);
                TownKit.set(w, x, G + y, 46, Material.WHITE_TERRACOTTA);
            }
            for (int z = 33; z <= 46; z++) {
                TownKit.set(w, 35, G + y, z, Material.WHITE_TERRACOTTA);
                TownKit.set(w, 48, G + y, z, Material.WHITE_TERRACOTTA);
            }
        }
        for (int y = 7; y <= 10; y++)
            for (int[] p : new int[][]{{35, 33}, {48, 33}, {35, 46}, {48, 46}, {38, 33}, {42, 33}, {45, 33}, {48, 36}, {48, 43}, {35, 39}, {41, 46}, {38, 46}})
                TownKit.set(w, p[0], G + y, p[1], Material.DARK_OAK_LOG);
        TownKit.win2Sill(w, 37, G + 8, 33, BlockFace.NORTH, Material.DARK_OAK_PLANKS);
        TownKit.win2Sill(w, 44, G + 8, 33, BlockFace.NORTH, Material.DARK_OAK_PLANKS);
        TownKit.win2(w, 48, G + 8, 35, BlockFace.EAST);
        TownKit.win2(w, 48, G + 8, 42, BlockFace.EAST);
        TownKit.win2(w, 35, G + 8, 36, BlockFace.WEST);
        TownKit.win2(w, 35, G + 8, 42, BlockFace.WEST);
        TownKit.win2(w, 39, G + 8, 46, BlockFace.SOUTH);
        for (Door.Half half : new Door.Half[]{Door.Half.BOTTOM, Door.Half.TOP}) {
            Door d = (Door) Bukkit.createBlockData(Material.SPRUCE_DOOR);
            d.setHalf(half);
            d.setFacing(BlockFace.EAST);
            w.getBlockAt(48, G + 7 + (half == Door.Half.BOTTOM ? 0 : 1), 40).setBlockData(d);
        }
        TownKit.roof(w, 35, 33, 48, 46, G + 11, Material.CHERRY_PLANKS);
        for (int y = 1; y <= 17; y++) TownKit.set(w, 36, G + y, 45, Material.BRICKS);
        TownKit.set(w, 36, G + 18, 45, Material.CAMPFIRE);
        TownKit.set(w, 43, G + 5, 33, Material.OAK_FENCE);
        TownKit.set(w, 43, G + 4, 33, TownKit.chain());
        TownKit.set(w, 43, G + 3, 33, Material.OAK_HANGING_SIGN);
        TownKit.lamp(w, 40, 33);
        TownKit.lamp(w, 44, 33);
        for (int[] p : new int[][]{{36, 1}, {37, 3}, {45, 5}, {46, 7}})
            TownKit.planter(w, p[0], 33, TownKit.FLOWERS[p[1]]);
        for (int x = 34; x <= 43; x++) TownKit.set(w, x, G + 5, 33, Material.DARK_OAK_STAIRS);
        for (int z = 39; z <= 42; z++) TownKit.slab(w, 49, G + 5, z, Material.SPRUCE_SLAB);
        TownKit.path(w, 40, 33, 43, 33);
        TownKit.path(w, 41, 47, 41, 48);
        poleBanner(w, 34, 33, Material.YELLOW_BANNER);
        TownKit.box(w, 49, 39, 49, 42, G + 6, G + 6, Material.SPRUCE_PLANKS);
        for (int z : new int[]{39, 41, 42}) TownKit.set(w, 49, G + 7, z, Material.SPRUCE_FENCE);

        TownKit.set(w, 40, G + 1, 35, Material.IRON_BARS);
        TownKit.set(w, 40, G + 2, 35, Material.BROWN_BANNER);
        TownKit.set(w, 40, G + 1, 36, Material.OAK_FENCE);
        TownKit.set(w, 40, G + 2, 36, Material.OAK_FENCE);
        for (int z = 37; z <= 41; z++) {
            TownKit.set(w, 45, G + 1, z, Material.SPRUCE_PLANKS);
            TownKit.set(w, 45, G + 2, z, Material.STRIPPED_SPRUCE_LOG);
        }
        for (int z : new int[]{36, 38, 40}) TownKit.set(w, 46, G + 1, z, Material.BARREL);
        TownKit.set(w, 46, G + 1, 37, Material.BREWING_STAND);
        TownKit.set(w, 46, G + 1, 41, Material.BREWING_STAND);
        TownKit.set(w, 46, G + 1, 42, Material.CAULDRON);
        for (int z = 37; z <= 41; z++) TownKit.set(w, 46, G + 3, z, Material.GLASS);
        TownKit.set(w, 44, G + 3, 38, Material.LANTERN);
        TownKit.set(w, 44, G + 3, 40, Material.LANTERN);

        TownKit.box(w, 40, 40, 42, 42, G, G, Material.BRICKS);
        sets(w, G + 1, new int[][]{{40, 40, 0}, {42, 40, 0}, {40, 42, 0}, {42, 42, 0}}, Material.BRICKS);
        TownKit.set(w, 41, G + 1, 41, Material.CAMPFIRE);

        table(w, 38, 37);
        table(w, 37, 42);
        table(w, 43, 43);
        table(w, 43, 36);
        for (int i = 1; i <= 5; i++) {
            if (i > 1) TownKit.box(w, 41 + i, 45, 41 + i, 45, G + 1, G + i - 1, Material.SPRUCE_PLANKS);
            TownKit.stair(w, 41 + i, G + i, 45, BlockFace.EAST, Material.SPRUCE_STAIRS);
        }

        sets(w, G + 1, new int[][]{{40, 43, 0}, {41, 43, 0}, {42, 43, 0}, {40, 44, 0}, {42, 44, 0}, {36, 36, 0}, {37, 36, 0}, {36, 44, 0}, {44, 44, 0}, {44, 43, 0}}, Material.RED_CARPET);
        sets(w, G + 1, new int[][]{{37, 39, 0}, {38, 39, 0}, {42, 39, 0}, {42, 38, 0}, {38, 44, 0}, {37, 44, 0}}, Material.BROWN_CARPET);
        for (int[] ch : new int[][]{{39, 38}, {40, 44}}) {
            TownKit.set(w, ch[0], G + 4, ch[1], Material.IRON_BARS);
            TownKit.set(w, ch[0] - 1, G + 4, ch[1], Material.IRON_BARS);
            TownKit.set(w, ch[0] + 1, G + 4, ch[1], Material.IRON_BARS);
            TownKit.set(w, ch[0], G + 4, ch[1] - 1, Material.IRON_BARS);
            TownKit.set(w, ch[0], G + 4, ch[1] + 1, Material.IRON_BARS);
            TownKit.set(w, ch[0], G + 3, ch[1], Material.LANTERN);
        }
        sets(w, G + 5, new int[][]{{36, 36, 0}, {46, 36, 0}, {36, 44, 0}, {41, 44, 0}, {41, 38, 0}}, Material.LANTERN);

        beds(w, 36, G + 7, new int[]{35, 38, 41}, BlockFace.EAST,
                new Material[]{Material.RED_BED, Material.BLUE_BED, Material.WHITE_BED});
        TownKit.set(w, 46, G + 7, 34, Material.BARREL);
        TownKit.set(w, 45, G + 7, 34, Material.BARREL);
        TownKit.set(w, 44, G + 7, 34, Material.CHEST);
        TownKit.set(w, 46, G + 8, 34, Material.LANTERN);
        sets(w, G + 10, new int[][]{{42, 38, 0}, {44, 42, 0}}, Material.OAK_PLANKS);
        sets(w, G + 9, new int[][]{{42, 38, 0}, {44, 42, 0}}, Material.LANTERN);
        TownKit.set(w, 42, G + 7, 41, Material.OAK_FENCE);
        TownKit.set(w, 43, G + 7, 41, Material.OAK_FENCE);
        TownKit.set(w, 42, G + 8, 41, Material.OAK_PLANKS);
        TownKit.set(w, 43, G + 8, 41, Material.OAK_PLANKS);
        TownKit.stair(w, 42, G + 7, 42, BlockFace.NORTH, Material.OAK_STAIRS);
        TownKit.stair(w, 43, G + 7, 42, BlockFace.NORTH, Material.OAK_STAIRS);
    }


    private static void blacksmith(World w) {
        TownKit.path(w, -47, 57, -45, 58);
        TownKit.shell(w, -48, 59, -39, 71, 6, Material.BRICKS);
        TownKit.box(w, -48, 59, -39, 71, G, G, Material.DEEPSLATE_BRICKS);
        TownKit.box(w, -47, 59, -45, 59, G + 1, G + 4, Material.AIR);
        TownKit.box(w, -47, 59, -45, 59, G + 5, G + 5, Material.STRIPPED_SPRUCE_WOOD);
        TownKit.set(w, -46, G + 4, 59, TownKit.chain());
        TownKit.set(w, -46, G + 3, 59, Material.OAK_HANGING_SIGN);
        sets(w, G + 4, new int[][]{{-47, 59, 0}, {-45, 59, 0}}, Material.LANTERN);
        TownKit.lamp(w, -44, 58);
        TownKit.lamp(w, -48, 58);
        TownKit.win2Sill(w, -44, G + 3, 59, BlockFace.NORTH, Material.BRICKS);
        TownKit.win2Sill(w, -41, G + 3, 59, BlockFace.NORTH, Material.BRICKS);
        TownKit.win2(w, -46, G + 3, 71, BlockFace.SOUTH);
        TownKit.win2(w, -42, G + 3, 71, BlockFace.SOUTH);
        TownKit.win2(w, -39, G + 3, 63, BlockFace.EAST);
        TownKit.win2(w, -39, G + 3, 67, BlockFace.EAST);
        TownKit.win2(w, -48, G + 3, 66, BlockFace.WEST);
        TownKit.roof(w, -48, 59, -39, 71, G + 7, Material.SPRUCE_PLANKS);

        TownKit.box(w, -47, 63, -45, 65, G + 1, G + 1, Material.NETHER_BRICKS);
        sets(w, G + 1, new int[][]{{-47, 63, 0}, {-45, 65, 0}}, Material.MAGMA_BLOCK);
        TownKit.set(w, -46, G + 1, 64, Material.CAMPFIRE);
        for (int y = 3; y <= 13; y++) TownKit.set(w, -46, G + y, 64, Material.BRICKS);
        TownKit.set(w, -46, G + 14, 64, Material.CAMPFIRE);
        for (int z = 62; z <= 66; z++) {
            TownKit.set(w, -47, G + 2, z, Material.IRON_BARS);
            TownKit.set(w, -47, G + 3, z, Material.IRON_BARS);
        }
        sets(w, G + 1, new int[][]{{-47, 63, 0}, {-47, 65, 0}}, Material.IRON_TRAPDOOR);
        TownKit.set(w, -45, G + 1, 61, Material.SPRUCE_LOG);
        TownKit.set(w, -45, G + 2, 61, Material.ANVIL);
        TownKit.set(w, -45, G + 5, 61, TownKit.chain());
        TownKit.set(w, -45, G + 4, 61, Material.LANTERN);
        TownKit.set(w, -40, G + 1, 62, Material.GRINDSTONE);
        TownKit.set(w, -40, G + 1, 64, Material.SMITHING_TABLE);
        TownKit.set(w, -40, G + 1, 66, Material.BLAST_FURNACE);
        TownKit.set(w, -40, G + 1, 68, Material.CHEST);
        TownKit.set(w, -40, G + 1, 70, Material.BARREL);
        sets(w, G + 1, new int[][]{{-42, 70, 0}, {-41, 70, 0}, {-42, 70, 1}}, Material.COAL_BLOCK);
        waterCauldron(w, -47, G + 1, 68);
        TownKit.set(w, -46, G + 1, 68, Material.HOPPER);
        TownKit.set(w, -41, G + 1, 61, Material.CRAFTING_TABLE);
        sets(w, G + 5, new int[][]{{-45, 66, 0}, {-42, 62, 0}, {-45, 69, 0}, {-41, 65, 0}}, TownKit.chain());
        sets(w, G + 4, new int[][]{{-45, 66, 0}, {-42, 62, 0}, {-45, 69, 0}, {-41, 65, 0}}, Material.LANTERN);

        for (int z = 61; z <= 68; z++) TownKit.slab(w, -51, G + 4, z, Material.SPRUCE_SLAB);
        for (int z : new int[]{62, 68})
            for (int y = 1; y <= 3; y++) TownKit.set(w, -52, G + y, z, Material.STRIPPED_SPRUCE_WOOD);
        TownKit.set(w, -51, G + 1, 63, Material.IRON_ORE);
        TownKit.set(w, -51, G + 2, 63, Material.IRON_ORE);
        TownKit.set(w, -51, G + 1, 64, Material.COAL_ORE);
        TownKit.set(w, -51, G + 1, 66, Material.IRON_ORE);
        TownKit.set(w, -51, G + 2, 66, Material.COAL_ORE);
        sets(w, G + 1, new int[][]{{-52, 59, 0}, {-52, 60, 0}, {-52, 61, 0}, {-52, 59, 1}, {-52, 60, 1}}, Material.SPRUCE_LOG);
        waterCauldron(w, -49, G + 1, 60);
        TownKit.set(w, -49, G + 1, 59, Material.SPRUCE_FENCE);
        TownKit.set(w, -50, G + 1, 60, Material.SPRUCE_FENCE);
    }


    private static void trainingYard(World w) {
        TownKit.clearArea(w, -103, 59, -61, 103, 6);
        TownKit.path(w, -86, 53, -83, 58);
        TownKit.path(w, -60, 78, -53, 80);
        for (int x = -104; x <= -60; x++) {
            if (x < -86 || x > -83) yardWall(w, x, 58);
            yardWall(w, x, 104);
        }
        for (int z = 58; z <= 104; z++) {
            yardWall(w, -104, z);
            if (z < 78 || z > 80) yardWall(w, -60, z);
        }
        TownKit.box(w, -86, 58, -83, 58, G + 5, G + 5, Material.STONE_BRICKS);
        TownKit.box(w, -60, 78, -60, 80, G + 5, G + 5, Material.STONE_BRICKS);
        sets(w, G + 4, new int[][]{{-85, 58, 0}, {-84, 58, 0}}, Material.LANTERN);
        TownKit.set(w, -87, G + 2, 58, Material.OAK_SIGN);
        sets(w, G + 3, new int[][]{{-87, 58, 0}, {-82, 58, 0}}, Material.RED_BANNER);
        for (int[] c : new int[][]{{-104, 58}, {-60, 58}, {-104, 104}, {-60, 104}}) tower(w, c[0], c[1]);

        for (int x = -90; x <= -74; x++)
            for (int z = 73; z <= 89; z++) {
                int d = Math.max(Math.abs(x + 82), Math.abs(z - 81));
                if (d == 8) TownKit.set(w, x, G + 1, z, Material.OAK_FENCE);
                else if (d == 7) TownKit.set(w, x, G, z, Material.PACKED_MUD);
                else if (d <= 6) TownKit.set(w, x, G, z, Material.DIRT_PATH);
            }
        for (int[] p : new int[][]{{-85, 77}, {-79, 77}, {-86, 81}, {-78, 81}, {-85, 85}, {-79, 85}}) dummy(w, p[0], p[1]);

        for (int z = 69; z <= 76; z++) {
            TownKit.set(w, -61, G + 2, z, Material.IRON_BARS);
            TownKit.set(w, -61, G + 3, z, Material.IRON_BARS);
            if (z % 2 == 0) TownKit.set(w, -61, G + 4, z, Material.POLISHED_BLACKSTONE_SLAB);
        }
        for (int z : new int[]{70, 72, 74}) TownKit.set(w, -61, G + 1, z, Material.IRON_TRAPDOOR);
        TownKit.set(w, -61, G + 3, 69, Material.RED_BANNER);
        TownKit.set(w, -61, G + 3, 76, Material.LIGHT_GRAY_BANNER);

        for (int x : new int[]{-91, -82, -73})
            for (int z = 97; z <= 100; z++)
                for (int y = 1; y <= 3; y++) TownKit.set(w, x, G + y, z, Material.OAK_FENCE);
        for (int x : new int[]{-99, -64})
            for (int z = 98; z <= 100; z++) TownKit.set(w, x, G + 1, z, Material.OAK_FENCE);
        for (int tx : new int[]{-95, -87, -78, -69}) {
            TownKit.box(w, tx - 1, 103, tx + 1, 103, G + 2, G + 4, Material.WHITE_CONCRETE);
            TownKit.set(w, tx, G + 3, 103, Material.RED_CONCRETE);
        }
        for (int x = -99; x <= -65; x++) {
            TownKit.stair(w, x, G + 1, 102, BlockFace.NORTH, Material.OAK_STAIRS);
            TownKit.set(w, x, G + 1, 101, Material.OAK_PLANKS);
            TownKit.stair(w, x, G + 2, 101, BlockFace.NORTH, Material.OAK_STAIRS);
        }

        for (int z = 64; z <= 72; z++) {
            TownKit.set(w, -99, G + 1, z, Material.OAK_FENCE);
            TownKit.set(w, -99, G + 2, z, Material.STRIPPED_OAK_LOG);
        }
        TownKit.box(w, -96, 74, -96, 78, G + 1, G + 4, Material.STONE_BRICKS);
        for (int z : new int[]{74, 76, 78}) ladderCol(w, -95, z, G + 1, G + 4, BlockFace.WEST);
        for (int x = -91; x <= -89; x++)
            for (int z = 71; z <= 73; z++) {
                if (x == -90 && z == 72) continue;
                TownKit.set(w, x, G + 1, z, Material.HAY_BLOCK);
                TownKit.set(w, x, G + 2, z, Material.HAY_BLOCK);
            }
        for (int z : new int[]{66, 70}) {
            TownKit.set(w, -84, G + 1, z, Material.OAK_FENCE);
            TownKit.set(w, -84, G + 2, z, Material.POLISHED_BLACKSTONE_SLAB);
            TownKit.set(w, -83, G + 1, z, Material.IRON_TRAPDOOR);
        }
        for (int z : new int[]{85, 90}) {
            TownKit.box(w, -70, z, -70, z, G + 2, G + 4, Material.GRAY_CONCRETE);
            TownKit.set(w, -70, G + 3, z, Material.YELLOW_CONCRETE);
            TownKit.set(w, -70, G + 1, z, Material.OAK_FENCE);
        }

        armory(w);
        TownKit.box(w, -73, 92, -71, 94, G + 1, G + 2, Material.COBBLESTONE);
        TownKit.set(w, -72, G + 1, 93, Material.WATER);
        TownKit.set(w, -72, G + 2, 93, Material.AIR);
        TownKit.set(w, -72, G + 3, 92, Material.SPRUCE_FENCE);
        TownKit.set(w, -71, G + 3, 93, Material.CAULDRON);
        TownKit.lamp(w, -87, 60);
        TownKit.lamp(w, -76, 60);
        TownKit.lamp(w, -62, 84);
        TownKit.lamp(w, -101, 92);
    }

    private static void yardWall(World w, int x, int z) {
        Material m = TownKit.hash(x, z) % 4 == 0 ? Material.MOSSY_STONE_BRICKS : Material.STONE_BRICKS;
        for (int y = 1; y <= 4; y++) TownKit.set(w, x, G + y, z, m);
        if (TownKit.hash(x, z) % 2 == 0) TownKit.set(w, x, G + 5, z, m);
        else TownKit.slab(w, x, G + 5, z, Material.STONE_BRICK_SLAB);
    }

    private static void tower(World w, int x, int z) {
        int dx = x == -104 ? 2 : -2, dz = z == 58 ? 2 : -2;
        TownKit.box(w, Math.min(x, x + dx), Math.min(z, z + dz), Math.max(x, x + dx), Math.max(z, z + dz), G + 1, G + 6, Material.STONE_BRICKS);
        int cx = x == -104 ? x + 1 : x - 1, cz = z == 58 ? z + 1 : z - 1;
        TownKit.set(w, cx, G + 7, cz, Material.LANTERN);
        TownKit.set(w, cx, G + 8, cz, Material.SPRUCE_FENCE);
        TownKit.set(w, cx, G + 9, cz, Material.SPRUCE_FENCE);
        TownKit.set(w, cx, G + 10, cz, Material.RED_BANNER);
    }

    private static void armory(World w) {
        for (int y = 1; y <= 4; y++) {
            for (int x = -67; x <= -61; x++) {
                TownKit.set(w, x, G + y, 61, Material.DARK_OAK_PLANKS);
                TownKit.set(w, x, G + y, 67, Material.DARK_OAK_PLANKS);
            }
            for (int z = 61; z <= 67; z++) {
                TownKit.set(w, -67, G + y, z, Material.DARK_OAK_PLANKS);
                TownKit.set(w, -61, G + y, z, Material.DARK_OAK_PLANKS);
            }
        }
        TownKit.box(w, -67, 61, -61, 67, G + 5, G + 5, Material.SPRUCE_PLANKS);
        TownKit.doorLocked(w, -64, 67, BlockFace.SOUTH);
        for (int x = -65; x <= -63; x++) { TownKit.set(w, x, G + 1, 63, Material.IRON_BARS); TownKit.set(w, x, G + 2, 63, Material.RED_BANNER); }
        TownKit.set(w, -63, G + 3, 62, Material.RED_BANNER);
        TownKit.set(w, -65, G + 3, 62, Material.LIGHT_GRAY_BANNER);
        TownKit.set(w, -62, G + 1, 62, Material.BARREL);
        TownKit.set(w, -62, G + 1, 63, Material.BARREL);
        TownKit.set(w, -62, G + 1, 64, Material.CHEST);
        TownKit.set(w, -66, G + 1, 64, Material.STONE_PRESSURE_PLATE);
        TownKit.set(w, -64, G + 4, 64, Material.LANTERN);
        TownKit.win2(w, -61, G + 2, 63, BlockFace.EAST);
    }
}
