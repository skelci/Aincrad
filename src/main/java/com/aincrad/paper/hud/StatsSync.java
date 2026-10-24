package com.aincrad.paper.hud;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.progression.ProgressionManager;
import com.aincrad.paper.skills.PaperSkillService;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public final class StatsSync {

    public static final String CHANNEL = "aincrad:stats";

    private StatsSync() {
    }

    public static void start(AincradPaperPlugin plugin) {
        try {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        } catch (Throwable ignored) {
        }
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                try {
                    int level = ProgressionManager.getInstance().getProgression(p.getUniqueId()).getLevel();
                    double mana = PaperSkillService.mana(p);
                    double maxMana = PaperSkillService.maxMana(p);
                    long col = EconomyManager.getInstance().getBalance(p.getUniqueId()).getAmount();
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    DataOutputStream d = new DataOutputStream(bs);
                    d.writeInt(Math.max(1, level));
                    d.writeDouble(mana);
                    d.writeDouble(maxMana);
                    d.writeLong(col);
                    d.flush();
                    p.sendPluginMessage(plugin, CHANNEL, bs.toByteArray());
                } catch (Throwable ignored) {
                }
            }
        }, 30L, 20L);
    }
}
