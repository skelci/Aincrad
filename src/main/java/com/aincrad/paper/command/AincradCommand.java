package com.aincrad.paper.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AincradCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6[Aincrad] §fUsage: /aincrad <guild|party|quest|warp|shop>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "guild" -> sender.sendMessage("§6[Aincrad] §fGuild system coming soon.");
            case "party" -> sender.sendMessage("§6[Aincrad] §fParty system coming soon.");
            case "quest" -> sender.sendMessage("§6[Aincrad] §fQuest system coming soon.");
            case "warp" -> sender.sendMessage("§6[Aincrad] §fWarp system coming soon.");
            case "shop" -> sender.sendMessage("§6[Aincrad] §fShop system coming soon.");
            default -> sender.sendMessage("§6[Aincrad] §fUnknown subcommand: " + args[0]);
        }
        return true;
    }
}
