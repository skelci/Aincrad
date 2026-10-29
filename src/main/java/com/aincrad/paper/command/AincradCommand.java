package com.aincrad.paper.command;

import com.aincrad.paper.AincradPaperPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AincradCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of(
            "guild", "party", "quest", "progression", "dungeon", "economy", "item", "mob", "skill",
            "restock", "restockall", "purgevanilla", "mobcount", "lastchange", "help",
            "ambient"
    );

    private final AincradPaperPlugin plugin;
    private final GuildCommandHandler guildHandler;
    private final PartyCommandHandler partyHandler;
    private final QuestCommandHandler questHandler;
    private final ProgressionCommandHandler progressionHandler;
    private final DungeonCommandHandler dungeonHandler;

    public AincradCommand(AincradPaperPlugin plugin) {
        this.plugin = plugin;
        this.guildHandler = new GuildCommandHandler(plugin);
        this.partyHandler = new PartyCommandHandler(plugin);
        this.questHandler = new QuestCommandHandler(plugin);
        this.progressionHandler = new ProgressionCommandHandler();
        this.dungeonHandler = new DungeonCommandHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "guild" -> guildHandler.handle(sender, args);
            case "party" -> partyHandler.handle(sender, args);
            case "quest" -> questHandler.handle(sender, args);
            case "progression" -> progressionHandler.handle(sender, args);
            case "dungeon" -> dungeonHandler.handle(sender, args);
            case "economy" -> handleEconomy(sender, args);
            case "item" -> com.aincrad.paper.items.ItemCommands.handle(sender, args, plugin);
            case "mob" -> handleMob(sender, args);
            case "skill" -> {
                if (sender instanceof org.bukkit.command.CommandSender s && s instanceof org.bukkit.entity.Player p
                        && plugin.getSkillService() != null) {
                    plugin.getSkillService().handle(p, args);
                } else sender.sendMessage("Player only");
            }
            case "floor" -> handleFloor(sender, args);
            case "skillrank" -> {
                if (sender instanceof org.bukkit.entity.Player pl && !pl.isOp()) {
                    sender.sendMessage("§cOp only.");
                    return true;
                }
                if (args.length < 4) {
                    sender.sendMessage("§cUsage: /aincrad skillrank <player> <skill> <rank>");
                    return true;
                }
                org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cPlayer not online: " + args[1]); return true; }
                String sid = args[2].toLowerCase();
                int rank = Integer.parseInt(args[3]);
                if (!plugin.getSkillService().setSkillRank(target, sid, rank)) {
                    sender.sendMessage("§cUnknown skill: " + sid);
                    return true;
                }
                sender.sendMessage("§aSet " + sid + " rank " + rank + " for " + target.getName());
                target.sendMessage("§6[Yua] §f" + sid + " set to rank " + rank + ".");
                return true;
            }
            case "lastchange" -> {
                if (sender instanceof org.bukkit.entity.Player pl && !pl.isOp()) {
                    sender.sendMessage("§cOp only.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /aincrad lastchange <text>");
                    return true;
                }
                String text = String.join(" ",
                        java.util.Arrays.copyOfRange(args, 1, args.length));
                try {
                    java.nio.file.Files.writeString(
                            new java.io.File(plugin.getDataFolder(), "last_change.txt").toPath(), text);
                    sender.sendMessage("§aSaved last change note.");
                } catch (Exception e) {
                    sender.sendMessage("§cFailed: " + e.getMessage());
                }
                return true;
            }
            case "mobcount" -> {
                if (sender instanceof org.bukkit.entity.Player pl && !pl.isOp()) {
                    sender.sendMessage("§cOp only.");
                    return true;
                }
                var fd = plugin.getFloorDungeons();
                var ms = plugin.getMobSpawnService();
                if (fd == null || ms == null) {
                    sender.sendMessage("§cNot ready.");
                    return true;
                }
                org.bukkit.World w = fd.worldForFloor(1);
                if (w == null) {
                    sender.sendMessage("§cfloor_1 not loaded");
                    return true;
                }
                for (int i = 0; i < 5; i++) {
                    var p = com.aincrad.paper.world.FloorDungeons.site(i);
                    int regular = ms.countRegularDungeonMobsNear(w, p.x(), p.z(), 260);
                    int bosses = ms.countDungeonBossesNear(w, p.x(), p.z(), 260);
                    int surface = ms.countSurfaceDungeonMobsNear(w, p.x(), p.z(), 60);
                    sender.sendMessage("§eSite " + i + ": interior=" + regular
                            + " bosses=" + bosses + " surface=" + surface);
                }
                int ambient = 0;
                for (org.bukkit.World world : plugin.getServer().getWorlds()) {
                    ambient += ms.countAmbientMobs(world);
                }
                sender.sendMessage("§eAmbient world mobs: " + ambient);
                return true;
            }
            case "ambient" -> {
                if (sender instanceof org.bukkit.entity.Player pl && !pl.isOp()) {
                    sender.sendMessage("§cOp only.");
                    return true;
                }
                var fd = plugin.getFloorDungeons();
                var ms = plugin.getMobSpawnService();
                org.bukkit.World w = fd == null ? null : fd.worldForFloor(1);
                if (w == null || ms == null) {
                    sender.sendMessage("§cfloor_1 not loaded.");
                    return true;
                }
                int x = 200, z = 40;
                int y = w.getHighestBlockYAt(x, z) + 1;
                int spawned = ms.spawnAmbientMob(new org.bukkit.Location(w, x + 0.5, y, z + 0.5));
                sender.sendMessage("§eAmbient spawn at " + x + "," + y + "," + z + ": " + spawned);
                return true;
            }
            case "purgevanilla" -> {
                if (sender instanceof org.bukkit.entity.Player pl && !pl.isOp()) {
                    sender.sendMessage("§cOp only.");
                    return true;
                }
                if (plugin.getMobSpawnService() == null) {
                    sender.sendMessage("§cMob service not started");
                    return true;
                }
                int removed = 0;
                for (org.bukkit.World w : plugin.getServer().getWorlds()) {
                    removed += plugin.getMobSpawnService().purgeVanillaMobs(w);
                }
                sender.sendMessage("§aPurged " + removed + " vanilla mobs.");
                return true;
            }
            case "restock" -> {
                if (args.length < 2 || !sender.hasPermission("aincrad.admin")) {
                    sender.sendMessage("§cUsage: /aincrad restock <0-4>");
                    return true;
                }
                var fd = plugin.getFloorDungeons();
                var fw = fd.worldForFloor(1);
                if (fd == null || fw == null) { sender.sendMessage("§cfloor_1 not loaded"); return true; }
                fd.restock(fw, Integer.parseInt(args[1]));
                sender.sendMessage("§aRestocked dungeon " + args[1]);
                return true;
            }
            case "restockall" -> {
                if (!sender.hasPermission("aincrad.admin")) {
                    sender.sendMessage("§cUsage: /aincrad restockall");
                    return true;
                }
                var fd = plugin.getFloorDungeons();
                var fw = fd.worldForFloor(1);
                if (fd == null || fw == null) { sender.sendMessage("§cfloor_1 not loaded"); return true; }
                for (int i = 0; i < 5; i++) fd.restock(fw, i);
                sender.sendMessage("§aRestocked all 5 dungeons.");
                return true;
            }
            case "boss" -> {
                if (!sender.hasPermission("aincrad.admin")) {
                    sender.sendMessage("§cAdmin only");
                    return true;
                }
                if (sender instanceof org.bukkit.entity.Player p) {
                    plugin.getFloorBoss().summon(p.getWorld(), 1, p.getLocation());
                }
            }
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage("§6[Aincrad] §fUnknown subcommand: " + args[0]);
        }
        return true;
    }

    private void handleFloor(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage("Player only");
            return;
        }
        int n = 1;
        if (args.length > 2) {
            try {
                n = Math.max(1, Math.min(100, Integer.parseInt(args[2])));
            } catch (NumberFormatException e) {
                sender.sendMessage("§cUsage: /aincrad floor <1-100>");
                return;
            }
        }
        if (n > com.aincrad.paper.world.FloorBoss.unlockedFloors() && !player.isOp()) {
            player.sendMessage("§cFloor " + n + " is sealed. Defeat the floor boss to unlock the next floor.");
            return;
        }
        if (n == 1 && com.aincrad.paper.AincradPaperPlugin.isFloor1(player.getWorld())) {
            player.sendMessage("§7You are already on Floor 1.");
            return;
        }
        org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(),
                "execute in aincrad:floor_" + n + " run tp " + player.getName() + " 0 66 40");
        player.sendMessage("§dTravelling to Floor " + n + "...");
    }

    private void handleMob(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage("Player only");
            return;
        }
        if (plugin.getMobSpawnService() == null) {
            sender.sendMessage("Mob service not started");
            return;
        }
        int floor = 1;
        if (args.length >= 3) {
            try { floor = Math.max(1, Integer.parseInt(args[2])); } catch (NumberFormatException ignored) { }
        }
        boolean spawned = plugin.getMobSpawnService().spawnNearPlayer(player, floor);
        sender.sendMessage(spawned ? "§aMob spawned near you." : "§cNo safe spot or spec found.");
    }

    private void handleEconomy(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§6[Economy] §fUsage: /aincrad economy <balance|pay>");
            return;
        }
        com.aincrad.paper.economy.EconomyManager em = com.aincrad.paper.economy.EconomyManager.getInstance();
        switch (args[1].toLowerCase()) {
            case "balance" -> {
                if (!(sender instanceof org.bukkit.entity.Player player)) { sender.sendMessage("§cPlayer only"); return; }
                sender.sendMessage("§6[Balance] §f" + em.getBalance(player.getUniqueId()).format() + " Col");
            }
            case "pay" -> {
                if (args.length < 4) { sender.sendMessage("§cUsage: /aincrad economy pay <player> <amount>"); return; }
                if (!(sender instanceof org.bukkit.entity.Player player)) { sender.sendMessage("§cPlayer only"); return; }
                org.bukkit.entity.Player target = sender.getServer().getPlayer(args[2]);
                if (target == null) { sender.sendMessage("§cPlayer not found"); return; }
                try {
                    long amount = Long.parseLong(args[3]);
                    em.transfer(player.getUniqueId(), target.getUniqueId(), new com.aincrad.paper.economy.Col(amount), "/pay");
                    sender.sendMessage("§aPaid " + com.aincrad.paper.economy.Col.format(amount) + " Col to " + target.getName());
                    target.sendMessage("§aReceived " + com.aincrad.paper.economy.Col.format(amount) + " Col from " + player.getName());
                } catch (Exception e) { sender.sendMessage("§c" + e.getMessage()); }
            }
            default -> sender.sendMessage("§cUnknown economy subcommand: " + args[1]);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Aincrad ===");
        sender.sendMessage("§f/aincrad guild - Guild management");
        sender.sendMessage("§f/aincrad party - Party management");
        sender.sendMessage("§f/aincrad quest - Quest tracking");
        sender.sendMessage("§f/aincrad progression - View progression");
        sender.sendMessage("§f/aincrad dungeon - Dungeon instances");
        sender.sendMessage("§f/aincrad economy - Col balance & payments");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                       @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(args[0].toLowerCase())) completions.add(sub);
            }
        }
        return completions;
    }
}
