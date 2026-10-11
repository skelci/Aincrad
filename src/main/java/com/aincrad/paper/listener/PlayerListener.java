package com.aincrad.paper.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // TODO: restore guild/party state
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // TODO: save guild/party state
    }
}
