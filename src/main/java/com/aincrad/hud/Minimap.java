package com.aincrad.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class Minimap extends HudElement {
    private static final int MAP_SIZE = 128;
    private static final int RADIUS = MAP_SIZE / 2;
    private static final int STEP = 2;
    private static final int SAMPLES = MAP_SIZE / STEP;
    private static final long REBUILD_NANOS = 2_000_000_000L;
    private static final double MOVE_REBUILD = 6.0;

    private final int[] terrain = new int[SAMPLES * SAMPLES];
    private boolean hasTerrain = false;
    private long lastRebuildNanos = 0;
    private double lastCenterX = Double.MAX_VALUE;
    private double lastCenterZ = Double.MAX_VALUE;
    private String lastDim = "";
    private int centerX = 0;
    private int centerZ = 0;
    private int centerY = 64;
    private String floorName = "Aincrad";
    private int mapSize = MAP_SIZE;

    public Minimap() {
        super(10, 10, MAP_SIZE, MAP_SIZE + 30);
        this.anchor = Anchor.TOP_RIGHT;
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        double px = mc.player.getX();
        double pz = mc.player.getZ();
        centerY = (int) Math.floor(mc.player.getY());
        String dim = String.valueOf(mc.level.dimension());
        boolean moved = Math.abs(px - lastCenterX) > MOVE_REBUILD || Math.abs(pz - lastCenterZ) > MOVE_REBUILD;
        boolean stale = System.nanoTime() - lastRebuildNanos > REBUILD_NANOS;
        boolean dimChanged = !dim.equals(lastDim);
        if (!hasTerrain || moved || stale || dimChanged) {
            lastCenterX = px;
            lastCenterZ = pz;
            lastDim = dim;
            lastRebuildNanos = System.nanoTime();
            centerX = (int) Math.floor(px);
            centerZ = (int) Math.floor(pz);
            floorName = parseFloorName(dim);
            rebuildTerrain(mc);
        }
    }

    private static String parseFloorName(String dim) {
        int idx = dim.indexOf("floor_");
        if (idx >= 0) {
            StringBuilder num = new StringBuilder();
            for (int i = idx + 6; i < dim.length(); i++) {
                char c = dim.charAt(i);
                if (Character.isDigit(c)) num.append(c);
                else break;
            }
            if (num.length() > 0) return "Floor " + num;
        }
        if (dim.contains("overworld")) return "Overworld";
        if (dim.contains("nether")) return "Crimson Realm";
        if (dim.contains("end")) return "The End";
        return "Aincrad";
    }

    private void rebuildTerrain(Minecraft mc) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int sy = 0; sy < SAMPLES; sy++) {
            int wz = centerZ - RADIUS + sy * STEP;
            for (int sx = 0; sx < SAMPLES; sx++) {
                int wx = centerX - RADIUS + sx * STEP;
                cursor.setX(wx);
                cursor.setZ(wz);
                int color = 0xFF101014;
                try {
                    if (!mc.level.isLoaded(cursor.immutable())) {
                        terrain[sy * SAMPLES + sx] = 0xFF14141A;
                        continue;
                    }
                    boolean found = false;
                    for (int y = centerY + 16; y >= centerY - 16; y--) {
                        cursor.setY(y);
                        BlockState st = mc.level.getBlockState(cursor);
                        if (st.isAir()) continue;
                        color = colorFor(mc, st, cursor);
                        found = true;
                        break;
                    }
                    if (!found) color = 0xFF14141A;
                } catch (Exception ignored) {
                    color = 0xFF14141A;
                }
                terrain[sy * SAMPLES + sx] = color;
            }
        }
        hasTerrain = true;
    }

    private static int shade(int argb, int factor) {
        int a = argb >>> 24;
        int r = Math.min(255, ((argb >> 16) & 0xFF) * factor / 9);
        int g = Math.min(255, ((argb >> 8) & 0xFF) * factor / 9);
        int b = Math.min(255, (argb & 0xFF) * factor / 9);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int colorFor(Minecraft mc, BlockState st, BlockPos pos) {
        if (!st.getFluidState().isEmpty()) {
            return 0xFF2E62D0;
        }
        String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(st.getBlock()).getPath();
        if (id == null) id = "";
        if (id.contains("grass_block") || id.equals("short_grass") || id.equals("fern")) return 0xFF4E8A38;
        if (id.contains("dirt") || id.contains("coarse") || id.contains("podzol") || id.contains("farmland") || id.contains("mud")) return 0xFF7A5230;
        if (id.contains("sand") || id.contains("gravel") || id.contains("terracotta") && !id.contains("glazed")) return 0xFFB69B6B;
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
        if (id.contains("crops") || id.contains("carrots") || id.contains("potatoes") || id.contains("wheat")) return 0xFF8FA83C;
        if (id.contains("gold_block") || id.contains("glowing")) return 0xFFE8C33A;
        if (id.contains("iron_block")) return 0xFFC7C7C9;
        if (id.contains("obsidian")) return 0xFF1B1426;
        if (id.contains("quartz") || id.contains("smooth_quartz")) return 0xFFDAD3CD;
        if (id.contains("sea_lantern") || id.contains("glowstone") || id.contains("lantern")) return 0xFFD9C97E;
        if (id.contains("grass") || id.equals("moss_block")) return 0xFF4E8A38;
        return 0xFF6E6E75;
    }

    @Override
    public void render(GuiGraphicsExtractor extractor) {
        if (!visible) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Font font = mc.font;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int mapX = anchor == Anchor.TOP_RIGHT ? screenW - width - x : x;
        int mapY = y;

        extractor.text(font, floorName, mapX + RADIUS - font.width(floorName) / 2, mapY, HudPalette.TEXT_ACCENT);
        mapY += 14;

        extractor.fill(mapX, mapY, mapX + MAP_SIZE, mapY + MAP_SIZE, 0xCC000000);

        if (hasTerrain) {
            for (int sy = 0; sy < SAMPLES; sy++) {
                for (int sx = 0; sx < SAMPLES; sx++) {
                    int dx = sx * STEP - RADIUS;
                    int dy = sy * STEP - RADIUS;
                    if (dx * dx + dy * dy >= RADIUS * RADIUS) continue;
                    int color = terrain[sy * SAMPLES + sx];
                    extractor.fill(mapX + RADIUS + dx, mapY + RADIUS + dy,
                            mapX + RADIUS + dx + STEP, mapY + RADIUS + dy + STEP, color);
                }
            }
            
        }

        renderEntities(extractor, mc, mapX, mapY);

        int pcx = mapX + RADIUS;
        int pcy = mapY + RADIUS;
        extractor.fill(pcx - 2, pcy - 2, pcx + 2, pcy + 2, 0xFFFFEE44);
        extractor.fill(pcx - 1, pcy - 4, pcx + 1, pcy - 2, 0xFFFF6A3D);
        extractor.fill(pcx - 1, pcy + 2, pcx + 1, pcy + 4, 0xFFFF6A3D);

        extractor.text(font, "N", pcx - 3, mapY + 2, HudPalette.TEXT_PRIMARY);
        String colLine = "Col " + String.format("%,d", com.aincrad.client.HudStats.col);
        extractor.text(font, colLine, mapX + RADIUS - font.width(colLine) / 2, mapY + MAP_SIZE + 2, 0xFFE8C33A);
        extractor.text(font, "S", pcx - 3, mapY + MAP_SIZE - 10, HudPalette.TEXT_PRIMARY);
        extractor.text(font, "E", mapX + MAP_SIZE - 8, pcy - 3, HudPalette.TEXT_PRIMARY);
        extractor.text(font, "W", mapX + 2, pcy - 3, HudPalette.TEXT_PRIMARY);

        String coords = "X: " + centerX + "  Z: " + centerZ + "  Y: " + centerY;
        extractor.text(font, coords, mapX + RADIUS - font.width(coords) / 2, mapY + MAP_SIZE + 12, HudPalette.TEXT_SECONDARY);
    }

    private void renderEntities(GuiGraphicsExtractor extractor, Minecraft mc, int mapX, int mapY) {
        try {
            double range = RADIUS;
            AABB box = mc.player.getBoundingBox().inflate(range, 16, range);
            List<Entity> list = mc.level.getEntities(mc.player, box, e -> e.isAlive() && e != mc.player && !e.isSpectator() && !(e instanceof net.minecraft.world.entity.item.ItemEntity));
            for (Entity e : list) {
                int dx = (int) (e.getX() - centerX);
                int dz = (int) (e.getZ() - centerZ);
                if (dx * dx + dz * dz >= (RADIUS - 3) * (RADIUS - 3)) continue;
                int color;
                int size = 1;
                if (e instanceof Player) {
                    color = 0xFF3FC1FF;
                } else if (e instanceof Monster) {
                    color = 0xFFFF4D3D;
                } else {
                    color = 0xFFB9E08C;
                }
                int sx = mapX + RADIUS + dx;
                int sy = mapY + RADIUS + dz;
                extractor.fill(sx - size, sy - size, sx + size + 1, sy + size + 1, color);
            }
        } catch (Exception ignored) {
        }
    }

    public void toggleSize() {
        if (mapSize == 96) mapSize = 128;
        else if (mapSize == 128) mapSize = 192;
        else mapSize = 96;
    }
}
