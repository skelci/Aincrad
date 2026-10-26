package com.aincrad.paper.world;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.items.ItemDefs;
import com.aincrad.paper.mobs.MobSpawnService;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.Door;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.Layout;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.Placed;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.RoomType;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Generated multi-level dungeons on floor 1.
 * Layouts come from DungeonLayoutGenerator (MystAsc port); three stacked
 * levels per site: entry, middle, boss. Non-final levels end in a stairs
 * room whose shaft drops into the next level (the boss pit).
 */
public final class FloorDungeons implements Listener {

    public record Point(int x, int z, int floor) {}

    private static final List<Point> POINTS = List.of(
            new Point(0, -420, 1),
            new Point(380, 180, 1),
            new Point(-360, 260, 1),
            new Point(0, 460, 1),
            new Point(-440, -220, 1));

    private static final int GRID = 9, CELL = 12, LEVELS = 3;
    private static final int[] FLOOR_Y = {54, 44, 34};
    private static final int ENTRANCE_X = 4;
    private static final long LOCKOUT_MS = 10 * 60_000L;
    private static final String[] NAMES = {
            "The Rusty Sword", "Goblin Warrens", "The Flooded Crypt",
            "Spider's Lair", "Crystal Caverns"};
    private static final int[] REC_LVL = {3, 6, 9, 11, 13};

    private static final class Palette {
        final Material wall, floor, accent, glow;
        Palette(Material wall, Material floor, Material accent, Material glow) {
            this.wall = wall; this.floor = floor; this.accent = accent; this.glow = glow;
        }
    }

    private static final Palette[] PALETTES = {
            new Palette(Material.MOSSY_STONE_BRICKS, Material.STONE_BRICKS,
                    Material.IRON_BLOCK, Material.LANTERN),
            new Palette(Material.ANDESITE, Material.COBBLED_DEEPSLATE,
                    Material.MOSS_BLOCK, Material.SOUL_LANTERN),
            new Palette(Material.DARK_PRISMARINE, Material.PRISMARINE_BRICKS,
                    Material.COPPER_BLOCK, Material.SEA_LANTERN),
            new Palette(Material.COBBLED_DEEPSLATE, Material.DEEPSLATE_BRICKS,
                    Material.COBBLESTONE, Material.SOUL_LANTERN),
            new Palette(Material.QUARTZ_BRICKS, Material.SMOOTH_QUARTZ,
                    Material.AMETHYST_BLOCK, Material.SHROOMLIGHT)};

    private final AincradPaperPlugin plugin;
    private MobSpawnService mobs;
    private final Map<Point, Long> lastArmed = new java.util.concurrent.ConcurrentHashMap<>();

