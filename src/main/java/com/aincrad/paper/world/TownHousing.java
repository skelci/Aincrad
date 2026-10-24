package com.aincrad.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;

import java.util.ArrayList;
import java.util.List;

import static com.aincrad.paper.world.TownKit.*;
public final class TownHousing {

    private TownHousing() {}

    private static final Material AIR = Material.AIR;

    private static final Material BARREL = Material.BARREL;
    private static final Material BIRCH_LOG = Material.BIRCH_LOG;
    private static final Material BOOKSHELF = Material.BOOKSHELF;
    private static final Material BRICKS = Material.BRICKS;
    private static final Material CALCITE = Material.CALCITE;
    private static final Material CAMPFIRE = Material.CAMPFIRE;
    private static final Material CAULDRON = Material.CAULDRON;
    private static final Material CHEST = Material.CHEST;
    private static final Material CRAFTING_TABLE = Material.CRAFTING_TABLE;
    private static final Material DARK_OAK_LOG = Material.DARK_OAK_LOG;
    private static final Material DARK_OAK_SLAB = Material.DARK_OAK_SLAB;
    private static final Material DEEPSLATE_BRICKS = Material.DEEPSLATE_BRICKS;
    private static final Material FLOWER_POT = Material.FLOWER_POT;
    private static final Material HAY_BLOCK = Material.HAY_BLOCK;
    private static final Material LANTERN = Material.LANTERN;
    private static final Material LILY_PAD = Material.LILY_PAD;
    private static final Material OAK_DOOR = Material.OAK_DOOR;
    private static final Material OAK_FENCE = Material.OAK_FENCE;
    private static final Material OAK_SLAB = Material.OAK_SLAB;
    private static final Material OAK_STAIRS = Material.OAK_STAIRS;
    private static final Material QUARTZ_BLOCK = Material.QUARTZ_BLOCK;
    private static final Material QUARTZ_PILLAR = Material.QUARTZ_PILLAR;
    private static final Material RED_BED = Material.RED_BED;
    private static final Material RED_CARPET = Material.RED_CARPET;
    private static final Material RED_WOOL = Material.RED_WOOL;
    private static final Material SANDSTONE = Material.SANDSTONE;
    private static final Material SMOKER = Material.SMOKER;
    private static final Material SMOOTH_SANDSTONE = Material.SMOOTH_SANDSTONE;
    private static final Material SPRUCE_FENCE = Material.SPRUCE_FENCE;
    private static final Material SPRUCE_PLANKS = Material.SPRUCE_PLANKS;
    private static final Material SPRUCE_SLAB = Material.SPRUCE_SLAB;
    private static final Material STONE_BRICKS = Material.STONE_BRICKS;
    private static final Material STONE_BRICK_WALL = Material.STONE_BRICK_WALL;
    private static final Material TUFF_BRICKS = Material.TUFF_BRICKS;
    private static final Material WATER = Material.WATER;
    private static final Material WHITE_CONCRETE = Material.WHITE_CONCRETE;
    private static final Material WHITE_TERRACOTTA = Material.WHITE_TERRACOTTA;

    private static final Material[] LIGHT = PLASTER;

