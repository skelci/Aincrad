package com.aincrad.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.WallSign;

import static com.aincrad.paper.world.TownKit.*;

public final class TownCore {

    private static final Material[] ROSE = {
            Material.RED_STAINED_GLASS, Material.LIME_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS,
            Material.MAGENTA_STAINED_GLASS, Material.ORANGE_STAINED_GLASS
    };
    private static final Material[] STRIPS = {
            Material.RED_STAINED_GLASS, Material.LIME_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS
    };
    private static final Material[] FLOOR_COLORS = {
            Material.GREEN_WOOL, Material.LIME_WOOL, Material.YELLOW_WOOL, Material.ORANGE_WOOL,
            Material.RED_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL, Material.CYAN_WOOL
    };
    private static final Material[] STALL_GOODS = {
            Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.MELON, Material.HAY_BLOCK,
            Material.NETHER_WART, Material.EMERALD_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK
    };
    private static final Material[] AWNINGS = {
            Material.RED_WOOL, Material.YELLOW_WOOL, Material.BLUE_WOOL,
            Material.LIME_WOOL, Material.ORANGE_WOOL, Material.PURPLE_WOOL
    };

    private TownCore() {}

    public static void build(World w) {
        plaza(w);
        temple(w);
        guild(w);
        marketHall(w);
        portalArcade(w);
        plazaFinal(w);
        refreshPanes(w);
    }

    private static void hangingLantern(World w, int x, int y, int z) {
        w.getBlockAt(x, y, z).setBlockData(Bukkit.createBlockData("lantern[hanging=true]"));
    }

    private static void standingSign(World w, int x, int y, int z, int rot) {
        w.getBlockAt(x, y, z).setBlockData(Bukkit.createBlockData("oak_sign[rotation=" + rot + "]"));
    }

    private static void wallSignSouth(World w, int x, int y, int z, int rot) {
        WallSign s = (WallSign) Bukkit.createBlockData(Material.OAK_WALL_SIGN);
        s.setFacing(BlockFace.SOUTH);
        w.getBlockAt(x, y, z).setBlockData(s);
    }

    private static void plaza(World w) {
        lampTall(w, -18, -18);
        lampTall(w, 18, -18);
        lampTall(w, -18, 18);
        lampTall(w, 18, 18);
        path(w, -1, -26, 1, -13);
        path(w, 13, -1, 26, 1);
        path(w, -26, -1, -20, 1);
        path(w, -16, -1, -11, 1);
        path(w, -9, 16, -7, 26);
        planter(w, -10, 20, pick(FLOWERS, hash(-10, 20)));
        planter(w, 10, 20, pick(FLOWERS, hash(10, 20)));
        planter(w, -10, -16, pick(FLOWERS, hash(-10, -16)));
        planter(w, 10, -16, pick(FLOWERS, hash(10, -16)));
        planter(w, 14, 9, pick(FLOWERS, hash(14, 9)));
        planter(w, -14, -9, pick(FLOWERS, hash(-14, -9)));
        fountain(w);
        stair(w, -6, G + 1, 16, BlockFace.NORTH, Material.STONE_BRICK_STAIRS);
        stair(w, -5, G + 1, 16, BlockFace.NORTH, Material.STONE_BRICK_STAIRS);
        stair(w, -4, G + 1, 16, BlockFace.NORTH, Material.STONE_BRICK_STAIRS);
        stair(w, 4, G + 1, 16, BlockFace.NORTH, Material.STONE_BRICK_STAIRS);
        stair(w, 5, G + 1, 16, BlockFace.NORTH, Material.STONE_BRICK_STAIRS);
        stair(w, 6, G + 1, 16, BlockFace.NORTH, Material.STONE_BRICK_STAIRS);
        stair(w, -6, G + 1, 26, BlockFace.SOUTH, Material.STONE_BRICK_STAIRS);
        stair(w, -5, G + 1, 26, BlockFace.SOUTH, Material.STONE_BRICK_STAIRS);
        stair(w, -4, G + 1, 26, BlockFace.SOUTH, Material.STONE_BRICK_STAIRS);
        stair(w, 4, G + 1, 26, BlockFace.SOUTH, Material.STONE_BRICK_STAIRS);
        stair(w, 5, G + 1, 26, BlockFace.SOUTH, Material.STONE_BRICK_STAIRS);
        stair(w, 6, G + 1, 26, BlockFace.SOUTH, Material.STONE_BRICK_STAIRS);
        for (int z = 20; z <= 22; z++) {
            stair(w, 8, G + 1, z, BlockFace.WEST, Material.STONE_BRICK_STAIRS);
            stair(w, -8, G + 1, z, BlockFace.EAST, Material.STONE_BRICK_STAIRS);
        }
    }

