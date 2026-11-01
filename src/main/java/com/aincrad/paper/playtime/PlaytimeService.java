package com.aincrad.paper.playtime;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks total and current-session playtime per player. */
public final class PlaytimeService implements Listener {

    private static final File FILE = new File("data/Aincrad/playtime.yml");
    private static final Map<UUID, Long> totalMillis = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> joinTime = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> activeStart = new ConcurrentHashMap<>();

    public static void wire(JavaPlugin plugin) {
        load();
        plugin.getServer().getPluginManager().registerEvents(new PlaytimeService(), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            commitActive();
            save();
        }, 6000L, 6000L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        long now = System.currentTimeMillis();
        joinTime.put(id, now);
        activeStart.put(id, now);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        commit(event.getPlayer());
        joinTime.remove(id);
        activeStart.remove(id);
        save();
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("Aincrad")) {
            commitActive();
            save();
        }
    }

    public static void commit(Player player) {
        UUID id = player.getUniqueId();
        Long start = activeStart.get(id);
        if (start == null) return;
        long now = System.currentTimeMillis();
        totalMillis.merge(id, Math.max(0, now - start), Long::sum);
        activeStart.put(id, now);
    }

    public static void commitActive() {
        for (Player player : Bukkit.getOnlinePlayers()) commit(player);
    }

    public static long totalMillis(UUID id) {
        long total = totalMillis.getOrDefault(id, 0L);
        Long start = activeStart.get(id);
        if (start != null) total += Math.max(0, System.currentTimeMillis() - start);
        return total;
    }

    public static long sessionMillis(UUID id) {
        Long start = joinTime.get(id);
        if (start == null) return 0L;
        return Math.max(0, System.currentTimeMillis() - start);
    }

    public static double totalHours(UUID id) {
        return totalMillis(id) / 3600000.0;
    }

    public static double sessionHours(UUID id) {
        return sessionMillis(id) / 3600000.0;
    }

    private static void load() {
        if (!FILE.exists()) return;
        try {
            YamlConfiguration y = YamlConfiguration.loadConfiguration(FILE);
            for (String key : y.getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    totalMillis.put(id, y.getLong(key, 0));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void save() {
        try {
            YamlConfiguration y = new YamlConfiguration();
            for (Map.Entry<UUID, Long> entry : totalMillis.entrySet()) {
                if (entry.getValue() > 0) y.set(entry.getKey().toString(), entry.getValue());
            }
            FILE.getParentFile().mkdirs();
            y.save(FILE);
        } catch (Exception ignored) {
        }
    }
}
