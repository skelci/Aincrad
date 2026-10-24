package com.aincrad.paper.world;

import org.bukkit.Material;
import org.bukkit.World;

import static com.aincrad.paper.world.TownKit.*;

/**
 * SAO-style spawn plaza: open 25x25 stone square with the fountain at its center.
 * Runs every start (cheap, idempotent) so the layout survives world edits.
 */
public final class SpawnPlaza {

    public static final int FOUNTAIN_X = 0;
    public static final int FOUNTAIN_Z = 21;
    public static final int SPAWN_X = 0;
    public static final int SPAWN_Y = TownKit.G + 1;
    public static final int SPAWN_Z = 31;

    private SpawnPlaza() {
    }

    public static void ensure(World w) {
        int cx = FOUNTAIN_X, cz = FOUNTAIN_Z;
        int minX = cx - 13, maxX = cx + 13;
        int minZ = cz - 13, maxZ = cz + 13;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = G + 1; y <= G + 10; y++) {
                    w.getBlockAt(x, y, z).setType(Material.AIR, false);
                }
                w.getBlockAt(x, G, z).setType(Material.SMOOTH_STONE, false);
            }
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                boolean edge = x == minX || x == maxX || z == minZ || z == maxZ;
                if (!edge) continue;
                boolean corner = (x == minX || x == maxX) && (z == minZ || z == maxZ);
                w.getBlockAt(x, G, z).setType(corner ? Material.POLISHED_ANDESITE : Material.STONE_BRICKS, false);
            }
        }

        // Sunken SAO fountain: walk-level rim, pool below ground, central spire
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                int r = Math.abs(dx) + Math.abs(dz);
                int x = cx + dx, z = cz + dz;
                if (r > 6) continue;
                set(w, x, G + 1, z, Material.AIR);
                if (r == 6) {
                    set(w, x, G, z, Material.POLISHED_ANDESITE);
                } else if (r <= 5) {
                    set(w, x, G, z, Material.AIR);
                    set(w, x, G - 1, z, Material.WATER);
                    set(w, x, G - 2, z, r == 5 ? Material.POLISHED_ANDESITE : Material.POLISHED_BLACKSTONE);
                }
            }
        }
        set(w, cx, G, cz, Material.POLISHED_BLACKSTONE);
        set(w, cx, G + 1, cz, Material.ANDESITE_WALL);
        set(w, cx, G + 2, cz, Material.ANDESITE_WALL);
        set(w, cx, G + 3, cz, Material.GOLD_BLOCK);
        try {
            w.setBlockData(cx, G + 4, cz,
                    org.bukkit.Bukkit.createBlockData("minecraft:chain[facing=down]"));
        } catch (Throwable t) {
            set(w, cx, G + 4, cz, Material.GOLD_BLOCK);
        }
        set(w, cx, G + 5, cz, Material.SEA_LANTERN);
        set(w, cx - 1, G + 1, cz, Material.GOLD_BLOCK);
        set(w, cx + 1, G + 1, cz, Material.GOLD_BLOCK);
        set(w, cx, G + 1, cz - 1, Material.GOLD_BLOCK);
        set(w, cx, G + 1, cz + 1, Material.GOLD_BLOCK);

        set(w, cx, G + 1, cz + 12, Material.STONE_BRICK_SLAB);
        set(w, cx, G + 1, cz + 13, Material.STONE_BRICK_SLAB);

        lampTall(w, -11, 10);
        lampTall(w, 11, 10);
        lampTall(w, -11, 32);
        lampTall(w, 11, 32);

        w.setSpawnLocation(SPAWN_X, SPAWN_Y, SPAWN_Z);
    }
}
