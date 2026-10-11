package com.aincrad.paper.command;

import com.aincrad.paper.dungeon.DungeonInstance;
import com.aincrad.paper.dungeon.DungeonManager;
import com.aincrad.paper.dungeon.DungeonTier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles /aincrad dungeon subcommands.
 */
public class DungeonCommandHandler {

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§6[Dungeon] §fUsage: /aincrad dungeon <create|join|leave|list>");
            return;
        }
        DungeonManager dm = DungeonManager.getInstance();
        switch (args[1].toLowerCase()) {
            case "create" -> handleCreate(sender, args, dm);
            case "join" -> handleJoin(sender, args, dm);
            case "leave" -> handleLeave(sender, args, dm);
            case "list" -> handleList(sender, dm);
            default -> sender.sendMessage("§cUnknown dungeon subcommand: " + args[1]);
        }
    }

    private void handleCreate(CommandSender sender, String[] args, DungeonManager dm) {
        if (args.length < 5) { sender.sendMessage("§cUsage: /aincrad dungeon create <id> <tier> <floor>"); return; }
        try {
            DungeonTier tier = DungeonTier.valueOf(args[3].toUpperCase());
            int floor = Integer.parseInt(args[4]);
            DungeonInstance inst = dm.createInstance(args[2], tier, floor);
            sender.sendMessage("§aDungeon created: " + inst);
        } catch (Exception e) { sender.sendMessage("§c" + e.getMessage()); }
    }

    private void handleJoin(CommandSender sender, String[] args, DungeonManager dm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad dungeon join <id>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        if (dm.join(args[2], player.getUniqueId()))
            sender.sendMessage("§aJoined dungeon.");
        else sender.sendMessage("§cCannot join (locked or not in lobby).");
    }

    private void handleLeave(CommandSender sender, String[] args, DungeonManager dm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad dungeon leave <id>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        dm.leave(args[2], player.getUniqueId());
        sender.sendMessage("§aLeft dungeon.");
    }

    private void handleList(CommandSender sender, DungeonManager dm) {
        var instances = dm.getAllInstances();
        sender.sendMessage("§6=== Dungeons (" + instances.size() + ") ===");
        for (DungeonInstance i : instances)
            sender.sendMessage("§f" + i.getDungeonId() + " " + i.getTier() + " F" + i.getFloor() + " " + i.getState());
    }
}