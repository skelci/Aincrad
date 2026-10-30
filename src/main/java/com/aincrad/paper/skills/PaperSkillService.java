package com.aincrad.paper.skills;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.listener.ItemMechanicListener;
import com.aincrad.paper.progression.ProgressionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaperSkillService {

    public enum Effect { CONE, AIR_CONE, DASH_CONE, HEAL, MASS_HEAL, BUFF_ATTACK, BUFF_DEFENSE, AOE, REGEN }

    public record SkillDef(String id, String name, String description, int mpCost, int cooldownSec,
                           int requiredLevel, Effect effect, double power,
                           String usage, int usagePerRank, int upgradeCost, int maxRank,
                           String unlockQuest, Material icon) {

        public boolean isQuestLocked() {
            return unlockQuest != null && !unlockQuest.isBlank();
        }
    }

    public static final NamespacedKey KEY_DRIVE = new NamespacedKey("aincrad", "skill_drive_slot");
    public static final int SLOT_COUNT = 9;

    private static final Map<String, SkillDef> SKILLS = new LinkedHashMap<>();

    private static final Map<UUID, Double> MANA = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> COOLDOWNS = new ConcurrentHashMap<>();
    private static final Map<UUID, SkillState> STATES = new ConcurrentHashMap<>();

    public static final class SkillState {
        public final Map<String, Integer> ranks = new HashMap<>();
        public final Map<String, Integer> uses = new HashMap<>();
        public final Set<String> unlocked = new HashSet<>();
        public final String[] slots = new String[SLOT_COUNT];

        public int rank(String id) {
            return ranks.getOrDefault(id, 1);
        }
    }

    private final AincradPaperPlugin plugin;

    static {
        register(new SkillDef("horizontal", "Horizontal Arpeggio",
                "A wide horizontal slash hitting all enemies in front.",
                12, 6, 1, Effect.CONE, 1.8, "HIT", 10, 120, 5, null, Material.IRON_SWORD));
        register(new SkillDef("vertical", "Vertical Arc",
                "A rising arc that strikes enemies ahead and lifts them slightly.",
                12, 6, 1, Effect.CONE, 1.7, "HIT", 10, 120, 5, null, Material.STONE_SWORD));
        register(new SkillDef("rush", "Sonic Leap",
                "Dash forward, damaging everything along the path; range grows with each rank.",
                10, 5, 3, Effect.DASH_CONE, 1.5, "HIT", 8, 150, 5, null, Material.SUGAR));
        register(new SkillDef("cleave", "Cavern Cleave",
                "Spin 360 degrees, striking every nearby enemy.",
                18, 8, 3, Effect.AOE, 1.45, "HIT", 12, 160, 5, "f1_cull_the_foe", Material.GOLDEN_AXE));
        register(new SkillDef("bulwark", "Bulwark",
                "Brace behind your blade: heavy damage reduction for a short time.",
                16, 20, 4, Effect.BUFF_DEFENSE, 0.55, "USE", 5, 140, 5, "f1_ranch_trouble", Material.SHIELD));
        register(new SkillDef("heal", "Healing Touch",
                "Channel moonlight to restore 4 hearts.",
                20, 15, 4, Effect.HEAL, 8.0, "HEAL", 6, 140, 5, "f1_cull_the_foe", Material.GLISTERING_MELON_SLICE));
        register(new SkillDef("chaos_streak", "Chaos Streak",
                "A wild flurry covering a wide cone.",
                18, 10, 5, Effect.CONE, 2.2, "HIT", 12, 180, 5, null, Material.BLAZE_POWDER));
        register(new SkillDef("air_slash", "Raining Edge",
                "A downward strike while airborne. Must be in the air.",
                15, 8, 6, Effect.AIR_CONE, 2.1, "HIT", 10, 170, 5, null, Material.FEATHER));
        register(new SkillDef("regen", "Renewal Wave",
                "Soothing light restores your health steadily over 6 seconds.",
                22, 25, 7, Effect.REGEN, 1.5, "HEAL", 5, 150, 5, "f1_weeping_stone", Material.GLASS_BOTTLE));
        register(new SkillDef("fighter_stance", "Fighter Stance",
                "+40% attack damage for 12 seconds.",
                15, 30, 7, Effect.BUFF_ATTACK, 0.4, "USE", 4, 150, 5, null, Material.IRON_SWORD));
        register(new SkillDef("guardian_stance", "Guardian Stance",
                "-45% damage taken for 12 seconds.",
                15, 30, 7, Effect.BUFF_DEFENSE, 0.45, "USE", 4, 150, 5, null, Material.CHAINMAIL_CHESTPLATE));
        register(new SkillDef("healing_prayer", "Healing Prayer",
                "Restores 2 hearts to you and nearby allies.",
                30, 40, 10, Effect.MASS_HEAL, 4.0, "HEAL", 4, 200, 5, null, Material.GOLDEN_APPLE));
        register(new SkillDef("vindicator", "Vindicator",
                "A committed two-handed overhead blow aimed at a single foe.",
                25, 12, 12, Effect.CONE, 3.0, "HIT", 15, 260, 5, "f1_ilfang_ending", Material.NETHERITE_SWORD));
    }

    private static void register(SkillDef def) {
        SKILLS.put(def.id(), def);
    }

    public PaperSkillService(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        load();
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::saveAll, 6000L, 6000L);
        plugin.getServer().getPluginManager().registerEvents(new SkillSlotListener(plugin, this), plugin);
        plugin.getLogger().info("PaperSkillService online with " + SKILLS.size() + " skills.");
    }

    public AincradPaperPlugin plugin() {
        return plugin;
    }

    // ── Registry ────────────────────────────────────────────────────

    public static Map<String, SkillDef> all() {
        return SKILLS;
    }

    public static SkillDef get(String id) {
        return SKILLS.get(id);
    }

    // ── Mana ────────────────────────────────────────────────────────

    public static double maxMana(Player player) {
        int level = ProgressionManager.getInstance().getProgression(player.getUniqueId()).getLevel();
        return 100 + level * 6.0;
    }

    public static double mana(Player player) {
        return MANA.computeIfAbsent(player.getUniqueId(), k -> maxMana(player));
    }

    public static boolean spendMana(Player player, double amount) {
        double current = mana(player);
        if (current < amount) return false;
        MANA.put(player.getUniqueId(), current - amount);
        return true;
    }

    public static void forget(UUID id) {
        MANA.remove(id);
        COOLDOWNS.remove(id);
    }

    private void tick() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            double current = MANA.computeIfAbsent(id, k -> maxMana(player));
            double max = maxMana(player);
            int level = ProgressionManager.getInstance().getProgression(id).getLevel();
            double rate = com.aincrad.paper.progression.PassiveSkills.mpRegenPerSecond(level);
            double next = Math.min(max, current + max * rate);
            MANA.put(id, next);
            player.setExp((float) (next / max));
        }
    }

    // ── Player skill state ──────────────────────────────────────────

    public static SkillState state(Player player) {
        return STATES.computeIfAbsent(player.getUniqueId(), k -> new SkillState());
    }

    public boolean setSkillRank(org.bukkit.entity.Player player, String skillId, int rank) {
        SkillDef def = SKILLS.get(skillId);
        if (def == null) return false;
        int r = Math.max(1, Math.min(def.maxRank(), rank));
        state(player).ranks.put(skillId, r);
        saveAll();
        return true;
    }

    public static void unlockSkill(UUID playerId, String skillId) {
        SkillDef def = SKILLS.get(skillId);
        if (def == null) return;
        SkillState st = STATES.computeIfAbsent(playerId, k -> new SkillState());
        st.unlocked.add(skillId);
    }

    public static boolean isUnlocked(Player player, SkillDef def) {
        if (!def.isQuestLocked()) return true;
        if (state(player).unlocked.contains(def.id())) return true;
        return com.aincrad.paper.quest.QuestManager.getInstance()
                .getTracker(player.getUniqueId()).isCompleted(def.unlockQuest());
    }

    public static boolean meetsReqs(Player player, SkillDef def) {
        int level = ProgressionManager.getInstance().getProgression(player.getUniqueId()).getLevel();
        return level >= def.requiredLevel() && isUnlocked(player, def);
    }

    public double rankPower(SkillDef def, int rank) {
        return def.power() * (1.0 + 0.15 * (rank - 1));
    }

    public int rankCooldownSec(SkillDef def, int rank) {
        return Math.max(2, (int) Math.round(def.cooldownSec() * (1.0 - 0.05 * (rank - 1))));
    }

    public static int usageNeeded(SkillDef def, int rank) {
        return def.usagePerRank() * rank;
    }

    public static int upgradeCost(SkillDef def, int rank) {
        return def.upgradeCost() * (1 << (rank - 1));
    }

    public void recordUsage(Player player, SkillDef def, int amount) {
        if (def.usage().equals("NONE") || amount <= 0) return;
        SkillState st = state(player);
        st.uses.merge(def.id(), amount, Integer::sum);
        int need = usageNeeded(def, st.rank(def.id()));
        int now = st.uses.getOrDefault(def.id(), 0);
        if (now == need && st.rank(def.id()) < def.maxRank()) {
            action(player, "§a✦ §e" + def.name() + " §7can now be upgraded §8(/skills)");
        }
    }

    public static String usageLabel(SkillDef def) {
        return switch (def.usage()) {
            case "HIT" -> "hits landed";
            case "HEAL" -> "heals cast";
            default -> "casts";
        };
    }

    public boolean tryUpgrade(Player player, String id) {
        SkillDef def = SKILLS.get(id);
        if (def == null) return false;
        SkillState st = state(player);
        int rank = st.rank(id);
        if (rank >= def.maxRank()) {
            player.sendMessage("§c" + def.name() + " is already at max rank.");
            return false;
        }
        int need = usageNeeded(def, rank);
        int have = st.uses.getOrDefault(id, 0);
        if (have < need) {
            player.sendMessage("§cNeeds " + (need - have) + " more " + usageLabel(def)
                    + " to reach rank " + (rank + 1) + ".");
            return false;
        }
        int cost = upgradeCost(def, rank);
        long bal = EconomyManager.getInstance().getBalance(player.getUniqueId()).getAmount();
        if (bal < cost) {
            player.sendMessage("§cNeed " + cost + " Col to rank up " + def.name() + " (have " + bal + ").");
            return false;
        }
        EconomyManager.getInstance().withdraw(player.getUniqueId(), new Col(cost), "skill_rankup_" + id);
        st.uses.put(id, have - need);
        st.ranks.put(id, rank + 1);
        player.sendMessage("§6✦ §e" + def.name() + " §areached rank §e" + (rank + 1) + "§a!");
        particle(ParticleNames.of("TOWN_AURA", "VILLAGER_HAPPY"), player.getEyeLocation(), 40);
        return true;
    }

    // ── Slots / drives ──────────────────────────────────────────────

    public void bindSlot(Player player, int slot, String skillId) {
        SkillState st = state(player);
        if (slot < 0 || slot >= SLOT_COUNT) return;
        if (skillId == null || skillId.isBlank()) {
            st.slots[slot] = null;
            return;
        }
        SkillDef def = SKILLS.get(skillId);
        if (def == null || !meetsReqs(player, def)) {
            player.sendMessage("§cThat skill is not unlocked yet.");
            return;
        }
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (skillId.equals(st.slots[i])) st.slots[i] = null;
        }
        st.slots[slot] = skillId;
        player.sendMessage("§aBound §f" + def.name() + " §ato drive slot §e" + (slot + 1) + "§a.");
    }

    public SkillDef boundSkill(Player player, int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) return null;
        String id = state(player).slots[slot];
        return id == null ? null : SKILLS.get(id);
    }

    public ItemStack driveItem(Player player, int slot) {
        SkillDef def = boundSkill(player, slot);
        if (def == null) {
            return null;
        }
        ItemStack item = new ItemStack(def.icon());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("§b✦ Skill Drive §7- §f" + def.name()));
        List<Component> lore = new ArrayList<>();
        lore.add(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("§7" + def.description()));
        lore.add(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("§eRight-click §7to cast §8(slot " + (slot + 1) + ")"));
        lore.add(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("§b" + def.mpCost() + " MP §8• §cRank " + state(player).rank(def.id())));
        meta.lore(lore);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.getPersistentDataContainer().set(KEY_DRIVE, PersistentDataType.BYTE, (byte) slot);
        item.setItemMeta(meta);
        return item;
    }

    public void giveDrives(Player player) {
        SkillState st = state(player);
        int given = 0;
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (st.slots[i] == null) continue;
            ItemStack drive = driveItem(player, i);
            if (drive == null) continue;
            Map<Integer, ItemStack> rest = player.getInventory().addItem(drive);
            if (!rest.isEmpty()) player.getWorld().dropItemNaturally(player.getLocation(), drive);
            given++;
        }
        if (given == 0) {
            player.sendMessage("§7No skills assigned yet — open §e/skills §7first.");
        } else {
            player.sendMessage("§aReceived §e" + given + " §aSkill Drives — right-click to cast!");
        }
    }

    // ── Casting ─────────────────────────────────────────────────────

    public void use(Player player, String id) {
        SkillDef def = SKILLS.get(id);
        if (def == null) {
            player.sendMessage("§cUnknown skill: " + id + ". Use §7/skills");
            return;
        }
        int level = ProgressionManager.getInstance().getProgression(player.getUniqueId()).getLevel();
        if (level < def.requiredLevel()) {
            player.sendMessage("§c" + def.name() + " unlocks at level " + def.requiredLevel() + ".");
            return;
        }
        if (!isUnlocked(player, def)) {
            player.sendMessage("§c" + def.name() + " is sealed — complete its quest to learn it. §7/skills");
            return;
        }
        Map<String, Long> cds = COOLDOWNS.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        Long readyAt = cds.get(def.id());
        if (readyAt != null && readyAt > System.currentTimeMillis()) {
            action(player, "§cOn cooldown: " + (readyAt - System.currentTimeMillis()) / 1000 + "s");
            return;
        }
        double mp = mana(player);
        if (mp < def.mpCost()) {
            player.sendMessage("§cNot enough MP (" + def.mpCost() + " needed).");
            return;
        }
        if (def.effect() == Effect.AIR_CONE && player.isOnGround()) {
            player.sendMessage("§c" + def.name() + " requires you to be airborne — jump!");
            return;
        }

        int rank = state(player).rank(def.id());
        MANA.put(player.getUniqueId(), mp - def.mpCost());
        player.swingMainHand();
        cds.put(def.id(), System.currentTimeMillis() + rankCooldownSec(def, rank) * 1000L);
        cast(player, def, rank);
    }

    private static void castSfx(Player player, SkillDef def) {
        var w = player.getWorld();
        var loc = player.getLocation();
        switch (def.id()) {
            case "heal", "regen" -> {
                w.playSound(loc, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.7f, 1.2f);
                w.playSound(loc, org.bukkit.Sound.ENTITY_WITCH_DRINK, 0.6f, 1.35f);
            }
            case "healing_prayer" -> {
                w.playSound(loc, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 0.6f, 1.1f);
                w.playSound(loc, org.bukkit.Sound.ENTITY_WITCH_DRINK, 0.8f, 1.2f);
            }
            case "bulwark" -> w.playSound(loc, org.bukkit.Sound.ITEM_SHIELD_BLOCK, 0.9f, 0.7f);
            case "guardian_stance", "fighter_stance" -> {
                w.playSound(loc, org.bukkit.Sound.BLOCK_ANVIL_USE, 0.5f, 0.85f);
                w.playSound(loc, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 0.35f, 1.3f);
            }
            case "vindicator" -> w.playSound(loc, org.bukkit.Sound.ENTITY_WARDEN_ATTACK_IMPACT, 0.8f, 1.25f);
            case "rush" -> w.playSound(loc, org.bukkit.Sound.ENTITY_BREEZE_SHOOT, 0.7f, 1.15f);
            case "air_slash" -> w.playSound(loc, org.bukkit.Sound.ENTITY_BREEZE_WIND_BURST, 0.6f, 1.05f);
            case "chaos_streak" -> w.playSound(loc, org.bukkit.Sound.ENTITY_SKELETON_SHOOT, 0.8f, 1.5f);
            case "cleave" -> w.playSound(loc, org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 0.75f);
            default -> w.playSound(loc, org.bukkit.Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.9f, 0.9f);
        }
    }

    private void cast(Player player, SkillDef def, int rank) {
        action(player, "§b» §f" + def.name() + " §8R" + rank);
        castSfx(player, def);
        switch (def.effect()) {
            case CONE -> recordUsage(player, def, hitCone(player, rankPower(def, rank), 5.0, 0.4));
            case AIR_CONE -> {
                recordUsage(player, def, hitCone(player, rankPower(def, rank), 5.5, 0.3));
                particle(ParticleNames.of("CLOUD", "EXPLOSION_NORMAL"), player.getLocation(), 30);
            }
            case DASH_CONE -> recordUsage(player, def, dash(player, rankPower(def, rank), dashRange(rank)));
            case AOE -> recordUsage(player, def, hitCone(player, rankPower(def, rank), 4.5, -1.0));
            case HEAL -> {
                heal(player, rankPower(def, rank));
                recordUsage(player, def, 1);
                particle(ParticleNames.of("HAPPY_VILLAGER", "VILLAGER_HAPPY"), player.getEyeLocation(), 25);
            }
            case MASS_HEAL -> {
                int healed = 0;
                heal(player, rankPower(def, rank));
                healed++;
                for (var e2 : player.getWorld().getNearbyEntities(player.getLocation(), 8, 8, 8,
                        Player.class::isInstance)) {
                    if (e2 instanceof Player mate && mate != player) {
                        heal(mate, rankPower(def, rank));
                        healed++;
                    }
                }
                recordUsage(player, def, healed);
                particle(ParticleNames.of("HAPPY_VILLAGER", "VILLAGER_HAPPY"), player.getLocation(), 60);
            }
            case REGEN -> {
                applyRegen(player, def, rank);
                recordUsage(player, def, 1);
                particle(ParticleNames.of("END_ROD", "WITCH"), player.getLocation(), 35);
            }
            case BUFF_ATTACK -> {
                ItemMechanicListener.stance(player.getUniqueId(), 1, rankPower(def, rank), 12_000);
                recordUsage(player, def, 1);
                particle(ParticleNames.of("FLAME", "FLAMES"), player.getLocation(), 30);
            }
            case BUFF_DEFENSE -> {
                ItemMechanicListener.stance(player.getUniqueId(), 2,
                        Math.min(0.85, rankPower(def, rank)), 12_000);
                recordUsage(player, def, 1);
                particle(ParticleNames.of("DUST", "RED_DUST", "CRIT_MAGIC"), player.getLocation(), 30);
            }
        }
    }

    private void applyRegen(Player player, SkillDef def, int rank) {
        double perTick = rankPower(def, rank);
        var holder = new int[]{0};
        Bukkit.getScheduler().runTaskTimer(plugin, t -> {
            holder[0]++;
            if (!player.isOnline() || holder[0] > 6) {
                t.cancel();
                return;
            }
            heal(player, perTick);
            particle(ParticleNames.of("HAPPY_VILLAGER", "VILLAGER_HAPPY"), player.getEyeLocation(), 12);
        }, 20L, 20L);
    }

    private int hitCone(Player player, double multiplier, double range, double minDot) {
        Vector facing = player.getLocation().getDirection().normalize();
        Location eye = player.getEyeLocation();
        int hits = 0;
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (!(e instanceof LivingEntity target) || e == player || !target.isValid()
                    || target.getHealth() <= 0) continue;
            Vector to = target.getLocation().toVector().add(new Vector(0, 1, 0))
                    .subtract(eye.toVector()).normalize();
            if (facing.dot(to) < minDot) continue;
            ItemMechanicListener.pendingSkill(player.getUniqueId(), multiplier, 0);
            player.attack(target);
            hits++;
        }
        if (hits > 0) {
            particle(ParticleNames.of("CRIT_MAGIC", "CRIT_MAGICAL"), eye, 20 * hits);
        }
        return hits;
    }

    private static double dashRange(int rank) {
        return 5.0 + Math.max(0, rank - 1) * 1.5;
    }

    private int dash(Player player, double multiplier, double distance) {
        Vector dir = player.getLocation().getDirection().normalize().multiply(distance);
        Location from = player.getLocation().clone();
        Location to = from.clone().add(dir);
        for (int step = 0; step <= 10; step++) {
            Location probe = from.clone().add(dir.clone().multiply(step / 10.0));
            probe.setY(probe.getY() + 0.5);
            if (probe.getBlock().getType().isSolid()) {
                to = from.clone().add(dir.clone().multiply((step - 1) / 10.0));
                break;
            }
        }
        Location feet = to.clone();
        int raised = 0;
        while ((feet.getBlock().getType().isSolid()
                || feet.clone().add(0, 1, 0).getBlock().getType().isSolid()) && raised < 6) {
            feet.add(0, 1, 0);
            raised++;
        }
        to = raised >= 6 ? from : feet;
        List<LivingEntity> path = new ArrayList<>();
        for (Entity e : player.getNearbyEntities(distance + 3, 4, distance + 3)) {
            if (e instanceof LivingEntity mob && e != player) {
                if (mob.getLocation().distance(from) <= to.distance(from) + 1.5
                        || mob.getLocation().distance(to) <= 2.5) {
                    path.add(mob);
                }
            }
        }
        player.teleport(to);
        for (LivingEntity mob : path) {
            ItemMechanicListener.pendingSkill(player.getUniqueId(), multiplier, 0);
            player.attack(mob);
        }
        particle(ParticleNames.of("SPELL_WITCH", "WITCH"), to, 25);
        return path.size();
    }

    private void heal(Player player, double amount) {
        double max = 20.0;
        for (String key : new String[]{"generic.max_health", "max_health"}) {
            org.bukkit.attribute.Attribute a = org.bukkit.Registry.ATTRIBUTE
                    .get(org.bukkit.NamespacedKey.minecraft(key));
            if (a != null && player.getAttribute(a) != null) {
                max = player.getAttribute(a).getValue();
                break;
            }
        }
        player.setHealth(Math.min(max, player.getHealth() + amount));
    }

    private void action(Player player, String text) {
        player.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
    }

    private void particle(Particle particle, Location loc, int count) {
        try {
            loc.getWorld().spawnParticle(particle, loc, count, 0.5, 0.5, 0.5, 0.01);
        } catch (Exception ignored) {
        }
    }

    // ── Chat commands ───────────────────────────────────────────────

    public boolean handle(Player player, String[] args) {
        if (args.length < 2) {
            usage(player);
            return true;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "list" -> listSkills(player);
            case "use" -> {
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /askill use <skill>");
                    return true;
                }
                use(player, args[2].toLowerCase(Locale.ROOT));
            }
            case "mp" -> {
                double mp = mana(player);
                player.sendMessage(String.format(Locale.ROOT, "§bMP: §f%.0f §7/ §f%.0f", mp, maxMana(player)));
            }
            default -> usage(player);
        }
        return true;
    }

    private void usage(Player player) {
        player.sendMessage("§6=== Skills ===");
        player.sendMessage("§e/skills §f- open the skill menu");
        player.sendMessage("§7/askill use <skill> §f- cast a skill");
        player.sendMessage("§7/askill mp §f- show mana");
    }

    private void listSkills(Player player) {
        int level = ProgressionManager.getInstance().getProgression(player.getUniqueId()).getLevel();
        player.sendMessage("§6=== Skills (Lv " + level + ") ===");
        for (SkillDef def : SKILLS.values()) {
            boolean ok = meetsReqs(player, def);
            String status = ok ? "§a" : "§8";
            player.sendMessage(status + def.name() + " §7- " + def.mpCost() + " MP, "
                    + def.cooldownSec() + "s cd" + (ok ? "" : " §8[locked]"));
        }
    }

    // ── Persistence ─────────────────────────────────────────────────

    private File file() {
        return new File("data/Aincrad/skills.yml");
    }

    public void load() {
        File f = file();
        if (!f.exists()) return;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection players = y.getConfigurationSection("players");
        if (players == null) return;
        for (String key : players.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                continue;
            }
            SkillState st = STATES.computeIfAbsent(uuid, k -> new SkillState());
            ConfigurationSection ps = players.getConfigurationSection(key);
            if (ps == null) continue;
            ConfigurationSection ranks = ps.getConfigurationSection("ranks");
            if (ranks != null) {
                for (String k : ranks.getKeys(false)) st.ranks.put(k, ranks.getInt(k));
            }
            ConfigurationSection uses = ps.getConfigurationSection("uses");
            if (uses != null) {
                for (String k : uses.getKeys(false)) st.uses.put(k, uses.getInt(k));
            }
            st.unlocked.addAll(ps.getStringList("unlocked"));
            List<String> slots = ps.getStringList("slots");
            for (int i = 0; i < Math.min(SLOT_COUNT, slots.size()); i++) {
                String v = slots.get(i);
                st.slots[i] = (v == null || v.isBlank() || v.equals("~")) ? null : v;
            }
        }
        plugin.getLogger().info("Loaded skill state for " + STATES.size() + " players.");
    }

    public void saveAll() {
        YamlConfiguration y = new YamlConfiguration();
        for (Map.Entry<UUID, SkillState> e : STATES.entrySet()) {
            String base = "players." + e.getKey();
            SkillState st = e.getValue();
            for (Map.Entry<String, Integer> r : st.ranks.entrySet()) {
                y.set(base + ".ranks." + r.getKey(), r.getValue());
            }
            for (Map.Entry<String, Integer> u : st.uses.entrySet()) {
                y.set(base + ".uses." + u.getKey(), u.getValue());
            }
            y.set(base + ".unlocked", new ArrayList<>(st.unlocked));
            List<String> slots = new ArrayList<>();
            for (String s : st.slots) slots.add(s == null ? "~" : s);
            y.set(base + ".slots", slots);
        }
        File f = file();
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        try {
            y.save(f);
        } catch (IOException ex) {
            plugin.getLogger().warning("Failed to save skills.yml: " + ex.getMessage());
        }
    }

    public static final class ParticleNames {
        static Particle of(String... names) {
            for (String name : names) {
                try {
                    return Particle.valueOf(name);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return Particle.END_ROD;
        }
    }
}
