package com.aincrad.paper.economy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.UUID;

/**
 * Persists all wallets to plugins/Aincrad/economy.yml.
 */
public final class EconomyPersistence {

    private final Plugin plugin;
    private final File file;

    public EconomyPersistence(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File("data/Aincrad/economy.yml");
    }

    public void load() {
        if (!file.exists()) return;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        int n = 0;
        for (String key : yml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long amount = yml.getLong(key);
                if (amount > 0) {
                    EconomyManager.getInstance().getWallet(uuid).deposit(new Col(amount), "load");
                    n++;
                }
            } catch (Exception ignored) {
            }
        }
        if (n > 0) {
            plugin.getLogger().info("[Aincrad] Economy loaded: " + n + " wallets.");
        }
    }

    public void save() {
        YamlConfiguration yml = new YamlConfiguration();
        for (PlayerWallet wallet : EconomyManager.getInstance().getAllWallets()) {
            if (wallet.getBalance().getAmount() == 0) continue;
            yml.set(wallet.getOwnerUuid().toString(), wallet.getBalance().getAmount());
        }
        try {
            file.getParentFile().mkdirs();
            yml.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("[Aincrad] Economy save failed: " + e.getMessage());
        }
    }
}
