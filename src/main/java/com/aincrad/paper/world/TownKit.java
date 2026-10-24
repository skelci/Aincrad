package com.aincrad.paper.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared builder API + town map for the Floor 1 town.
 *
 * TOWN MAP (G=64, all coords absolute x,z):
 *  Wall: |c|=120, 16 tall, 7-wide gate at each avenue end. Towers at corners, beacon at (±120, 85, ±120).
 *  Outer ring road: square band |c| in 107..113. Secondary streets: x in -57..-53 and 53..57 (full z), z likewise.
 *  Avenues 7 wide: S: x -3..3, z 32..107 | E: z -3..3, x 32..107 | W: z -3..3, x -107..-32 | N: x -3..3, z -44..-107
 *  Plaza: paved square |x|,|z| <= 31 (inner decorative plaza <= 26, ring road band 27..31).
 *  Temple: x -10..10, z -12..12, doors on SOUTH face (z=12), steps z 13..15. Spawn (0,65,15).
 *  Fountain: x -6..6, z 17..25.
 *  Guild: x 33..55, z -13..13, main doors WEST face (x=33).
 *  Market hall: x -55..-33, z -13..13, doors EAST face (x=-33).
 *  Portal arcade: x -14..14, z -33..-43, opens SOUTH (z=-33) toward plaza.
 *  Tavern: x 33..49, z 33..49.
 *  Blacksmith: x -52..-38, z 58..72. Training yard: x -104..-60, z 58..104.
 *  City blocks (between streets), cols: 32..52, 58..106, -106..-58, -52..-32 (x), same for z rows.
 */
public final class TownKit {

    public static final int G = 64;
    public static final int WALL = 120;

    private TownKit() {}

    static final List<int[]> panes = new ArrayList<>();

    // ── primitives ──────────────────────────────────────────────────────

    public static void set(World w, int x, int y, int z, Material m) {
        if (!m.isBlock()) return;
        w.getBlockAt(x, y, z).setType(m);
    }

    public static void box(World w, int x1, int z1, int x2, int z2, int y1, int y2, Material m) {
        if (!m.isBlock()) return;
        int ax1 = Math.min(x1, x2), ax2 = Math.max(x1, x2);
        int az1 = Math.min(z1, z2), az2 = Math.max(z1, z2);
        int ay1 = Math.min(y1, y2), ay2 = Math.max(y1, y2);
        for (int x = ax1; x <= ax2; x++)
            for (int z = az1; z <= az2; z++)
                for (int y = ay1; y <= ay2; y++) w.getBlockAt(x, y, z).setType(m);
    }

    public static void clearArea(World w, int x1, int z1, int x2, int z2, int hUp) {
        box(w, x1, z1, x2, z2, G + 1, G + hUp, Material.AIR);
    }

    public static int hash(int x, int z) {
        int h = (x * 73856093) ^ (z * 19349663);
        h = h ^ (h >>> 13);
        return h & 0x7FFFFFFF;
    }

    public static void leaf(World w, int x, int y, int z) {
        Leaves d = (Leaves) Bukkit.createBlockData(Material.OAK_LEAVES);
        d.setPersistent(true);
        w.getBlockAt(x, y, z).setBlockData(d);
    }

    // ── paving & roads ──────────────────────────────────────────────────

    public static void pave(World w, int x1, int z1, int x2, int z2) {
        int ax1 = Math.min(x1, x2), ax2 = Math.max(x1, x2);
        int az1 = Math.min(z1, z2), az2 = Math.max(z1, z2);
        for (int x = ax1; x <= ax2; x++)
            for (int z = az1; z <= az2; z++) {
                int h = hash(x, z) % 100;
                Material m = h < 4 ? Material.ANDESITE : h < 6 ? Material.MOSSY_STONE_BRICKS : Material.STONE_BRICKS;
                w.getBlockAt(x, G, z).setType(m);
            }
    }

    public static void road(World w, int x1, int z1, int x2, int z2) {
        pave(w, x1, z1, x2, z2);
    }

