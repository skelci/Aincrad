package com.aincrad.paper.items;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;

public final class ItemCommands {

    private static final NamespacedKey KEY_KIT = new NamespacedKey("aincrad", "starter_kit");

    private ItemCommands() {
    }

    public static void grantStarterKitIfNew(Player player) {
        Boolean got = player.getPersistentDataContainer().get(KEY_KIT, PersistentDataType.BOOLEAN);
        if (Boolean.TRUE.equals(got)) return;
        player.getPersistentDataContainer().set(KEY_KIT, PersistentDataType.BOOLEAN, true);
        com.aincrad.paper.economy.EconomyManager.getInstance()
                .deposit(player.getUniqueId(), new com.aincrad.paper.economy.Col(100), "starting_funds");
        player.sendMessage("§6[ Aincrad ] §aWelcome. 100 Col granted — gear up at the Market Hall.");
    }

    public static void handle(CommandSender sender, String[] args, Object plugin) {
        if (args.length < 2) {
            sender.sendMessage("§7/aincrad item give <id|all> [count]");
            sender.sendMessage("§7/aincrad item list [page]");
            sender.sendMessage("§7/aincrad item kit - re-grant starter kit");
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "give" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Player only");
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /aincrad item give <id|all> [count]");
                    return;
                }
                int count = args.length >= 4 ? parseCount(args[3]) : 1;
                String id = args[2].toLowerCase(Locale.ROOT);
                if (id.equals("all")) {
                    for (String defId : ItemDefs.all().keySet()) {
                        if (!ItemDefs.isCrystal(defId)) give(player, defId, 1);
                    }
                    player.sendMessage("§aGave one of every non-crystal Aincrad item; crystals are shop-only.");
                    return;
                }
                if (ItemDefs.isCrystal(id)) {
                    player.sendMessage("§cCrystals must be purchased from /shop, not granted for free.");
                    return;
                }
                if (ItemDefs.get(id) == null) {
                    player.sendMessage("§cUnknown item id: " + id + " — see §7/aincrad item list");
                    return;
                }
                give(player, id, count);
                player.sendMessage("§aGave " + count + "x " + ItemDefs.get(id).name() + ".");
            }
            case "grant" -> {
                if (sender instanceof Player pl && !pl.isOp()) {
                    sender.sendMessage("§cOp only.");
                    return;
                }
                if (args.length < 4) {
                    sender.sendMessage("§cUsage: /aincrad item grant <player> <id> [count]");
                    return;
                }
                org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayerExact(args[2]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not online: " + args[2]);
                    return;
                }
                String gid = args[3].toLowerCase(Locale.ROOT);
                if (ItemDefs.isCrystal(gid)) {
                    sender.sendMessage("§cCrystals must be purchased from /shop, not granted for free.");
                    return;
                }
                if (ItemDefs.get(gid) == null) {
                    sender.sendMessage("§cUnknown item id: " + gid);
                    return;
                }
                int gcount = args.length >= 5 ? parseCount(args[4]) : 1;
                give(target, gid, gcount);
                sender.sendMessage("§aGave " + gcount + "x " + ItemDefs.get(gid).name()
                        + " to " + target.getName());
            }
            case "kit" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Player only");
                    return;
                }
                give(player, "anneal_blade", 1);
                give(player, "sprout_coif", 1);
                give(player, "sprout_jacket", 1);
                give(player, "sprout_leggings", 1);
                give(player, "sprout_boots", 1);
                player.sendMessage("§aStarter kit granted.");
            }
            case "list" -> {
                List<String> ids = new java.util.ArrayList<>(ItemDefs.all().keySet());
                ids.sort(String::compareTo);
                int page = args.length >= 3 ? Math.max(1, parseCount(args[2])) : 1;
                int perPage = 12;
                int pages = (ids.size() + perPage - 1) / perPage;
                page = Math.min(page, pages);
                sender.sendMessage("§6=== Aincrad Items (page " + page + "/" + pages + ") ===");
                for (int i = (page - 1) * perPage; i < Math.min(ids.size(), page * perPage); i++) {
                    ItemDefs.Def d = ItemDefs.get(ids.get(i));
                    sender.sendMessage("§7" + d.id() + " §f- " + d.name() + " §8(" + d.rarity()
                            + (d.damage() > 0 ? ", dmg " + d.damage() : "")
                            + (d.defense() > 0 ? ", def " + d.defense() : "")
                            + ", Lv" + d.levelReq() + ")");
                }
            }
            default -> sender.sendMessage("§cUnknown item subcommand: " + args[1]);
        }
    }

    private static int parseCount(String raw) {
        try {
            return Math.max(1, Math.min(64, Integer.parseInt(raw)));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static void give(Player player, String id, int count) {
        var stack = ItemDefs.create(id, count);
        if (stack == null) return;
        var leftover = player.getInventory().addItem(stack);
        for (var rest : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), rest);
        }
    }
}