    private static void fountain(World w) {
        int cx = 0, cz = 21;
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                int r = Math.abs(dx) + Math.abs(dz);
                int x = cx + dx, z = cz + dz;
                if (r == 6) {
                    box(w, x, z, x, z, G + 1, G + 2, Material.POLISHED_ANDESITE);
                    continue;
                }
                if (r > 5) continue;
                set(w, x, G, z, Material.SMOOTH_STONE);
                set(w, x, G + 1, z, Material.WATER);
                if (r == 3 && (dx == 0 || dz == 0))
                    set(w, x, G + 1, z, Material.POLISHED_ANDESITE);
            }
        }
        box(w, cx, cz, cx, cz, G + 1, G + 2, Material.POLISHED_ANDESITE);
        set(w, cx, G + 3, cz, Material.GOLD_BLOCK);
        set(w, cx, G + 4, cz, Material.GLOWSTONE);
        lamp(w, -8, 17);
        lamp(w, 8, 17);
        lamp(w, -8, 25);
        lamp(w, 8, 25);
    }

    private static void temple(World w) {
        clearArea(w, -11, -20, 11, 6, 28);
        pave(w, -10, -19, 10, 6);
        for (int x = -10; x <= 10; x++) {
            for (int z = -19; z <= 5; z++) {
                if (!(x == -10 || x == 10 || z == -19 || z == 5)) continue;
                for (int y = 1; y <= 20; y++) {
                    Material m = y <= 9 ? Material.STONE_BRICKS : Material.SMOOTH_QUARTZ;
                    if ((x == -10 || x == 10) && (z == -19 || z == 5) && y <= 12) m = Material.MOSSY_STONE_BRICKS;
                    if (y == 10 || y == 11) m = Material.CHISELED_STONE_BRICKS;
                    set(w, x, G + y, z, m);
                }
            }
        }
        for (int x = -9; x <= 9; x += 6) {
            if (x == 0) continue;
            for (int zz : new int[]{5, -19}) box(w, x, zz, x + 1, zz, G + 1, G + 20, Material.POLISHED_ANDESITE);
        }
        for (int zz : new int[]{-17, 3}) {
            for (int y = 1; y <= 20; y++) {
                set(w, -10, G + y, zz, Material.POLISHED_ANDESITE);
                set(w, 10, G + y, zz, Material.POLISHED_ANDESITE);
            }
        }
        for (int x = -1; x <= 1; x++)
            for (int z = 4; z <= 5; z++)
                for (int y = 1; y <= 4; y++)
                    set(w, x, G + y, z, Material.AIR);
        doorWood(w, -6, 5, BlockFace.SOUTH, Material.OAK_DOOR);
        doorWood(w, -5, 5, BlockFace.SOUTH, Material.OAK_DOOR);
        doorWood(w, 5, 5, BlockFace.SOUTH, Material.OAK_DOOR);
        doorWood(w, 6, 5, BlockFace.SOUTH, Material.OAK_DOOR);
        win2(w, -6, G + 6, 5, BlockFace.SOUTH);
        win2(w, 5, G + 6, 5, BlockFace.SOUTH);
        win2(w, -1, G + 6, 5, BlockFace.SOUTH);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                int r = Math.max(Math.abs(dx), Math.abs(dy));
                if (r > 4) continue;
                Material m;
                if (r == 4) m = Material.STONE_BRICKS;
                else if (r == 0) m = Material.GLOWSTONE;
                else m = pick(ROSE, (dx + 9) * 7 + (dy + 9) * 13);
                set(w, dx, G + 15 + dy, 5, m);
                set(w, dx, G + 15 + dy, 4, m);
            }
        }
        for (int x = -10; x <= 10; x += 20) {
            for (int z0 = -16; z0 <= -1; z0 += 3) {
                Material c = pick(STRIPS, hash(z0, x));
                for (int zz = z0; zz <= z0 + 1; zz++)
                    for (int y = 7; y <= 14; y++)
                        set(w, x, G + y, zz, c);
            }
        }
        doorLocked(w, 0, -19, BlockFace.NORTH);
        win2(w, -7, G + 3, -19, BlockFace.NORTH);
        win2(w, 6, G + 3, -19, BlockFace.NORTH);
        win2(w, -7, G + 14, -19, BlockFace.NORTH);
        win2(w, 6, G + 14, -19, BlockFace.NORTH);
        for (int x = -4; x <= 4; x++)
            for (int z = 6; z <= 9; z++)
                slab(w, x, G + 1, z, Material.STONE_BRICK_SLAB);
        for (int x : new int[]{-9, -7, 7, 9}) {
            box(w, x, 7, x, 7, G + 1, G + 4, Material.POLISHED_ANDESITE);
            slab(w, x, G + 5, 7, Material.POLISHED_ANDESITE_SLAB);
            set(w, x, G + 1, 6, Material.POLISHED_ANDESITE_SLAB);
        }
        bellTower(w);
        box(w, -10, -19, 10, 5, G + 20, G + 21, Material.STONE_BRICKS);
        roofCap(w, -10, -19, 10, 5, G + 22, Material.STONE_BRICK_SLAB);
        set(w, 2, G + 28, -7, Material.GOLD_BLOCK);
        set(w, 2, G + 29, -7, Material.END_ROD);
        templeInterior(w);
    }

    private static void bellTower(World w) {
        for (int x = -10; x <= -6; x++) {
            for (int z = -19; z <= -15; z++) {
                if (!(x == -10 || x == -6 || z == -19 || z == -15)) continue;
                for (int y = 13; y <= 22; y++) {
                    Material m = (y % 7) < 6 ? Material.SMOOTH_QUARTZ : Material.STONE_BRICKS;
                    if ((x == -10 && z == -19) || (x == -6 && z == -19) || (x == -10 && z == -15)) m = Material.POLISHED_ANDESITE;
                    set(w, x, G + y, z, m);
                }
            }
        }
        set(w, -8, G + 20, -17, Material.AIR);
        set(w, -8, G + 21, -17, Material.AIR);
        for (int x = -9; x <= -7; x++)
            for (int z = -18; z <= -16; z++)
                set(w, x, G + 22, z, Material.GLOWSTONE);
        set(w, -8, G + 22, -17, TownKit.chain());
        set(w, -8, G + 21, -17, Material.BELL);
        set(w, -8, G + 20, -17, Material.LANTERN);
        box(w, -9, -18, -7, -16, G + 23, G + 23, Material.GOLD_BLOCK);
        box(w, -8, -17, -8, -17, G + 24, G + 24, Material.GOLD_BLOCK);
    }

    private static void roofCap(World w, int x1, int z1, int x2, int z2, int baseY, Material mat) {
        int maxS = (z2 - z1) / 2 - 1;
        for (int s = 0; s <= maxS; s++) {
            int ax1 = x1 + s, ax2 = x2 - s, az1 = z1 + s, az2 = z2 - s;
            if (ax1 > ax2 || az1 > az2) break;
            for (int x = ax1; x <= ax2; x++) {
                slab(w, x, baseY + s, az1, mat);
                slab(w, x, baseY + s, az2, mat);
            }
            for (int z = az1 + 1; z < az2; z++) {
                slab(w, ax1, baseY + s, z, mat);
                slab(w, ax2, baseY + s, z, mat);
            }
        }
    }

    private static void templeInterior(World w) {
        for (int px = -5; px <= 5; px += 5) {
            if (px == 0) continue;
            for (int pz = -11; pz <= 1; pz += 4) {
                for (int y = 1; y <= 16; y++)
                    set(w, px, G + y, pz, y == 10 ? Material.GLOWSTONE : Material.SMOOTH_QUARTZ);
                box(w, px, pz, px, pz, G + 17, G + 18, Material.POLISHED_ANDESITE);
                set(w, px, G + 19, pz, Material.END_ROD);
            }
        }
        for (int z = -6; z <= 0; z += 2) {
            for (int x = -7; x <= -2; x++) stair(w, x, G + 1, z, BlockFace.NORTH, Material.OAK_STAIRS);
            for (int x = 2; x <= 7; x++) stair(w, x, G + 1, z, BlockFace.NORTH, Material.OAK_STAIRS);
        }
        for (int x = -8; x <= 8; x += 4) {
            for (int z = -11; z <= 1; z += 4) {
                set(w, x, G, z, Material.GLOWSTONE);
                set(w, x, G + 1, z, (x >= -1 && x <= 1) ? Material.RED_CARPET : Material.WHITE_CARPET);
            }
        }
        for (int x : new int[]{-8, 8}) {
            for (int z : new int[]{-17, 3}) {
                set(w, x, G, z, Material.SEA_LANTERN);
                set(w, x, G + 1, z, Material.WHITE_CARPET);
            }
        }
        for (int x = -1; x <= 1; x++)
            for (int z = -11; z <= 4; z++)
                if (w.getBlockAt(x, G + 1, z).getType() != Material.GLOWSTONE) set(w, x, G + 1, z, Material.RED_CARPET);
        for (int x = -8; x <= 8; x++) {
            if (x == 0) continue;
            for (int z = -18; z <= -14; z++) slab(w, x, G + 6, z, Material.STONE_BRICK_SLAB);
            for (int z = -18; z <= -16; z++) slab(w, x, G + 11, z, Material.STONE_BRICK_SLAB);
        }
        slab(w, -9, G + 6, -13, Material.STONE_BRICK_SLAB);
        slab(w, 9, G + 6, -13, Material.STONE_BRICK_SLAB);
        for (int x = -8; x <= 8; x++) {
            if (x >= -2 && x <= 2) continue;
            set(w, x, G + 7, -14, Material.STONE_BRICK_WALL);
            if (Math.abs(x) != 9) set(w, x, G + 12, -16, Material.STONE_BRICK_WALL);
        }
        for (int x : new int[]{-6, 6})
            box(w, x, -14, x, -14, G + 1, G + 5, Material.POLISHED_ANDESITE);
        for (int side = -1; side <= 1; side += 2) {
            int px = side * 9;
            for (int k = 1; k <= 5; k++) {
                set(w, px, G + k, -k - 7, Material.AIR);
                stair(w, px, G + k, -k - 7, BlockFace.NORTH, Material.STONE_BRICK_STAIRS);
            }
        }
        for (int x = -8; x <= 8; x += 4) hangingLantern(w, x, G + 5, -15);
        for (int x = -6; x <= 6; x += 6) hangingLantern(w, x, G + 10, -17);
        for (int x = -9; x <= -6; x++) box(w, x, -18, x, -18, G + 7, G + 8, Material.BOOKSHELF);
        for (int x = 6; x <= 9; x++) box(w, x, -18, x, -18, G + 7, G + 8, Material.BOOKSHELF);
        box(w, -3, -18, 3, -13, G + 1, G + 1, Material.SMOOTH_QUARTZ);
        for (int x = -2; x <= 2; x++) slab(w, x, G + 1, -12, Material.SMOOTH_QUARTZ_SLAB);
        for (int x = -1; x <= 1; x++) {
            set(w, x, G + 2, -12, Material.RED_CARPET);
            set(w, x, G + 2, -13, Material.RED_CARPET);
        }
        set(w, 0, G + 2, -13, Material.RED_CARPET);
        set(w, 0, G + 2, -15, Material.GOLD_BLOCK);
        pot(w, 0, G + 3, -15, Material.POPPY);
        set(w, -2, G + 2, -15, Material.END_ROD);
        set(w, 2, G + 2, -15, Material.END_ROD);
        set(w, -1, G + 2, -16, Material.GOLD_BLOCK);
        set(w, 1, G + 2, -16, Material.GOLD_BLOCK);
        set(w, 2, G + 1, -10, Material.LECTERN);
        box(w, -8, 4, -4, 4, G + 1, G + 2, Material.BOOKSHELF);
        box(w, 4, 4, 8, 4, G + 1, G + 2, Material.BOOKSHELF);
        pot(w, -6, G + 3, 4, Material.POPPY);
        pot(w, 6, G + 3, 4, Material.POPPY);
    }

    private static void guild(World w) {
        shell(w, 33, -13, 55, 13, 11, Material.CUT_SANDSTONE);
        for (int x = 33; x <= 55; x++) {
            set(w, x, G + 6, -13, Material.CHISELED_SANDSTONE);
            set(w, x, G + 6, 13, Material.CHISELED_SANDSTONE);
        }
        for (int z = -13; z <= 13; z++) {
            set(w, 33, G + 6, z, Material.CHISELED_SANDSTONE);
            set(w, 55, G + 6, z, Material.CHISELED_SANDSTONE);
        }
        for (int x : new int[]{33, 55}) {
            for (int z : new int[]{-13, 13}) {
                box(w, x, z, x, z, G + 1, G + 11, Material.CHISELED_SANDSTONE);
                set(w, x, G + 12, z, Material.RED_BANNER);
            }
        }
        for (int z : new int[]{-11, -6, 5, 9}) {
            win2Sill(w, 33, G + 3, z, BlockFace.WEST, Material.CUT_SANDSTONE);
            win2Sill(w, 33, G + 8, z, BlockFace.WEST, Material.CUT_SANDSTONE);
        }
        for (int x : new int[]{37, 42, 47}) {
            win2Sill(w, x, G + 3, -13, BlockFace.NORTH, Material.CUT_SANDSTONE);
            win2Sill(w, x, G + 3, 13, BlockFace.SOUTH, Material.CUT_SANDSTONE);
        }
        for (int z : new int[]{-8, -4, 4, 8})
            win2Sill(w, 55, G + 3, z, BlockFace.EAST, Material.CUT_SANDSTONE);
        for (int z = 0; z <= 2; z++)
            for (int y = 1; y <= 4; y++)
                set(w, 33, G + y, z, Material.AIR);
        for (int z = -1; z <= 2; z++) set(w, 33, G + 5, z, Material.CHISELED_SANDSTONE);
        set(w, 33, G + 1, -1, Material.CUT_SANDSTONE);
        set(w, 33, G + 1, 2, Material.CUT_SANDSTONE);
        set(w, 34, G + 4, -1, Material.GLOWSTONE);
        set(w, 34, G + 4, 2, Material.GLOWSTONE);
        for (int x = 34; x <= 54; x++) box(w, x, -12, x, 12, G + 11, G + 11, Material.DARK_OAK_PLANKS);
        for (int x = 37; x <= 51; x += 4)
            for (int z = -9; z <= 9; z += 6)
                set(w, x, G + 11, z, Material.GLOWSTONE);
        roof(w, 34, -12, 54, 12, G + 12, Material.RED_NETHER_BRICKS);
        guildInterior(w);
        for (int y = 12; y <= 15; y++) set(w, 55, G + y, 0, Material.BRICKS);
        set(w, 55, G + 16, 0, Material.CAMPFIRE);
    }

    private static void guildInterior(World w) {
        box(w, 35, 4, 35, 7, G + 1, G + 1, Material.OAK_PLANKS);
        box(w, 36, 4, 36, 7, G + 1, G + 1, Material.OAK_PLANKS);
        set(w, 35, G + 2, 5, Material.OAK_STAIRS);
        set(w, 35, G + 2, 6, Material.OAK_STAIRS);
        standingSign(w, 36, G + 2, 5, 8);
        set(w, 36, G + 2, 7, Material.LANTERN);
        for (int x = 39; x <= 46; x++) box(w, x, -12, x, -12, G + 1, G + 3, Material.LODESTONE);
        set(w, 38, G + 1, -12, Material.LODESTONE);
        set(w, 47, G + 1, -12, Material.LODESTONE);
        standingSign(w, 40, G + 4, -12, 0);
        standingSign(w, 42, G + 4, -12, 1);
        standingSign(w, 44, G + 4, -12, 0);
        standingSign(w, 46, G + 4, -12, 1);
        set(w, 38, G + 2, -12, Material.RED_BANNER);
        set(w, 47, G + 2, -12, Material.RED_BANNER);
        for (int x = 41; x <= 47; x++) set(w, x, G + 7, 0, Material.IRON_BARS);
        for (int z = -3; z <= 3; z++) set(w, 44, G + 7, z, Material.IRON_BARS);
        for (int y = 8; y <= 10; y++) set(w, 44, G + y, 0, Material.IRON_BARS);
        hangingLantern(w, 41, G + 6, 0);
        hangingLantern(w, 47, G + 6, 0);
        hangingLantern(w, 44, G + 6, 2);
        hangingLantern(w, 44, G + 6, -2);
        hangingLantern(w, 44, G + 6, 0);
        box(w, 54, -1, 54, 1, G + 1, G + 2, Material.BRICKS);
        set(w, 54, G + 2, 0, Material.CAMPFIRE);
        for (int tz : new int[]{-4, 4}) {
            for (int x = 39; x <= 47; x += 4) set(w, x, G + 1, tz, Material.DARK_OAK_LOG);
            box(w, 38, tz, 48, tz, G + 2, G + 2, Material.DARK_OAK_PLANKS);
            for (int x = 38; x <= 48; x += 2) {
                set(w, x, G + 1, tz - 1, Material.OAK_FENCE);
                set(w, x, G + 1, tz + 1, Material.OAK_FENCE);
            }
        }
        for (int x = 37; x <= 51; x++) {
            for (int z = -2; z <= 2; z++)
                set(w, x, G + 1, z, ((x + z) & 1) == 0 ? Material.RED_CARPET : Material.BLACK_CARPET);
        }
        for (int x = 34; x <= 36; x++) {
            set(w, x, G + 1, 0, Material.RED_CARPET);
            set(w, x, G + 1, 1, Material.RED_CARPET);
        }
        set(w, 35, G + 1, -4, Material.CARTOGRAPHY_TABLE);
        set(w, 35, G + 1, -6, Material.LOOM);
        set(w, 35, G + 2, -4, Material.END_ROD);
        set(w, 36, G + 1, -10, Material.LANTERN);
        for (int x = 34; x <= 54; x++) {
            if (x >= 43 && x <= 45) continue;
            for (int z = -12; z <= -7; z++) slab(w, x, G + 6, z, Material.DARK_OAK_SLAB);
        }
        for (int x = 36; x <= 52; x += 4) box(w, x, -7, x, -7, G + 1, G + 5, Material.DARK_OAK_LOG);
        for (int z = -1; z >= -6; z--) set(w, 53, G + (-z), z, Material.AIR);
        for (int k = 1; k <= 6; k++) stair(w, 53, G + k, 1 - k, BlockFace.NORTH, Material.DARK_OAK_STAIRS);
        for (int x = 47; x <= 52; x++) set(w, x, G + 7, -7, Material.STONE_BRICK_WALL);
        set(w, 34, G + 7, -12, Material.STONE_BRICK_WALL);
        for (int x = 36; x <= 41; x++) box(w, x, -12, x, -12, G + 7, G + 8, Material.BOOKSHELF);
        set(w, 37, G + 9, -12, Material.LANTERN);
        set(w, 48, G + 7, -10, Material.ENCHANTING_TABLE);
        set(w, 47, G + 7, -11, Material.BOOKSHELF);
        set(w, 49, G + 7, -11, Material.BOOKSHELF);
        for (int x = -6; x <= 6; x += 3) hangingLantern(w, x + 44, G + 5, -9);
        for (int x = 50; x <= 52; x++) set(w, x, G + 1, 9, Material.BARREL);
        set(w, 50, G + 2, 9, Material.BARREL);
        set(w, 51, G + 1, 10, Material.CHEST);
        set(w, 51, G + 1, 11, Material.CHEST);
        box(w, 52, 10, 52, 11, G + 1, G + 2, Material.DRIED_KELP_BLOCK);
        box(w, 53, 10, 53, 11, G + 1, G + 2, Material.HAY_BLOCK);
        for (int x = 37; x <= 49; x += 4) {
            set(w, x, G + 1, 12, Material.POLISHED_BLACKSTONE_WALL);
            set(w, x, G + 2, 12, Material.POLISHED_BLACKSTONE_WALL);
            set(w, x, G + 3, 12, Material.LANTERN);
            set(w, x, G + 1, -12, Material.POLISHED_BLACKSTONE_WALL);
            set(w, x, G + 2, -12, Material.POLISHED_BLACKSTONE_WALL);
            set(w, x, G + 3, -12, Material.LANTERN);
        }
    }

    private static void marketHall(World w) {
        clearArea(w, -55, -13, -33, 13, 13);
        pave(w, -55, -13, -33, 13);
        path(w, -54, -12, -34, 12);
        for (int x = -55; x <= -33; x++) {
            for (int z = -13; z <= 13; z++) {
                if (!(x == -55 || x == -33 || z == -13 || z == 13)) continue;
                if (x == -33 && z >= -1 && z <= 1) continue;
                set(w, x, G + 1, z, Material.STONE_BRICKS);
            }
        }
        for (int x = -55; x <= -33; x++) {
            for (int z = -13; z <= 13; z++) {
                if (!(x == -55 || x == -33 || z == -13 || z == 13)) continue;
                boolean corner = (x == -55 || x == -33) && (z == -13 || z == 13);
                boolean post = corner
                        || ((z == -13 || z == 13) && ((x + 55) % 4 == 0))
                        || ((x == -55 || x == -33) && ((z + 13) % 4 == 0));
                if (post && corner) box(w, x, z, x, z, G + 2, G + 9, Material.DARK_OAK_LOG);
                for (int y = 2; y <= 10; y++) {
                    Material m;
                    if (post) m = Material.DARK_OAK_LOG;
                    else if (y == 6 || y == 7) m = Material.DARK_OAK_PLANKS;
                    else m = Material.WHITE_TERRACOTTA;
                    set(w, x, G + y, z, m);
                }
            }
        }
        for (int z = -1; z <= 1; z++)
            for (int y = 1; y <= 4; y++)
                set(w, -33, G + y, z, Material.AIR);
        for (int z = -2; z <= 2; z++) set(w, -33, G + 5, z, Material.CHISELED_SANDSTONE);
        set(w, -33, G + 1, -2, Material.DARK_OAK_LOG);
        set(w, -33, G + 1, 2, Material.DARK_OAK_LOG);
        win2Sill(w, -55, G + 8, -8, BlockFace.WEST, Material.DARK_OAK_PLANKS);
        win2Sill(w, -55, G + 8, 0, BlockFace.WEST, Material.DARK_OAK_PLANKS);
        win2Sill(w, -33, G + 8, -8, BlockFace.EAST, Material.DARK_OAK_PLANKS);
        win2Sill(w, -33, G + 8, 4, BlockFace.EAST, Material.DARK_OAK_PLANKS);
        win2Sill(w, -53, G + 8, -13, BlockFace.NORTH, Material.DARK_OAK_PLANKS);
        win2Sill(w, -45, G + 8, -13, BlockFace.NORTH, Material.DARK_OAK_PLANKS);
        win2Sill(w, -53, G + 8, 13, BlockFace.SOUTH, Material.DARK_OAK_PLANKS);
        win2Sill(w, -45, G + 8, 13, BlockFace.SOUTH, Material.DARK_OAK_PLANKS);
        for (int z : new int[]{-6, 6}) {
            box(w, -52, z, -36, z, G + 10, G + 10, Material.DARK_OAK_LOG);
            for (int x = -50; x <= -38; x += 6) box(w, x, z, x, z, G + 1, G + 9, Material.DARK_OAK_LOG);
        }
        for (int x = -50; x <= -38; x += 6) box(w, x, -6, x, 6, G + 10, G + 10, Material.DARK_OAK_LOG);
        roof(w, -54, -12, -34, 12, G + 11, Material.BRICKS);
        for (int x = -50; x <= -38; x += 4) {
            hangingLantern(w, x, G + 9, -6);
            hangingLantern(w, x, G + 9, 6);
        }
        for (int x = -51; x <= -36; x += 4) {
            set(w, x, G + 10, 0, TownKit.chain());
            hangingLantern(w, x, G + 9, 0);
        }
        for (int i = 0; i < 3; i++) marketStall(w, -54, -8 + i * 6, true, i);
        marketStall(w, -48, -11, false, 3);
        marketStall(w, -42, -11, false, 4);
        marketStall(w, -48, 11, false, 5);
        box(w, -46, -3, -43, 0, G + 1, G + 1, Material.STONE_BRICKS);
        for (int z = -3; z <= 0; z++) {
            stair(w, -42, G + 2, z, BlockFace.EAST, Material.STONE_BRICK_STAIRS);
            stair(w, -47, G + 2, z, BlockFace.WEST, Material.STONE_BRICK_STAIRS);
        }
        for (int x = -46; x <= -43; x++) stair(w, x, G + 2, 1, BlockFace.SOUTH, Material.STONE_BRICK_STAIRS);
        set(w, -45, G + 2, -2, Material.GOLD_BLOCK);
        set(w, -44, G + 2, -2, Material.GOLD_BLOCK);
        box(w, -45, 0, -45, 0, G + 2, G + 4, Material.POLISHED_BLACKSTONE_WALL);
        set(w, -45, G + 5, 0, Material.BELL);
        standingSign(w, -44, G + 3, -2, 4);
        for (int x = -52; x <= -51; x++) {
            set(w, x, G + 1, 10, Material.BARREL);
            set(w, x, G + 1, 11, Material.CHEST);
        }
        set(w, -52, G + 2, 10, Material.BARREL);
        set(w, -51, G + 2, 11, Material.DRIED_KELP_BLOCK);
        box(w, -37, 8, -35, 12, G + 1, G + 2, Material.POLISHED_BLACKSTONE_WALL);
        set(w, -36, G + 2, 9, Material.WATER);
        set(w, -36, G + 2, 10, Material.WATER);
        set(w, -36, G + 2, 11, Material.WATER);
        set(w, -34, G + 1, 9, Material.CAULDRON);
        for (int x = -44; x <= -41; x++)
            for (int z = 4; z <= 7; z++)
                set(w, x, G + 1, z, Material.GRAY_WOOL);
        set(w, -42, G + 1, 5, Material.CAULDRON);
        set(w, -43, G + 1, 6, Material.LOOM);
    }

    private static void marketStall(World w, int cx, int cz, boolean alongZ, int idx) {
        Material awn = AWNINGS[idx % AWNINGS.length];
        for (int t = 0; t < 3; t++) {
            int x = alongZ ? cx : cx + t;
            int z = alongZ ? cz + t : cz;
            set(w, x, G + 1, z, Material.OAK_PLANKS);
            set(w, x, G + 2, z, STALL_GOODS[(idx * 3 + t) % STALL_GOODS.length]);
        }
        int ox = alongZ ? 1 : 0;
        int oz = alongZ ? 0 : (cz < 0 ? 1 : -1);
        for (int t = 0; t < 3; t++) {
            int sx = (alongZ ? cx : cx + t) + ox;
            int sz = (alongZ ? cz + t : cz) + oz;
            stair(w, sx, G + 2, sz, alongZ ? BlockFace.EAST : (cz < 0 ? BlockFace.SOUTH : BlockFace.NORTH), Material.OAK_STAIRS);
            set(w, sx, G + 4, sz, awn);
            set(w, sx, G + 3, sz, Material.OAK_HANGING_SIGN);
        }
        for (int t = 0; t < 3; t++) {
            int bx = alongZ ? cx : cx + t;
            int bz = alongZ ? cz + t : cz;
            set(w, bx, G + 4, bz, awn);
        }
    }

    private static void portalArcade(World w) {
        clearArea(w, -14, -43, 14, -33, 8);
        pave(w, -14, -43, 14, -33);
        box(w, -14, -43, 14, -39, G + 1, G + 1, Material.CUT_SANDSTONE);
        for (int x = -13; x <= 13; x++) slab(w, x, G + 1, -38, Material.CUT_SANDSTONE);
        box(w, -14, -43, 14, -43, G + 1, G + 7, Material.CUT_SANDSTONE);
        for (int x : new int[]{-14, 14}) box(w, x, -43, x, -33, G + 1, G + 7, Material.CUT_SANDSTONE);
        win2Sill(w, -14, G + 3, -41, BlockFace.WEST, Material.CUT_SANDSTONE);
        win2Sill(w, 14, G + 3, -41, BlockFace.EAST, Material.CUT_SANDSTONE);
        win2Sill(w, -14, G + 3, -36, BlockFace.WEST, Material.CUT_SANDSTONE);
        win2Sill(w, 14, G + 3, -36, BlockFace.EAST, Material.CUT_SANDSTONE);
        for (int i = 0; i < 8; i++) {
            int lx = -12 + i * 3;
            Material col = FLOOR_COLORS[i];
            for (int y = 1; y <= 5; y++) {
                set(w, lx, G + y, -42, Material.OBSIDIAN);
                set(w, lx + 2, G + y, -42, Material.OBSIDIAN);
            }
            box(w, lx, -42, lx + 2, -42, G + 5, G + 5, Material.OBSIDIAN);
            for (int y = 1; y <= 4; y++) set(w, lx + 1, G + y, -42, Material.AIR);
            for (int x = lx; x <= lx + 2; x++) {
                set(w, x, G + 2, -42, col);
                set(w, x, G + 2, -41, col);
            }
            wallSignSouth(w, lx + 1, G + 4, -42, i);
            set(w, lx + 1, G + 2, -40, Material.END_ROD);
            set(w, lx, G + 2, -40, Material.GOLD_BLOCK);
            set(w, lx + 2, G + 2, -40, Material.GOLD_BLOCK);
        }
        for (int x : new int[]{-10, -4, 2, 8}) {
            box(w, x, -40, x, -40, G + 1, G + 3, Material.POLISHED_BLACKSTONE_WALL);
            set(w, x, G + 4, -40, Material.LANTERN);
        }
        for (int x = -12; x <= 12; x += 2) {
            slab(w, x, G + 1, -33, Material.CUT_SANDSTONE_SLAB);
            box(w, x, -33, x, -33, G + 2, G + 4, Material.CUT_SANDSTONE);
            set(w, x, G + 5, -33, Material.SANDSTONE_WALL);
        }
        for (int x : new int[]{-13, 13}) box(w, x, -33, x, -33, G + 1, G + 5, Material.CUT_SANDSTONE);
        box(w, -14, -43, 14, -33, G + 6, G + 6, Material.CUT_SANDSTONE);
        for (int x = -14; x <= 14; x++) {
            set(w, x, G + 7, -33, Material.SANDSTONE_WALL);
            set(w, x, G + 7, -43, Material.SANDSTONE_WALL);
        }
        for (int z = -43; z <= -33; z++) {
            set(w, -14, G + 7, z, Material.SANDSTONE_WALL);
            set(w, 14, G + 7, z, Material.SANDSTONE_WALL);
        }
        for (int x : new int[]{-14, 0, 14})
            for (int z : new int[]{-43, -33})
                set(w, x, G + 8, z, Material.GOLD_BLOCK);
        for (int x = -4; x <= 4; x++) {
            stair(w, x, G + 1, -35, BlockFace.NORTH, Material.DARK_OAK_STAIRS);
            set(w, x, G + 1, -36, Material.GRAY_CARPET);
            stair(w, x, G + 1, -37, BlockFace.SOUTH, Material.DARK_OAK_STAIRS);
        }
        for (int x : new int[]{-12, -6, 6, 12}) set(w, x, G + 2, -35, Material.END_ROD);
        for (int x : new int[]{-9, -3, 3, 9}) {
            set(w, x, G + 2, -34, Material.END_ROD);
            set(w, x, G + 2, -32, Material.END_ROD);
        }
    }

    private static void plazaFinal(World w) {
        for (int z = 6; z <= 9; z++) set(w, 0, G + 2, z, Material.RED_CARPET);
        for (int z = 10; z <= 16; z++) set(w, 0, G + 1, z, Material.RED_CARPET);
                for (int z = 16; z <= 20; z++) set(w, -7, G + 1, z, Material.RED_CARPET);
        for (int x : new int[]{-3, 3}) {
            bannerPole(w, x, 26);
            bannerPole(w, x, -26);
        }
        for (int z : new int[]{-3, 3}) {
            bannerPole(w, 26, z);
            bannerPole(w, -26, z);
        }
        heroStatue(w);
    }

    private static void bannerPole(World w, int x, int z) {
        for (int y = 1; y <= 6; y++) set(w, x, G + y, z, Material.DARK_OAK_FENCE);
        set(w, x, G + 7, z, Material.RED_BANNER);
        set(w, x, G + 8, z, Material.GOLD_BLOCK);
        set(w, x, G + 9, z, Material.END_ROD);
    }

    private static void heroStatue(World w) {
        int cx = -18, cz = 0;
        for (int x = cx - 1; x <= cx + 1; x++)
            for (int z = cz - 1; z <= cz + 1; z++)
                box(w, x, z, x, z, G + 1, G + 2, Material.MOSSY_STONE_BRICKS);
        for (int x = cx - 1; x <= cx + 1; x++) {
            slab(w, x, G + 3, cz - 1, Material.STONE_BRICK_SLAB);
            slab(w, x, G + 3, cz + 1, Material.STONE_BRICK_SLAB);
        }
        slab(w, cx - 1, G + 3, cz, Material.STONE_BRICK_SLAB);
        slab(w, cx + 1, G + 3, cz, Material.STONE_BRICK_SLAB);
        for (int y = 3; y <= 5; y++) set(w, cx, G + y, cz, Material.BLACKSTONE_WALL);
        set(w, cx, G + 6, cz, Material.POLISHED_BLACKSTONE_STAIRS);
        set(w, cx, G + 7, cz, Material.POLISHED_BLACKSTONE_SLAB);
        TrapDoor l = (TrapDoor) Bukkit.createBlockData(Material.SPRUCE_TRAPDOOR);
        l.setFacing(BlockFace.EAST);
        l.setOpen(true);
        w.getBlockAt(cx - 1, G + 4, cz).setBlockData(l);
        TrapDoor r = (TrapDoor) Bukkit.createBlockData(Material.SPRUCE_TRAPDOOR);
        r.setFacing(BlockFace.WEST);
        r.setOpen(true);
        w.getBlockAt(cx + 1, G + 4, cz).setBlockData(r);
        for (int y = 3; y <= 5; y++) set(w, cx, G + y, cz + 1, Material.GOLD_BLOCK);
        set(w, cx, G + 6, cz + 1, Material.GOLD_BLOCK);
        for (int x : new int[]{cx - 1, cx + 1})
            for (int z : new int[]{cz - 1, cz + 1})
                set(w, x, G + 3, z, Material.LANTERN);
        lampTall(w, cx - 4, cz);
        lampTall(w, cx + 4, cz);
    }
}
