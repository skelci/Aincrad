package com.aincrad.paper.command;

import com.aincrad.paper.progression.PlayerProgression;
import com.aincrad.paper.progression.ProgressionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles /aincrad progression subcommands.
 */
public class ProgressionCommandHandler {

    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("§cPlayer only"); return; }
        ProgressionManager pm = ProgressionManager.getInstance();
        PlayerProgression prog = pm.getProgression(player.getUniqueId());
        sender.sendMessage("§6=== Progression ===");
        sender.sendMessage("§fLevel: " + prog.getLevel() + (prog.isMaxLevel() ? " (MAX)" : "") + "\n§cStrength: §f" + com.aincrad.paper.progression.ProgressionManager.strengthOf(prog.getLevel()) + " §7(+" + com.aincrad.paper.progression.ProgressionManager.strengthOf(prog.getLevel()) + "% dmg)");
        sender.sendMessage("§fXP: " + String.format("%.1f", prog.getXp()));
        sender.sendMessage("§fProgress: " + String.format("%.1f%%", prog.getProgressToNextLevel() * 100));
        sender.sendMessage("§fSkill Points: " + prog.getSkillPoints());
        sender.sendMessage("§fStat Points: " + prog.getStatPoints());
        sender.sendMessage("§fNG+: " + prog.getNgPlusCount());
        sender.sendMessage("§fMilestones: " + prog.getMilestonesAchieved());
    }
}