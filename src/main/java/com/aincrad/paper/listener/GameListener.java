package com.aincrad.paper.listener;

import com.aincrad.paper.AincradPaperPlugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player dead = event.getPlayer();
        if (!dead.getWorld().getName().toLowerCase().contains("floor_")) {
            return;
        }
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.setDroppedExp(0);
        try {
            var wallet = com.aincrad.paper.economy.EconomyManager.getInstance()
                    .getWallet(dead.getUniqueId());
            long lost = (long) (wallet.getBalance().getAmount() * 0.05);
            if (lost > 10) {
                com.aincrad.paper.economy.EconomyManager.getInstance()
                        .withdraw(dead.getUniqueId(),
                                new com.aincrad.paper.economy.Col(lost), "death_penalty");
                dead.getWorld().getPlayers().forEach(p -> p.sendMessage(
                        "§8» §7☠ " + dead.getName() + " has fallen. §8(-5% Col)"));
            } else {
                dead.getWorld().getPlayers().forEach(p -> p.sendMessage(
                        "§8» §7☠ " + dead.getName() + " has fallen."));
            }
        } catch (Exception ignored) {
        }
    }

    private final AincradPaperPlugin plugin;

    public GameListener(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        // Prevent block breaking in Aincrad floors (adventure mode)
        if (isAincradWorld(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        // Prevent block placing in Aincrad floors
        if (isAincradWorld(event.getBlock().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Prevent vanilla mob spawning in Aincrad floors
        // Custom mob spawning is handled by MobSpawnIntegration
        if (com.aincrad.paper.mobs.MobSpawnService.suppressSpawnEvents) return;
        if (isAincradWorld(event.getLocation().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Set adventure mode on join
        player.setGameMode(GameMode.ADVENTURE);
        
        // Teleport to Floor 1 if not already in an Aincrad world
        if (!isAincradWorld(player.getWorld())) {
            teleportToFloor1(player);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = player.getWorld();
        
        // If leaving Aincrad world, keep adventure mode
        if (isAincradWorld(to)) {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCombust(org.bukkit.event.entity.EntityCombustEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.LivingEntity living)) return;
        if (!isAincradWorld(living.getWorld())) return;
        if (living instanceof org.bukkit.entity.Skeleton
                || living instanceof org.bukkit.entity.Zombie
                || living instanceof org.bukkit.entity.Phantom
                || living instanceof org.bukkit.entity.Drowned) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        // Respawn in Floor 1 instead of overworld
        if (isAincradWorld(player.getWorld())) {
            // Find Floor 1 world
            World floor1 = findFloor1World();
            if (floor1 != null) {
                event.setRespawnLocation(floor1.getSpawnLocation());
            }
        } else {
            // If somehow in overworld, teleport to Floor 1
            teleportToFloor1(player);
        }
    }

    private boolean isAincradWorld(World world) {
        if (world == null) return false;
        String name = world.getName().toLowerCase();
        return name.contains("floor_") || name.contains("aincrad");
    }

    private World findFloor1World() {
        return org.bukkit.Bukkit.getWorlds().stream()
            .filter(com.aincrad.paper.AincradPaperPlugin::isFloor1)
            .findFirst().orElse(null);
    }

    private void teleportToFloor1(Player player) {
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World floor1 = findFloor1World();
            if (floor1 != null) {
                player.teleport(floor1.getSpawnLocation());
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                player.sendMessage("§c[Server] Floor dimensions not found. Contact admin.");
            }
        }, 1L);
    }
}
