package com.aincrad.paper;

import com.aincrad.paper.command.AincradCommand;
import com.aincrad.paper.listener.PlayerListener;
import com.aincrad.paper.persistence.DataStore;
import org.bukkit.plugin.java.JavaPlugin;

public class AincradPaperPlugin extends JavaPlugin {

    private DataStore dataStore;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.dataStore = new DataStore(this);

        getCommand("aincrad").setExecutor(new AincradCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getLogger().info("Aincrad Paper plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Aincrad Paper plugin disabled.");
    }

    public DataStore getDataStore() {
        return dataStore;
    }
}
