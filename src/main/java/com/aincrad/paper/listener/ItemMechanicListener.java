package com.aincrad.paper.listener;

import com.aincrad.paper.items.ItemDefs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemMechanicListener implements Listener {

    public static final Map<UUID, double[]> PENDING_SKILL = new ConcurrentHashMap<>();
    public static final Map<UUID, double[]> STANCES = new ConcurrentHashMap<>();

    public static void pendingSkill(UUID id, double multiplier, double kind) {
        PENDING_SKILL.put(id, new double[]{multiplier, kind, System.currentTimeMillis()});
    }

    public static void stance(UUID id, int type, double value, long durationMs) {
        STANCES.put(id, new double[]{type, value, System.currentTimeMillis() + durationMs});
    }

    private static double attackBuff(Player p) {
        double[] s = STANCES.get(p.getUniqueId());
        if (s == null) return 1.0;
        if (System.currentTimeMillis() > (long) s[2]) {
            STANCES.remove(p.getUniqueId());
            return 1.0;
        }
        return s[0] == 1 ? 1.0 + s[1] : 1.0;
    }

    private static double defenseBuff(Player p) {
        double[] s = STANCES.get(p.getUniqueId());
        if (s == null) return 1.0;
        if (System.currentTimeMillis() > (long) s[2]) {
            STANCES.remove(p.getUniqueId());
            return 1.0;
        }
        return s[0] == 2 ? Math.max(0.2, 1.0 - s[1]) : 1.0;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemDefs.Def weapon = ItemDefs.resolve(hand);

        if (weapon != null && weapon.damage() > 0
                && weapon.levelReq() > com.aincrad.paper.progression.ProgressionManager.getInstance()
                        .getProgression(player.getUniqueId()).getLevel()) {
            event.setDamage(1.0);
            return;
        }

        double base = event.getDamage();
        double mult = attackBuff(player);

        double[] pending = PENDING_SKILL.remove(player.getUniqueId());
        double skillMult = 1.0;
        if (pending != null && System.currentTimeMillis() - (long) pending[2] < 3000) {
            skillMult = pending[0];
        } else {
            pending = null;
        }

        if (weapon != null && weapon.damage() > 0) {
            base = weapon.damage();
        }
        double damage = base * mult * skillMult;
        event.setDamage(damage);

        if (weapon != null) {
            consumeDurability(player, hand, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFoodLevelGate(org.bukkit.event.player.PlayerInteractEvent event) {
        var action = event.getAction();
        if (action != org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                && action != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        ItemStack is = event.getItem();
        if (is == null || !is.getType().isEdible()) return;
        ItemDefs.Def def = ItemDefs.resolve(is);
        if (def == null) return;
        int level = com.aincrad.paper.progression.ProgressionManager.getInstance()
                .getProgression(event.getPlayer().getUniqueId()).getLevel();
        if (level < def.levelReq()) {
            event.setCancelled(true);
            event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
            event.getPlayer().sendMessage("\u00a7c" + def.name() + " requires level "
                    + def.levelReq() + " (you are " + level + ")");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        var action = event.getAction();
        ItemStack cur = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if (action == org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY
                && blockedArmor(cur, player)) {
            event.setCancelled(true);
            return;
        }
        var top = event.getView().getTopInventory();
        if (top == null || !(top.getHolder() instanceof Player) || event.getClickedInventory() != top)
            return;
        int slot = event.getSlot();
        if ((slot == 5 || slot == 6 || slot == 7 || slot == 8) && blockedArmor(cursor, player))
            event.setCancelled(true);
    }

    private static boolean blockedArmor(ItemStack is, Player player) {
        if (is == null) return false;
        ItemDefs.Def def = ItemDefs.resolve(is);
        if (def == null || def.defense() <= 0) return false;
        int level = com.aincrad.paper.progression.ProgressionManager.getInstance()
                .getProgression(player.getUniqueId()).getLevel();
        if (level < def.levelReq()) {
            player.sendMessage("\u00a7c" + def.name() + " requires level " + def.levelReq()
                    + " (you are " + level + ")");
            return true;
        }
        return false;
    }

    public static void enforceArmor(Player player) {
        if (!player.isOnline()) return;
        var inv = player.getInventory();
        org.bukkit.inventory.EquipmentSlot[] slots = {
                org.bukkit.inventory.EquipmentSlot.HEAD,
                org.bukkit.inventory.EquipmentSlot.CHEST,
                org.bukkit.inventory.EquipmentSlot.LEGS,
                org.bukkit.inventory.EquipmentSlot.FEET};
        for (var slot : slots) {
            ItemStack is = inv.getItem(slot);
            if (is == null) continue;
            ItemDefs.Def def = ItemDefs.resolve(is);
            if (def == null || def.defense() <= 0) continue;
            int level = com.aincrad.paper.progression.ProgressionManager.getInstance()
                    .getProgression(player.getUniqueId()).getLevel();
            if (level >= def.levelReq()) continue;
            inv.setItem(slot, null);
            if (!player.getInventory().addItem(is).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), is);
            }
            player.sendMessage("\u00a7c" + def.name() + " requires level " + def.levelReq()
                    + " — unequipped.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerHurt(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event instanceof EntityDamageByEntityEvent byEntity) {
            Entity src = byEntity.getDamager();
            if (src instanceof LivingEntity mob) {
                Integer lvl = mob.getPersistentDataContainer().get(KEY_MOB_LEVEL, PersistentDataType.INTEGER);
                if (lvl != null) {
                    event.setDamage(2.0 + lvl * 0.55);
                }
            }
        }
        double defense = 0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            ItemDefs.Def def = ItemDefs.resolve(armor);
            if (def != null) defense += def.defense();
        }
        double reduced = event.getDamage() * defenseBuff(player) * (100.0 / (100.0 + defense * 2.2));
        event.setDamage(Math.max(0.0, reduced));

        if (event.getFinalDamage() > 0.5) {
            for (Map.Entry<Integer, ItemStack> entry : armorSlots(player).entrySet()) {
                consumeDurability(player, entry.getValue(), 1);
            }
        }
    }

    private Map<Integer, ItemStack> armorSlots(Player player) {
        Map<Integer, ItemStack> map = new java.util.HashMap<>();
        var inv = player.getInventory();
        map.put(3, inv.getHelmet());
        map.put(2, inv.getChestplate());
        map.put(1, inv.getLeggings());
        map.put(0, inv.getBoots());
        return map;
    }

    private static final org.bukkit.NamespacedKey KEY_DUR = new org.bukkit.NamespacedKey("aincrad", "dur");
    private static final int DURABILITY = 400;

    private void consumeDurability(Player player, ItemStack stack, int amount) {
        if (stack == null || stack.isEmpty()) return;
        ItemDefs.Def def = ItemDefs.resolve(stack);
        if (def == null) return;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        var pdc = meta.getPersistentDataContainer();
        int remaining = pdc.getOrDefault(KEY_DUR, PersistentDataType.INTEGER, DURABILITY) - amount;
        if (remaining <= 0) {
            player.getInventory().remove(stack);
            player.sendMessage("§cYour " + def.name() + " shattered.");
            return;
        }
        pdc.set(KEY_DUR, PersistentDataType.INTEGER, remaining);
        stack.setItemMeta(meta);
    }

    public static final org.bukkit.NamespacedKey KEY_MOB_LEVEL =
            new org.bukkit.NamespacedKey("aincrad", "mob_level");
}
