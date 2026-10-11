package com.aincrad.paper.command;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.quest.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles all /aincrad quest subcommands.
 */
public class QuestCommandHandler {

    private final AincradPaperPlugin plugin;

    public QuestCommandHandler(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§6[Quest] §fUsage: /aincrad quest <list|accept|turnin|abandon|progress>");
            return;
        }
        QuestManager qm = QuestManager.getInstance();
        switch (args[1].toLowerCase()) {
            case "list" -> handleList(sender, qm);
            case "accept" -> handleAccept(sender, args, qm);
            case "turnin" -> handleTurnIn(sender, args, qm);
            case "abandon" -> handleAbandon(sender, args, qm);
            case "progress" -> handleProgress(sender, qm);
            default -> sender.sendMessage("§cUnknown quest subcommand: " + args[1]);
        }
    }

    private void handleList(CommandSender sender, QuestManager qm) {
        var quests = qm.getAllQuests();
        sender.sendMessage("§6=== Quests (" + quests.size() + ") ===");
        for (Quest q : quests) sender.sendMessage("§f[" + q.type() + "] " + q.name() + " (Floor " + q.floor() + ")");
    }

    private void handleAccept(CommandSender sender, String[] args, QuestManager qm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad quest accept <id>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        try {
            qm.acceptQuest(player.getUniqueId(), args[2]);
            sender.sendMessage("§aQuest accepted: " + args[2]);
        } catch (QuestManager.QuestException e) { sender.sendMessage("§c" + e.getMessage()); }
    }

    private void handleTurnIn(CommandSender sender, String[] args, QuestManager qm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad quest turnin <id>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        try {
            QuestReward reward = qm.turnInQuest(player.getUniqueId(), args[2]);
            sender.sendMessage("§aQuest turned in! Reward: " + reward);
        } catch (QuestManager.QuestException e) { sender.sendMessage("§c" + e.getMessage()); }
    }

    private void handleAbandon(CommandSender sender, String[] args, QuestManager qm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad quest abandon <id>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        if (qm.abandonQuest(player.getUniqueId(), args[2]))
            sender.sendMessage("§aQuest abandoned.");
        else sender.sendMessage("§cQuest not active.");
    }

    private void handleProgress(CommandSender sender, QuestManager qm) {
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        QuestTracker tracker = qm.getTracker(player.getUniqueId());
        var active = tracker.activeQuests();
        sender.sendMessage("§6=== Active Quests (" + active.size() + "/" + qm.getMaxActiveQuests() + ") ===");
        for (Quest q : active) {
            sender.sendMessage("§f" + q.name() + ":");
            for (QuestObjective obj : q.objectives())
                sender.sendMessage("§f  " + obj);
        }
    }
}