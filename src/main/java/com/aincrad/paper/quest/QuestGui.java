package com.aincrad.paper.quest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QuestGui implements InventoryHolder {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static QuestGui current;

    private final Player player;
    private final Inventory inventory;

    private QuestGui(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 27, LEGACY.deserialize("§1§l❖ Guild Notice Board"));
        render();
    }

    public static void open(Player player) {
        current = new QuestGui(player);
        player.openInventory(current.inventory);
    }

    public static void register(org.bukkit.plugin.Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void render() {
        QuestManager qm = QuestManager.getInstance();
        QuestTracker tracker = qm.getTracker(player.getUniqueId());

        List<Quest> active = new ArrayList<>(tracker.activeQuests());
        List<Quest> available = new ArrayList<>();
        List<Quest> completed = new ArrayList<>();
        for (Quest q : qm.getAllQuests()) {
            if (active.contains(q)) continue;
            if (tracker.isCompleted(q.id())) completed.add(q);
            else available.add(q);
        }

        for (int i = 0; i < 9 && i < active.size(); i++) {
            Quest q = active.get(i);
            boolean done = q.isComplete();
            inventory.setItem(i, icon(done ? Material.EMERALD_BLOCK : Material.BOOK,
                    (done ? "§a✔ " : "§e● ") + q.name(),
                    objectiveLore(q, done)));
        }
        for (int i = 0; i < 9 && i < available.size(); i++) {
            Quest q = available.get(i);
            boolean locked = q.prerequisites().stream().anyMatch(p -> !tracker.isCompleted(p));
            inventory.setItem(9 + i, icon(locked ? Material.BOOK : Material.WRITABLE_BOOK,
                    (locked ? "§c✖ " : "§b● ") + q.name() + " §8[" + q.type() + "]",
                    questLore(q, locked, tracker)));
        }
        for (int i = 0; i < 7 && i < completed.size(); i++) {
            Quest q = completed.get(i);
            inventory.setItem(18 + i, icon(Material.GREEN_STAINED_GLASS_PANE, "§a✔ " + q.name(),
                    List.of(LEGACY.deserialize("§7Completed"))));
        }
        inventory.setItem(25, icon(Material.COMPASS, "§fHow it works",
                List.of(LEGACY.deserialize("§7Left-click §fblue §7= accept quest"),
                        LEGACY.deserialize("§7Left-click §fgreen §7= turn in for rewards"),
                        LEGACY.deserialize("§7§oRight-click §fan active quest to abandon"),
                        LEGACY.deserialize("§7Kill listed mobs; §flook §7for sparkles for"),
                        LEGACY.deserialize("§7investigation quests. Quests unlock skills!"))));
        fill();
    }

    private List<Component> objectiveLore(Quest q, boolean done) {
        List<Component> lore = new ArrayList<>();
        for (QuestObjective o : q.objectives()) {
            String col = o.isComplete() ? "§a" : "§7";
            lore.add(LEGACY.deserialize(col + label(o) + " " + o.getCurrent() + "/" + o.getRequired()));
        }
        rewardLore(q, lore);
        lore.add(LEGACY.deserialize(done ? "§eClick to turn in!" : "§8In progress..."));
        return lore;
    }

    private List<Component> questLore(Quest q, boolean locked, QuestTracker tracker) {
        List<Component> lore = new ArrayList<>();
        for (QuestObjective o : q.objectives()) {
            lore.add(LEGACY.deserialize("§7- " + label(o) + " x" + o.getRequired()));
        }
        rewardLore(q, lore);
        for (String p : q.prerequisites()) {
            if (!tracker.isCompleted(p)) {
                Quest pq = QuestManager.getInstance().getQuest(p);
                lore.add(LEGACY.deserialize("§cRequires: " + (pq == null ? p : pq.name())));
            }
        }
        lore.add(LEGACY.deserialize(locked ? "§cPrerequisites missing" : "§eClick to accept"));
        return lore;
    }

    private void rewardLore(Quest q, List<Component> lore) {
        QuestReward r = q.rewards();
        lore.add(LEGACY.deserialize("§8Reward: §b" + (int) r.getXp() + " XP §8• §6" + r.getCol() + " Col"));
        for (String skillId : QuestRewards.unlocksFor(q.id())) {
            var def = com.aincrad.paper.skills.PaperSkillService.get(skillId);
            if (def != null) {
                lore.add(LEGACY.deserialize("§d✦ Unlocks skill: " + def.name()));
            }
        }
    }

    private static String label(QuestObjective o) {
        return switch (o.getType()) {
            case KILL -> "Slay " + o.getTarget();
            case DEFEAT -> "Defeat " + o.getTarget();
            case COLLECT -> "Collect " + o.getTarget();
            case INVESTIGATE -> "Investigate: " + o.getTarget();
            case TALK -> "Speak with " + o.getTarget();
        };
    }

    private void fill() {
        ItemStack pane = icon(Material.GRAY_STAINED_GLASS_PANE, "§7 ", List.of());
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, pane);
        }
    }

    private static ItemStack icon(Material material, String name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY.deserialize(name));
        if (!lore.isEmpty()) meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static final class GuiListener implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof QuestGui gui)) return;
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player p)) return;
            int raw = event.getRawSlot();
            if (raw < 0 || raw > 25) return;

            QuestManager qm = QuestManager.getInstance();
            QuestTracker tracker = qm.getTracker(p.getUniqueId());
            List<Quest> active = new ArrayList<>(tracker.activeQuests());

            if (raw < 9) {
                if (raw >= active.size()) return;
                Quest q = active.get(raw);
                if (event.isRightClick()) {
                    qm.abandonQuest(p.getUniqueId(), q.id());
                    p.sendMessage("§7Quest abandoned: §f" + q.name());
                } else if (q.isComplete()) {
                    try {
                        QuestReward reward = qm.turnInQuest(p.getUniqueId(), q.id());
                        QuestRewards.grant(p, q.id(), reward);
                    } catch (QuestManager.QuestException e) {
                        p.sendMessage("§c" + e.getMessage());
                    }
                } else {
                    p.sendMessage("§e" + q.name() + " §7— " + q.objectives().get(0));
                }
                gui.refresh();
                return;
            }
            if (raw >= 9 && raw < 18) {
                List<Quest> available = new ArrayList<>();
                for (Quest q : qm.getAllQuests()) {
                    if (!active.contains(q) && !tracker.isCompleted(q.id())) available.add(q);
                }
                if (raw - 9 >= available.size()) return;
                Quest q = available.get(raw - 9);
                try {
                    qm.acceptQuest(p.getUniqueId(), q.id());
                    p.sendMessage("§aQuest accepted: §f" + q.name());
                    QuestRewards.hint(p, q);
                } catch (QuestManager.QuestException e) {
                    p.sendMessage("§c" + e.getMessage());
                }
                gui.refresh();
            }
        }
    }

    private void refresh() {
        inventory.clear();
        render();
    }

    static {
        // no-op locale guard to keep imports honest
        Locale.getDefault();
    }
}
