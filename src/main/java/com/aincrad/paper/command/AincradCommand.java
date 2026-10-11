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
            "guild", "party", "quest", "progression", "dungeon", "economy", "help"
    );

    private final GuildCommandHandler guildHandler;
    private final PartyCommandHandler partyHandler;
    private final QuestCommandHandler questHandler;
    private final ProgressionCommandHandler progressionHandler;
    private final DungeonCommandHandler dungeonHandler;

    public AincradCommand(AincradPaperPlugin plugin) {
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
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage("§6[Aincrad] §fUnknown subcommand: " + args[0]);
        }
        return true;
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
