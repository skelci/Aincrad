package com.aincrad.paper.quest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Sidebar widget showing the player's active quest and objective progress.
 */
public final class QuestWidget {

    private static final Map<java.util.UUID, Scoreboard> BOARDS = new HashMap<>();

    private QuestWidget() {}

    public static void register(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, QuestWidget::tick, 60L, 40L);
        plugin.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) {
                BOARDS.remove(event.getPlayer().getUniqueId());
            }
        }, plugin);
    }

    private static void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                update(player);
            } catch (Exception ignored) {
            }
        }
    }

    private static void update(Player player) {
        QuestManager qm = QuestManager.getInstance();
        QuestTracker tracker = qm.hasTracker(player.getUniqueId())
                ? qm.getTracker(player.getUniqueId()) : null;
        var quests = tracker == null ? java.util.List.<Quest>of()
                : new java.util.ArrayList<>(tracker.activeQuests());

        if (quests.isEmpty()) {
            Scoreboard old = BOARDS.remove(player.getUniqueId());
            if (old != null && player.getScoreboard().equals(old)) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
            return;
        }

        Scoreboard board = BOARDS.get(player.getUniqueId());
        if (board == null || !player.getScoreboard().equals(board)) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = board.registerNewObjective("aincrad_quest",
                    org.bukkit.scoreboard.Criteria.DUMMY,
                    net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                            .legacyAmpersand().deserialize("§6§l✦ ACTIVE QUEST"));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            BOARDS.put(player.getUniqueId(), board);
            player.setScoreboard(board);
        }

        Objective obj = board.getObjective("aincrad_quest");
        if (obj == null) return;
        Set<String> keep = new HashSet<>();
        int slot = 0;
        for (Quest q : quests) {
            if (slot > 12) break;
            String head = "§e" + trim(q.name(), 26);
            setLine(board, obj, keep, head, slot++);
            for (QuestObjective o : q.objectives()) {
                if (slot > 12) break;
                String bar = progressBar(o);
                setLine(board, obj, keep,
                        "§7" + trim(label(o), 17) + " §a" + o.getCurrent() + "/" + o.getRequired() + bar,
                        slot++);
            }
        }
        for (String entry : new HashSet<>(board.getEntries())) {
            if (!keep.contains(entry)) board.resetScores(entry);
        }
    }

    private static String label(QuestObjective o) {
        return switch (o.getType()) {
            case KILL, DEFEAT -> "Kill " + o.getTarget();
            case COLLECT -> "Collect " + o.getTarget();
            case TALK -> "Talk to " + o.getTarget();
            case INVESTIGATE -> "Investigate " + o.getTarget();
        };
    }

    private static String progressBar(QuestObjective o) {
        if (o.isComplete()) return " §a✔";
        int filled = o.getRequired() <= 0 ? 0
                : Math.min(5, (int) Math.floor(o.getCurrent() * 5.0 / o.getRequired()));
        return " §8[" + "▮".repeat(filled) + "§7" + "▯".repeat(5 - filled) + "§8]";
    }

    private static void setLine(Scoreboard board, Objective obj, Set<String> keep, String text, int slot) {
        String entry = text;
        int dup = 0;
        while (keep.contains(entry)) {
            dup++;
            entry = text + "§r" + " ".repeat(dup);
        }
        keep.add(entry);
        Score s = obj.getScore(entry);
        if (s != null) s.setScore(20 - slot);
    }

    private static String trim(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
