package com.aincrad.paper.world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 * Interactive service points, placed where they belong:
 * skill shelf + quest lectern inside the Guild Hall front room,
 * merchant bell already provided by the Market Hall counter (-45, 0).
 * Right-clicking opens the respective GUIs.
 */
public final class BureauKiosk {

    public static final int SKILL_X = 37, SKILL_Z = 3;
    public static final int QUEST_X = 37, QUEST_Z = 5;
    public static final int SHOP_X = -45, SHOP_Z = 0;

    private BureauKiosk() {}

    private static final int G = TownKit.G;

    public static void build(World world) {
        scrubOld(world);
        set(world, SKILL_X, G + 1, SKILL_Z, Material.BOOKSHELF);
        set(world, SKILL_X, G + 2, SKILL_Z, Material.LANTERN);
        set(world, QUEST_X, G + 1, QUEST_Z, Material.LECTERN);
        set(world, QUEST_X, G + 1, QUEST_Z - 1, Material.SEA_LANTERN);
    }

    /** Removes the old floating plaza pillars from earlier layouts. */
    private static void scrubOld(World w) {
        int[][] spots = {{20, -4}, {-20, -4}, {12, 27}, {-332, 0}, {-332, 2},
                {-332, 4}, {-332, 6}, {-332, 8}};
        for (int[] s : spots) {
            for (int y = G + 1; y <= G + 5; y++) {
                org.bukkit.block.Block b = w.getBlockAt(s[0], y, s[1]);
                Material m = b.getType();
                if (m == Material.BOOKSHELF || m == Material.LECTERN || m == Material.STONE_BRICKS
                        || m == Material.ANDESITE_WALL || m == Material.LANTERN || m == Material.BELL
                        || m == Material.SEA_LANTERN || m == Material.POLISHED_ANDESITE) {
                    b.setType(Material.AIR, false);
                }
            }
        }
    }

    public static boolean isSkillBlock(org.bukkit.block.Block block) {
        return matches(block, Material.BOOKSHELF, SKILL_X, SKILL_Z);
    }

    public static boolean isQuestBlock(org.bukkit.block.Block block) {
        return matches(block, Material.LECTERN, QUEST_X, QUEST_Z)
                || matches(block, Material.STONE_BRICKS, QUEST_X, QUEST_Z);
    }

    public static boolean isShopBlock(org.bukkit.block.Block block) {
        return matches(block, Material.BELL, SHOP_X, SHOP_Z);
    }

    private static boolean matches(org.bukkit.block.Block block, Material expected, int x, int z) {
        if (block.getType() != expected) return false;
        int dx = Math.abs(block.getX() - x);
        int dz = Math.abs(block.getZ() - z);
        if (dx <= 1 && dz <= 1) return true;
        org.bukkit.block.Block below = block.getRelative(0, -1, 0);
        return Math.abs(below.getX() - x) <= 1 && Math.abs(below.getZ() - z) <= 1;
    }

    private static void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m, false);
    }
}
