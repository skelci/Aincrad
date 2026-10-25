package com.aincrad.paper.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.aincrad.paper.quest.QuestGui;
import com.aincrad.paper.skills.ShopGui;

/**
 * Stationary named NPCs that open the quest / shop GUIs on right-click.
 * A timer re-summons them if they despawn or the world is regenerated.
 */
public final class TownNpcs implements Listener {

    public static final NamespacedKey KEY_NPC = new NamespacedKey("aincrad", "npc");

    private record NpcDef(String role, String name, double x, double z) {}

    private static final NpcDef[] DEFS = {
            new NpcDef("quests", "&d✦ Receptionist", BureauKiosk.QUEST_X + 1.5, BureauKiosk.QUEST_Z + 0.5),
            new NpcDef("shop", "&6⚒ Merchant", -42.5, 1.5),
    };

    private final JavaPlugin plugin;
    private final World world;

    private TownNpcs(JavaPlugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }

    public static void register(JavaPlugin plugin) {
        World main = plugin.getServer().getWorlds().get(0);
        TownNpcs self = new TownNpcs(plugin, main);
        plugin.getServer().getPluginManager().registerEvents(self, plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, self::ensure, 100L, 60 * 20L);
    }

    private void ensure() {
        for (NpcDef def : DEFS) {
            boolean found = false;
            for (Entity e : world.getEntities()) {
                if (e.hasMetadata("ignored")) continue;
                if (def.role.equals(e.getPersistentDataContainer()
                        .get(KEY_NPC, PersistentDataType.STRING))) {
                    found = true;
                    break;
                }
            }
            if (found) continue;
            Location loc = new Location(world, def.x(),
                    world.getHighestBlockYAt((int) def.x(), (int) def.z()) + 1, def.z());
            Villager v = (Villager) world.spawn(loc, Villager.class);
            v.customName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                    .legacyAmpersand().deserialize(def.name() + " §8(right-click)"));
            v.setCustomNameVisible(true);
            v.setPersistent(true);
            v.setSilent(true);
            v.setProfession(def.role.equals("shop") ? Villager.Profession.CLERIC : Villager.Profession.LIBRARIAN);
            v.setRemoveWhenFarAway(false);
            v.getPersistentDataContainer().set(KEY_NPC, PersistentDataType.STRING, def.role());
            v.getInventory().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onNpcClick(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager v)) return;
        String role = v.getPersistentDataContainer().get(KEY_NPC, PersistentDataType.STRING);
        if (role == null) return;
        event.setCancelled(true);
        Player p = event.getPlayer();
        double dx = p.getLocation().getX() - v.getLocation().getX();
        double dz = p.getLocation().getZ() - v.getLocation().getZ();
        v.setRotation((float) Math.toDegrees(Math.atan2(-dx, dz)), 0);
        switch (role) {
            case "shop" -> ShopGui.open(p);
            case "quests" -> QuestGui.open(p);
            default -> { }
        }
    }

}