    public static void build(World w) {
        row(w, 32, 52, -32, BlockFace.SOUTH, 11, 101, null, 3);
        rowV(w, 52, -52, -43, BlockFace.EAST, 10, 102, null);
        rowV(w, 52, -95, -69, BlockFace.EAST, 11, 103, null);
        row(w, 32, 52, -58, BlockFace.SOUTH, 11, 104, null, 0);
        row(w, 32, 52, -106, BlockFace.NORTH, 11, 105, null, 0);
        rowV(w, 58, -52, -32, BlockFace.WEST, 11, 106, null);
        rowV(w, 106, -52, -32, BlockFace.EAST, 11, 107, null);
        row(w, 69, 95, -52, BlockFace.NORTH, 10, 108, null, 0);
        row(w, 70, 94, -32, BlockFace.SOUTH, 11, 139, null, 0);
        rowV(w, 58, -106, -58, BlockFace.WEST, 11, 109, null);
        rowV(w, 106, -106, -58, BlockFace.EAST, 11, 110, null);
        row(w, 69, 95, -58, BlockFace.SOUTH, 11, 111, null, 0);
        row(w, 69, 95, -106, BlockFace.NORTH, 11, 112, null, 0);
        rowV(w, 58, 32, 52, BlockFace.WEST, 11, 113, null);
        rowV(w, 106, 32, 52, BlockFace.EAST, 11, 114, null);
        row(w, 69, 95, 52, BlockFace.SOUTH, 11, 115, null, 0);
        row(w, 32, 52, 58, BlockFace.NORTH, 11, 116, null, 0);
        row(w, 32, 52, 106, BlockFace.SOUTH, 11, 117, null, 0);
        rowV(w, 52, 69, 95, BlockFace.EAST, 11, 118, null);
        row(w, 58, 106, 58, BlockFace.NORTH, 11, 119, new int[][]{{73, 74}}, 1);
        rowV(w, 58, 70, 106, BlockFace.WEST, 11, 120, new int[][]{{88, 89}});
        rowV(w, -52, 32, 52, BlockFace.WEST, 10, 121, null);
        row(w, -42, -32, 32, BlockFace.NORTH, 10, 122, null, 0);
        row(w, -42, -32, 52, BlockFace.SOUTH, 10, 123, null, 0);
        rowV(w, -58, 32, 52, BlockFace.EAST, 11, 124, null);
        rowV(w, -106, 32, 52, BlockFace.WEST, 11, 125, null);
        row(w, -95, -69, 32, BlockFace.NORTH, 10, 126, null, 0);
        row(w, -95, -69, 52, BlockFace.SOUTH, 10, 127, null, 0);
        row(w, -52, -32, -32, BlockFace.SOUTH, 11, 128, null, 0);
        rowV(w, -32, -52, -43, BlockFace.EAST, 10, 129, null);
        rowV(w, -58, -52, -32, BlockFace.EAST, 11, 130, null);
        rowV(w, -106, -52, -32, BlockFace.WEST, 11, 131, null);
        row(w, -95, -69, -52, BlockFace.NORTH, 11, 132, null, 0);
        rowV(w, -32, -106, -58, BlockFace.EAST, 11, 133, null);
        row(w, -52, -43, -58, BlockFace.SOUTH, 11, 134, null, 0);
        row(w, -105, -69, -58, BlockFace.SOUTH, 11, 135, null, 0);
        rowV(w, -58, -106, -58, BlockFace.EAST, 11, 136, new int[][]{{-80, -79}});
        row(w, -52, -32, 106, BlockFace.SOUTH, 11, 137, null, 0);
        rowV(w, -52, 73, 95, BlockFace.WEST, 11, 138, null);

        well(w, 80, -37);
        notice(w, 85, -34);
        bench(w, 83, -39, BlockFace.NORTH);
        lamp(w, 76, -34);
        cart(w, 78, 36);
        notice(w, 84, 35);
        bench(w, 74, 34, BlockFace.SOUTH);
        lamp(w, 75, 39);
        cart(w, 82, -84);
        notice(w, 76, -84);
        lampTall(w, 82, -78);
        notice(w, 37, -82);
        bench(w, 38, -86, BlockFace.EAST);
        lamp(w, 37, -76);

        parkP1(w);
        parkP2(w);
        parkP3(w);
        refreshPanes(w);
    }

    private static void row(World w, int a1, int a2, int pf, BlockFace face, int depth,
                            int sb, int[][] alley, int showCount) {
        rowCore(w, a1, a2, pf, face, depth, sb, alley, showCount, true);
    }

    private static void rowV(World w, int pf, int a1, int a2, BlockFace face, int depth,
                             int sb, int[][] alley) {
        rowCore(w, a1, a2, pf, face, depth, sb, alley, 0, false);
    }

