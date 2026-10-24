package com.aincrad.paper.world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Plaza service kiosks: skill bookshelf, guild notice board (lectern) and
 * a merchant bell for the shop — all inside the town plaza.
 */
public final class BureauKiosk {

    public static final int SKILL_X = 20;
    public static final int SKILL_Z = -4;
    public static final int QUEST_X = -20;
    public static final int QUEST_Z = -4;
    public static final int SHOP_X = 12;
    public static final int SHOP_Z = 27;

    private static boolean scrubbed;

    private BureauKiosk() {
    }

    public static void build(World w) {
        try {
            scrubOld(w);
            int gy = w.getHighestBlockYAt(SKILL_X, SKILL_Z);
            set(w, SKILL_X, gy + 1, SKILL_Z, Material.POLISHED_ANDESITE);
            set(w, SKILL_X, gy + 2, SKILL_Z, Material.BOOKSHELF);
            set(w, SKILL_X, gy + 3, SKILL_Z, Material.BOOKSHELF);
            set(w, SKILL_X, gy + 4, SKILL_Z, Material.LANTERN);
            set(w, SKILL_X + 1, gy + 1, SKILL_Z, Material.SEA_LANTERN);

            int qy = w.getHighestBlockYAt(QUEST_X, QUEST_Z);
            set(w, QUEST_X, qy + 1, QUEST_Z, Material.STONE_BRICKS);
            set(w, QUEST_X, qy + 2, QUEST_Z, Material.LECTERN);
            set(w, QUEST_X - 1, qy + 1, QUEST_Z, Material.SEA_LANTERN);

            int sy = w.getHighestBlockYAt(SHOP_X, SHOP_Z);
            set(w, SHOP_X, sy + 1, SHOP_Z, Material.ANDESITE_WALL);
            set(w, SHOP_X, sy + 2, SHOP_Z, Material.BELL);
            set(w, SHOP_X + 1, sy + 1, SHOP_Z, Material.SEA_LANTERN);
            set(w, SHOP_X, sy + 3, SHOP_Z, Material.LANTERN);
        } catch (Throwable ignored) {
        }
    }

    private static void scrubOld(World w) {
        if (scrubbed) return;
        scrubbed = true;
        for (int x : new int[]{14, -14}) {
            int gy = w.getHighestBlockYAt(x, -332);
            for (int y = gy + 1; y <= gy + 4; y++) {
                Block b = w.getBlockAt(x, y, -332);
                if (b.getType() != Material.AIR) b.setType(Material.AIR, false);
            }
        }
    }

    public static boolean isSkillBlock(Block b) {
        return b.getType() == Material.BOOKSHELF
                && Math.abs(b.getX() - SKILL_X) <= 1 && Math.abs(b.getZ() - SKILL_Z) <= 1;
    }

    public static boolean isQuestBlock(Block b) {
        return (b.getType() == Material.LECTERN || b.getType() == Material.STONE_BRICKS)
                && Math.abs(b.getX() - QUEST_X) <= 1 && Math.abs(b.getZ() - QUEST_Z) <= 1;
    }

    public static boolean isShopBlock(Block b) {
        return b.getType() == Material.BELL
                && Math.abs(b.getX() - SHOP_X) <= 1 && Math.abs(b.getZ() - SHOP_Z) <= 1;
    }

    private static void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m, false);
    }
}