    public FloorDungeons(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public void setMobService(MobSpawnService service) {
        this.mobs = service;
    }

    public static boolean nearDungeon(World w, int x, int z, int radius) {
        for (Point p : POINTS)
            if (Math.hypot(x - p.x(), z - p.z()) < radius) return true;
        return false;
    }

    private final java.util.Set<Point> built = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public void buildAll() {
        for (int i = 0; i < POINTS.size(); i++) {
            Point p = POINTS.get(i);
            if (!built.add(p)) continue;
            World w = floorWorld(p.floor());
            if (w == null) {
                built.remove(p);
                continue;
            }
            try {
                build(w, p, i);
                org.bukkit.Bukkit.getLogger().info("[Aincrad] Dungeon built: " + NAMES[i]
                        + " at floor " + p.floor() + " (" + p.x() + ", " + p.z() + ")");
            } catch (Exception e) {
                built.remove(p);
                org.bukkit.Bukkit.getLogger().warning("[Aincrad] Dungeon build failed: " + e);
            }
        }
    }

    private World floorWorld(int floor) {
        for (World w : plugin.getServer().getWorlds()) {
            String n = w.getName().toLowerCase();
            if (n.contains("floor_") && FloorTerrain.parseFloor(n) == floor) return w;
        }
        return null;
    }

    private void build(World w, Point p, int idx) {
        int cx = p.x(), cz = p.z();
        for (int cX = (cx - 68) >> 4; cX <= (cx + 68) >> 4; cX++)
            for (int cZ = (cz - 68) >> 4; cZ <= (cz + 68) >> 4; cZ++)
                w.loadChunk(cX, cZ, true);
        var wpc = org.bukkit.persistence.PersistentDataType.BYTE;
        if (w.getPersistentDataContainer()
                .has(new org.bukkit.NamespacedKey(plugin, "dungeon_built_" + idx), wpc)) {
            built.add(p);
            return;
        }

        int base = 128;
        for (int dx = -7; dx <= 7; dx++)
            for (int dz = -7; dz <= 7; dz++)
                base = Math.min(base, w.getHighestBlockYAt(cx + dx, cz + dz));
        base = Math.max(base, 60);

        int gx = cx - (GRID * CELL) / 2, gz = cz - 2;
        Palette pal = PALETTES[idx];
        long seed = 7919L * (idx + 1) + 13;

        levelHutArea(w, cx, cz, base);
        hut(w, cx, cz, base, idx);

        int anchorX = ENTRANCE_X + 3, anchorY = GRID - 4;
        for (int lvl = 0; lvl < LEVELS; lvl++) {
            boolean finalLevel = lvl == LEVELS - 1;
            Layout layout = DungeonLayoutGenerator.generate(
                    seed + lvl * 104729L, GRID, GRID, DungeonLayoutGenerator.DEFAULT_POOL,
                    lvl == 0, finalLevel, ENTRANCE_X, anchorX, anchorY, 8);
            buildLevel(w, layout, gx, gz, FLOOR_Y[lvl], pal);

            Placed fin = layout.find(finalLevel ? RoomType.BOSS : RoomType.STAIRS);
            anchorX = fin.x();
            anchorY = fin.y();
            if (!finalLevel) {
                int sx = gx + (fin.x() + 1) * CELL, sz = gz + (fin.y() + 1) * CELL;
                shaft(w, sx, sz, FLOOR_Y[lvl] - 1, FLOOR_Y[lvl + 1]);
            }
            if (lvl == 0) {
                Placed e = layout.find(RoomType.ENTRANCE);
                int[] dim = DungeonLayoutGenerator.rotDim(e.t().w(), e.t().h(), e.rot());
                int ex = gx + e.x() * CELL + dim[0] * CELL / 2;
                int ez = gz + e.y() * CELL + dim[1] * CELL / 2;
                shaft(w, ex, ez, base, FLOOR_Y[0]);
            }
            verify(w, layout, gx, gz, FLOOR_Y[lvl], idx);
        }
        set(w, cx - 2, FLOOR_Y[0], cz, Material.GOLD_BLOCK);
        w.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(plugin, "dungeon_built_" + idx),
                org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
    }

    private void buildLevel(World w, Layout layout, int gx, int gz, int fy, Palette pal) {
        for (Placed r : layout.rooms) {
            int[] dim = DungeonLayoutGenerator.rotDim(r.t().w(), r.t().h(), r.rot());
            int x0 = gx + r.x() * CELL, z0 = gz + r.y() * CELL;
            int x1 = x0 + dim[0] * CELL - 1, z1 = z0 + dim[1] * CELL - 1;
            for (int x = x0; x <= x1; x++)
                for (int z = z0; z <= z1; z++) {
                    boolean ring = x == x0 || x == x1 || z == z0 || z == z1;
                    set(w, x, fy - 1, z, pal.floor);
                    for (int y = fy; y <= fy + 3; y++)
                        set(w, x, y, z, ring ? pal.wall : Material.AIR);
                    set(w, x, fy + 4, z, pal.wall);
                }
        }
        for (var c : layout.conns)
            openDoor(layout, c, w, gx, gz, fy, pal);
        for (Placed r : layout.rooms) {
            int[] dim = DungeonLayoutGenerator.rotDim(r.t().w(), r.t().h(), r.rot());
            int x0 = gx + r.x() * CELL, z0 = gz + r.y() * CELL;
            decorate(w, r, x0, x0 + dim[0] * CELL - 1, z0, z0 + dim[1] * CELL - 1, fy, pal,
                    layout, idxOf(pal) * 31 + r.x() * 7 + r.y() * 13);
        }
    }

    private int idxOf(Palette pal) {
        return palIdx(pal);
    }

    private void decorate(World w, Placed r, int x0, int x1, int z0, int z1,
                          int fy, Palette pal, Layout layout, long seed) {
        java.util.Random rnd = new java.util.Random(seed);
        int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        int iw = x1 - x0 - 1, id = z1 - z0 - 1;

        for (int x = x0 + 1; x <= x1 - 1; x++)
            for (int z = z0 + 1; z <= z1 - 1; z++)
                if (rnd.nextInt(100) < 6) set(w, x, fy, z, mossy(pal.floor));
        for (int t = 0; t < 2; t++) {
            int px = rnd.nextBoolean() ? x0 + 1 : x1 - 1;
            int pz = rnd.nextInt(id) + z0 + 1;
            if (rnd.nextInt(100) < 45) set(w, px, fy + 3, pz, Material.COBWEB);
            int pz2 = rnd.nextBoolean() ? z0 + 1 : z1 - 1;
            int px2 = rnd.nextInt(iw) + x0 + 1;
            if (rnd.nextInt(100) < 45) set(w, px2, fy + 3, pz2, Material.COBWEB);
        }
        boolean corners = iw >= 8 && id >= 8;
        if (corners && r.t().type() != RoomType.BOSS) {
            for (int[] c : new int[][]{{x0 + 1, z0 + 1}, {x1 - 1, z0 + 1},
                    {x0 + 1, z1 - 1}, {x1 - 1, z1 - 1}}) {
                if (rnd.nextInt(100) < 35) set(w, c[0], fy, c[1], pal.glow);
                if (rnd.nextInt(100) < 55) {
                    set(w, c[0], fy + 1, c[1], Material.STONE_BRICK_WALL);
                    set(w, c[0], fy + 2, c[1], Material.STONE_BRICK_WALL);
                }
            }
        }
        if (corners && iw >= 10 && id >= 10)
            for (int[] c : new int[][]{{mx, z0 + 1}, {mx, z1 - 1}, {x0 + 1, mz}, {x1 - 1, mz}})
                if (rnd.nextInt(100) < 60) {
                    set(w, c[0], fy, c[1], pal.accent);
                    set(w, c[0], fy + 1, c[1], Material.LANTERN);
                }

        switch (r.t().type()) {
            case ENTRANCE -> {
                for (int x = mx - 1; x <= mx + 1; x++)
                    for (int z = mz - 1; z <= mz + 1; z++)
                        set(w, x, fy, z, pal.accent);
                set(w, mx, fy, mz, pal.glow);
                set(w, x0 + 2, fy, z1 - 2, Material.RESPAWN_ANCHOR);
                set(w, x1 - 2, fy, z1 - 2, Material.SMITHING_TABLE);
                set(w, x0 + 2, fy, z0 + 2, Material.BARREL);
                set(w, x1 - 2, fy, z0 + 2, Material.COMPOSTER);
                Material chain = org.bukkit.Material.matchMaterial("IRON_CHAIN") != null
                        ? org.bukkit.Material.matchMaterial("IRON_CHAIN")
                        : org.bukkit.Material.matchMaterial("CHAIN");
                if (chain != null) set(w, mx, fy + 2, mz, chain);
                set(w, mx, fy + 3, mz, pal.glow);
            }
            case PASSING -> {
                for (int i = 0; i < 3; i++) {
                    int px = x0 + 2 + rnd.nextInt(Math.max(1, iw - 3));
                    int pz = z0 + 2 + rnd.nextInt(Math.max(1, id - 3));
                    set(w, px, fy, pz, rnd.nextInt(100) < 50
                            ? Material.COBBLESTONE : mossy(pal.floor));
                    if (rnd.nextInt(100) < 30) set(w, px, fy + 1, pz, Material.COBWEB);
                }
                set(w, mx, fy, mz, pal.glow);
            }
            case MONSTER -> {
                set(w, mx, fy, mz, Material.BONE_BLOCK);
                set(w, mx, fy + 1, mz, pal.glow);
                for (int y = fy + 1; y <= fy + 2; y++) {
                    set(w, mx - 1, y, mz, Material.IRON_BARS);
                    set(w, mx + 1, y, mz, Material.IRON_BARS);
                    set(w, mx, y, mz - 1, Material.IRON_BARS);
                    set(w, mx, y, mz + 1, Material.IRON_BARS);
                }
                for (int[] c : new int[][]{{x0 + 2, z0 + 2}, {x1 - 2, z1 - 2},
                        {x0 + 2, z1 - 2}, {x1 - 2, z0 + 2}})
                    if (rnd.nextInt(100) < 60) {
                        set(w, c[0], fy, c[1], Material.BONE_BLOCK);
                        set(w, c[0], fy + 1, c[1], Material.BONE_MEAL);
                    }
            }
            case MINI_BOSS -> {
                for (int x = mx - 2; x <= mx + 2; x++)
                    for (int z = mz - 2; z <= mz + 2; z++)
                        if (Math.abs(x - mx) == 2 || Math.abs(z - mz) == 2)
                            set(w, x, fy, z, pal.accent);
                set(w, mx - 2, fy, mz - 2, pal.glow);
                set(w, mx + 2, fy, mz - 2, pal.glow);
                set(w, mx - 2, fy, mz + 2, pal.glow);
                set(w, mx + 2, fy, mz + 2, pal.glow);
                set(w, mx, fy + 3, mz, pal.glow);
            }
            case LOOT -> {
                for (int x = mx - 1; x <= mx + 1; x++)
                    for (int z = mz - 1; z <= mz + 1; z++)
                        set(w, x, fy, z, pal.accent);
                set(w, mx, fy + 2, mz, pal.glow);
                chest(w, x0 + 2, fy, z0 + 2, loot(palIdx(pal), x0 + z0, true));
                chest(w, x1 - 2, fy, z0 + 2, loot(palIdx(pal), x0 - z0, false));
                chest(w, x0 + 2, fy, z1 - 2, loot(palIdx(pal), x0 * z0, false));
                chest(w, x1 - 2, fy, z1 - 2, loot(palIdx(pal), x0 + z1, true));
                set(w, mx, fy + 1, mz, Material.ENCHANTING_TABLE);
            }
            case STAIRS -> {
                set(w, x0 + 2, fy, z1 - 2, pal.glow);
                set(w, x1 - 2, fy, z0 + 1, pal.glow);
            }
            case BOSS -> {
                for (int x = x0 + 2; x <= x1 - 2; x++)
                    for (int z = z0 + 2; z <= z1 - 2; z++)
                        set(w, x, fy, z, (x == x0 + 2 || x == x1 - 2 || z == z0 + 2 || z == z1 - 2)
                                ? Material.RED_CONCRETE : Material.NETHER_BRICKS);
                for (int[] c : new int[][]{{x0 + 2, z0 + 2}, {x1 - 2, z0 + 2},
                        {x0 + 2, z1 - 2}, {x1 - 2, z1 - 2}}) {
                    set(w, c[0], fy, c[1], Material.POLISHED_BLACKSTONE);
                    set(w, c[0], fy + 1, c[1], Material.CAMPFIRE);
                }
                set(w, mx, fy, mz, Material.CRYING_OBSIDIAN);
                set(w, mx, fy + 3, mz, Material.SHROOMLIGHT);
                for (int y = fy + 1; y <= fy + 2; y++) {
                    set(w, mx - 1, y, mz, Material.IRON_BARS);
                    set(w, mx + 1, y, mz, Material.IRON_BARS);
                    set(w, mx, y, mz - 1, Material.IRON_BARS);
                    set(w, mx, y, mz + 1, Material.IRON_BARS);
                }
            }
        }
    }

    private Material mossy(Material m) {
        if (m == Material.STONE_BRICKS) return Material.MOSSY_STONE_BRICKS;
        if (m == Material.COBBLED_DEEPSLATE) return Material.MOSSY_COBBLESTONE;
        if (m == Material.PRISMARINE_BRICKS) return Material.DARK_PRISMARINE;
        return m;
    }

    private int palIdx(Palette pal) {
        for (int i = 0; i < PALETTES.length; i++) if (PALETTES[i] == pal) return i;
        return 0;
    }

    private ItemStack[] loot(int idx, int salt, boolean weapon) {
        var ids = ItemDefs.idsForLevel(REC_LVL[idx] + 2);
        String id = ids.isEmpty() ? "anneal_blade"
                : ids.get(Math.abs(salt * 31 + (weapon ? 7 : 13)) % ids.size());
        return new ItemStack[] {
                ItemDefs.create(id, 1),
                new ItemStack(Material.GOLD_INGOT, 10 + idx * 4),
                new ItemStack(Material.EXPERIENCE_BOTTLE, 6 + idx * 3)};
    }

    private void openDoor(Layout layout, DungeonLayoutGenerator.Conn c, World w,
                          int gx, int gz, int fy, Palette pal) {
        Placed a = layout.rooms.get(c.a());
        Placed b = layout.rooms.get(c.b());
        for (Door ad : a.doors()) {
            int[] outer = DungeonLayoutGenerator.doorCells(a, ad, true);
            int[] inner = null;
            for (Door bd : b.doors()) {
                int[] ic = DungeonLayoutGenerator.doorCells(b, bd, false);
                if (ic[0] == outer[0] && ic[1] == outer[1]
                        && ic[2] == outer[2] && ic[3] == outer[3]) {
                    inner = ic;
                    break;
                }
            }
            if (inner == null) continue;
            int ux0 = Math.min(outer[0], inner[0]), uz0 = Math.min(outer[1], inner[1]);
            int ux1 = Math.max(outer[2], inner[2]), uz1 = Math.max(outer[3], inner[3]);
            int[] wa = DungeonLayoutGenerator.rotDim(a.t().w(), a.t().h(), a.rot());
            int ax0 = a.x(), ax1 = a.x() + wa[0] - 1, az0 = a.y(), az1 = a.y() + wa[1] - 1;
            if (ux0 > ax1) {
                int xc = gx + (ax1 + 1) * CELL;
                int zc = gz + (uz0 + uz1 + 1) * CELL / 2;
                carveDoorV(w, xc, zc, fy, pal);
            } else if (ux1 < ax0) {
                int xc = gx + ax0 * CELL;
                int zc = gz + (uz0 + uz1 + 1) * CELL / 2;
                carveDoorV(w, xc, zc, fy, pal);
            } else if (uz0 > az1) {
                int zc = gz + (az1 + 1) * CELL;
                int xc = gx + (ux0 + ux1 + 1) * CELL / 2;
                carveDoorH(w, xc, zc, fy, pal);
            } else if (uz1 < az0) {
                int zc = gz + az0 * CELL;
                int xc = gx + (ux0 + ux1 + 1) * CELL / 2;
                carveDoorH(w, xc, zc, fy, pal);
            }
        }
    }

    private void carveDoorV(World w, int xc, int zc, int fy, Palette pal) {
        for (int z = zc - 2; z <= zc + 2; z++)
            for (int y = fy; y <= fy + 2; y++) {
                set(w, xc - 1, y, z, Material.AIR);
                set(w, xc, y, z, Material.AIR);
                set(w, xc + 1, y, z, Material.AIR);
            }
        doorFrame(w, xc, fy, zc, true, pal);
    }

    private void carveDoorH(World w, int xc, int zc, int fy, Palette pal) {
        for (int x = xc - 2; x <= xc + 2; x++)
            for (int y = fy; y <= fy + 2; y++) {
                set(w, x, y, zc - 1, Material.AIR);
                set(w, x, y, zc, Material.AIR);
                set(w, x, y, zc + 1, Material.AIR);
            }
        doorFrame(w, xc, fy, zc, false, pal);
    }

    private int[] doorCenter(Layout layout, DungeonLayoutGenerator.Conn c) {
        Placed a = layout.rooms.get(c.a());
        Placed b = layout.rooms.get(c.b());
        for (Door ad : a.doors()) {
            int[] outer = DungeonLayoutGenerator.doorCells(a, ad, true);
            for (Door bd : b.doors()) {
                int[] inner = DungeonLayoutGenerator.doorCells(b, bd, false);
                if (inner[0] != outer[0] || inner[1] != outer[1]
                        || inner[2] != outer[2] || inner[3] != outer[3]) continue;
                int ux0 = Math.min(outer[0], inner[0]), uz0 = Math.min(outer[1], inner[1]);
                int ux1 = Math.max(outer[2], inner[2]), uz1 = Math.max(outer[3], inner[3]);
                int[] wa = DungeonLayoutGenerator.rotDim(a.t().w(), a.t().h(), a.rot());
                int ax0 = a.x(), ax1 = a.x() + wa[0] - 1, az0 = a.y(), az1 = a.y() + wa[1] - 1;
                if (ux0 > ax1) return new int[]{ax1 + 1, uz0 + uz1 + 1, 1};
                if (ux1 < ax0) return new int[]{ax0, uz0 + uz1 + 1, 1};
                if (uz0 > az1) return new int[]{ux0 + ux1 + 1, az1 + 1, 0};
                if (uz1 < az0) return new int[]{ux0 + ux1 + 1, az0, 0};
            }
        }
        return null;
    }

    private void doorFrame(World w, int xc, int fy, int zc, boolean vertical, Palette pal) {
        int h = vertical ? 0 : 0;
        Material f = pal.accent;
        if (vertical) {
            set(w, xc, fy, zc - 3, f);
            set(w, xc, fy, zc + 3, f);
            set(w, xc, fy + 1, zc - 3, f);
            set(w, xc, fy + 1, zc + 3, f);
            set(w, xc - 1, fy + 3, zc, pal.glow);
            set(w, xc + 1, fy + 3, zc, pal.glow);
        } else {
            set(w, xc - 3, fy, zc, f);
            set(w, xc + 3, fy, zc, f);
            set(w, xc - 3, fy + 1, zc, f);
            set(w, xc + 3, fy + 1, zc, f);
            set(w, xc, fy + 3, zc - 1, pal.glow);
            set(w, xc, fy + 3, zc + 1, pal.glow);
        }
    }

    private void hut(World w, int cx, int cz, int base, int idx) {
        for (int dx = -4; dx <= 4; dx++)
            for (int dz = -4; dz <= 4; dz++) {
                set(w, cx + dx, base - 1, cz + dz, Material.STONE);
                set(w, cx + dx, base, cz + dz, Material.SMOOTH_STONE);
            }
        for (int y = 1; y <= 3; y++)
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++) {
                    boolean door = dz == 3 && Math.abs(dx) <= 1 && y <= 2;
                    if ((Math.abs(dx) == 3 || Math.abs(dz) == 3) && !door)
                        set(w, cx + dx, base + y, cz + dz, Material.CRACKED_STONE_BRICKS);
                }
        set(w, cx, base + 1, cz + 4, Material.LANTERN);
        for (int dx = -3; dx <= 3; dx++)
            for (int dz = -3; dz <= 3; dz++)
                if (Math.abs(dx) == 3 || Math.abs(dz) == 3)
                    set(w, cx + dx, base + 4, cz + dz, Material.STONE_BRICKS);
        set(w, cx, base + 4, cz, Material.GOLD_BLOCK);
        set(w, cx - 3, base + 1, cz - 3, Material.LANTERN);
        set(w, cx + 3, base + 1, cz - 3, Material.LANTERN);
        set(w, cx + 4, base, cz, Material.SMITHING_TABLE);
    }