    private static void rowCore(World w, int a1, int a2, int pf, BlockFace face, int depth,
                                int sb, int[][] alley, int showCount, boolean runX) {
        int pin = (face == BlockFace.SOUTH || face == BlockFace.EAST) ? -1 : 1;
        int[][] out = plan(a1, a2, pf, sb, alley, showCount, showCount > 0 ? (a2 - a1 + 1 == 21 ? 7 : 9) : 0);
        List<int[]> units = new ArrayList<>();
        List<int[]> alleys = new ArrayList<>();
        for (int[] e : out) {
            if (e[0] == 1) units.add(e);
            else alleys.add(e);
        }
        for (int[] u : units) {
            int sd = hash(u[1] * 5 + sb, pf * 3 + depth);
            int h = u[3] * 4;
            unitShell(w, u[1], u[2], pf, pin, runX, depth, h,
                    LIGHT[Math.abs(sd >>> 1) % LIGHT.length]);
        }
        for (int i = 0; i < units.size(); i++) {
            int[] u = units.get(i);
            boolean showcase = showCount > 0 && i < showCount;
            int nj = 8;
            if (i > 0) nj = Math.max(nj, units.get(i - 1)[3] * 4);
            if (i < units.size() - 1) nj = Math.max(nj, units.get(i + 1)[3] * 4);
            decorate(w, u[1], u[2], u[3] * 4, showcase, pf, pin, runX, face, depth, sb, nj);
        }
        for (int[] al : alleys) {
            int[] c1 = at(al[1], 0, pf, pin, runX);
            int[] c2 = at(al[2], depth - 1, pf, pin, runX);
            pave(w, Math.min(c1[0], c2[0]), Math.min(c1[1], c2[1]), Math.max(c1[0], c2[0]), Math.max(c1[1], c2[1]));
            int[] cm = at(al[1], depth - 1, pf, pin, runX);
            lamp(w, cm[0], cm[1]);
        }
        if (!units.isEmpty()) {
            int[] u0 = units.get(0);
            int[] e1 = at(u0[1], 5, pf, pin, runX);
            BlockFace s1 = runX ? BlockFace.WEST : BlockFace.NORTH;
            win2(w, e1[0], G + 6, e1[1], s1);
            if (u0[3] == 3) win2(w, e1[0], G + 10, e1[1], s1);
            int[] un = units.get(units.size() - 1);
            int[] e2 = at(un[2], 5, pf, pin, runX);
            BlockFace s2 = runX ? BlockFace.EAST : BlockFace.SOUTH;
            win2(w, e2[0], G + 6, e2[1], s2);
            if (un[3] == 3) win2(w, e2[0], G + 10, e2[1], s2);
        }
    }

    private static int[] at(int r, int k, int pf, int pin, boolean runX) {
        int p = pf + pin * k;
        return runX ? new int[]{r, p} : new int[]{p, r};
    }

    private static void unitShell(World w, int a1, int a2, int pf, int pin, boolean runX,
                                  int depth, int h, Material wallMat) {
        int[] c1 = at(a1, 0, pf, pin, runX);
        int[] c2 = at(a2, depth - 1, pf, pin, runX);
        int x1 = Math.min(c1[0], c2[0]), x2 = Math.max(c1[0], c2[0]);
        int z1 = Math.min(c1[1], c2[1]), z2 = Math.max(c1[1], c2[1]);
        pave(w, x1, z1, x2, z2);
        clearArea(w, x1, z1, x2, z2, h);
        for (int y = 1; y <= h; y++) {
            for (int x = x1; x <= x2; x++) {
                set(w, x, G + y, z1, wallMat);
                set(w, x, G + y, z2, wallMat);
            }
            for (int z = z1; z <= z2; z++) {
                set(w, x1, G + y, z, wallMat);
                set(w, x2, G + y, z, wallMat);
            }
        }
    }

