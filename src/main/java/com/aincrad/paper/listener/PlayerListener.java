package com.aincrad.paper.listener;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.guild.Guild;
import com.aincrad.paper.guild.GuildManager;
import com.aincrad.paper.party.Party;
import com.aincrad.paper.party.PartyManager;
import com.aincrad.paper.progression.PlayerProgression;
import com.aincrad.paper.progression.ProgressionManager;
import com.aincrad.paper.items.ItemDefs;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    private final AincradPaperPlugin plugin;

    public PlayerListener(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§8§m-§c§m--------§8§m-§r §c§lAINCRAD§r §8§m-§c§m--------§8§m-");
        event.getPlayer().sendMessage("§7Floor 1 — Aincrad, the black iron castle.");
        event.getPlayer().sendMessage("§7Guild Hall: §fquest board §8+ §fskill shelf §8| §7Market Hall: §fmerchant bell");
        event.getPlayer().sendMessage("§8§m---------------------------------");
        var player = event.getPlayer();
        ProgressionManager.getInstance().getProgression(player.getUniqueId());
        ProgressionManager.getInstance().syncActionBar(player);
        com.aincrad.paper.items.ItemCommands.grantStarterKitIfNew(player);
        migrateTeleportCrystals(player);

        String note = plugin.getLastChangeNote();
        if (note != null && !note.isBlank()) {
            player.sendMessage("§6[Yua] §7Last update: §f" + note);
        }

        Guild guild = GuildManager.getInstance(plugin.getDataStore()).findGuildOf(player.getUniqueId());
        if (guild != null) {
            player.setPlayerListName("[" + guild.getTag() + "] " + player.getName());
        }

        Party party = PartyManager.getInstance().getPartyForPlayer(player.getUniqueId());
        if (party != null) {
            player.sendMessage("§6[Party] §fYou are in a party (" + party.getMemberCount() + "/" + Party.MAX_MEMBERS + ")");
        }

        // Only relocate players who are NOT already on a floor (keeps logout position)
        String wn = player.getWorld().getName().toLowerCase();
        if (!com.aincrad.paper.AincradPaperPlugin.isFloorWorld(player.getWorld())
                && !wn.contains("floor_")) {
            sendToFloor1(player);
        }
    }

    private void migrateTeleportCrystals(org.bukkit.entity.Player player) {
        for (ItemStack is : player.getInventory().getContents()) {
            if (is == null) continue;
            var def = ItemDefs.resolve(is);
            if (def != null && def.id().equals("teleport_crystal")
                    && is.getType() != Material.AMETHYST_SHARD) {
                is.setType(Material.AMETHYST_SHARD);
            }
        }
        ItemStack off = player.getInventory().getItemInOffHand();
        var def = ItemDefs.resolve(off);
        if (def != null && def.id().equals("teleport_crystal")
                && off.getType() != Material.AMETHYST_SHARD) {
            off.setType(Material.AMETHYST_SHARD);
        }
    }

    private boolean hasPoisonCrystal(org.bukkit.entity.Player player) {
        for (ItemStack is : player.getInventory().getContents()) {
            var def = ItemDefs.resolve(is);
            if (def != null && def.id().equals("poison_crystal")) return true;
        }
        var off = player.getInventory().getItemInOffHand();
        var def = ItemDefs.resolve(off);
        return def != null && def.id().equals("poison_crystal");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPoisonDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.POISON && hasPoisonCrystal(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) return;
        var effect = event.getNewEffect();
        if (effect != null && effect.getType() == PotionEffectType.POISON && hasPoisonCrystal(player)) {
            event.setCancelled(true);
        }
    }

    private void sendToFloor1(org.bukkit.entity.Player player) {
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }
            var world = org.bukkit.Bukkit.getWorlds().stream()
                .filter(com.aincrad.paper.AincradPaperPlugin::isFloor1)
                .findFirst().orElse(null);
            if (world == null) {
                player.sendMessage("§dDescending to Floor 1...");
                org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(),
                        "execute in aincrad:floor_1 run tp " + player.getName()
                                + " 0 66 40");
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin,
                        () -> plugin.setupFloor1(), 80L);
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    var f1 = org.bukkit.Bukkit.getWorlds().stream()
                        .filter(com.aincrad.paper.AincradPaperPlugin::isFloor1)
                        .findFirst().orElse(null);
                    if (f1 != null && player.isOnline()) {
                        player.teleport(f1.getSpawnLocation());
                    }
                }, 200L);
                return;
            }
            player.teleport(world.getSpawnLocation());
        }, 20L);
    }

    @EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent event) {
        com.aincrad.paper.progression.ProgressionManager.getInstance()
                .syncActionBar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        com.aincrad.paper.skills.PaperSkillService.forget(event.getPlayer().getUniqueId());
        var player = event.getPlayer();
        // Save guild data
        GuildManager.getInstance(plugin.getDataStore()).saveAll();
        // Clear player list name
        player.setPlayerListName(player.getName());
    }
}
