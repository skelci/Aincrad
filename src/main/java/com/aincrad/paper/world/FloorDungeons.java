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

    private static final int GRID = 11, CELL = 3, LEVELS = 3;
    private static final int[] FLOOR_Y = {-40, -50, -60};
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
        for (int cX = (cx - 24) >> 4; cX <= (cx + 24) >> 4; cX++)
            for (int cZ = (cz - 24) >> 4; cZ <= (cz + 24) >> 4; cZ++)
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
                    set(w, x, fy, z, ring ? pal.wall : Material.AIR);
                    set(w, x, fy + 1, z, ring ? pal.wall : Material.AIR);
                    set(w, x, fy + 2, z, ring ? pal.wall : Material.AIR);
                    set(w, x, fy + 3, z, pal.wall);
                }
            decorate(w, r, x0, x1, z0, z1, fy, pal, layout);
        }
        for (var c : layout.conns)
            openDoor(layout, c, w, gx, gz, fy, pal);
    }

    private void decorate(World w, Placed r, int x0, int x1, int z0, int z1,
                          int fy, Palette pal, Layout layout) {
        int mx = (x0 + x1) / 2, mz = (z0 + z1) / 2;
        switch (r.t().type()) {
            case ENTRANCE -> {
                set(w, mx - 1, fy, mz - 1, pal.glow);
                set(w, x0 + 1, fy, z1 - 1, Material.RESPAWN_ANCHOR);
                set(w, x1 - 1, fy, z1 - 1, pal.accent);
            }
            case PASSING -> {
                if ((x0 + z0) % 3 == 0) set(w, mx, fy + 2, mz, pal.glow);
                set(w, x0 + 1, fy + 1, z0 + 1, (x0 + z0) % 2 == 0 ? Material.COBWEB : pal.wall);
                set(w, x1 - 1, fy + 1, z1 - 1, (x0 + z0) % 2 == 1 ? Material.COBWEB : pal.wall);
            }
            case MONSTER -> {
                set(w, mx, fy, mz, Material.BONE_BLOCK);
                set(w, mx, fy + 1, mz, pal.glow);
                set(w, x0 + 1, fy + 2, z0 + 1, Material.IRON_BARS);
                set(w, x1 - 1, fy + 2, z1 - 1, Material.IRON_BARS);
            }
            case MINI_BOSS -> {
                set(w, x0 + 1, fy, z0 + 1, pal.accent);
                set(w, x1 - 1, fy, z1 - 1, pal.accent);
                set(w, mx, fy + 2, mz, pal.glow);
            }
            case LOOT -> {
                chest(w, x0 + 1, fy, z0 + 1, loot(palIdx(pal), x0 + z0, true));
                chest(w, x1 - 1, fy, z0 + 1, loot(palIdx(pal), x0 - z0, false));
                set(w, mx, fy, mz, pal.accent);
                set(w, mx, fy + 2, mz, pal.glow);
            }
            case STAIRS -> set(w, x0 + 1, fy, z1 - 1, pal.glow);
            case BOSS -> {
                for (int x = x0 + 2; x <= x1 - 2; x++)
                    for (int z = z0 + 2; z <= z1 - 2; z++)
                        set(w, x, fy, z, (x == x0 + 2 || x == x1 - 2 || z == z0 + 2 || z == z1 - 2)
                                ? Material.RED_CONCRETE : Material.NETHER_BRICKS);
                set(w, x0 + 1, fy + 2, z0 + 1, Material.SHROOMLIGHT);
                set(w, x1 - 1, fy + 2, z0 + 1, Material.SHROOMLIGHT);
                set(w, x0 + 1, fy + 2, z1 - 1, Material.SHROOMLIGHT);
                set(w, x1 - 1, fy + 2, z1 - 1, Material.SHROOMLIGHT);
            }
        }
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
            boolean match = false;
            for (Door bd : b.doors()) {
                int[] inner = DungeonLayoutGenerator.doorCells(b, bd, false);
                if (inner[0] == outer[0] && inner[1] == outer[1]
                        && inner[2] == outer[2] && inner[3] == outer[3]) {
                    match = true;
                    break;
                }
            }
            if (!match) continue;
            for (int x = outer[0]; x <= outer[2]; x++)
                for (int z = outer[1]; z <= outer[3]; z++) {
                    set(w, gx + x, fy, gz + z, Material.AIR);
                    set(w, gx + x, fy + 1, gz + z, Material.AIR);
                }
            set(w, gx + outer[0], fy + 2, gz + outer[1], pal.glow);
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
            Placed a = layout.rooms.get(c.a());
            Placed b = layout.rooms.get(c.b());
            boolean found = false;
            for (Door ad : a.doors()) {
                int[] outer = DungeonLayoutGenerator.doorCells(a, ad, true);
                for (Door bd : b.doors()) {
                    int[] inner = DungeonLayoutGenerator.doorCells(b, bd, false);
                    if (inner[0] != outer[0] || inner[2] != outer[2]
                            || inner[1] != outer[1] || inner[3] != outer[3]) continue;
                    if (!w.getBlockAt(gx + outer[0], fy, gz + outer[1]).getType().isSolid()) found = true;
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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null
                || (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())) return;
        Player player = event.getPlayer();
        if (player.getY() > -30) return;
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