    private static void decorate(World w, int a1, int a2, int h, boolean showcase, int pf, int pin,
                                 boolean runX, BlockFace face, int depth, int sb, int njH) {
        int sd = hash(a1 * 7 + sb, a2 * 11 + pf);
        int stories = h / 4;
        int ph = 1 + ((sd >>> 5) & 1);
        int[] f1 = at(a1, 0, pf, pin, runX);
        int[] f2 = at(a2, 0, pf, pin, runX);
        int fx1 = f1[0], fz1 = f1[1];
        for (int r = a1; r <= a2; r++) {
            int[] c = at(r, 0, pf, pin, runX);
            for (int y = 1; y <= ph; y++) set(w, c[0], G + y, c[1], STONE_BRICKS);
        }
        for (int y = 1; y <= h; y++) {
            set(w, f1[0], G + y, f1[1], QUARTZ_PILLAR);
            set(w, f2[0], G + y, f2[1], QUARTZ_PILLAR);
        }
        int rC = (a1 + a2) / 2;
        int[] dc = at(rC, 0, pf, pin, runX);
        boolean wood = (sd >>> 2) % 5 == 0;
        set(w, dc[0], G + 1, dc[1], AIR);
        set(w, dc[0], G + 2, dc[1], AIR);
        set(w, dc[0], G + 3, dc[1], LANTERN);
        int[] ab = at(rC, 1, pf, pin, runX);
        set(w, ab[0], G + 1, ab[1], AIR);
        set(w, ab[0], G + 2, ab[1], AIR);
        int pxA = -1;
        if (rC + 3 <= a2 - 1) pxA = rC + 2;
        else if (rC + 2 <= a2 - 1) pxA = rC + 1;
        int pxB = -1;
        if (rC - 4 >= a1 + 1) pxB = rC - 4;
        else if (rC - 3 >= a1 + 1) pxB = rC - 3;
        if (pxA != -1) {
            int[] c = at(pxA, 0, pf, pin, runX);
            win2Sill(w, c[0], G + 2, c[1], face, STONE_BRICKS);
        }
        if (pxB != -1) {
            int[] c = at(pxB, 0, pf, pin, runX);
            win2Sill(w, c[0], G + 2, c[1], face, STONE_BRICKS);
        }
        int upx = Math.min(rC, a2 - 2);
        int[] uc = at(upx, 0, pf, pin, runX);
        win2Sill(w, uc[0], G + 6, uc[1], face, QUARTZ_BLOCK);
        if (stories == 3) win2Sill(w, uc[0], G + 10, uc[1], face, QUARTZ_BLOCK);
        int[] bc = at(rC, depth - 1, pf, pin, runX);
        win2(w, bc[0], G + 6, bc[1], face.getOppositeFace());
        if ((((sd >>> 7) & 3) == 0 || showcase) && !((pxA != -1 && pxA <= rC - 2 && rC - 2 <= pxA + 1))
                && rC - 2 != rC) {
            int[] pc = at(rC - 2, 0, pf, pin, runX);
            planter(w, pc[0], pc[1], pick(FLOWERS, sd));
        }
        if (pxA != -1 && (pxA < rC - 2 || pxA > rC + 2) && (pxA + 1 < rC + 2 || pxA > rC + 2)) {
            int[] pc = at(rC + 2, 0, pf, pin, runX);
            if (pxA != rC + 2 && pxA != rC + 1) planter(w, pc[0], pc[1], pick(FLOWERS, sd + 3));
        }
        if (wood && !showcase) {
            int[] lc = at(pxA != -1 ? pxA : rC, 1, pf, pin, runX);
            set(w, lc[0], G + 1, lc[1], LANTERN);
        }
        int[] c1 = at(a1, 0, pf, pin, runX);
        int[] c2 = at(a2, depth - 1, pf, pin, runX);
        int x1 = Math.min(c1[0], c2[0]), x2 = Math.max(c1[0], c2[0]);
        int z1 = Math.min(c1[1], c2[1]), z2 = Math.max(c1[1], c2[1]);
        if (showcase) {
            roof(w, x1, z1, x2, z2, G + h + 1, pick(ROOFS, sd + 11));
            interior(w, a1, a2, pf, pin, runX, face, depth);
        } else {
            roof(w, x1, z1, x2, z2, G + h, pick(ROOFS, sd + 11));
            for (int yf = 5; yf + 2 <= h; yf += 4)
                for (int r = a1 + 1; r <= a2 - 1; r++)
                    for (int k = 2; k <= 8; k++) {
                        int[] s = at(r, k, pf, pin, runX);
                        slab(w, s[0], G + yf, s[1], OAK_SLAB);
                    }
        }
        if (((sd >>> 9) & 15) < 6 || showcase) {
            int th = Math.max(h, njH) + 4;
            int[] cm = at(a2, depth / 2, pf, pin, runX);
            chimney(w, cm[0], cm[1], th, BRICKS);
        }
        if (((sd >>> 12) & 1) == 1) {
            int[] jl = at(a2, 0, pf, pin, runX);
            set(w, jl[0], G + 4, jl[1], LANTERN);
        }
    }

