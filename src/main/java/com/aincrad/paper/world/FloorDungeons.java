package com.aincrad.paper.world;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.items.ItemDefs;
import com.aincrad.paper.mobs.MobSpawnService;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.Door;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.Layout;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.Placed;
import com.aincrad.paper.world.gen.DungeonLayoutGenerator.RoomType;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Generated multi-level dungeons ("Aincard" sites) on floor 1.
 * All 3 levels of a site share one seed: rooms line up vertically, the hut
 * sits exactly over the entrance room, and the stairs room of each level
 * connects down into the next one. Rooms join via real corridors.
 */
public final class FloorDungeons implements Listener {

    public record Point(int x, int z, int floor) {}

    private static final List<Point> POINTS = List.of(
            new Point(0, -420, 1),
            new Point(380, 180, 1),
            new Point(-360, 260, 1),
            new Point(0, 460, 1),
            new Point(-440, -220, 1));

    private static final int GRID = 9;       // layout cells
    private static final int CELL = 6;       // blocks per template cell (2-cell room = 12x12, interior 10x10)
    private static final int PITCH = 13;     // cell pitch: 7-block corridor gaps between rooms
    private static final int LEVELS = 3;
    private static final int[] FLOOR_Y = {54, 44, 34};
    private static final int WALL_TOP = 5;   // interior fy..fy+WALL_TOP, ceiling +1
    private static final int ENTRANCE_X = 4;
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
    private final java.util.List<Object[]> bossTracked =
            java.util.Collections.synchronizedList(new java.util.ArrayList<>());

    private boolean stockTaskArmed;