    private void shaft(World w, int sx, int sz, int top, int bottom) {
        for (int y = bottom; y <= top; y++) {
            set(w, sx, y, sz, Material.AIR);
            set(w, sx + 1, y, sz, Material.AIR);
            set(w, sx, y, sz + 1, y % 6 == 0 ? Material.LANTERN : Material.AIR);
            set(w, sx + 1, y, sz + 1, Material.LADDER);
        }
    }

    private void levelHutArea(World w, int cx, int cz, int base) {
        for (int dx = -5; dx <= 5; dx++)
            for (int dz = -5; dz <= 5; dz++) {
                int y = w.getHighestBlockYAt(cx + dx, cz + dz);
                for (int yy = base + 1; yy <= Math.min(y, base + 16); yy++)
                    set(w, cx + dx, yy, cz + dz, Material.AIR);
            }
    }

    private void verify(World w, Layout layout, int gx, int gz, int fy, int idx) {
        int open = 0, blocked = 0;
        for (var c : layout.conns) {
            int[] dc = doorCenter(layout, c);
            boolean found = false;
            if (dc != null) {
                int xc = dc[2] == 1 ? gx + dc[0] * CELL : gx + dc[0] * CELL / 2;
                int zc = dc[2] == 1 ? gz + dc[1] * CELL / 2 : gz + dc[1] * CELL;
                found = !w.getBlockAt(xc, fy, zc).getType().isSolid();
            }
            if (found) open++;
            else blocked++;
        }
        org.bukkit.Bukkit.getLogger().info("[Aincrad] Dungeon " + NAMES[idx]
                + " fy=" + fy + " rooms=" + layout.rooms.size()
                + " conns=" + layout.conns.size() + " open=" + open + " blocked=" + blocked);
    }

