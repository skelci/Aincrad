package com.aincrad.paper.listener;

import com.aincrad.paper.world.FloorTerrain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class FloorWorldListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        String name = event.getWorld().getName().toLowerCase();
        if (!name.contains("floor_")) return;
        try {
            FloorTerrain.decorate(event.getChunk());
        } catch (Exception ignored) {
        }
    }
}
