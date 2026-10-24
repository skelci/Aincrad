package com.aincrad.paper.skills;

import com.aincrad.paper.AincradPaperPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class SkillSlotListener implements Listener {

    private final AincradPaperPlugin plugin;
    private final PaperSkillService service;

    public SkillSlotListener(AincradPaperPlugin plugin, PaperSkillService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDriveClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        var pdc = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = PaperSkillService.KEY_DRIVE;
        if (!pdc.has(key, PersistentDataType.BYTE)) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        byte slot = pdc.get(key, PersistentDataType.BYTE);
        PaperSkillService.SkillDef def = service.boundSkill(player, slot);
        if (def == null) {
            player.sendMessage("§cThis drive lost its skill — re-bind it in §e/skills§c.");
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> service.use(player, def.id()));
    }
}
