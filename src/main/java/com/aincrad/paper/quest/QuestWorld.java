package com.aincrad.paper.quest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Physical hidden items for INVESTIGATE objectives. Spots are seeded near
 * themed anchors, sparkle so players can find them by exploring, and are
 * claimed by standing close.
 */
public final class QuestWorld {

    private record Spot(Location location, Material item) {
    }

    private static final Map<String, Spot> SPOTS = new ConcurrentHashMap<>();

    private static final Map<String, int[]> ANCHORS = Map.of(
            "The Weeping Stone", new int[]{300, 40},
            "The Broken Seal", new int[]{-330, 235},
            "Hunter's Ledger", new int[]{10, -16});

    private static final Map<String, Material[]> POOLS = Map.of(
            "The Weeping Stone", new Material[]{Material.GLOWSTONE, Material.PRISMARINE,
                    Material.AMETHYST_BLOCK},
            "The Broken Seal", new Material[]{Material.AMETHYST_BLOCK, Material.SMALL_DRIPLEAF,
                    Material.GLOWSTONE},
            "Hunter's Ledger", new Material[]{Material.BOOKSHELF, Material.OAK_PLANKS,
                    Material.CRAFTING_TABLE});

    private QuestWorld() {
    }

    public static String describe(String target) {
        int[] anchor = ANCHORS.get(target);
        String where = anchor == null ? "" : " §8(near X=" + anchor[0] + ", Z=" + anchor[1] + ")";
        return switch (target) {
            case "The Weeping Stone" -> "§7\"Beyond the eastern hills, where a thin waterfall veils a leaning"
                    + " grey slab, the water weeps year-round over something carved long ago."
                    + " Stand before it and take proof of what remains.\"" + where;
            case "The Broken Seal" -> "§7\"West of town the wind whistles through a ring of pale birches."
                    + " Deep between their roots a broken seal still hums one stubborn note."
                    + " Follow the hum.\"" + where;
            case "Hunter's Ledger" -> "§7\"A hunter drank too deep at the guild last night and stumbled out"
                    + " through the main gate. Whatever fell from his pack is still somewhere among"
                    + " the flower beds beneath the great arch.\"" + where;
            default -> "§7Something is waiting to be found. Follow the road and look closely." + where;
        };
    }

    public static void tick() {
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            scan(player);
        }
    }

    private static void scan(Player player) {
        try {
            QuestManager qm = QuestManager.getInstance();
            if (!qm.hasTracker(player.getUniqueId())) return;
            QuestTracker tracker = qm.getTracker(player.getUniqueId());
            for (Quest quest : tracker.activeQuests()) {
                var objs = quest.objectives();
                for (int i = 0; i < objs.size(); i++) {
                    QuestObjective o = objs.get(i);
                    if (o.getType() != QuestObjective.Type.INVESTIGATE || o.isComplete()) continue;
                    String target = o.getTarget();
                    Spot spot = SPOTS.get(target);
                    if (spot == null || !spot.location().getWorld().equals(player.getWorld())) {
                        spot = create(player.getWorld(), target);
                        if (spot == null) continue;
                        SPOTS.put(target, spot);
                        player.sendMessage("§7Something about the day feels... pointed. §8(look near X="
                                + spot.location().getBlockX() + " Z=" + spot.location().getBlockZ() + ")");
                    }
                    if (!spot.location().getWorld().equals(player.getWorld())) continue;
                    double dist = player.getLocation().distance(spot.location());
                    if (dist < 18) {
                        spot.location().getWorld().spawnParticle(org.bukkit.Particle.END_ROD,
                                spot.location(), 6, 0.25, 0.25, 0.25, 0.0);
                        spot.location().getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER,
                                spot.location().add(0, 0.4, 0), 2, 0.2, 0.1, 0.2, 0.0);
                    }
                    if (dist < 8.0) {
                        claim(player, quest, i, target, spot);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static Spot create(World world, String target) {
        int[] anchor = ANCHORS.get(target);
        if (anchor == null) return null;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Material[] pool = POOLS.get(target);
        for (int attempt = 0; attempt < 32; attempt++) {
            int x = anchor[0] + rnd.nextInt(-14, 15);
            int z = anchor[1] + rnd.nextInt(-14, 15);
            world.loadChunk(x >> 4, z >> 4, true);
            int y = world.getHighestBlockYAt(x, z) + 1;
            if (y > 300) continue;
            var at = world.getBlockAt(x, y, z);
            if (!at.getType().isAir()) continue;
            var below = world.getBlockAt(x, y - 1, z);
            Material b = below.getType();
            if (b == Material.OAK_LEAVES || b == Material.OAK_LOG || b == Material.GLASS
                    || b == Material.STONE_BRICKS || b == Material.STONE_BRICK_WALL
                    || b == Material.OAK_PLANKS || b == Material.POLISHED_ANDESITE) continue;
            Material item = pool[rnd.nextInt(pool.length)];
            at.setType(item, false);
            world.setChunkForceLoaded(x >> 4, z >> 4, true);
            return new Spot(new Location(world, x + 0.5, y + 0.5, z + 0.5), item);
        }
        int x = anchor[0], z = anchor[1];
        int y = world.getHighestBlockYAt(x, z) + 1;
        if (y > 300) return null;
        Material item = pool[rnd.nextInt(pool.length)];
        try {
            world.getBlockAt(x, y, z).setType(item, false);
            return new Spot(new Location(world, x + 0.5, y + 0.5, z + 0.5), item);
        } catch (Exception e) {
            return null;
        }
    }

    private static void claim(Player player, Quest quest, int objectiveIndex, String target, Spot spot) {
        SPOTS.remove(target);
        try {
            spot.location().getBlock().setType(Material.AIR, false);
        } catch (Exception ignored) {
        }
        QuestManager.getInstance().updateProgress(player.getUniqueId(), quest.id(), objectiveIndex, 1);
        player.sendMessage("§6✦ §aYou found §e" + target + "§a! §7(§f" + quest.name()
                + " §7— turn in at the guild)");
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.5f);
        ItemStack token = new ItemStack(spot.item());
        ItemMeta meta = token.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("§d" + target + " §7(token)"));
        meta.lore(List.of(Component.text("Proof of a quiet moment worth remembering.")));
        token.setItemMeta(meta);
        player.getInventory().addItem(token);
    }
}
