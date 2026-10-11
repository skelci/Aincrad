package com.aincrad.paper.command;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.guild.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Handles all /aincrad guild subcommands.
 */
public class GuildCommandHandler {

    private final AincradPaperPlugin plugin;

    public GuildCommandHandler(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§6[Guild] §fUsage: /aincrad guild <create|disband|invite|kick|promote|demote|info|list|chat|bank|skill|notice>");
            return;
        }
        GuildManager gm = GuildManager.getInstance(plugin.getDataStore());
        switch (args[1].toLowerCase()) {
            case "create" -> handleCreate(sender, args, gm);
            case "disband" -> handleDisband(sender, args, gm);
            case "invite" -> handleInvite(sender, args, gm);
            case "kick" -> handleKick(sender, args, gm);
            case "info" -> handleInfo(sender, args, gm);
            case "list" -> handleList(sender, gm);
            case "notice" -> handleNotice(sender, args, gm);
            default -> sender.sendMessage("§cUnknown guild subcommand: " + args[1]);
        }
    }

    private void handleCreate(CommandSender sender, String[] args, GuildManager gm) {
        if (args.length < 4) { sender.sendMessage("§cUsage: /aincrad guild create <name> <tag>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        try {
            String tag = args[3].substring(0, Math.min(GuildLevel.TAG_LENGTH, args[3].length()));
            Guild g = gm.createGuild(args[2], tag, player.getUniqueId());
            sender.sendMessage("§aGuild '" + g.getName() + "' created!");
        } catch (Exception e) { sender.sendMessage("§c" + e.getMessage()); }
    }

    private void handleDisband(CommandSender sender, String[] args, GuildManager gm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad guild disband <name>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        if (gm.disbandGuild(args[2], player.getUniqueId()))
            sender.sendMessage("§aGuild disbanded.");
        else sender.sendMessage("§cCannot disband guild.");
    }

    private void handleInvite(CommandSender sender, String[] args, GuildManager gm) {
        if (args.length < 4) { sender.sendMessage("§cUsage: /aincrad guild invite <guild> <player>"); return; }
        Player target = plugin.getServer().getPlayer(args[3]);
        if (target == null) { sender.sendMessage("§cPlayer not found"); return; }
        if (sender instanceof Player player) {
            if (gm.invitePlayer(args[2], target.getUniqueId(), player.getUniqueId())) {
                sender.sendMessage("§aInvited " + target.getName());
                target.sendMessage("§6[Guild] §fYou've been invited to '" + args[2] + "'");
            } else sender.sendMessage("§cCannot invite.");
        }
    }

    private void handleKick(CommandSender sender, String[] args, GuildManager gm) {
        if (args.length < 4) { sender.sendMessage("§cUsage: /aincrad guild kick <guild> <player>"); return; }
        Player target = plugin.getServer().getPlayer(args[3]);
        if (target == null) { sender.sendMessage("§cPlayer not found"); return; }
        if (sender instanceof Player player) {
            if (gm.kickMember(args[2], target.getUniqueId(), player.getUniqueId()))
                sender.sendMessage("§aKicked " + target.getName());
            else sender.sendMessage("§cCannot kick.");
        }
    }

    private void handleInfo(CommandSender sender, String[] args, GuildManager gm) {
        Guild g = args.length >= 3 ? gm.findByName(args[2]) : gm.findGuildOf(((Player) sender).getUniqueId());
        if (g == null) { sender.sendMessage("§cNo guild found"); return; }
        sender.sendMessage("§6=== §f" + g.getName() + " §6[" + g.getTag() + "] §fLv" + g.getXp().getLevel() + " ===");
        sender.sendMessage("§fMembers: " + g.getMemberCount() + "/" + GuildLevel.memberCap(g.getXp().getLevel()));
        sender.sendMessage("§fXP: " + g.getXp().getProgressXp() + "/" + g.getXp().getXpToNext());
        sender.sendMessage("§fNotice: " + (g.getNotice().isEmpty() ? "(none)" : g.getNotice()));
    }

    private void handleList(CommandSender sender, GuildManager gm) {
        List<Guild> guilds = gm.listGuilds();
        sender.sendMessage("§6=== Guilds (" + guilds.size() + ") ===");
        for (Guild g : guilds) sender.sendMessage("§f" + g.getName() + " [" + g.getTag() + "] Lv" + g.getXp().getLevel());
    }

    private void handleNotice(CommandSender sender, String[] args, GuildManager gm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad guild notice <message>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        Guild g = gm.findGuildOf(player.getUniqueId());
        if (g == null) { sender.sendMessage("§cNot in a guild"); return; }
        String msg = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        g.setNotice(msg);
        sender.sendMessage("§aNotice updated.");
    }
}