package com.aincrad.paper.world;

import com.aincrad.paper.items.ItemDefs;
import com.aincrad.paper.mobs.MobSpawnService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fixed physical dungeons: surface entrance hut, shaft down to a hall with
 * loot room, mob room and boss room. Going underground arms the defenders,
 * once per dungeon per lockout window.
 */
public final class FloorDungeons implements Listener {

    private static final int G = TownKit.G;
    private static final int DEEP = 50;
    private static final long LOCKOUT_MS = 10 * 60 * 1000L;

    private record Point(int x, int z, int floor) {
    }

    private static final List<Point> POINTS = List.of(
            new Point(0, -420, 1),
            new Point(380, 180, 1),
            new Point(-360, 260, 1),
            new Point(0, 460, 1),
            new Point(-440, -220, 1));

    private final Map<Point, Long> lastArmed = new HashMap<>();
    private final Plugin plugin;
    private MobSpawnService mobs;

    public FloorDungeons(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setMobService(MobSpawnService service) {
        this.mobs = service;
    }

    public static boolean nearDungeon(World w, int x, int z, int radius) {
        int floor = FloorTerrain.parseFloor(w.getName());
        for (Point p : POINTS) {
            if (p.floor() != floor) continue;
            if (Math.hypot(x - p.x(), z - p.z()) < radius) return true;
        }
        return false;
    }

    public void buildAll() {
        for (Point p : POINTS) {
            World w = floorWorld(p.floor());
            if (w == null) continue;
            if (w.getBlockAt(p.x(), G + 6, p.z()).getType() == Material.GOLD_BLOCK) continue;
            w.getChunkAt(p.x(), p.z()).load(true);
            try {
                build(w, p);
                plugin.getServer().getLogger().info(
                        "[Aincrad] Dungeon built at floor " + p.floor() + " (" + p.x() + ", " + p.z() + ")");
            } catch (Exception e) {
                plugin.getServer().getLogger().warning("[Aincrad] Dungeon build failed: " + e);
            }
        }
    }

    private World floorWorld(int floor) {
        for (World w : plugin.getServer().getWorlds()) {
            String n = w.getName().toLowerCase();
            if (n.contains("floor_") && FloorTerrain.parseFloor(n) == floor) {
                return w;
            }
        }
        return null;
    }

    private void build(World w, Point p) {
        int minX = (p.x() - 8) >> 4, maxX = (p.x() + 12) >> 4;
        int minZ = (p.z() - 44) >> 4, maxZ = (p.z() + 8) >> 4;
        for (int cX = minX; cX <= maxX; cX++)
            for (int cZ = minZ; cZ <= maxZ; cZ++)
                w.loadChunk(cX, cZ, true);

        int cx = p.x(), cz = p.z();
        int base = G;
        for (int dx = -5; dx <= 9; dx++)
            for (int dz = -5; dz <= 5; dz++)
                base = Math.max(base, w.getHighestBlockYAt(cx + dx, cz + dz));
        for (int dx = -5; dx <= 9; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                for (int y = base + 1; y <= base + 14; y++)
                    w.getBlockAt(cx + dx, y, cz + dz).setType(Material.AIR, false);
                w.getBlockAt(cx + dx, base - 1, cz + dz).setType(Material.STONE, false);
                w.getBlockAt(cx + dx, base, cz + dz).setType(Material.SMOOTH_STONE, false);
            }
        }
        hut(w, cx, cz, base);
        shaft(w, cx + 6, cz, base);
        hall(w, cx, cz);
        tunnel(w, cx, cz);
        verify(w, cx, cz);
        w.getBlockAt(cx + 8, base + 2, cz).setType(Material.GOLD_BLOCK, false);
        w.getBlockAt(cx, G + 6, cz).setType(Material.GOLD_BLOCK, false);
    }

