package com.aincrad.paper.listener;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.guild.Guild;
import com.aincrad.paper.guild.GuildManager;
import com.aincrad.paper.party.Party;
import com.aincrad.paper.party.PartyManager;
import com.aincrad.paper.progression.PlayerProgression;
import com.aincrad.paper.progression.ProgressionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final AincradPaperPlugin plugin;

    public PlayerListener(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        // Ensure progression exists
        ProgressionManager.getInstance().getProgression(player.getUniqueId());

        // Restore guild display
        Guild guild = GuildManager.getInstance(plugin.getDataStore()).findGuildOf(player.getUniqueId());
        if (guild != null) {
            player.setPlayerListName("[" + guild.getTag() + "] " + player.getName());
        }

        // Restore party state
        Party party = PartyManager.getInstance().getPartyForPlayer(player.getUniqueId());
        if (party != null) {
            player.sendMessage("§6[Party] §fYou are in a party (" + party.getMemberCount() + "/" + Party.MAX_MEMBERS + ")");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        // Save guild data
        GuildManager.getInstance(plugin.getDataStore()).saveAll();
        // Clear player list name
        player.setPlayerListName(player.getName());
    }
}
