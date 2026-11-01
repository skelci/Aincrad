package com.aincrad.paper.skills;

import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ShopMenu implements InventoryHolder {

    private static final int SIZE = 54;
    private static final int BALANCE_SLOT = 49;
    private static final int[] CATEGORY_SLOTS = {10, 12, 14, 16};

    private final Player player;
    private final Inventory inventory;

    private ShopMenu(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, SIZE, Component.text("Aincrad Shop"));
    }

    public static void open(Player player) {
        ShopMenu menu = new ShopMenu(player);
        menu.render();
        player.openInventory(menu.inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void render() {
        inventory.clear();
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < SIZE; i++) inventory.setItem(i, pane);

        ShopGui.Category[] categories = ShopGui.Category.values();
        for (int i = 0; i < categories.length; i++) {
            ShopGui.Category category = categories[i];
            ItemStack icon = icon(new ItemStack(category.icon()),
                    Component.text("§f" + category.label()),
                    List.of(Component.text("§7" + ShopGui.catalog(category).size() + " items"),
                            Component.text("§eClick to browse")));
            inventory.setItem(CATEGORY_SLOTS[i], icon);
        }

        long bal = EconomyManager.getInstance().getBalance(player.getUniqueId()).getAmount();
        inventory.setItem(BALANCE_SLOT, icon(new ItemStack(Material.SUNFLOWER),
                Component.text("§eYour Col: §6" + Col.format(bal)),
                List.of(Component.text("§7Click a category to browse items"))));
    }

    private static ItemStack icon(ItemStack base, Component name, List<Component> lore) {
        ItemMeta meta = base.getItemMeta();
        meta.displayName(name);
        meta.lore(lore);
        base.setItemMeta(meta);
        return base;
    }
}
