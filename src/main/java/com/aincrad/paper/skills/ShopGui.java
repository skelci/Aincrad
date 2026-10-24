package com.aincrad.paper.skills;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.command.ShopCommandHandler;
import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.items.ItemDefs;
import com.aincrad.paper.progression.ProgressionManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ShopGui implements InventoryHolder {

    private static final int SIZE = 54;
    private static final int PAGE = 45;
    private static final List<ItemDefs.Def> CATALOG = new ArrayList<>();

    static {
        for (String id : ItemDefs.idsForLevel(99)) CATALOG.add(ItemDefs.get(id));
        CATALOG.sort((a, b) -> Integer.compare(a.levelReq(), b.levelReq()));
    }

    private final Player player;
    private final Inventory inventory;
    private int page;

    private ShopGui(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, SIZE, Component.text("Aincrad Shop"));
    }

    public static void open(Player player) {
        ShopGui gui = new ShopGui(player);
        gui.render();
        player.openInventory(gui.inventory);
    }

    public static void registerListener(AincradPaperPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new G(), plugin);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void render() {
        inventory.clear();
        int from = page * PAGE;
        for (int i = 0; i < PAGE && from + i < CATALOG.size(); i++) {
            ItemDefs.Def def = CATALOG.get(from + i);
            long price = ShopCommandHandler.priceOf(def);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Level req: §f" + def.levelReq()));
            if (def.damage() > 0) lore.add(Component.text("§7Damage: §f" + def.damage()));
            if (def.defense() > 0) lore.add(Component.text("§7Defense: §f" + def.defense()));
            lore.add(Component.text("§6" + Col.format(price) + " Col"));
            lore.add(Component.text("§eClick §7buy 1 §8| §eShift-click §7buy 5"));
            inventory.setItem(i, icon(ItemDefs.create(def.id(), 1),
                    Component.text("§f" + def.name()), lore));
        }
        if (page > 0) {
            inventory.setItem(45, icon(new ItemStack(Material.ARROW),
                    Component.text("§f← Previous page"), List.of()));
        }
        if ((page + 1) * PAGE < CATALOG.size()) {
            inventory.setItem(53, icon(new ItemStack(Material.ARROW),
                    Component.text("§fNext page →"), List.of()));
        }
        long bal = EconomyManager.getInstance().getBalance(player.getUniqueId()).getAmount();
        inventory.setItem(49, icon(new ItemStack(Material.SUNFLOWER),
                Component.text("§eYour Col: §6" + Col.format(bal)),
                List.of(Component.text("§7Sell: §fclick Aincrad items in §eyour inventory"))));
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int s = 45; s < SIZE; s++) {
            if (inventory.getItem(s) == null) inventory.setItem(s, pane);
        }
    }

    private static ItemStack icon(ItemStack base, Component name, List<Component> lore) {
        ItemMeta m = base.getItemMeta();
        m.displayName(name);
        m.lore(lore);
        base.setItemMeta(m);
        return base;
    }

    private void buy(ItemDefs.Def def, int amount) {
        int level = ProgressionManager.getInstance().getProgression(player.getUniqueId()).getLevel();
        if (level < def.levelReq()) {
            player.sendMessage("§cRequires level " + def.levelReq() + " (you are " + level + ")");
            return;
        }
        long total = ShopCommandHandler.priceOf(def) * amount;
        try {
            EconomyManager.getInstance().withdraw(player.getUniqueId(), new Col(total), "shop");
        } catch (IllegalStateException e) {
            player.sendMessage("§cNot enough Col: need " + Col.format(total));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1f);
            return;
        }
        ItemStack item = ItemDefs.create(def.id(), amount);
        if (!player.getInventory().addItem(item).isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        player.sendMessage("§aBought §f" + amount + "x " + def.name() + " §afor §6" + Col.format(total) + " Col");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.4f);
        render();
    }

    private void sellStack(ItemStack stack) {
        ItemDefs.Def def = ItemDefs.resolve(stack);
        if (def == null) return;
        int count = stack.getAmount();
        long total = ShopCommandHandler.priceOf(def) / 2 * count;
        stack.setAmount(0);
        EconomyManager.getInstance().deposit(player.getUniqueId(), new Col(total), "shop sell");
        player.sendMessage("§aSold §f" + count + "x " + def.name() + " §afor §6" + Col.format(total) + " Col");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.9f, 1.2f);
        render();
    }

    private static final class G implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof ShopGui gui)) return;
            event.setCancelled(true);
            if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
            int slot = event.getSlot();
            if (event.getClickedInventory() != gui.inventory) {
                sellOrKeep(gui, event);
                return;
            }
            if (slot == 45) {
                if (gui.page > 0) gui.page--;
                gui.render();
                return;
            }
            if (slot == 53) {
                if ((gui.page + 1) * PAGE < CATALOG.size()) gui.page++;
                gui.render();
                return;
            }
            if (slot < PAGE) {
                int idx = gui.page * PAGE + slot;
                if (idx < CATALOG.size()) {
                    gui.buy(CATALOG.get(idx), event.isShiftClick() ? 5 : 1);
                }
            }
        }

        private void sellOrKeep(ShopGui gui, InventoryClickEvent event) {
            ItemStack cur = event.getCurrentItem();
            ItemDefs.Def def = ItemDefs.resolve(cur);
            if (def != null) gui.sellStack(cur);
        }
    }
}
