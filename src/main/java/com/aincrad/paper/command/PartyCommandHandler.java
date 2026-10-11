package com.aincrad.paper.command;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.party.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles all /aincrad party subcommands.
 */
public class PartyCommandHandler {

    private final AincradPaperPlugin plugin;

    public PartyCommandHandler(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§6[Party] §fUsage: /aincrad party <create|disband|invite|kick|leave|info|join|loot>");
            return;
        }
        PartyManager pm = PartyManager.getInstance();
        switch (args[1].toLowerCase()) {
            case "create" -> handleCreate(sender, pm);
            case "invite" -> handleInvite(sender, args, pm);
            case "kick" -> handleKick(sender, args, pm);
            case "leave" -> handleLeave(sender, pm);
            case "info" -> handleInfo(sender, pm);
            case "loot" -> handleLoot(sender, args, pm);
            default -> sender.sendMessage("§cUnknown party subcommand: " + args[1]);
        }
    }

    private void handleCreate(CommandSender sender, PartyManager pm) {
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        try {
            Party p = pm.createParty(player.getUniqueId(), player.getName(), 1);
            sender.sendMessage("§aParty created! ID: " + p.getPartyId().toString().substring(0, 8));
        } catch (Exception e) { sender.sendMessage("§c" + e.getMessage()); }
    }

    private void handleInvite(CommandSender sender, String[] args, PartyManager pm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad party invite <player>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        Party party = pm.getPartyForPlayer(player.getUniqueId());
        if (party == null) { sender.sendMessage("§cNot in a party"); return; }
        if (!party.isLeader(player.getUniqueId())) { sender.sendMessage("§cOnly leader can invite"); return; }
        Player target = plugin.getServer().getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found"); return; }
        if (pm.invitePlayer(party.getPartyId(), target.getUniqueId(), target.getName(), 1)) {
            sender.sendMessage("§aInvited " + target.getName());
            target.sendMessage("§6[Party] §fYou've been invited to " + player.getName() + "'s party!");
        } else sender.sendMessage("§cCannot invite.");
    }

    private void handleKick(CommandSender sender, String[] args, PartyManager pm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad party kick <player>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        Party party = pm.getPartyForPlayer(player.getUniqueId());
        if (party == null) { sender.sendMessage("§cNot in a party"); return; }
        Player target = plugin.getServer().getPlayer(args[2]);
        if (target == null) { sender.sendMessage("§cPlayer not found"); return; }
        if (pm.kickMember(party.getPartyId(), target.getUniqueId(), player.getUniqueId()))
            sender.sendMessage("§aKicked " + target.getName());
        else sender.sendMessage("§cCannot kick.");
    }

    private void handleLeave(CommandSender sender, PartyManager pm) {
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        if (pm.leaveParty(player.getUniqueId()))
            sender.sendMessage("§aLeft party.");
        else sender.sendMessage("§cNot in a party.");
    }

    private void handleInfo(CommandSender sender, PartyManager pm) {
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        Party party = pm.getPartyForPlayer(player.getUniqueId());
        if (party == null) { sender.sendMessage("§cNot in a party"); return; }
        sender.sendMessage("§6=== Party (" + party.getMemberCount() + "/" + Party.MAX_MEMBERS + ") ===");
        sender.sendMessage("§fLeader: " + party.getMembers().stream()
                .filter(m -> m.getPlayerId().equals(party.getLeaderId()))
                .map(m -> m.getName()).findFirst().orElse("?"));
        sender.sendMessage("§fLoot: " + party.getLootRule().getDisplayName());
        for (PartyMember m : party.getMembers())
            sender.sendMessage("§f" + m.getName() + " Lv" + m.getLevel() + " [" + m.getRole() + "]");
    }

    private void handleLoot(CommandSender sender, String[] args, PartyManager pm) {
        if (args.length < 3) { sender.sendMessage("§cUsage: /aincrad party loot <free_for_all|round_robin|need_greed>"); return; }
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        Party party = pm.getPartyForPlayer(player.getUniqueId());
        if (party == null) { sender.sendMessage("§cNot in a party"); return; }
        try {
            PartyLootRule rule = PartyLootRule.valueOf(args[2].toUpperCase());
            party.setLootRule(rule);
            sender.sendMessage("§aLoot rule: " + rule.getDisplayName());
        } catch (IllegalArgumentException e) { sender.sendMessage("§cInvalid loot rule"); }
    }
}