    private static void openDoor(World w, int x, int z) {
        org.bukkit.block.Block b = w.getBlockAt(x, G + 1, z);
        if (!(b.getBlockData() instanceof Door d)) return;
        d.setOpen(true);
        b.setBlockData(d);
    }

    private static void interior(World w, int a1, int a2, int pf, int pin, boolean runX,
                                 BlockFace face, int depth) {
        BlockFace in = face.getOppositeFace();
        int rC = (a1 + a2) / 2;
        for (int k = 1; k <= 3; k++) {
            int[] c = at(rC, k, pf, pin, runX);
            set(w, c[0], G + 1, c[1], RED_CARPET);
        }
        for (int dr = -1; dr <= 1; dr++)
            for (int y = 1; y <= 4; y++) {
                int[] c = at(rC + dr, depth - 1, pf, pin, runX);
                set(w, c[0], G + y, c[1], BRICKS);
            }
        int[] fc = at(rC, depth - 1, pf, pin, runX);
        set(w, fc[0], G + 1, fc[1], CAMPFIRE);
        for (int y = 9; y <= 12; y++) set(w, fc[0], G + y, fc[1], BRICKS);
        set(w, fc[0], G + 13, fc[1], CAMPFIRE);
        int rA = a1 + 1;
        for (int r = a1 + 1; r <= a2 - 1; r++)
            for (int k = 2; k <= 6; k++) {
                int[] c = at(r, k, pf, pin, runX);
                slab(w, c[0], G + 5, c[1], OAK_SLAB);
            }
        int[] s1 = at(rA, 7, pf, pin, runX);
        int[] s2 = at(rA, 8, pf, pin, runX);
        BlockFace up = runX ? (pin < 0 ? BlockFace.NORTH : BlockFace.SOUTH)
                : (pin < 0 ? BlockFace.WEST : BlockFace.EAST);
        stair(w, s1[0], G + 1, s1[1], up);
        stair(w, s2[0], G + 2, s2[1], up);
        for (int r = a1 + 1; r <= a2 - 1; r++) {
            int[] c = at(r, 4, pf, pin, runX);
            set(w, c[0], G + 6, c[1], LANTERN);
        }
        set(w, at(rA, 5, pf, pin, runX)[0], G + 6, at(rA, 5, pf, pin, runX)[1], BARREL);
        org.bukkit.block.Block bh = w.getBlockAt(at(rA, depth - 2, pf, pin, runX)[0], G + 1,
                at(rA, depth - 2, pf, pin, runX)[1]);
        Bed bd = (Bed) Bukkit.createBlockData(RED_BED);
        bd.setFacing(in);
        bd.setPart(Bed.Part.HEAD);
        bh.setBlockData(bd);
        int[] bf = at(rA, depth - 3, pf, pin, runX);
        Bed bd2 = (Bed) Bukkit.createBlockData(RED_BED);
        bd2.setFacing(in);
        bd2.setPart(Bed.Part.FOOT);
        w.getBlockAt(bf[0], G + 1, bf[1]).setBlockData(bd2);
        for (int k = depth - 4; k <= depth - 5; k--) {
            int[] c = at(rA, k, pf, pin, runX);
            set(w, c[0], G + 1, c[1], RED_WOOL);
            set(w, c[0] + (runX ? 1 : 0), G + 1, c[1] + (runX ? 0 : 1), RED_WOOL);
        }
        int[] bc = at(rA + 1, depth - 2, pf, pin, runX);
        set(w, bc[0], G + 1, bc[1], LANTERN);
        int rB = a2 - 1;
        int[] bs = at(rB, depth - 1, pf, pin, runX);
        set(w, bs[0], G + 1, bs[1], BOOKSHELF);
        set(w, bs[0], G + 2, bs[1], BOOKSHELF);
        set(w, bs[0], G + 3, bs[1], LANTERN);
        int[] bb = at(rB, depth - 2, pf, pin, runX);
        set(w, bb[0], G + 1, bb[1], BARREL);
        set(w, bb[0], G + 2, bb[1], BARREL);
        int[] t1 = at(rC, 5, pf, pin, runX);
        set(w, t1[0], G + 1, t1[1], OAK_FENCE);
        pot(w, t1[0], G + 2, t1[1], Material.POPPY);
        BlockFace runPos = runX ? BlockFace.EAST : BlockFace.SOUTH;
        int[] ch1 = at(rC - 1, 5, pf, pin, runX);
        int[] ch2 = at(rC + 1, 5, pf, pin, runX);
        stair(w, ch1[0], G + 1, ch1[1], runPos);
        stair(w, ch2[0], G + 1, ch2[1], runPos.getOppositeFace());
        int[] k1 = at(rB, 7, pf, pin, runX);
        set(w, k1[0], G + 1, k1[1], BARREL);
        int[] k2 = at(rB, 6, pf, pin, runX);
        set(w, k2[0], G + 1, k2[1], CHEST);
        int[] k3 = at(rB, 5, pf, pin, runX);
        set(w, k3[0], G + 1, k3[1], CAULDRON);
        int[] k4 = at(rB, 4, pf, pin, runX);
        set(w, k4[0], G + 1, k4[1], SMOKER);
        int[] k5 = at(rB, 3, pf, pin, runX);
        set(w, k5[0], G + 1, k5[1], CRAFTING_TABLE);
    }

