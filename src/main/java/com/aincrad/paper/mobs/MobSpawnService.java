package com.aincrad.paper.mobs;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.items.ItemDefs;
import com.aincrad.paper.listener.ItemMechanicListener;
import com.aincrad.paper.progression.ProgressionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobSpawnService implements Listener {

    public static volatile boolean suppressSpawnEvents = false;

    public static final NamespacedKey KEY_MOB = new NamespacedKey("aincrad", "mob");
    public static final NamespacedKey KEY_MOB_LEVEL = ItemMechanicListener.KEY_MOB_LEVEL;
    public static final NamespacedKey KEY_MOB_FLOOR = new NamespacedKey("aincrad", "mob_floor");

    private record Spec(String id, String displayName, EntityTypeFallback type, int level, double hp,
                        double speed, double weight) {
    }

    public record EntityTypeFallback(String... runtimeNames) {
        public EntityType resolve() {
            for (String name : runtimeNames) {
                try {
                    return EntityType.valueOf(name);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return EntityType.ZOMBIE;
        }
    }

    private static final List<Spec> SPECS = new ArrayList<>();

    private static void add(String id, String display, EntityTypeFallback type, int level, double hp,
                            double speed, double weight, int floor) {
        SPECS.add(new Spec(id + "@f" + floor, display, type, level, hp, speed, weight));
    }

    static {
        add("frenzy_boar", "Frenzy Boar", new EntityTypeFallback("PIGLIN_ZOMBIFIED", "ZOMBIFIED_PIGLIN"),
                2, 26, 1.20, 30, 1);
        add("dire_wolf", "Dire Wolf", new EntityTypeFallback("WOLF"), 3, 30, 1.35, 25, 1);
        add("fury_cow", "Berserk Cow", new EntityTypeFallback("COW"), 2, 34, 1.3, 22, 1);
        add("blood_fang_pig", "Blood-Fang Pig", new EntityTypeFallback("PIG"), 2, 28, 1.4, 20, 1);
        add("razor_hen", "Razor Hen", new EntityTypeFallback("CHICKEN"), 1, 14, 1.7, 16, 1);
        add("mad_fleece", "Mad Fleece", new EntityTypeFallback("SHEEP"), 2, 26, 1.35, 14, 1);
        add("poisoned_fang", "Poisoned Fang Spider", new EntityTypeFallback("SPIDER"), 3, 24, 1.15, 25, 1);
        add("bandit_archer", "Bandit Archer", new EntityTypeFallback("SKELETON"), 4, 22, 1.0, 15, 1);

        add("crag_stalker", "Crag Stalker", new EntityTypeFallback("CAVE_SPIDER"), 7, 48, 1.2, 30, 2);
        add("timber_wraith", "Timber Wraith", new EntityTypeFallback("ZOMBIE"), 8, 60, 0.95, 30, 2);
        add("frenzied_panther", "Frenzied Panther", new EntityTypeFallback("WOLF"), 9, 55, 1.45, 20, 2);
        add("swarm_lancer", "Swarm Lancer", new EntityTypeFallback("SILVERFISH"), 6, 18, 1.5, 12, 2);

        add("hollow_knight", "Hollow Knight", new EntityTypeFallback("ZOMBIE"), 12, 95, 1.0, 30, 3);
        add("mason_wasp", "Mason Wasp", new EntityTypeFallback("BEE"), 11, 40, 1.5, 25, 3);
        add("venom_fang", "Venom Fang", new EntityTypeFallback("CAVE_SPIDER"), 13, 85, 1.25, 25, 3);

        add("feral_tiger", "Feral Tiger", new EntityTypeFallback("SPIDER"), 15, 110, 1.3, 30, 4);
        add("cursed_revenant", "Cursed Revenant", new EntityTypeFallback("ZOMBIE"), 16, 130, 0.95, 25, 4);
        add("hex_witch", "Hex Witch", new EntityTypeFallback("WITCH"), 15, 90, 1.0, 20, 4);

        add("night_raider", "Night Raider", new EntityTypeFallback("ZOMBIE"), 19, 155, 1.1, 30, 5);
        add("ghost_blade", "Ghost Blade Emissary", new EntityTypeFallback("WITCH"), 20, 140, 1.05, 20, 5);
        add("shadow_weaver", "Shadow Weaver", new EntityTypeFallback("ENDERMITE"), 18, 100, 1.55, 15, 5);
    }

    private final AincradPaperPlugin plugin;
    private final Random random = new Random();

    public MobSpawnService(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 160L, 100L);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::furyTick, 40L, 3L);
        plugin.getLogger().info("MobSpawnService online with " + SPECS.size() + " mob definitions.");
    }

    public boolean spawnNearPlayer(Player player, int floor) {
        Spec spec = pickSpec(floor);
        if (spec == null) return false;
        Location loc = findSpawnLocation(player.getWorld(), player.getLocation());
        if (loc == null) return false;
        return spawnSpec(spec, loc) > 0;
    }

    public int spawnRandomAt(Location loc, int floor) {
        Spec spec = pickSpec(Math.max(1, floor));
        return spec == null ? -1 : spawnSpec(spec, loc);
    }

    public void spawnGuardian(Location loc, int floor) {
        Spec spec = pickSpec(Math.max(1, floor));
        if (spec == null) return;
        suppressSpawnEvents = true;
        try {
            if (!(loc.getWorld().spawnEntity(loc, spec.type().resolve()) instanceof LivingEntity g)) return;
            g.customName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                    .legacyAmpersand().deserialize("&4&l☠ Floor " + floor + " Guardian &r"));
            g.setCustomNameVisible(true);
            Attribute mh = resolveAttribute(new String[]{"generic.max_health", "max_health"});
            if (mh != null && g.getAttribute(mh) != null) {
                g.getAttribute(mh).setBaseValue(spec.hp() * 6.0);
                g.setHealth(g.getAttribute(mh).getValue());
            }
        } finally {
            suppressSpawnEvents = false;
        }
    }

    public int spawnMob(Player player, String specId, Location loc) {
        Spec spec = SPECS.stream().filter(s -> s.id().equals(specId)).findFirst().orElse(null);
        if (spec == null) return -1;
        return spawnSpec(spec, loc);
    }

    private void furyTick() {
        for (World world : plugin.getServer().getWorlds()) {
            if (!world.getName().toLowerCase().contains("floor_")) continue;
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof org.bukkit.entity.Creature creature)) continue;
                var pdc = creature.getPersistentDataContainer();
                String id = pdc.get(KEY_MOB, org.bukkit.persistence.PersistentDataType.STRING);
                if (id == null || !(id.startsWith("fury") || id.contains("blood_fang")
                        || id.contains("razor_hen") || id.contains("mad_fleece"))) {
                    continue;
                }
                int level = pdc.get(KEY_MOB_LEVEL, org.bukkit.persistence.PersistentDataType.INTEGER) != null
                        ? pdc.get(KEY_MOB_LEVEL, org.bukkit.persistence.PersistentDataType.INTEGER) : 1;
                Player target = nearestPlayer(creature.getLocation(), 22);
                if (target == null) continue;
                org.bukkit.util.Vector to = target.getLocation().toVector()
                        .subtract(creature.getLocation().toVector());
                double dist = to.length();
                to.setY(0).normalize();
                double speed = 0.28 + Math.min(0.2, level * 0.015);
                org.bukkit.util.Vector v = to.multiply(speed);
                v.setY(creature.isOnGround() && random.nextInt(9) == 0 ? 0.42 : 0.04);
                creature.setVelocity(v);
                org.bukkit.Location d = target.getLocation();
                creature.setRotation((float) (Math.toDegrees(Math.atan2(
                        d.getZ() - creature.getLocation().getZ(),
                        d.getX() - creature.getLocation().getX())) - 90), 0);
                if (dist < 1.9 && creature.getTicksLived() % 8 == 0) {
                    target.damage(1.6 + level * 0.35, creature);
                }
            }
        }
    }

    private Player nearestPlayer(Location loc, double radius) {
        Player best = null;
        double bestD = radius * radius;
        for (Player p : loc.getWorld().getPlayers()) {
            if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.CREATIVE) continue;
            double d = p.getLocation().distanceSquared(loc);
            if (d < bestD) {
                bestD = d;
                best = p;
            }
        }
        return best;
    }

    private void tick() {
        for (World world : plugin.getServer().getWorlds()) {
            String name = world.getName().toLowerCase();
            if (!(name.contains("floor_") || name.contains("aincrad"))) continue;
            int floor = parseFloor(name);
            List<Player> players = world.getPlayers();
            if (players.isEmpty()) continue;

            int cap = Math.min(48, 10 + players.size() * 8);
            int alive = countAincradMobs(world);
            int toSpawn = Math.min(4, cap - alive);
            for (int i = 0; i < toSpawn; i++) {
                Player anchor = players.get(random.nextInt(players.size()));
                Location loc = findSpawnLocation(world, anchor.getLocation());
                if (loc == null) continue;
                Spec spec = pickSpec(floor);
                if (spec == null) continue;
                spawnSpec(spec, loc);
            }
            cleanupFarMobs(world, players);
        }
    }

    private Spec pickSpec(int floor) {
        List<Spec> pool = SPECS.stream()
                .filter(s -> Integer.parseInt(s.id().substring(s.id().lastIndexOf("@f") + 2)) <= floor
                        && Integer.parseInt(s.id().substring(s.id().lastIndexOf("@f") + 2)) >= floor - 2)
                .toList();
        if (pool.isEmpty()) {
            pool = SPECS.stream()
                    .filter(s -> Integer.parseInt(s.id().substring(s.id().lastIndexOf("@f") + 2)) <= floor)
                    .toList();
        }
        if (pool.isEmpty()) return null;
        double total = pool.stream().mapToDouble(Spec::weight).sum();
        double roll = random.nextDouble() * total;
        for (Spec s : pool) {
            roll -= s.weight();
            if (roll <= 0) return s;
        }
        return pool.get(pool.size() - 1);
    }

    private int spawnSpec(Spec spec, Location loc) {
        Entity spawned;
        suppressSpawnEvents = true;
        try {
            spawned = loc.getWorld().spawnEntity(loc, spec.type().resolve());
        } finally {
            suppressSpawnEvents = false;
        }
        if (!(spawned instanceof LivingEntity mob)) {
            spawned.remove();
            return -1;
        }
        mob.customName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize("&cLv" + spec.level() + " &f" + spec.displayName()));
        mob.setCustomNameVisible(true);

        Attribute maxHealth = resolveAttribute(new String[]{"generic.max_health", "max_health"});
        if (maxHealth != null && mob.getAttribute(maxHealth) != null) {
            mob.getAttribute(maxHealth).setBaseValue(spec.hp);
            mob.setHealth(spec.hp);
        }
        Attribute speed = resolveAttribute(new String[]{"generic.movement_speed", "movement_speed"});
        if (speed != null && mob.getAttribute(speed) != null) {
            mob.getAttribute(speed).setBaseValue(mob.getAttribute(speed).getBaseValue() * spec.speed());
        }

        mob.getPersistentDataContainer().set(KEY_MOB, PersistentDataType.STRING, spec.id());
        mob.getPersistentDataContainer().set(KEY_MOB_LEVEL, PersistentDataType.INTEGER, spec.level());
        mob.getPersistentDataContainer().set(KEY_MOB_FLOOR, PersistentDataType.INTEGER,
                Integer.parseInt(spec.id().substring(spec.id().lastIndexOf("@f") + 2)));
        return 1;
    }

    private double baseSpeed(LivingEntity mob) {
        Attribute a = resolveAttribute(new String[]{"generic.movement_speed", "movement_speed"});
        if (a == null || mob.getAttribute(a) == null) return 0.3;
        return mob.getAttribute(a).getBaseValue();
    }

    private Attribute resolveAttribute(String[] keys) {
        for (String key : keys) {
            Attribute a = org.bukkit.Registry.ATTRIBUTE.get(org.bukkit.NamespacedKey.minecraft(key));
            if (a != null) return a;
        }
        return null;
    }

    private void setAttribute(LivingEntity mob, String[] keys, double value) {
        Attribute a = resolveAttribute(keys);
        if (a == null || mob.getAttribute(a) == null) return;
        mob.getAttribute(a).setBaseValue(value);
    }

    private Location findSpawnLocation(World world, Location anchor) {
        for (int attempt = 0; attempt < 12; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 40 + random.nextDouble() * 70;
            int x = anchor.getBlockX() + (int) (Math.cos(angle) * dist);
            int z = anchor.getBlockZ() + (int) (Math.sin(angle) * dist);
            if (Math.hypot(x, z) < 128) continue;
            int baseY = anchor.getBlockY();
            for (int y = baseY + 4; y >= baseY - 4; y--) {
                Location feet = new Location(world, x + 0.5, y, z + 0.5);
                Location below = feet.clone().subtract(0, 1, 0);
                if (feet.getBlock().getType().isSolid()) continue;
                if (feet.clone().add(0, 1, 0).getBlock().getType().isSolid()) continue;
                if (!below.getBlock().getType().isSolid()) continue;
                return feet;
            }
        }
        return null;
    }

    private int countAincradMobs(World world) {
        int count = 0;
        for (Entity e : world.getEntities()) {
            if (e.getPersistentDataContainer().has(KEY_MOB)) count++;
        }
        return count;
    }

    private void cleanupFarMobs(World world, List<Player> players) {
        for (Entity e : world.getEntities()) {
            if (!e.getPersistentDataContainer().has(KEY_MOB)) continue;
            if (Math.hypot(e.getLocation().getX(), e.getLocation().getZ()) < 122) {
                e.remove();
                continue;
            }
            boolean near = false;
            for (Player p : players) {
                if (p.getWorld() == e.getWorld() && p.getLocation().distanceSquared(e.getLocation()) < 160 * 160) {
                    near = true;
                    break;
                }
            }
            if (!near) e.remove();
        }
    }

    @EventHandler
    public void onMobHurt(org.bukkit.event.entity.EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity mob)) return;
        String id = mob.getPersistentDataContainer().get(KEY_MOB, PersistentDataType.STRING);
        if (id == null) return;
        Player attacker = event instanceof org.bukkit.event.entity.EntityDamageByEntityEvent be
                && be.getDamager() instanceof Player p ? p : null;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> updateHealthTag(mob, attacker), 1L);
    }

    private void updateHealthTag(LivingEntity mob, Player attacker) {
        if (!mob.isValid() || mob.getHealth() <= 0) return;
        Spec spec = null;
        String id = mob.getPersistentDataContainer().get(KEY_MOB, PersistentDataType.STRING);
        for (Spec s : SPECS) {
            if (s.id().equals(id)) { spec = s; break; }
        }
        double max = spec != null ? spec.hp() : 20;
        double hp = Math.max(0, mob.getHealth());
        String bar = "&c♥ " + (int) Math.ceil(hp) + " &7/ &c" + (int) max;
        String base = "&cLv" + (spec == null ? "?" : spec.level()) + " &f"
                + (spec == null ? mob.getName() : spec.displayName());
        mob.customName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize(base + " " + bar));
        if (attacker != null && attacker.isOnline()) {
            attacker.sendActionBar(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                    .legacyAmpersand().deserialize("&f" + (spec == null ? "" : spec.displayName())
                            + " &7" + (int) hp + "&7/&c" + (int) max));
        }
    }

    private void progressKillQuests(Player killer) {
        try {
            var qm = com.aincrad.paper.quest.QuestManager.getInstance();
            if (!qm.hasTracker(killer.getUniqueId())) return;
            var tracker = qm.getTracker(killer.getUniqueId());
            for (var quest : tracker.activeQuests()) {
                var objs = quest.objectives();
                for (int i = 0; i < objs.size(); i++) {
                    var o = objs.get(i);
                    if (o.getType() != com.aincrad.paper.quest.QuestObjective.Type.KILL
                            && o.getType() != com.aincrad.paper.quest.QuestObjective.Type.DEFEAT) continue;
                    if (!o.getTarget().equalsIgnoreCase("any")
                            && (mob == null || mob.getCustomName() == null
                            || !mob.getCustomName().contains(o.getTarget()))) {
                        continue;
                    }
                    if (tracker.updateProgress(quest.id(), i, 1)
                            && o.getCurrent() >= o.getRequired()) {
                        killer.sendMessage("§a✔ Quest objective complete: §f" + quest.name()
                                + " §7- report at the Guild Hall quest board");
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private transient LivingEntity mob;

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity mob = event.getEntity();
        this.mob = mob;
        String id = mob.getPersistentDataContainer().get(KEY_MOB, PersistentDataType.STRING);
        if (id == null) return;
        event.getDrops().clear();
        event.setDroppedExp(0);

        Player killer = mob.getKiller();
        if (killer == null) return;
        Integer level = mob.getPersistentDataContainer().get(KEY_MOB_LEVEL, PersistentDataType.INTEGER);
        int lvl = level == null ? 1 : level;

        double xp = 6 + lvl * 4.0;
        long col = 6L + lvl * 5L;
        ProgressionManager.getInstance().addXp(killer.getUniqueId(), xp);
        EconomyManager.getInstance().deposit(killer.getUniqueId(), new Col(col), "mob_kill:" + id);
        progressKillQuests(killer);
        killer.sendActionBar(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize("&a+" + (int) xp + " XP &7| &e+" + col + " Col"));

        var nearby = killer.getWorld().getNearbyEntities(killer.getLocation(), 40, 40, 40,
                Player.class::isInstance);
        for (var e : nearby) {
            if (!(e instanceof Player mate) || mate == killer) continue;
            ProgressionManager.getInstance().addXp(mate.getUniqueId(), xp * 0.35);
            EconomyManager.getInstance().deposit(mate.getUniqueId(), new Col(col / 3L), "mob_kill_share");
        }

        this.mob = null;
        List<String> pool = ItemDefs.idsForLevel(Math.max(1, lvl));
        if (!pool.isEmpty() && random.nextDouble() < 0.10) {
            String drop = pool.get(random.nextInt(pool.size()));
            var stack = ItemDefs.create(drop, 1);
            if (stack != null) {
                mob.getWorld().dropItemNaturally(mob.getLocation(), stack);
            }
        }
    }

    private int parseFloor(String worldName) {
        try {
            String digits = worldName.replaceAll("\\D+", "");
            if (!digits.isEmpty()) return Integer.parseInt(digits);
        } catch (NumberFormatException ignored) {
        }
        return 1;
    }
}