    public static void path(World w, int x1, int z1, int x2, int z2) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++)
                w.getBlockAt(x, G, z).setType(hash(x, z) % 9 == 0 ? Material.ANDESITE : Material.STONE_BRICKS);
    }

    // ── street furniture ────────────────────────────────────────────────

    public static Material chain() {
        Material m = Material.matchMaterial("IRON_CHAIN");
        return m != null ? m : Material.matchMaterial("CHAIN");
    }

    public static void lamp(World w, int x, int z) {
        for (int y = 1; y <= 3; y++) set(w, x, G + y, z, Material.ANDESITE_WALL);
        set(w, x, G + 4, z, Material.LANTERN);
    }

    public static void lampTall(World w, int x, int z) {
        for (int y = 1; y <= 4; y++) set(w, x, G + y, z, Material.ANDESITE_WALL);
        set(w, x, G + 5, z, chain());
        set(w, x, G + 6, z, Material.LANTERN);
    }

    public static void planter(World w, int x, int z, Material flower) {
        pot(w, x, G + 1, z, flower);
    }

    public static void pot(World w, int x, int y, int z, Material flower) {
        w.getBlockAt(x, y, z).setType(Material.AIR, false);
        String key = flower.getKey().getKey();
        try {
            w.getBlockAt(x, y, z).setBlockData(
                    org.bukkit.Bukkit.createBlockData("minecraft:potted_" + key));
            return;
        } catch (Exception ignored) {
        }
        set(w, x, y, z, Material.FLOWER_POT);
    }

    // ── doors & windows ─────────────────────────────────────────────────

    public static void doorWood(World w, int x, int z, BlockFace face, Material wood) {
        if (!(Bukkit.createBlockData(wood) instanceof Door)) {
            set(w, x, G + 1, z, wood);
            set(w, x, G + 2, z, wood);
            return;
        }
        Door b = (Door) Bukkit.createBlockData(wood);
        b.setHalf(Door.Half.BOTTOM);
        b.setFacing(face);
        w.getBlockAt(x, G + 1, z).setBlockData(b);
        Door t = (Door) Bukkit.createBlockData(wood);
        t.setHalf(Door.Half.TOP);
        t.setFacing(face);
        w.getBlockAt(x, G + 2, z).setBlockData(t);
    }

    /** Iron door = player can't open (no redstone). Scenery house. */
    public static void doorLocked(World w, int x, int z, BlockFace face) {
        Door b = (Door) Bukkit.createBlockData(Material.IRON_DOOR);
        b.setHalf(Door.Half.BOTTOM);
        b.setFacing(face);
        w.getBlockAt(x, G + 1, z).setBlockData(b);
        Door t = (Door) Bukkit.createBlockData(Material.IRON_DOOR);
        t.setHalf(Door.Half.TOP);
        t.setFacing(face);
        w.getBlockAt(x, G + 2, z).setBlockData(t);
        set(w, x, G + 3, z, Material.LANTERN);
    }

    /** 2 wide x 2 tall glass pane window. face = direction the pane pair spans. */
    public static void win2(World w, int x, int y, int z, BlockFace face) {
        int[][] offs = (face == BlockFace.NORTH || face == BlockFace.SOUTH)
                ? new int[][]{{0, 0}, {1, 0}} : new int[][]{{0, 0}, {0, 1}};
        for (int[] o : offs) {
            int px = x + o[0], pz = z + o[1];
            for (int dy = 0; dy <= 1; dy++) {
                set(w, px, y + dy, pz, Material.GLASS_PANE);
                panes.add(new int[]{px, y + dy, pz});
            }
        }
    }

    /** Full window: 2x2 panes with sill slab under and lintel above. */
    public static void win2Sill(World w, int x, int y, int z, BlockFace face, Material sill) {
        win2(w, x, y, z, face);
        int[][] offs = (face == BlockFace.NORTH || face == BlockFace.SOUTH)
                ? new int[][]{{0, 0}, {1, 0}} : new int[][]{{0, 0}, {0, 1}};
        for (int[] o : offs) {
            set(w, x + o[0], y - 1, z + o[1], sill);
        }
    }

    /** Re-set every recorded pane so panes connect to neighbours placed after them. Call LAST, after all buildings. */
    public static void refreshPanes(World w) {
        for (int[] p : panes) {
            org.bukkit.block.Block b = w.getBlockAt(p[0], p[1], p[2]);
            if (b.getType() == Material.GLASS_PANE) {
                b.setType(Material.AIR, false);
                b.setType(Material.GLASS_PANE, true);
            }
        }
        panes.clear();
    }

    // ── shells & roofs ──────────────────────────────────────────────────

    /** Hollow shell: floor paved, perimeter walls G+1..G+h, interior clear, roof open. */
    public static void shell(World w, int x1, int z1, int x2, int z2, int h, Material wallMat) {
        clearArea(w, x1 - 1, z1 - 1, x2 + 1, z2 + 1, h + 8);
        pave(w, x1, z1, x2, z2);
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

    /** Stepped hip roof, ridge runs along the longer axis. Overhang by 1 on all sides at y0. */
    public static void roof(World w, int x1, int z1, int x2, int z2, int y0, Material mat) {
        int wN = x2 - x1, dN = z2 - z1;
        boolean alongX = wN >= dN;
        int maxL = Math.min(wN, dN) / 2 + 1;
        for (int l = -1; l < maxL; l++) {
            int ax1 = x1 - Math.max(l, 0), ax2 = x2 + Math.max(l, 0);
            int az1 = z1 - Math.max(l, 0), az2 = z2 + Math.max(l, 0);
            if (l >= 0) {
                ax1 = x1 + l; ax2 = x2 - l; az1 = z1 + l; az2 = z2 - l;
            }
            if (ax1 > ax2 || az1 > az2) continue;
            for (int x = ax1; x <= ax2; x++)
                for (int z = az1; z <= az2; z++) {
                    boolean edge = (x == ax1 || x == ax2 || z == az1 || z == az2);
                    if (edge || l >= maxL - 1) w.getBlockAt(x, y0 + Math.max(l, 0), z).setType(mat);
                }
        }
    }

    /** Chimney with campfire smoke. */
    public static void chimney(World w, int x, int z, int topY, Material mat) {
        for (int y = 1; y <= topY; y++) set(w, x, G + y, z, mat);
        set(w, x, G + topY + 1, z, Material.CAMPFIRE);
    }

    // ── stairs helpers ──────────────────────────────────────────────────

    public static void stair(World w, int x, int y, int z, BlockFace face) {
        Stairs s = (Stairs) Bukkit.createBlockData(Material.OAK_STAIRS);
        s.setFacing(face);
        w.getBlockAt(x, y, z).setBlockData(s);
    }

    public static void stair(World w, int x, int y, int z, BlockFace face, Material mat) {
        var d = Bukkit.createBlockData(mat);
        if (d instanceof Stairs s) {
            s.setFacing(face);
            w.getBlockAt(x, y, z).setBlockData(d);
        } else {
            w.getBlockAt(x, y, z).setType(mat);
        }
    }

    public static void slab(World w, int x, int y, int z, Material mat) {
        var d = Bukkit.createBlockData(mat);
        if (d instanceof Slab s) {
            s.setType(Slab.Type.BOTTOM);
            w.getBlockAt(x, y, z).setBlockData(d);
        } else {
            w.getBlockAt(x, y, z).setType(mat);
        }
    }

    // ── tree & greenery ─────────────────────────────────────────────────

    public static void tree(World w, int x, int z, int trunkH) {
        for (int y = 1; y <= trunkH; y++) set(w, x, G + y, z, Material.OAK_LOG);
        int cy = G + trunkH;
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                for (int dy = 0; dy <= 2; dy++) {
                    int d = Math.abs(dx) + Math.abs(dz) + dy;
                    if (d <= 3 && !(dx == 0 && dz == 0 && dy == 0)) leaf(w, x + dx, cy + dy, z + dz);
                }
        leaf(w, x, G + trunkH + 3, z);
    }

    public static void hedge(World w, int x1, int z1, int x2, int z2, int h, int[] gate1, int[] gate2) {
        int ax1 = Math.min(x1, x2), ax2 = Math.max(x1, x2);
        int az1 = Math.min(z1, z2), az2 = Math.max(z1, z2);
        for (int x = ax1; x <= ax2; x++)
            for (int z = az1; z <= az2; z++) {
                if (!(x == ax1 || x == ax2 || z == az1 || z == az2)) continue;
                if (gate1 != null) {
                    int gx = gate1[0], gz = gate1[1], gx2 = gate2[0], gz2 = gate2[1];
                    if ((z == az1 || z == az2) && x >= Math.min(gx, gx2) && x <= Math.max(gx, gx2)) continue;
                    if ((x == ax1 || x == ax2) && z >= Math.min(gz, gz2) && z <= Math.max(gz, gz2)) continue;
                }
                for (int y = 1; y <= h; y++) leaf(w, x, G + y, z);
            }
    }

    // ── palettes ────────────────────────────────────────────────────────

    public static final Material[] PLASTER = {
            Material.WHITE_CONCRETE, Material.WHITE_TERRACOTTA, Material.SANDSTONE,
            Material.SMOOTH_SANDSTONE, Material.QUARTZ_BRICKS, Material.CALCITE,
            Material.TUFF_BRICKS, Material.STONE_BRICKS, Material.BRICKS, Material.MOSSY_COBBLESTONE
    };
    public static final Material[] ROOFS = {
            Material.RED_NETHER_BRICKS, Material.BRICKS, Material.SPRUCE_PLANKS,
            Material.DARK_OAK_PLANKS, Material.MANGROVE_PLANKS, Material.CHERRY_PLANKS,
            Material.COBBLESTONE, Material.STONE_BRICKS
    };
    public static final Material[] FLOWERS = {
            Material.POPPY, Material.DANDELION, Material.RED_TULIP, Material.ORANGE_TULIP,
            Material.BLUE_ORCHID, Material.AZURE_BLUET, Material.ALLIUM, Material.PINK_TULIP
    };

    public static Material pick(Material[] arr, int seed) {
        return arr[Math.abs(seed) % arr.length];
    }
}
