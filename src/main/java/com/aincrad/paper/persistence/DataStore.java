package com.aincrad.paper.persistence;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * YAML-based persistence layer for Aincrad Paper plugin.
 * All game data is stored in data/<system>.yml files under the plugin folder.
 */
public class DataStore {

    private final JavaPlugin plugin;
    private final File dataFolder;

    public DataStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File("data/Aincrad/data");
        this.dataFolder.mkdirs();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public FileConfiguration load(String name) {
        File file = new File(dataFolder, name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create " + file.getName(), e);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void save(String name, FileConfiguration config) {
        File file = new File(dataFolder, name + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + file.getName(), e);
        }
    }

    public void delete(String name) {
        File file = new File(dataFolder, name + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }
}
