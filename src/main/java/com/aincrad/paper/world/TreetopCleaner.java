package com.aincrad.paper.world;

import com.aincrad.paper.AincradPaperPlugin;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumSet;

/** Removes stray flower blocks left on treetops by the old terrain generator. */
public final class TreetopCleaner {

    private static final EnumSet<Material> PLANTS = EnumSet.of(
            Material.POPPY, Material.DANDELION, Material.TALL_GRASS, Material.FERN,
            Material.ROSE_BUSH, Material.LILAC, Material.PEONY, Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM, Material.DEAD_BUSH, Material.OAK_SAPLING);

    private TreetopCleaner() {
    }

    public static void start(AincradPaperPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (World w : plugin.getServer().getWorlds()) {
                if (!AincradPaperPlugin.isFloor1(w)) continue;
                clean(w);
            }
        }, 100L, 6000L);
    }

    private static void clean(World w) {
        int removed = 0;
        for (Chunk c : w.getLoadedChunks()) {
            int bx = c.getX() << 4, bz = c.getZ() << 4;
            for (int x = bx; x < bx + 16; x++) {
                for (int z = bz; z < bz + 16; z++) {
                    for (int y = 50; y < 100; y++) {
                        var b = w.getBlockAt(x, y, z);
                        Material m = b.getType();
                        if (!PLANTS.contains(m)) continue;
                        if (w.getBlockAt(x, y - 1, z).getType().toString().contains("LEAVES")) {
                            b.setType(Material.AIR, false);
                            removed++;
                        }
                    }
                }
            }
        }
        if (removed > 0) {
            pluginLog(w, removed);
        }
    }

    private static void pluginLog(World w, int n) {
        System.out.println("[Aincrad] TreetopCleaner: removed " + n + " stray plant blocks in " + w.getName());
    }
}
