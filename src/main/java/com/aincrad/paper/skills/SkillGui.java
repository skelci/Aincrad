package com.aincrad.paper.skills;

import com.aincrad.paper.skills.PaperSkillService.SkillDef;
import com.aincrad.paper.skills.PaperSkillService.SkillState;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class SkillGui implements InventoryHolder {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final int MAIN = 0;
    private static final int DETAIL = 1;

    private final Player player;
    private final PaperSkillService service;
    private final Inventory inventory;
    private final int view;
    private final String skillId;
    private boolean live;

    private SkillGui(Player player, PaperSkillService service, int view, String skillId) {
        this.player = player;
        this.service = service;
        this.view = view;
        this.skillId = skillId;
        String title = "§1§l✦ Skill Crystallum";
        if (view != MAIN) {
            SkillDef d = PaperSkillService.get(skillId);
            title = "§1§l✦ " + (d == null ? "Skill" : d.name());
        }
        this.inventory = Bukkit.createInventory(this, view == MAIN ? 27 : 36,
                LEGACY.deserialize(title));
        render();
    }

    public static void open(Player player, PaperSkillService service) {
        SkillGui gui = new SkillGui(player, service, MAIN, null);
        gui.live = true;
        player.openInventory(gui.inventory);
    }

    private void openDetail(String id) {
        SkillGui gui = new SkillGui(player, service, DETAIL, id);
        gui.live = true;
        player.openInventory(gui.inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void render() {
        fill(view == MAIN ? 27 : 36);
        if (view == MAIN) renderMain();
        else renderDetail();
    }

    private void renderMain() {
        int slot = 0;
        for (SkillDef def : PaperSkillService.all().values()) {
            if (slot >= 18) break;
            boolean ok = PaperSkillService.meetsReqs(player, def);
            int rank = PaperSkillService.state(player).rank(def.id());
            List<Component> lore = new ArrayList<>();
            lore.add(LEGACY.deserialize("§7" + def.description()));
            lore.add(LEGACY.deserialize("§b" + def.mpCost() + " MP §8• §cCD " + def.cooldownSec()
                    + "s §8• §aRank " + rank + "/" + def.maxRank()));
            lore.add(LEGACY.deserialize("§6Power §f" + fmtPower(def.power())
                    + " §8• §e" + def.usage() + " §8×" + def.usagePerRank() + "§7/rank"));
            lore.add(LEGACY.deserialize("§dLv " + def.requiredLevel()
                    + " §8• §7Rank-up: §c" + def.upgradeCost() + " Col"));
            if (!ok) {
                int lvl = com.aincrad.paper.progression.ProgressionManager.getInstance()
                        .getProgression(player.getUniqueId()).getLevel();
                if (lvl < def.requiredLevel()) {
                    lore.add(LEGACY.deserialize("§c✖ Requires level " + def.requiredLevel()));
                }
                if (def.isQuestLocked() && !PaperSkillService.isUnlocked(player, def)) {
                    lore.add(LEGACY.deserialize("§c✖ Sealed — complete quest §e" + def.unlockQuest()));
                }
            } else {
                lore.add(LEGACY.deserialize("§a✔ Click to inspect, rank up and bind"));
            }
            inventory.setItem(slot++, icon(def.icon(),
                    (ok ? "§a" : "§8") + def.name(), lore));
        }
        inventory.setItem(23, icon(Material.AMETHYST_SHARD, "§dGive me my Skill Drives",
                List.of(LEGACY.deserialize("§7Puts your bound §bSkill Drives §7in your"),
                        LEGACY.deserialize("§7hotbar. Right-click a drive to §fcast"),
                        LEGACY.deserialize("§7the skill — your §fskill buttons§7."))));
    }

    private void renderDetail() {
        SkillDef def = PaperSkillService.get(skillId);
        if (def == null) return;
        SkillState st = PaperSkillService.state(player);
        boolean ok = PaperSkillService.meetsReqs(player, def);
        int rank = st.rank(def.id());
        int need = PaperSkillService.usageNeeded(def, rank);
        int have = st.uses.getOrDefault(def.id(), 0);
        int cost = PaperSkillService.upgradeCost(def, rank);
        boolean maxed = rank >= def.maxRank();

        List<Component> lore = new ArrayList<>();
        lore.add(LEGACY.deserialize("§7" + def.description()));
        lore.add(LEGACY.deserialize("§b" + def.mpCost() + " MP §8• §cCD "
                + service.rankCooldownSec(def, rank) + "s §8• §fDMG x"
                + String.format(java.util.Locale.ROOT, "%.2f", service.rankPower(def, rank))));
        lore.add(LEGACY.deserialize("§aRank " + rank + "§8/§a" + def.maxRank()
                + (maxed ? " §6(MAX)" : " §8— §e" + Math.min(have, need) + "/" + need
                        + " " + PaperSkillService.usageLabel(def))));
        if (def.isQuestLocked() && !PaperSkillService.isUnlocked(player, def)) {
            lore.add(LEGACY.deserialize("§c✖ Sealed — complete quest §e" + def.unlockQuest()));
        }
        inventory.setItem(4, icon(def.icon(), "§f" + def.name() + " §8R" + rank, lore));

        for (int i = 0; i < PaperSkillService.SLOT_COUNT; i++) {
            String bound = st.slots[i];
            boolean here = def.id().equals(bound);
            String other = bound != null && !here ? PaperSkillService.get(bound).name() : null;
            List<Component> slotLore = new ArrayList<>();
            if (here) {
                slotLore.add(LEGACY.deserialize("§aBound to this skill"));
                slotLore.add(LEGACY.deserialize("§eClick to unbind"));
            } else if (other != null) {
                slotLore.add(LEGACY.deserialize("§7Currently: §f" + other));
                slotLore.add(LEGACY.deserialize("§eClick to re-bind to this skill"));
            } else {
                slotLore.add(LEGACY.deserialize("§7Empty"));
                slotLore.add(LEGACY.deserialize(ok ? "§eClick to bind this skill"
                        : "§cSkill locked"));
            }
            inventory.setItem(9 + i, icon(here ? Material.LIME_STAINED_GLASS_PANE
                            : other != null ? Material.YELLOW_STAINED_GLASS_PANE
                            : Material.CYAN_STAINED_GLASS_PANE,
                    "§fDrive Slot " + (i + 1), slotLore));
        }

        List<Component> upLore = new ArrayList<>();
        if (maxed) {
            upLore.add(LEGACY.deserialize("§6This skill is at max rank."));
        } else {
            upLore.add(LEGACY.deserialize("§7Requirement: §e" + Math.min(have, need) + "/" + need
                    + " " + PaperSkillService.usageLabel(def)));
            upLore.add(LEGACY.deserialize("§7Cost: §6" + cost + " Col"));
            boolean can = have >= need;
            upLore.add(LEGACY.deserialize(can ? "§a✔ Click to rank up §f(+15% power, -5% CD)"
                    : "§c✖ Keep using the skill to train it"));
        }
        inventory.setItem(22, icon(maxed ? Material.ENCHANTED_BOOK : Material.BOOK,
                maxed ? "§6★ Max Rank" : (have >= need ? "§a⬆ Upgrade to R" + (rank + 1)
                        : "§c⬆ Rank " + (rank + 1) + " (training)"), upLore));

        inventory.setItem(31, icon(Material.ARROW, "§f← Back to all skills", List.of()));
    }

    @EventHandler
    public void onClickUnused() {
    }

    private static final class GuiListener implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof SkillGui gui)) return;
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player p) || !gui.live) return;
            int raw = event.getRawSlot();

            if (gui.view == MAIN) {
                if (raw == 23) {
                    gui.live = false;
                    gui.service.plugin().getServer().getScheduler().runTask(gui.service.plugin(), () -> {
                        gui.player.closeInventory();
                        gui.service.giveDrives(p);
                    });
                    return;
                }
                if (raw >= 0 && raw < 18) {
                    List<SkillDef> defs = new ArrayList<>(PaperSkillService.all().values());
                    if (raw < defs.size()) {
                        String id = defs.get(raw).id();
                        gui.live = false;
                        gui.service.plugin().getServer().getScheduler().runTask(gui.service.plugin(),
                                () -> gui.openDetail(id));
                    }
                }
                return;
            }

            SkillDef def = PaperSkillService.get(gui.skillId);
            if (def == null) return;
            if (raw == 31) {
                gui.live = false;
                gui.service.plugin().getServer().getScheduler().runTask(gui.service.plugin(),
                        () -> open(p, gui.service));
            } else if (raw == 22) {
                if (gui.service.tryUpgrade(p, def.id())) gui.rebuild();
            } else if (raw >= 9 && raw < 18) {
                int slot = raw - 9;
                String bound = PaperSkillService.state(p).slots[slot];
                if (def.id().equals(bound)) gui.service.bindSlot(p, slot, null);
                else gui.service.bindSlot(p, slot, def.id());
                gui.rebuild();
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (event.getInventory().getHolder(false) instanceof SkillGui gui && gui.live) {
                gui.live = false;
            }
        }
    }

    public static void registerListener(com.aincrad.paper.AincradPaperPlugin plugin) {
        org.bukkit.Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
    }

    private void rebuild() {
        inventory.clear();
        render();
    }

    private void fill(int size) {
        ItemStack pane = icon(Material.GRAY_STAINED_GLASS_PANE, "§7 ", List.of());
        for (int i = 0; i < size; i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, pane);
        }
    }

    private static String fmtPower(double p) {
        return p == Math.floor(p) ? String.valueOf((long) p) : String.format("%.1f", p);
    }

    private static ItemStack icon(Material material, String name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LEGACY.deserialize(name));
        if (!lore.isEmpty()) meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static final class SkillDefHelper {
        private SkillDefHelper() {
        }
    }
}
