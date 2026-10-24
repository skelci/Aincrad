package com.aincrad.paper.world;

import org.bukkit.Material;
import org.bukkit.World;

/**
 * Floor 1 "Town of Beginnings" orchestrator.
 * Ground, walls, gates, full road grid, then delegates to TownCore / TownDistricts / TownHousing.
 */
public class PlazaGenerator {

    private static final int G = TownKit.G;
    private static final int WALL = TownKit.WALL;
    private static final int IN = WALL - 2;

    private PlazaGenerator() {}

    public static void generatePlaza(World w) {
        if (w.getBlockAt(WALL, G + 21, WALL).getType() == Material.BEACON
                || w.getBlockAt(WALL, G + 21, -WALL).getType() == Material.BEACON) {
            return;
        }
        long t0 = System.currentTimeMillis();
        org.bukkit.Bukkit.getLogger().info("[Aincrad] Building Floor 1 town...");

        ground(w);
        ringWall(w);
        roads(w);

        TownCore.build(w);
        TownDistricts.build(w);
        TownHousing.build(w);

        TownKit.refreshPanes(w);

        org.bukkit.Bukkit.getLogger().info("[Aincrad] Floor 1 town complete in "
                + ((System.currentTimeMillis() - t0) / 1000) + "s");
    }

    private static void ground(World w) {
        for (int x = -WALL - 6; x <= WALL + 6; x++) {
            for (int z = -WALL - 6; z <= WALL + 6; z++) {
                for (int y = G - 4; y < G; y++) w.getBlockAt(x, y, z).setType(Material.STONE);
                boolean inner = Math.abs(x) <= IN && Math.abs(z) <= IN;
                w.getBlockAt(x, G, z).setType(inner ? Material.GRASS_BLOCK : Material.COARSE_DIRT);
                for (int y = G + 1; y <= G + 24; y++) w.getBlockAt(x, y, z).setType(Material.AIR);
            }
        }
    }

    private static void roads(World w) {
        TownKit.pave(w, -31, -31, 31, 31);
        TownKit.road(w, -3, 32, 3, WALL);
        TownKit.road(w, -3, -WALL, 3, -44);
        TownKit.road(w, 32, -3, WALL, 3);
        TownKit.road(w, -WALL, -3, -32, 3);
        TownKit.road(w, 53, -IN, 57, IN);
        TownKit.road(w, -57, -IN, -53, IN);
        TownKit.road(w, -IN, 53, IN, 57);
        TownKit.road(w, -IN, -57, IN, -53);
        for (int s = -1; s <= 1; s += 2) {
            TownKit.road(w, -113, 107 * s, 113, 107 * s + (s > 0 ? 6 : -6));
            TownKit.road(w, 107 * s, -113, 107 * s + (s > 0 ? 6 : -6), 113);
        }

        for (int p = -96; p <= 96; p += 14) {
            if (Math.abs(p) > 30) {
                TownKit.lamp(w, 6, p);
                TownKit.lamp(w, -6, p);
                TownKit.lamp(w, p, 6);
                TownKit.lamp(w, p, -6);
            }
        }
        TownKit.lampTall(w, 24, 24);
        TownKit.lampTall(w, -24, 24);
        TownKit.lampTall(w, 24, -24);
        TownKit.lampTall(w, -24, -24);
    }

    private static void ringWall(World w) {
        for (int side = 0; side < 4; side++) {
            boolean horiz = side >= 2;
            int s = (side % 2 == 0) ? 1 : -1;
            for (int a = -WALL; a <= WALL; a++) {
                boolean gate = Math.abs(a) <= 3;
                int fixed = horiz ? (side == 2 ? 120 : -120) : (side == 0 ? 120 : -120);
                if (gate) {
                    for (int y = 1; y <= 16; y++) {
                        TownKit.set(w, a, G + y, fixed, Material.AIR);
                        if (horiz) TownKit.set(w, fixed, G + y, a, Material.AIR);
                    }
                    for (int y = 17; y <= 20; y++) {
                        TownKit.set(w, a, G + y, fixed, Material.STONE_BRICKS);
                        TownKit.set(w, a, G + y, fixed + s, Material.STONE_BRICKS);
                        if (horiz) {
                            TownKit.set(w, fixed, G + y, a, Material.STONE_BRICKS);
                            TownKit.set(w, fixed + s, G + y, a, Material.STONE_BRICKS);
                        }
                    }
                    TownKit.set(w, a, G + 18, fixed - s, Material.LANTERN);
                    continue;
                }
                for (int y = 1; y <= 16; y++) {
                    for (int d = 0; d <= 2; d++) {
                        TownKit.set(w, a, G + y, fixed - d * s, Material.STONE_BRICKS);
                        if (horiz) TownKit.set(w, fixed - d * s, G + y, a, Material.STONE_BRICKS);
                    }
                }
                for (int d = 0; d <= 2; d += 2) {
                    if (((a / 2) & 1) == 0) {
                        TownKit.set(w, a, G + 17, fixed - d * s, Material.STONE_BRICKS);
                        if (horiz) TownKit.set(w, fixed - d * s, G + 17, a, Material.STONE_BRICKS);
                    }
                }
                if ((a & 3) == 0) {
                    TownKit.set(w, a, G + 15, fixed - 3 * s, Material.STONE_BRICK_WALL);
                    if (horiz) TownKit.set(w, fixed - 3 * s, G + 15, a, Material.STONE_BRICK_WALL);
                }
            }
        }

        for (int cx = -1; cx <= 1; cx += 2) {
            for (int cz = -1; cz <= 1; cz += 2) {
                int tx = cx * WALL, tz = cz * WALL;
                TownKit.box(w, tx - 7, tz - 7, tx + 7, tz + 7, G + 1, G + 22, Material.CRACKED_STONE_BRICKS);
                TownKit.box(w, tx - 5, tz - 5, tx + 5, tz + 5, G + 23, G + 25, Material.STONE_BRICKS);
                TownKit.box(w, tx - 1, tz - 1, tx + 1, tz + 1, G + 1, G + 19, Material.AIR);
                TownKit.pave(w, tx - 1, tz - 1, tx + 1, tz + 1);
                for (int y = 1; y <= 19; y += 6) {
                    TownKit.set(w, tx, G + y, tz - cz * 6, Material.LANTERN);
                }
                TownKit.set(w, tx, G + 21, tz, Material.BEACON);
                for (int dx = -7; dx <= 7; dx += 14) {
                    for (int dz = -7; dz <= 7; dz += 14) {
                        TownKit.set(w, tx + dx, G + 23, tz + dz, Material.GOLD_BLOCK);
                    }
                }
            }
        }
    }
}
