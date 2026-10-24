package com.aincrad.paper.quest;

import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.progression.ProgressionManager;
import com.aincrad.paper.skills.PaperSkillService;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public final class QuestRewards {

    private static final Map<String, List<String>> SKILL_UNLOCKS = Map.of(
            "f1_cull_the_foe", List.of("cleave", "heal"),
            "f1_ranch_trouble", List.of("bulwark"),
            "f1_weeping_stone", List.of("regen"),
            "f1_broken_seal", List.of(),
            "f1_ilfang_ending", List.of("vindicator"));

    private QuestRewards() {
    }

    public static List<String> unlocksFor(String questId) {
        return SKILL_UNLOCKS.getOrDefault(questId, List.of());
    }

    public static void grant(Player player, String questId, QuestReward reward) {
        if (reward.getCol() > 0) {
            EconomyManager.getInstance().deposit(player.getUniqueId(),
                    new Col(reward.getCol()), "quest:" + questId);
        }
        if (reward.getXp() > 0) {
            ProgressionManager.getInstance().addXp(player.getUniqueId(), reward.getXp());
        }
        for (String skillId : unlocksFor(questId)) {
            PaperSkillService.unlockSkill(player.getUniqueId(), skillId);
            PaperSkillService.SkillDef def = PaperSkillService.get(skillId);
            if (def != null) {
                player.sendMessage("§6✦ §eNew skill unlocked: §a" + def.name()
                        + " §7— bind it in §e/skills");
            }
        }
        player.sendMessage("§aQuest complete! §f+" + (int) reward.getXp() + " XP, §6+"
                + reward.getCol() + " Col");
    }

    public static void hint(Player player, Quest quest) {
        for (QuestObjective obj : quest.objectives()) {
            if (obj.getType() == QuestObjective.Type.INVESTIGATE) {
                player.sendMessage("§7» " + QuestWorld.describe(obj.getTarget()));
            }
        }
    }
}
