package com.aincrad.paper.command;

import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.items.ItemDefs;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopCommandHandler implements CommandExecutor {

    public static long priceOf(ItemDefs.Def def) {
        double base = def.damage() * 9 + def.defense() * 7 + 15;
        long v = (long) (base * def.rarity().multiplier * (1.0 + def.levelReq() * 0.18) / 5) * 5;
        return Math.max(25, v);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Player only");
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            String filter = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : null;
            List<String> ids = new ArrayList<>();
            for (String id : ItemDefs.idsForLevel(99)) ids.add(id);
            ids.sort((a, b) -> Integer.compare(
                    ItemDefs.get(a).levelReq(), ItemDefs.get(b).levelReq()));
            int shown = 0;
            player.sendMessage("§6§m--------§e Aincrad Shop §6§m--------");
            for (String id : ids) {
                ItemDefs.Def def = ItemDefs.get(id);
                if (def == null) continue;
                if (filter != null && !id.contains(filter)
                        && !def.name().toLowerCase(Locale.ROOT).contains(filter)) {
                    continue;
                }
                if (shown++ >= 12) {
                    player.sendMessage("§7... /shop <name> to filter");
                    break;
                }
                player.sendMessage("§e" + def.id() + " §7- " + def.name()
                        + " §a" + priceOf(def) + " Col §7(lv" + def.levelReq() + ")");
            }
            player.sendMessage("§7/shop buy <id> [amt] §8| §7/shop sell [id]");
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "buy" -> buy(player, args);
            case "sell" -> sell(player, args);
            default -> player.sendMessage("§c/shop <list|buy|sell>");
        }
        return true;
    }

    private void buy(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c/shop buy <id> [amount]");
            return;
        }
        ItemDefs.Def def = ItemDefs.get(args[1].toLowerCase(Locale.ROOT));
        if (def == null) {
            player.sendMessage("§cUnknown item: " + args[1]);
            return;
        }
        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Math.max(1, Math.min(64, Integer.parseInt(args[2])));
            } catch (NumberFormatException e) {
                player.sendMessage("§cBad amount");
                return;
            }
        }
        if (player.getLevel() < def.levelReq()) {
            player.sendMessage("§cRequires level " + def.levelReq());
            return;
        }
        long total = priceOf(def) * amount;
        try {
            EconomyManager.getInstance().withdraw(player.getUniqueId(), new Col(total), "shop");
        } catch (IllegalStateException e) {
            player.sendMessage("§cNot enough Col: need " + total);
            return;
        }
        give(player, ItemDefs.create(def.id(), amount));
        player.sendMessage("§aBought " + amount + "x " + def.name() + " for " + total + " Col");
    }

    private void sell(Player player, String[] args) {
        String id = args.length > 1 ? args[1].toLowerCase(Locale.ROOT) : null;
        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemDefs.Def def;
        if (id != null) {
            def = ItemDefs.get(id);
        } else {
            def = ItemDefs.resolve(hand);
        }
        if (def == null) {
            player.sendMessage("§cHold an Aincrad item or give its id");
            return;
        }
        int count;
        long unit = priceOf(def) / 2;
        if (id != null) {
            count = countInInventory(player, def);
            if (count == 0) {
                player.sendMessage("§cYou have no " + def.name());
                return;
            }
            removeByDef(player, def, count);
        } else {
            count = hand.getAmount();
            hand.setAmount(hand.getAmount() - count);
        }
        long total = unit * count;
        EconomyManager.getInstance().deposit(player.getUniqueId(), new Col(total), "shop sell");
        player.sendMessage("§aSold " + count + "x " + def.name() + " for " + total + " Col");
    }

    private int countInInventory(Player player, ItemDefs.Def def) {
        int n = 0;
        for (ItemStack s : player.getInventory().getStorageContents()) {
            if (s != null && ItemDefs.resolve(s) == def) n += s.getAmount();
        }
        return n;
    }

    private void removeByDef(Player player, ItemDefs.Def def, int count) {
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (int i = 0; i < contents.length && count > 0; i++) {
            ItemStack s = contents[i];
            if (s == null || ItemDefs.resolve(s) != def) continue;
            int take = Math.min(count, s.getAmount());
            count -= take;
            if (take >= s.getAmount()) {
                player.getInventory().setItem(i, null);
            } else {
                s.setAmount(s.getAmount() - take);
            }
        }
    }

    private void give(Player player, ItemStack stack) {
        var leftover = player.getInventory().addItem(stack).values();
        for (ItemStack rest : leftover) {
            player.getWorld().dropItemNaturally(player.getLocation(), rest);
        }
    }
}
