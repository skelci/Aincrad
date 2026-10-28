package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MapTracker {
    private static final Map<Long, Integer> COLORS = new HashMap<>();
    private static final Set<Long> DISCOVERED = new HashSet<>();
    private static String dimension = "";
    private static int lastCx = Integer.MIN_VALUE;
    private static int lastCz = Integer.MIN_VALUE;

    private MapTracker() {
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;
        String dim = String.valueOf(mc.level.dimension());
        if (!dim.equals(dimension)) {
            DISCOVERED.clear();
            COLORS.clear();
            dimension = dim;
            lastCx = Integer.MIN_VALUE;
            lastCz = Integer.MIN_VALUE;
        }
        int cx = mc.player.getBlockX() >> 4;
        int cz = mc.player.getBlockZ() >> 4;
        if (cx == lastCx && cz == lastCz) return;
        lastCx = cx;
        lastCz = cz;
        long key = key(cx, cz);
        if (DISCOVERED.add(key)) COLORS.put(key, sampleChunk(mc, cx, cz));
    }

    public static long key(int cx, int cz) {
        return ((long) cx << 32) | (cz & 0xFFFFFFFFL);
    }

    public static int chunkX(long key) {
        return (int) (key >> 32);
    }

    public static int chunkZ(long key) {
        return (int) key;
    }

    public static boolean has(long key) {
        return DISCOVERED.contains(key);
    }

    public static int color(long key) {
        return COLORS.getOrDefault(key, 0xFF14141A);
    }

    public static int count() {
        return DISCOVERED.size();
    }

    public static String dimension() {
        return dimension;
    }

    public static String floorName() {
        int idx = dimension.indexOf("floor_");
        if (idx >= 0) {
            StringBuilder num = new StringBuilder();
            for (int i = idx + 6; i < dimension.length(); i++) {
                char c = dimension.charAt(i);
                if (Character.isDigit(c)) num.append(c);
                else break;
            }
            if (num.length() > 0) return "Floor " + num;
        }
        if (dimension.contains("overworld")) return "Overworld";
        if (dimension.contains("nether")) return "Crimson Realm";
        if (dimension.contains("end")) return "The End";
        return "Aincrad";
    }

    private static int sampleChunk(Minecraft mc, int cx, int cz) {
        int wx = cx * 16 + 8;
        int wz = cz * 16 + 8;
        int baseY = (int) Math.floor(mc.player.getY());
        BlockPos pos = new BlockPos(wx, baseY, wz);
        for (int y = baseY + 16; y >= baseY - 16; y--) {
            pos = new BlockPos(wx, y, wz);
            if (!mc.level.isLoaded(pos)) continue;
            BlockState st = mc.level.getBlockState(pos);
            if (!st.isAir()) return colorFor(st);
        }
        return 0xFF14141A;
    }

    private static int colorFor(BlockState st) {
        if (!st.getFluidState().isEmpty()) return 0xFF2E62D0;
        String id = BuiltInRegistries.BLOCK.getKey(st.getBlock()).getPath();
        if (id.contains("grass_block") || id.equals("short_grass") || id.equals("fern")) return 0xFF4E8A38;
        if (id.contains("dirt") || id.contains("coarse") || id.contains("podzol") || id.contains("farmland") || id.contains("mud")) return 0xFF7A5230;
        if (id.contains("sand") || id.contains("gravel") || (id.contains("terracotta") && !id.contains("glazed"))) return 0xFFB69B6B;
        if (id.contains("stone") && !id.contains("brick")) return 0xFF7E7E84;
        if (id.contains("cobblestone") || id.contains("brick")) return 0xFF8C8178;
        if (id.contains("andesite")) return 0xFF8A8D8B;
        if (id.contains("diorite")) return 0xFFBFBEC2;
        if (id.contains("granite")) return 0xFF956B52;
        if (id.contains("deepslate")) return 0xFF3B3B42;
        if (id.contains("smooth_stone") || id.contains("stone_brick")) return 0xFF9A9A9E;
        if (id.contains("log") || id.contains("wood") || id.contains("planks") || id.contains("stem")) return 0xFF6B4A2B;
        if (id.contains("leaves")) return 0xFF2F6B28;
        if (id.contains("wool") || id.contains("concrete") || id.contains("terracotta") || id.contains("carpet")) return 0xFF9B6A7E;
        if (id.contains("glass") || id.contains("pane") || id.contains("ice")) return 0xFFA9C7D8;
        if (id.contains("water")) return 0xFF2E62D0;
        if (id.contains("lava")) return 0xFFD05A1E;
        if (id.contains("snow")) return 0xFFE6E8EA;
        if (id.contains("gold_block") || id.contains("glowing")) return 0xFFE8C33A;
        if (id.contains("iron_block")) return 0xFFC7C7C9;
        if (id.contains("obsidian")) return 0xFF1B1426;
        if (id.contains("quartz")) return 0xFFDAD3CD;
        if (id.contains("sea_lantern") || id.contains("glowstone") || id.contains("lantern")) return 0xFFD9C97E;
        if (id.contains("grass") || id.equals("moss_block")) return 0xFF4E8A38;
        return 0xFF6E6E75;
    }
}