    private static int[][] plan(int a1, int a2, int pf, int sb, int[][] alley, int showCount, int showWidth) {
        List<int[]> out = new ArrayList<>();
        int a = a1;
        for (int i = 0; i < showCount && a <= a2; i++) {
            int u2 = Math.min(a + showWidth - 1, a2);
            if (u2 - a + 1 < 5) break;
            out.add(new int[]{1, a, u2, 2});
            a = u2 + 1;
        }
        int na = a1 + 34 + Math.abs(hash(a1 + sb, pf)) % 9;
        while (a <= a2) {
            int fs = nextAlley(alley, a);
            if (fs != -1 && a == fs) {
                out.add(new int[]{-2, a, a + 1, 0});
                a += 2;
                continue;
            }
            if (a >= na && a2 - a >= 4) {
                out.add(new int[]{-2, a, a + 1, 0});
                a += 2;
                na = a + 34 + Math.abs(hash(a, pf)) % 9;
                continue;
            }
            int limit = fs != -1 ? fs - 1 : a2;
            if (limit - a + 1 < 5) {
                if (!out.isEmpty() && out.get(out.size() - 1)[0] == 1)
                    out.get(out.size() - 1)[2] = limit;
                else if (limit - a + 1 >= 3) out.add(new int[]{1, a, limit, 2});
                a = limit + 1;
                continue;
            }
            int sd = hash(a * 3 + sb, pf + sb * 5);
            int wdt = 7 + Math.abs(sd) % 3;
            if (a + wdt - 1 > limit) wdt = limit - a + 1;
            if (wdt < 5) {
                if (!out.isEmpty() && out.get(out.size() - 1)[0] == 1)
                    out.get(out.size() - 1)[2] = limit;
                else out.add(new int[]{1, a, limit, 2});
                a = limit + 1;
                continue;
            }
            int stories = ((sd >>> 4) & 3) == 0 ? 3 : 2;
            out.add(new int[]{1, a, a + wdt - 1, stories});
            a += wdt;
            if (fs != -1 && a == fs) {
                out.add(new int[]{-2, a, a + 1, 0});
                a += 2;
            }
        }
        return out.toArray(new int[0][]);
    }

    private static int nextAlley(int[][] alley, int a) {
        if (alley == null) return -1;
        for (int[] al : alley) {
            if (al[1] >= a) return al[0];
            if (al[0] <= a && a <= al[1]) return a;
        }
        return -1;
    }