    private void hut(World w, int cx, int cz, int base) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                boolean wall = Math.abs(dx) == 3 || Math.abs(dz) == 3;
                if (!wall) continue;
                boolean door = dz == 3 && Math.abs(dx) <= 1;
                for (int y = 1; y <= 5; y++) {
                    if (door && (y == 1 || y == 2)) continue;
                    Material m = y >= 4 ? Material.CRACKED_STONE_BRICKS : Material.STONE_BRICKS;
                    set(w, cx + dx, base + y, cz + dz, m);
                }
            }
        }
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                set(w, cx + dx, base + 6, cz + dz,
                        (Math.abs(dx) + Math.abs(dz)) % 2 == 0
                                ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS);
            }
        }
        set(w, cx, base + 1, cz - 2, Material.REDSTONE_LAMP);
        set(w, cx - 2, base + 1, cz - 2, Material.REDSTONE_LAMP);
        set(w, cx + 2, base + 1, cz - 2, Material.REDSTONE_LAMP);
        set(w, cx, base + 1, cz + 1, Material.LANTERN);
        set(w, cx, base + 2, cz + 1, TownKit.chain());
        set(w, cx - 2, base + 1, cz + 3, Material.SKELETON_SKULL);
        set(w, cx + 2, base + 1, cz + 3, Material.WITHER_SKELETON_SKULL);
        set(w, cx + 6, base + 1, cz - 2, Material.LANTERN);
        set(w, cx + 8, base + 1, cz, Material.OAK_SIGN);
    }

    private void shaft(World w, int sx, int sz, int base) {
        for (int y = base; y >= DEEP; y--) {
            for (int dx = 0; dx <= 2; dx++) {
                for (int dz = 0; dz <= 2; dz++) {
                    set(w, sx + dx, y, sz + dz, Material.AIR);
                }
            }
            set(w, sx + 2, y, sz + 2, Material.LADDER);
            if ((y - DEEP) % 5 == 0) {
                set(w, sx, y, sz + 1, Material.LANTERN);
            }
        }
        set(w, sx - 1, base + 1, sz + 1, Material.LANTERN);
    }

    /** Guaranteed 3-wide, 2-tall air corridor from shaft bottom into the hall core. */
    private void tunnel(World w, int cx, int cz) {
        for (int z = cz + 1; z >= cz - 36; z--) {
            for (int x = cx + 6; x <= cx + 8; x++) {
                for (int y = DEEP; y <= DEEP + 1; y++) {
                    w.getBlockAt(x, y, z).setType(Material.AIR, false);
                }
                w.getBlockAt(x, DEEP - 1, z).setType(Material.POLISHED_BLACKSTONE, false);
            }
        }
        for (int z = cz; z >= cz - 34; z -= 6) {
            w.getBlockAt(cx + 6, DEEP + 1, z).setType(Material.LANTERN, false);
        }
    }

    /** Self-diagnostic: walk the intended path and log anything that blocks it. */
    private void verify(World w, int cx, int cz) {
        int blocked = 0;
        for (int y = DEEP; y <= DEEP + 1; y++) {
            for (int z = cz + 1; z >= cz - 36; z--) {
                var t = w.getBlockAt(cx + 7, y, z).getType();
                if (t.isSolid() && t != Material.LANTERN) blocked++;
            }
        }
        var ladder = w.getBlockAt(cx + 8, DEEP, cz + 2).getType();
        org.bukkit.Bukkit.getLogger().info(
                "[Aincrad] Dungeon verify " + w.getName() + " (" + cx + "," + cz + "): tunnel-blocked=" + blocked
                        + " ladder@bottom=" + ladder);
    }

    private void hall(World w, int cx, int cz) {
        int hx = cx + 7;
        int hallZ0 = cz - 36, hallZ1 = cz;
        for (int z = hallZ0; z <= hallZ1; z++) {
            for (int x = hx - 2; x <= hx + 2; x++) {
                boolean wall = x == hx - 2 || x == hx + 2 || z == hallZ0 || z == hallZ1;
                for (int dy = 0; dy <= 1; dy++) {
                    set(w, x, DEEP + dy, z, wall ? Material.POLISHED_BLACKSTONE_BRICKS : Material.AIR);
                }
                set(w, x, DEEP + 2, z, Material.POLISHED_BLACKSTONE_BRICKS);
                set(w, x, DEEP - 1, z, Material.POLISHED_BLACKSTONE_BRICKS);
            }
        }
        lootRoom(w, hx, cz - 32);
        mobRoom(w, hx, cz - 21);
        bossRoom(w, hx, cz - 10);
        set(w, hx, DEEP + 1, hallZ1, Material.LANTERN);
    }

    private void roomCavity(World w, int x, int z, int rad) {
        for (int dx = -rad; dx <= rad; dx++) {
            for (int dz = -rad; dz <= rad; dz++) {
                boolean wall = Math.abs(dx) == rad || Math.abs(dz) == rad;
                for (int dy = 0; dy <= 2; dy++) {
                    set(w, x + dx, DEEP + dy, z + dz,
                            wall ? Material.POLISHED_BLACKSTONE_BRICKS : Material.AIR);
                }
                set(w, x + dx, DEEP + 3, z + dz, Material.POLISHED_BLACKSTONE_BRICKS);
                set(w, x + dx, DEEP - 1, z + dz, Material.POLISHED_BLACKSTONE_BRICKS);
            }
        }
        for (int dx = -rad; dx <= rad; dx++) {
            set(w, x + dx, DEEP, z - rad, Material.POLISHED_BLACKSTONE);
            set(w, x + dx, DEEP, z + rad, Material.POLISHED_BLACKSTONE);
        }
    }

    private void corridor(World w, int x, int z0, int z1) {
        for (int z = Math.min(z0, z1); z <= Math.max(z0, z1); z++) {
            for (int d = -1; d <= 1; d++) {
                set(w, x + d, DEEP, z, Material.AIR);
                set(w, x + d, DEEP + 1, z, Material.AIR);
                set(w, x + d, DEEP - 1, z, Material.POLISHED_BLACKSTONE_BRICKS);
            }
        }
    }

    private void lootRoom(World w, int x, int z) {
        roomCavity(w, x, z, 4);
        corridor(w, x, z - 4, z + 4);
        chest(w, x - 2, DEEP, z - 2, new ItemStack[]{
                ItemDefs.create(pickLoot(x + z, true), 1),
                new ItemStack(Material.GOLD_INGOT, 14),
                new ItemStack(Material.IRON_INGOT, 24)});
        chest(w, x + 2, DEEP, z - 2, new ItemStack[]{
                ItemDefs.create(pickLoot(x - z, false), 1),
                new ItemStack(Material.EXPERIENCE_BOTTLE, 12),
                new ItemStack(Material.ENDER_PEARL, 3)});
        set(w, x - 3, DEEP, z, Material.GOLD_BLOCK);
        set(w, x + 3, DEEP, z, Material.GOLD_BLOCK);
        set(w, x - 3, DEEP + 1, z, Material.SHROOMLIGHT);
        set(w, x + 3, DEEP + 1, z, Material.SHROOMLIGHT);
        set(w, x, DEEP, z + 2, Material.CRAFTING_TABLE);
    }

    private void mobRoom(World w, int x, int z) {
        roomCavity(w, x, z, 4);
        corridor(w, x, z - 4, z + 4);
        for (int dx = -3; dx <= 3; dx += 6) {
            for (int dz = -3; dz <= 3; dz += 6) {
                set(w, x + dx, DEEP + 1, z + dz, Material.IRON_BARS);
                set(w, x + dx, DEEP + 2, z + dz, Material.COAL_BLOCK);
            }
        }
        set(w, x, DEEP, z, Material.BONE_BLOCK);
        set(w, x, DEEP + 1, z, Material.LANTERN);
    }

    private void bossRoom(World w, int x, int z) {
        roomCavity(w, x, z, 5);
        corridor(w, x, z - 5, z + 5);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                set(w, x + dx, DEEP, z + dz,
                        Math.abs(dx) == 2 || Math.abs(dz) == 2
                                ? Material.RED_CONCRETE : Material.NETHER_BRICKS);
            }
        }
        set(w, x - 4, DEEP + 1, z - 4, Material.SHROOMLIGHT);
        set(w, x + 4, DEEP + 1, z - 4, Material.SHROOMLIGHT);
        set(w, x - 4, DEEP + 1, z + 4, Material.SHROOMLIGHT);
        set(w, x + 4, DEEP + 1, z + 4, Material.SHROOMLIGHT);
        set(w, x, DEEP, z - 4, Material.CHEST);
    }

    private void chest(World w, int x, int y, int z, ItemStack[] items) {
        set(w, x, y, z, Material.CHEST);
        BlockState state = w.getBlockAt(x, y, z).getState();
        if (state instanceof Container container) {
            var inv = container.getInventory();
            for (int i = 0; i < items.length && i < inv.getSize(); i++) {
                inv.setItem(i, items[i]);
            }
            container.update(true, false);
        }
    }

    private String pickLoot(int salt, boolean weapon) {
        var ids = ItemDefs.idsForLevel(8);
        if (ids.isEmpty()) return "anneal_blade";
        return ids.get(Math.abs(salt * 31 + (weapon ? 7 : 13)) % ids.size());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null
                || (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getY() > G - 5) return;
        int floor = FloorTerrain.parseFloor(player.getWorld().getName());
        for (Point p : POINTS) {
            if (p.floor() != floor) continue;
            if (Math.hypot(player.getX() - p.x(), player.getZ() - p.z()) > 40) continue;
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
            Location loc = new Location(w, p.x() + 7, DEEP, p.z() - 21 + i * 2);
            mobs.spawnRandomAt(loc, floor + 1);
        }
        mobs.spawnGuardian(new Location(w, p.x() + 7, DEEP, p.z() - 10), floor);
    }

    private void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m, false);
    }
}
