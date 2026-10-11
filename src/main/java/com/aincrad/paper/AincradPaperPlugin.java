package com.aincrad.paper;

import com.aincrad.paper.command.AincradCommand;
import com.aincrad.paper.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AincradPaperPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("aincrad").setExecutor(new AincradCommand());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getLogger().info("Aincrad Paper plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Aincrad Paper plugin disabled.");
    }
}