    private static void parkP1(World w) {
        hedge(w, -96, -96, -69, -69, 2, new int[]{-76, -80}, new int[]{-75, -79});
        path(w, -84, -80, -69, -79);
        lamp(w, -70, -80);
        lamp(w, -64, -80);
        pond(w, -90, -88, -85, -85, STONE_BRICKS);
        bench(w, -88, -84, BlockFace.SOUTH);
        bench(w, -86, -89, BlockFace.NORTH);
        bench(w, -91, -86, BlockFace.EAST);
        bench(w, -84, -87, BlockFace.WEST);
        tree(w, -93, -74, 4);
        tree(w, -73, -93, 5);
        flowers(w, -95, -95, -87, -90);
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) {
                set(w, -77 + dx, G + 4, -76 + dz, SPRUCE_SLAB);
            }
        set(w, -77, G + 1, -77, SPRUCE_FENCE);
        set(w, -77, G + 2, -77, SPRUCE_FENCE);
        set(w, -77, G + 3, -77, SPRUCE_FENCE);
        set(w, -77, G + 1, -75, SPRUCE_FENCE);
        set(w, -77, G + 2, -75, SPRUCE_FENCE);
        set(w, -77, G + 3, -75, SPRUCE_FENCE);
        set(w, -75, G + 1, -77, SPRUCE_FENCE);
        set(w, -75, G + 2, -77, SPRUCE_FENCE);
        set(w, -75, G + 3, -77, SPRUCE_FENCE);
        set(w, -75, G + 1, -75, SPRUCE_FENCE);
        set(w, -75, G + 2, -75, SPRUCE_FENCE);
        set(w, -75, G + 3, -75, SPRUCE_FENCE);
        set(w, -76, G + 3, -76, LANTERN);
    }

    private static void parkP2(World w) {
        hedge(w, 69, 69, 101, 101, 2, new int[]{73, 88}, new int[]{74, 89});
        path(w, 73, 68, 74, 72);
        path(w, 69, 88, 72, 89);
        lamp(w, 75, 71);
        lamp(w, 71, 90);
        for (int x = 74; x <= 83; x++)
            for (int z = 78; z <= 83; z++) set(w, x, G, z, WATER);
        for (int x = 84; x <= 89; x++)
            for (int z = 78; z <= 81; z++) set(w, x, G, z, WATER);
        for (int x = 73; x <= 90; x++)
            for (int z = 77; z <= 84; z++)
                if (!isP2Pond(x, z) && (isP2Pond(x - 1, z) || isP2Pond(x + 1, z)
                        || isP2Pond(x, z - 1) || isP2Pond(x, z + 1)))
                    set(w, x, G, z, DEEPSLATE_BRICKS);
        set(w, 75, G + 1, 79, LILY_PAD);
        set(w, 80, G + 1, 82, LILY_PAD);
        set(w, 86, G + 1, 79, LILY_PAD);
        for (int x = 86; x <= 87; x++) {
            slab(w, x, G + 2, 78, OAK_SLAB);
            slab(w, x, G + 2, 79, OAK_SLAB);
            slab(w, x, G + 2, 80, OAK_SLAB);
            slab(w, x, G + 2, 81, OAK_SLAB);
            stair(w, x, G + 1, 77, BlockFace.NORTH);
            stair(w, x, G + 1, 82, BlockFace.SOUTH);
        }
        for (int z = 78; z <= 81; z++) {
            set(w, 85, G + 3, z, STONE_BRICK_WALL);
            set(w, 88, G + 3, z, STONE_BRICK_WALL);
        }
        bench(w, 76, 77, BlockFace.SOUTH);
        bench(w, 81, 77, BlockFace.SOUTH);
        bench(w, 73, 80, BlockFace.EAST);
        bench(w, 90, 80, BlockFace.WEST);
        bench(w, 78, 84, BlockFace.NORTH);
        bench(w, 81, 84, BlockFace.NORTH);
        willow(w, 92, 86);
        willow(w, 72, 95);
        willow(w, 95, 73);
        tree(w, 97, 96, 4);
        picnic(w, 92, 90);
        picnic(w, 95, 92);
        flowers(w, 70, 92, 78, 100);
        cart(w, 99, 78);
    }

    private static void parkP3(World w) {
        hedge(w, -51, -104, -43, -70, 2, new int[]{-51, -86}, new int[]{-51, -85});
        path(w, -52, -86, -44, -85);
        path(w, -47, -103, -47, -71);
        path(w, -50, -93, -44, -93);
        path(w, -50, -80, -44, -80);
        lamp(w, -48, -87);
        lamp(w, -46, -74);
        int[] tz = {-102, -96, -90, -84, -78, -72};
        for (int z : tz) {
            tree(w, -49, z, 4);
            tree(w, -45, z, 4);
        }
        well(w, -44, -75);
        bench(w, -45, -77, BlockFace.NORTH);
        bench(w, -49, -77, BlockFace.NORTH);
        bench(w, -47, -75, BlockFace.WEST);
        flowers(w, -50, -100, -44, -97);
        notice(w, -45, -88);
    }

    private static boolean isP2Pond(int x, int z) {
        return (x >= 74 && x <= 83 && z >= 78 && z <= 83) || (x >= 84 && x <= 89 && z >= 78 && z <= 81);
    }

    private static void pond(World w, int x1, int z1, int x2, int z2, Material rim) {
        for (int x = x1; x <= x2; x++)
            for (int z = z1; z <= z2; z++) set(w, x, G, z, WATER);
        for (int x = x1 - 1; x <= x2 + 1; x++)
            for (int z = z1 - 1; z <= z2 + 1; z++)
                if (x == x1 - 1 || x == x2 + 1 || z == z1 - 1 || z == z2 + 1)
                    set(w, x, G, z, rim);
    }

    private static void well(World w, int x, int z) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                set(w, x + dx, G + 1, z + dz, STONE_BRICK_WALL);
            }
        set(w, x, G + 1, z, WATER);
        set(w, x - 1, G + 1, z - 1, DARK_OAK_LOG);
        set(w, x - 1, G + 2, z - 1, DARK_OAK_LOG);
        set(w, x - 1, G + 3, z - 1, DARK_OAK_LOG);
        set(w, x - 1, G + 4, z - 1, DARK_OAK_LOG);
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) set(w, x + dx, G + 5, z + dz, DARK_OAK_SLAB);
        set(w, x, G + 4, z, LANTERN);
    }

    private static void cart(World w, int x, int z) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                set(w, x + dx, G + 1, z + dz, SPRUCE_FENCE);
            }
        set(w, x, G + 1, z, HAY_BLOCK);
        slab(w, x - 1, G + 2, z, SPRUCE_SLAB);
        slab(w, x, G + 2, z, SPRUCE_SLAB);
        slab(w, x + 1, G + 2, z, SPRUCE_SLAB);
    }

    private static void notice(World w, int x, int z) {
        set(w, x - 1, G + 1, z, SPRUCE_FENCE);
        set(w, x - 1, G + 2, z, SPRUCE_FENCE);
        set(w, x + 1, G + 1, z, SPRUCE_FENCE);
        set(w, x + 1, G + 2, z, SPRUCE_FENCE);
        set(w, x, G + 2, z, SPRUCE_PLANKS);
    }

    private static void bench(World w, int x, int z, BlockFace face) {
        stair(w, x, G + 1, z, face, OAK_STAIRS);
    }

    private static void picnic(World w, int x, int z) {
        for (int dx = 0; dx <= 2; dx++) {
            set(w, x + dx, G + 1, z, SPRUCE_PLANKS);
            stair(w, x + dx, G + 1, z - 1, BlockFace.SOUTH);
            stair(w, x + dx, G + 1, z + 1, BlockFace.NORTH);
        }
    }

    private static void willow(World w, int x, int z) {
        for (int y = 1; y <= 4; y++) set(w, x, G + y, z, BIRCH_LOG);
        for (int dx = -3; dx <= 3; dx++)
            for (int dz = -3; dz <= 3; dz++) {
                int d = Math.abs(dx) + Math.abs(dz);
                if (d <= 3) leaf(w, x + dx, G + 5, z + dz);
                if (d == 3) {
                    leaf(w, x + dx, G + 4, z + dz);
                    leaf(w, x + dx, G + 3, z + dz);
                }
            }
        leaf(w, x, G + 6, z);
    }

    private static void flowers(World w, int x1, int z1, int x2, int z2) {
        for (int x = x1; x <= x2; x++)
            for (int z = z1; z <= z2; z++)
                if (hash(x, z) % 5 == 0)
                    set(w, x, G + 1, z, pick(FLOWERS, hash(x + 1, z - 2)));
    }
}
