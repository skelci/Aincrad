package com.aincrad.paper.progression;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

/**
 * Persists player progression (xp, level, points, milestones, NG+) to
 * data/Aincrad/progression.yml. Same lifecycle as skills.yml persistence:
 * load on enable, autosave every 5 minutes, save on quit and on disable.
 */
public final class ProgressionPersistence {

    private ProgressionPersistence() {}

    private static File file() {
        return new File("data/Aincrad/progression.yml");
    }

    public static void wire(JavaPlugin plugin) {
        load();
        plugin.getServer().getScheduler().runTaskTimer(plugin, ProgressionPersistence::saveAll, 6000L, 6000L);
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                Player p = event.getPlayer();
                if (ProgressionManager.getInstance().hasProgression(p.getUniqueId())) saveOne(p.getUniqueId());
            }

            @EventHandler
            public void onDisable(PluginDisableEvent event) {
                if (event.getPlugin().equals(plugin)) saveAll();
            }
        }, plugin);
    }

    public static void saveAll() {
        try {
            YamlConfiguration y = new YamlConfiguration();
            for (var entry : ProgressionManager.getInstance().getAllProgressions().entrySet()) {
                PlayerProgression p = entry.getValue();
                String base = "players." + entry.getKey() + ".";
                y.set(base + "xp", p.getXp());
                y.set(base + "level", p.getLevel());
                y.set(base + "skillPoints", p.getSkillPoints());
                y.set(base + "statPoints", p.getStatPoints());
                y.set(base + "ngPlus", p.getNgPlusCount());
                y.set(base + "milestones", p.getMilestonesAchieved().stream().map(Enum::name).toList());
            }
            File f = file();
            f.getParentFile().mkdirs();
            y.save(f);
        } catch (Exception ex) {
            System.err.println("Failed to save progression.yml: " + ex.getMessage());
        }
    }

    private static void saveOne(UUID id) {
        saveAll();
    }

    public static void load() {
        File f = file();
        if (!f.exists()) return;
        try {
            YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
            ConfigurationSection players = y.getConfigurationSection("players");
            if (players == null) return;
            ProgressionManager mgr = ProgressionManager.getInstance();
            for (String key : players.getKeys(false)) {
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException ex) {
                    continue;
                }
                String base = key + ".";
                PlayerProgression p = mgr.getProgression(id);
                p.resetXp(players.getDouble(base + "xp", 0));
                p.setLevel(players.getInt(base + "level", 1));
                int sp = players.getInt(base + "skillPoints", 0);
                int stp = players.getInt(base + "statPoints", 0);
                int ng = players.getInt(base + "ngPlus", 0);
                if (sp > 0) p.addSkillPoints(sp);
                if (stp > 0) p.addStatPoints(stp);
                for (int i = 0; i < ng; i++) p.incrementNgPlus();
                for (String m : players.getStringList(base + "milestones")) {
                    try {
                        p.achieveMilestone(Milestone.valueOf(m));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Failed to load progression.yml: " + ex.getMessage());
        }
    }
}