    private void armEntryStock() {
        if (stockTaskArmed) return;
        stockTaskArmed = true;
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, this::entryStock, 60L, 40L);
    }

    private final java.util.Set<java.util.UUID> insideZone =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    private void entryStock() {
        if (mobs == null) return;
        for (org.bukkit.entity.Player p
                : org.bukkit.Bukkit.getOnlinePlayers()) {
            var w = p.getWorld();
            String wn = w.getName();
            boolean floor1 = wn.equals("floor_1") || wn.endsWith("_floor_1") || wn.endsWith(":floor_1");
            if (!floor1 || p.getY() > 61) {
                insideZone.remove(p.getUniqueId());
                continue;
            }
            boolean near = false;
            for (Point pt : POINTS) {
                if (Math.abs(p.getX() - pt.x()) < 170 && Math.abs(p.getZ() - pt.z()) < 170) {
                    near = true;
                    break;
                }
            }
            if (!near) {
                insideZone.remove(p.getUniqueId());
                continue;
            }
            if (!insideZone.add(p.getUniqueId())) continue;
            for (int i = 0; i < POINTS.size(); i++) {
                Point pt = POINTS.get(i);
                if (Math.abs(p.getX() - pt.x()) < 170 && Math.abs(p.getZ() - pt.z()) < 170) {
                    if (!stockSite(w, i)) insideZone.remove(p.getUniqueId());
                }
            }
        }
    }

    public void restock(org.bukkit.World w, int idx) {
        w.getPersistentDataContainer().remove(
                new org.bukkit.NamespacedKey(plugin, "dungeon_stock9_" + idx));
        w.getPersistentDataContainer().remove(
                new org.bukkit.NamespacedKey(plugin, "dungeon_stock_ok_" + idx));
        Point pt = POINTS.get(idx);
        if (mobs != null) mobs.purgeDungeonMobsNear(w, pt.x(), pt.z(), 260);
        stockSite(w, idx);
    }

    private boolean stockSite(org.bukkit.World w, int idx) {
        var ok = new org.bukkit.NamespacedKey(plugin, "dungeon_stock_ok_" + idx);
        var key = new org.bukkit.NamespacedKey(plugin, "dungeon_stock9_" + idx);
        Point pt = POINTS.get(idx);
        if (w.getPersistentDataContainer().has(ok, org.bukkit.persistence.PersistentDataType.BYTE))
            return true;
        boolean hasMobs = mobs != null
                && mobs.countRegularDungeonMobsNear(w, pt.x(), pt.z(), 260) > 0;
        if (hasMobs) {
            w.getPersistentDataContainer().set(ok, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
            return true;
        }
        w.getPersistentDataContainer().remove(key);
        w.getPersistentDataContainer().remove(ok);
        build(w, pt, idx, true);
        if (mobs != null && mobs.countDungeonMobsNear(w, pt.x(), pt.z(), 260) > 0) {
            w.getPersistentDataContainer().set(ok, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
            return true;
        }
        return false;
    }

    private int stockRooms(World w, List<Layout> layouts, int gx, int gz, int idx) {
        if (mobs == null) return 0;
        int n = 0;
        java.util.Random rnd = new java.util.Random(4242L + idx * 7919L);
        for (int lvl = 0; lvl < layouts.size(); lvl++) {
            int fy = FLOOR_Y[lvl];
            for (Placed r : layouts.get(lvl).rooms) {
                RoomType t = r.t().type();
                int count = switch (t) {
                    case ENTRANCE -> 0;
                    case MONSTER -> 8;
                    case LOOT -> 6;
                    case STAIRS, PASSING -> 2;
                    case MINI_BOSS -> 4;
                    case BOSS -> 6;
                };
                if (count == 0) continue;
                int[] dim = DungeonLayoutGenerator.rotDim(r.t().w(), r.t().h(), r.rot());
                int x0 = gx + r.x() * PITCH, z0 = gz + r.y() * PITCH;
                int x1 = x0 + dim[0] * CELL - 1, z1 = z0 + dim[1] * CELL - 1;
                for (int i = 0; i < count; i++) {
                    int px = x0 + 2 + rnd.nextInt(Math.max(1, x1 - x0 - 3));
                    int pz = z0 + 2 + rnd.nextInt(Math.max(1, z1 - z0 - 3));
                    if (w.getBlockAt(px, fy, pz).getType() == Material.AIR
                            && w.getBlockAt(px, fy - 1, pz).getType().isSolid()) {
                        mobs.spawnDungeonMob(new Location(w, px + 0.5, fy, pz + 0.5), lvl + 1);
                        n++;
                    }
                }
                if (t == RoomType.MINI_BOSS || t == RoomType.BOSS) {
                    int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
                    Location loc = new Location(w, mx + 0.5, fy, mz + 0.5);
                    String bn = NAMES[idx] + (t == RoomType.BOSS ? " Overlord" : " Champion");
                    trackBoss(mobs.spawnDungeonBoss(loc, lvl + 1, bn), bn);
                    n++;
                }
            }
        }
        return n;
    }

    private void trackBoss(java.util.UUID id, String name) {
        if (id == null) return;
        org.bukkit.boss.BossBar bar = org.bukkit.Bukkit.createBossBar(
                "§4§l☠ " + name, org.bukkit.boss.BarColor.RED,
                org.bukkit.boss.BarStyle.SOLID);
        bar.setVisible(false);
        bossTracked.add(new Object[]{id, bar});
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, this::tickBossBars, 40L, 30L);
    }

    private void tickBossBars() {
        synchronized (bossTracked) {
            var it = bossTracked.iterator();
            while (it.hasNext()) {
                Object[] entry = it.next();
                org.bukkit.entity.Entity e =
                        org.bukkit.Bukkit.getEntity((java.util.UUID) entry[0]);
                org.bukkit.boss.BossBar bar = (org.bukkit.boss.BossBar) entry[1];
                if (!(e instanceof org.bukkit.entity.LivingEntity lv) || lv.isDead()) {
                    bar.removeAll();
                    it.remove();
                    continue;
                }
                bar.setProgress(Math.max(0.0, Math.min(1.0, lv.getHealth() / lv.getMaxHealth())));
                for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                    boolean near = p.getWorld().equals(e.getWorld())
                            && p.getLocation().distanceSquared(e.getLocation()) < 55 * 55;
                    boolean seeing = bar.getPlayers().contains(p);
                    if (near && !seeing) bar.addPlayer(p);
                    if (!near && seeing) bar.removePlayer(p);
                }
            }
        }
    }

    public FloorDungeons(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public void setMobService(MobSpawnService service) {
        this.mobs = service;
        if (service != null) armEntryStock();
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

    /** Stock every site without waiting for a player to enter. */
    public void stockAll() {
        for (int i = 0; i < POINTS.size(); i++) {
            Point p = POINTS.get(i);
            World w = floorWorld(p.floor());
            if (w == null) continue;
            restock(w, i);
            stockExterior(w, p, i);
        }
    }

    private void stockExterior(World w, Point pt, int idx) {
        if (mobs == null) return;
        var key = new org.bukkit.NamespacedKey(plugin, "dungeon_ext_" + idx);
        var wpc = org.bukkit.persistence.PersistentDataType.BYTE;
        if (w.getPersistentDataContainer().has(key, wpc)) return;
        int[][] offsets = {{22, 0}, {-22, 0}, {0, 22}, {0, -22}};
        for (int[] o : offsets) {
            int x = pt.x() + o[0];
            int z = pt.z() + o[1];
            int y = w.getHighestBlockYAt(x, z) + 1;
            mobs.spawnDungeonMob(new Location(w, x + 0.5, y, z + 0.5), 1);
        }
        w.getPersistentDataContainer().set(key, wpc, (byte) 1);
        plugin.getLogger().info("Dungeon exterior guards stocked: " + NAMES[idx]);
    }

    public World worldForFloor(int floor) { return floorWorld(floor); }

    private World floorWorld(int floor) {
        for (World w : plugin.getServer().getWorlds()) {
            String n = w.getName().toLowerCase();
            if (n.contains("floor_") && FloorTerrain.parseFloor(n) == floor) return w;
        }
        return null;
    }

    // ------------------------------------------------------------------
    //  build
    // ------------------------------------------------------------------

    private void build(World w, Point p, int idx) {
        build(w, p, idx, false);
    }

    private void build(World w, Point p, int idx, boolean stockOnly) {
        int cx = p.x(), cz = p.z();
        for (int cX = (cx - 120) >> 4; cX <= (cx + 120) >> 4; cX++)
            for (int cZ = (cz - 120) >> 4; cZ <= (cz + 120) >> 4; cZ++)
                w.loadChunk(cX, cZ, true);
        var wpc = org.bukkit.persistence.PersistentDataType.BYTE;
        if (w.getPersistentDataContainer()
                .has(new org.bukkit.NamespacedKey(plugin, "dungeon_built4_" + idx), wpc)) {
            built.add(p);
            if (!stockOnly) return;
        }

        if (mobs != null) mobs.purgeDungeonMobsNear(w, cx, cz, 260);
        Palette pal = PALETTES[idx];
        long seed = 7919L * (idx + 1) + 13;   // same seed -> aligned layouts

        List<Layout> layouts = new ArrayList<>();
        int anchorX = ENTRANCE_X + 3, anchorY = GRID - 4;
        for (int lvl = 0; lvl < LEVELS; lvl++) {
            boolean finalLevel = lvl == LEVELS - 1;
            Layout layout = DungeonLayoutGenerator.generate(
                    seed, GRID, GRID, DungeonLayoutGenerator.DEFAULT_POOL,
                    lvl == 0, finalLevel, ENTRANCE_X, anchorX, anchorY, 8);
            layouts.add(layout);
            Placed fin = layout.find(finalLevel ? RoomType.BOSS : RoomType.STAIRS);
            if (fin != null) {
                anchorX = fin.x();
                anchorY = fin.y();
            }
        }

        // hut + entrance shaft exactly over the entrance room center
        Placed e = layouts.get(0).find(RoomType.ENTRANCE);
        int[] edim = DungeonLayoutGenerator.rotDim(e.t().w(), e.t().h(), e.rot());
        int exRel = e.x() * PITCH + edim[0] * CELL / 2;
        int ezRel = e.y() * PITCH + edim[1] * CELL / 2;
        int gx = cx - exRel, gz = cz - ezRel;
        int ex = cx, ez = cz;

        if (stockOnly) {
            int n = stockRooms(w, layouts, gx, gz, idx);
            w.playSound(new Location(w, cx + 0.5, FLOOR_Y[0] + 2.0, cz + 0.5),
                    org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 3f, 0.75f);
            plugin.getLogger().info("Dungeon stocked: " + NAMES[idx] + " (" + n + " mobs)");
            return;
        }

        int base = 128;
        for (int dx = -7; dx <= 7; dx++)
            for (int dz = -7; dz <= 7; dz++)
                base = Math.min(base, w.getHighestBlockYAt(ex + dx, ez + dz));
        base = Math.max(base, 60);

        levelHutArea(w, ex, ez, base);
        hut(w, ex, ez, base, idx);

        for (int lvl = 0; lvl < LEVELS; lvl++) {
            buildLevel(w, layouts.get(lvl), gx, gz, FLOOR_Y[lvl], pal, idx, lvl);
        }

        // entrance shaft: hut floor -> entrance room
        shaft(w, ex, ez, base, FLOOR_Y[0], pal);
        set(w, ex, FLOOR_Y[0] + WALL_TOP, ez, pal.glow);

        // vertical links: fin (stairs/boss) room of level L down to fin room of L+1
        for (int lvl = 0; lvl < LEVELS - 1; lvl++) {
            Placed fa = layouts.get(lvl).find(lvl == LEVELS - 1 ? RoomType.BOSS : RoomType.STAIRS);
            Placed fb = layouts.get(lvl + 1).find(lvl + 1 == LEVELS - 1 ? RoomType.BOSS : RoomType.STAIRS);
            if (fa == null || fb == null) continue;
            int[] da = DungeonLayoutGenerator.rotDim(fa.t().w(), fa.t().h(), fa.rot());
            int[] db = DungeonLayoutGenerator.rotDim(fb.t().w(), fb.t().h(), fb.rot());
            int fax = gx + fa.x() * PITCH + da[0] * CELL / 2;
            int faz = gz + fa.y() * PITCH + da[1] * CELL / 2;
            int fbx = gx + fb.x() * PITCH + db[0] * CELL / 2;
            int fbz = gz + fb.y() * PITCH + db[1] * CELL / 2;
            shaft(w, fax, faz, FLOOR_Y[lvl], FLOOR_Y[lvl + 1], pal);
            if (fax != fbx || faz != fbz) {
                corridor(w, fax, faz, fbx, fbz, FLOOR_Y[lvl + 1], pal);
            }
            set(w, fax, FLOOR_Y[lvl], faz, Material.GOLD_BLOCK);
        }

        for (int lvl = 0; lvl < LEVELS; lvl++) {
            verify(w, layouts.get(lvl), gx, gz, FLOOR_Y[lvl], idx);
        }
        w.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(plugin, "dungeon_built4_" + idx),
                org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
    }

    private void buildLevel(World w, Layout layout, int gx, int gz, int fy,
                            Palette pal, int idx, int lvl) {
        int[] conns = new int[layout.rooms.size()];
        for (var c : layout.conns) {
            conns[c.a()]++;
            conns[c.b()]++;
        }

        // shell pass (skip some passing rooms -> open corridor junctions)
        for (int ri = 0; ri < layout.rooms.size(); ri++) {
            Placed r = layout.rooms.get(ri);
            int[] dim = DungeonLayoutGenerator.rotDim(r.t().w(), r.t().h(), r.rot());
            int x0 = gx + r.x() * PITCH, z0 = gz + r.y() * PITCH;
            int x1 = x0 + dim[0] * CELL - 1, z1 = z0 + dim[1] * CELL - 1;
            java.util.Random rr = new java.util.Random(977L * idx + 131L * lvl + ri * 17);
            if (r.t().type() == RoomType.PASSING && conns[ri] >= 2 && rr.nextInt(100) < 35) {
                // open junction: floor + partial walls, no roof
                for (int x = x0; x <= x1; x++)
                    for (int z = z0; z <= z1; z++) {
                        set(w, x, fy - 1, z, pal.floor);
                        for (int y = fy; y <= fy + 2; y++)
                            set(w, x, y, z, Material.AIR);
                        set(w, x, fy + 3, z, Material.AIR);
                    }
                continue;
            }
            for (int x = x0; x <= x1; x++)
                for (int z = z0; z <= z1; z++) {
                    boolean ring = x == x0 || x == x1 || z == z0 || z == z1;
                    set(w, x, fy - 1, z, pal.floor);
                    for (int y = fy; y <= fy + WALL_TOP; y++)
                        set(w, x, y, z, ring ? pal.wall : Material.AIR);
                    set(w, x, fy + WALL_TOP + 1, z, pal.wall);
                }
        }

        // doors + corridors between rooms that face each other
        for (var c : layout.conns) {
            openDoor(layout, c, w, gx, gz, fy, pal);
        }

        // decorate + populate
        for (int ri = 0; ri < layout.rooms.size(); ri++) {
            Placed r = layout.rooms.get(ri);
            int[] dim = DungeonLayoutGenerator.rotDim(r.t().w(), r.t().h(), r.rot());
            int x0 = gx + r.x() * PITCH, z0 = gz + r.y() * PITCH;
            decorate(w, r, x0, x0 + dim[0] * CELL - 1, z0, z0 + dim[1] * CELL - 1, fy, pal,
                    991L * idx + 61L * lvl + x0 * 7 + z0 * 13, lvl);
        }
    }

    // ------------------------------------------------------------------
    //  decoration
    // ------------------------------------------------------------------

    private void decorate(World w, Placed r, int x0, int x1, int z0, int z1,
                          int fy, Palette pal, long seed, int lvl) {
        java.util.Random rnd = new java.util.Random(seed);
        int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        int iw = x1 - x0 - 1, id = z1 - z0 - 1;
        if (iw < 4 || id < 4) {
            return; // open junction corridors get no props
        }
        int top = fy + WALL_TOP;

        // --- floor pattern (varies per room) ---
        int pattern = rnd.nextInt(6);
        for (int x = x0 + 1; x <= x1 - 1; x++)
            for (int z = z0 + 1; z <= z1 - 1; z++) {
                switch (pattern) {
                    case 0: if (rnd.nextInt(100) < 7) set(w, x, fy, z, mossy(pal.floor)); break;
                    case 1: if ((x + z) % 2 == 0 && rnd.nextInt(100) < 22)
                                set(w, x, fy, z, mossy(pal.floor)); break;
                    case 2:
                        boolean border = x == x0 + 1 || x == x1 - 1 || z == z0 + 1 || z == z1 - 1;
                        if (border) set(w, x, fy, z, pal.accent);
                        else if (rnd.nextInt(100) < 5) set(w, x, fy, z, mossy(pal.floor));
                        break;
                    case 3: if (Math.abs(x - z) % 4 == 0 && rnd.nextInt(100) < 40)
                                set(w, x, fy, z, mossy(pal.floor)); break;
                    case 4:
                        int rx = x - x0 - 1, rz = z - z0 - 1;
                        if (rnd.nextInt(100) < (rx < 2 || rz < 2 ? 20 : 8))
                            set(w, x, fy, z, Material.COBBLESTONE);
                        break;
                    default: break;
                }
            }
        if (pattern == 2 && iw >= 6 && id >= 6) {
            set(w, mx, fy, mz, pal.glow);
        }

        // --- cobwebs high in corners + edges ---
        for (int[] c : new int[][]{{x0 + 1, z0 + 1}, {x1 - 1, z0 + 1},
                {x0 + 1, z1 - 1}, {x1 - 1, z1 - 1}, {mx, z0 + 1}, {mx, z1 - 1},
                {x0 + 1, mz}, {x1 - 1, mz}}) {
            if (rnd.nextInt(100) < 40) set(w, c[0], top, c[1], Material.COBWEB);
            if (rnd.nextInt(100) < 20) set(w, c[0], top - 1, c[1], Material.COBWEB);
        }

        // --- wall sconces every 4 blocks, both heights ---
        for (int x = x0 + 2; x <= x1 - 2; x += 4) {
            if (rnd.nextInt(100) < 75) {
                set(w, x, fy + 3, z0 + 1, pal.glow);
                set(w, x, fy + 3, z1 - 1, pal.glow);
            }
        }
        for (int z = z0 + 2; z <= z1 - 2; z += 4)
            if (rnd.nextInt(100) < 75) {
                set(w, x0 + 1, fy + 3, z, pal.glow);
                set(w, x1 - 1, fy + 3, z, pal.glow);
            }

        // --- hanging chains with lamps at mid ceiling ---
        if (iw >= 8 && id >= 8 && rnd.nextInt(100) < 70)
            for (int[] c : new int[][]{{mx, mz}, {x0 + 3, z0 + 3}, {x1 - 3, z1 - 3}}) {
                if (rnd.nextInt(100) < 55) {
                    set(w, c[0], top - 1, c[1], Material.LANTERN);
                    set(w, c[0], top, c[1], chain());
                }
            }

        // --- random grunge props ---
        int extras = 2 + rnd.nextInt(4);
        for (int i = 0; i < extras; i++) {
            int px = x0 + 1 + rnd.nextInt(iw);
            int pz = z0 + 1 + rnd.nextInt(id);
            switch (rnd.nextInt(9)) {
                case 0 -> set(w, px, fy + 1, pz, randomMushroom(rnd));
                case 1 -> { if (rnd.nextInt(100) < 60) set(w, px, fy + 1, pz, Material.DEAD_BUSH); }
                case 2 -> set(w, px, fy + 1, pz, rnd.nextBoolean()
                        ? Material.GRAVEL : Material.COBBLESTONE_WALL);
                case 3 -> set(w, px, fy + 1, pz, Material.BARREL);
                case 4 -> set(w, px, fy + 1, pz, Material.ROOTED_DIRT);
                case 5 -> set(w, px, fy + 1, pz, Material.SCAFFOLDING);
                case 6 -> {
                    set(w, px, fy + 1, pz, Material.COARSE_DIRT);
                    if (rnd.nextInt(100) < 40) set(w, px, fy + 2, pz, Material.COBWEB);
                }
                case 7 -> set(w, px, fy + 1, pz, Material.POINTED_DRIPSTONE);
                default -> { }
            }
        }

        // --- type flavor ---
        switch (r.t().type()) {
            case ENTRANCE -> {
                for (int x = mx - 2; x <= mx + 2; x++)
                    for (int z = mz - 2; z <= mz + 2; z++)
                        if (Math.abs(x - mx) == 2 || Math.abs(z - mz) == 2)
                            set(w, x, fy, z, pal.accent);
                set(w, mx, fy, mz, pal.glow);
                set(w, x0 + 2, fy, z1 - 2, Material.RESPAWN_ANCHOR);
                set(w, x1 - 2, fy, z1 - 2, Material.SMITHING_TABLE);
                set(w, x0 + 2, fy, z0 + 2, Material.BARREL);
                set(w, x1 - 2, fy, z0 + 2, Material.COMPOSTER);
                for (int i = 0; i < 4; i++) {
                    set(w, mx - 3 + i * 2, fy + 1, mz - 3, Material.LANTERN);
                    set(w, mx - 3 + i * 2, fy + 1, mz + 3, Material.LANTERN);
                }
            }
            case PASSING -> {
                set(w, mx, fy + 1, mz, pal.glow);
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
                        set(w, c[0], fy + 1, c[1], Material.BONE_BLOCK);
                        if (rnd.nextInt(100) < 50) set(w, c[0], fy + 2, c[1], Material.CALCITE);
                    }
            }
            case MINI_BOSS -> {
                for (int x = mx - 2; x <= mx + 2; x++)
                    for (int z = mz - 2; z <= mz + 2; z++)
                        if (Math.abs(x - mx) == 2 || Math.abs(z - mz) == 2)
                            set(w, x, fy, z, pal.accent);
                for (int[] c : new int[][]{{mx - 2, mz - 2}, {mx + 2, mz - 2},
                        {mx - 2, mz + 2}, {mx + 2, mz + 2}}) {
                    set(w, c[0], fy + 1, c[1], pal.glow);
                    set(w, c[0], fy + 2, c[1], Material.STONE_BRICK_WALL);
                }
                set(w, mx, fy, mz, Material.GOLD_BLOCK);
                set(w, mx, top, mz, pal.glow);
                chest(w, mx, fy + 1, mz, loot(palIdx(pal), x0 + z1, true));
            }
            case LOOT -> {
                for (int x = mx - 1; x <= mx + 1; x++)
                    for (int z = mz - 1; z <= mz + 1; z++)
                        set(w, x, fy, z, pal.accent);
                set(w, mx, fy + 2, mz, pal.glow);
                chest(w, x0 + 2, fy + 1, z0 + 2, loot(palIdx(pal), x0 + z0, true));
                chest(w, x1 - 2, fy + 1, z0 + 2, loot(palIdx(pal), x0 - z0, false));
                chest(w, x0 + 2, fy + 1, z1 - 2, loot(palIdx(pal), x0 * z0, false));
                chest(w, x1 - 2, fy + 1, z1 - 2, loot(palIdx(pal), x0 + z1, true));
                set(w, mx, fy + 1, mz, Material.ENCHANTING_TABLE);
                for (int dx = -1; dx <= 1; dx += 2)
                    for (int dz = -1; dz <= 1; dz += 2)
                        if (rnd.nextInt(100) < 70) set(w, mx + dx * 3, fy + 1, mz + dz * 3,
                                Material.BOOKSHELF);
            }
            case STAIRS -> {
                set(w, x0 + 2, fy + 1, z1 - 2, pal.glow);
                set(w, x1 - 2, fy + 1, z0 + 1, pal.glow);
                set(w, mx, fy, mz, Material.GOLD_BLOCK);
            }
            case BOSS -> {
                for (int x = x0 + 2; x <= x1 - 2; x++)
                    for (int z = z0 + 2; z <= z1 - 2; z++)
                        set(w, x, fy, z, (Math.abs(x - mx) + Math.abs(z - mz) <= 2)
                                ? Material.GOLD_BLOCK
                                : (x == x0 + 2 || x == x1 - 2 || z == z0 + 2 || z == z1 - 2
                                        ? Material.RED_CONCRETE : Material.NETHER_BRICKS));
                for (int[] c : new int[][]{{x0 + 2, z0 + 2}, {x1 - 2, z0 + 2},
                        {x0 + 2, z1 - 2}, {x1 - 2, z1 - 2}}) {
                    for (int y = fy + 1; y <= fy + 3; y++)
                        set(w, c[0], y, c[1], Material.RED_NETHER_BRICKS);
                    set(w, c[0], fy + 4, c[1], Material.SOUL_CAMPFIRE);
                }
                set(w, mx, fy, mz, Material.CRYING_OBSIDIAN);
                set(w, mx, top, mz, Material.SHROOMLIGHT);
                for (int y = fy + 1; y <= fy + 2; y++) {
                    set(w, mx - 1, y, mz, Material.IRON_BARS);
                    set(w, mx + 1, y, mz, Material.IRON_BARS);
                    set(w, mx, y, mz - 1, Material.IRON_BARS);
                    set(w, mx, y, mz + 1, Material.IRON_BARS);
                }
                chest(w, mx, fy + 1, mz, loot(palIdx(pal), mx + mz, true));
            }
        }
    }

    private Material chain() {
        Material m = Material.matchMaterial("IRON_CHAIN");
        return m != null ? m : Material.LANTERN;
    }

    private Material randomMushroom(java.util.Random rnd) {
        return switch (rnd.nextInt(4)) {
            case 0 -> Material.RED_MUSHROOM;
            case 1 -> Material.BROWN_MUSHROOM;
            case 2 -> Material.CRIMSON_FUNGUS;
            default -> Material.WARPED_FUNGUS;
        };
    }

    private Material mossy(Material m) {
        if (m == Material.STONE_BRICKS) return Material.MOSSY_STONE_BRICKS;
        if (m == Material.COBBLED_DEEPSLATE) return Material.MOSSY_COBBLESTONE;
        if (m == Material.PRISMARINE_BRICKS) return Material.DARK_PRISMARINE;
        if (m == Material.STONE) return Material.MOSSY_COBBLESTONE;
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
                new ItemStack(Material.EXPERIENCE_BOTTLE, 6 + idx * 3),
                new ItemStack(Material.BREAD, 4 + idx),
                weapon ? new ItemStack(Material.IRON_BLOCK, 2 + idx)
                        : new ItemStack(Material.COOKED_BEEF, 3 + idx)};
    }

    // ------------------------------------------------------------------
    //  doors + corridors
    // ------------------------------------------------------------------

    private void openDoor(Layout layout, DungeonLayoutGenerator.Conn c, World w,
                          int gx, int gz, int fy, Palette pal) {
        Placed a = layout.rooms.get(c.a());
        Placed b = layout.rooms.get(c.b());
        int[] wa = DungeonLayoutGenerator.rotDim(a.t().w(), a.t().h(), a.rot());
        int[] wb = DungeonLayoutGenerator.rotDim(b.t().w(), b.t().h(), b.rot());
        int ax0 = gx + a.x() * PITCH, az0 = gz + a.y() * PITCH;
        int ax1 = ax0 + wa[0] * CELL - 1, az1 = az0 + wa[1] * CELL - 1;
        int bx0 = gx + b.x() * PITCH, bz0 = gz + b.y() * PITCH;
        int bx1 = bx0 + wb[0] * CELL - 1, bz1 = bz0 + wb[1] * CELL - 1;

        for (Door ad : a.doors()) {
            int[] outer = DungeonLayoutGenerator.doorCells(a, ad, true);
            for (Door bd : b.doors()) {
                int[] ic = DungeonLayoutGenerator.doorCells(b, bd, false);
                if (ic[0] != outer[0] || ic[1] != outer[1]
                        || ic[2] != outer[2] || ic[3] != outer[3]) continue;
                int[] A = doorPoint(ax0, az0, ax1, az1, ad);
                int[] B = doorPoint(bx0, bz0, bx1, bz1, bd);
                corridor(w, A[0], A[1], B[0], B[1], fy, pal);
                doorFrame(w, A, fy, pal);
                doorFrame(w, B, fy, pal);
                return;
            }
        }
    }

    /** Door center block on a room face + outward normal. */
    private int[] doorPoint(int x0, int z0, int x1, int z1, Door d) {
        int along = (d.start() + 1) * CELL;
        return switch (d.side()) {
            case 0 -> new int[]{x0 + along, z0, 0, -1};
            case 1 -> new int[]{x1, z0 + along, 1, 0};
            case 2 -> new int[]{x0 + along, z1, 0, 1};
            default -> new int[]{x0, z0 + along, -1, 0};
        };
    }

    private void doorFrame(World w, int[] A, int fy, Palette pal) {
        boolean vertical = A[2] != 0;
        int x = A[0], z = A[1];
        for (int d = -1; d <= 1; d++)
            for (int y = fy; y <= fy + 3; y++) {
                if (vertical) set(w, x, y, z + d, Material.AIR);
                else set(w, x + d, y, z, Material.AIR);
            }
        set(w, x - A[2], fy, z - A[3], pal.accent);
        set(w, x - A[2], fy + 1, z - A[3], pal.accent);
        set(w, x, fy + 4, z, pal.glow);
    }


    /** 3-wide, 5-tall L corridor between two points on floor fy. */
    private void corridor(World w, int x1, int z1, int x2, int z2, int fy, Palette pal) {
        int steps = 0;
        int x = x1, z = z1;
        while (x != x2 && steps++ < 400) {
            x += Integer.signum(x2 - x);
            carveNode(w, x, z, fy, pal, steps);
        }
        steps = 0;
        while (z != z2 && steps++ < 400) {
            z += Integer.signum(z2 - z);
            carveNode(w, x, z, fy, pal, steps);
        }
    }

    private void carveNode(World w, int x, int z, int fy, Palette pal, int i) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) {
                set(w, x + dx, fy - 1, z + dz, pal.floor);
                for (int y = fy; y <= fy + 4; y++)
                    set(w, x + dx, y, z + dz, Material.AIR);
                set(w, x + dx, fy + 5, z + dz, pal.wall);
            }
        if (i % 3 == 0) set(w, x, fy + 5, z, Material.SEA_LANTERN);
        if (i % 6 == 0) {
            set(w, x, fy + 4, z, chain());
            set(w, x, fy + 3, z, pal.glow);
        }
    }

    // ------------------------------------------------------------------
    //  shaft, hut, verify
    // ------------------------------------------------------------------

    private void shaft(World w, int sx, int sz, int top, int bottom, Palette pal) {
        for (int y = bottom; y <= top; y++) {
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++) {
                    boolean ring = Math.abs(dx) == 1 || Math.abs(dz) == 1;
                    set(w, sx + dx, y, sz + dz, ring ? pal.wall : Material.AIR);
                }
            set(w, sx + 1, y, sz + 1, Material.LADDER);
            if (y % 6 == 0) set(w, sx - 1, y, sz - 1, Material.SEA_LANTERN);
        }
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                set(w, sx + dx, bottom - 1, sz + dz, pal.floor);
        set(w, sx + 1, top, sz + 1, Material.LADDER);
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
            Placed a = layout.rooms.get(c.a());
            int[] wa = DungeonLayoutGenerator.rotDim(a.t().w(), a.t().h(), a.rot());
            boolean found = false;
            for (Door ad : a.doors()) {
                int ax0 = gx + a.x() * PITCH, az0 = gz + a.y() * PITCH;
                int ax1 = ax0 + wa[0] * CELL - 1, az1 = az0 + wa[1] * CELL - 1;
                int xc = ad.side() == 1 ? ax1 : ad.side() == 3 ? ax0
                        : ax0 + (ad.start() + 1) * CELL;
                int zc = ad.side() == 2 ? az1 : ad.side() == 0 ? az0
                        : az0 + (ad.start() + 1) * CELL;
                int xo = ad.side() == 1 ? 1 : ad.side() == 3 ? -1 : 0;
                int zo = ad.side() == 2 ? 1 : ad.side() == 0 ? -1 : 0;
                if (!w.getBlockAt(xc + xo, fy, zc + zo).getType().isSolid()) {
                    found = true;
                    break;
                }
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
}