    private void chest(World w, int x, int y, int z, ItemStack[] items) {
        set(w, x, y, z, Material.CHEST);
        var state = w.getBlockAt(x, y, z).getState();
        if (state instanceof Container container) {
            var inv = container.getInventory();
            for (int i = 0; i < items.length && i < inv.getSize(); i++)
                inv.setItem(i, items[i]);
            container.update(true, false);
        }
    }

    private void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m, false);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null
                || (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())) return;
        Player player = event.getPlayer();
        if (player.getY() > 58) return;
        int floor = FloorTerrain.parseFloor(player.getWorld().getName());
        for (Point p : POINTS) {
            if (p.floor() != floor) continue;
            if (Math.hypot(player.getX() - p.x(), player.getZ() - p.z()) > 26) continue;
            Long last = lastArmed.get(p);
            long now = System.currentTimeMillis();
            if (last != null && now - last < LOCKOUT_MS) continue;
            lastArmed.put(p, now);
            armDefenders(player, p, floor);
        }
    }

    private void armDefenders(Player player, Point p, int floor) {
        player.sendActionBar(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize("&4&lThe dungeon stirs..."));
        if (mobs == null) return;
        World w = player.getWorld();
        for (int i = 0; i < 4; i++) {
            Location loc = new Location(w, p.x() + 1 + i, FLOOR_Y[0], p.z() - 2);
            if (loc.getBlock().getType() == Material.AIR) mobs.spawnRandomAt(loc, floor + 1);
        }
        Location g = new Location(w, p.x() - 1, FLOOR_Y[0], p.z() - 2);
        if (g.getBlock().getType() == Material.AIR) mobs.spawnGuardian(g, floor);
    }
}
