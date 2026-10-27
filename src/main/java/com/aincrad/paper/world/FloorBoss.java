package com.aincrad.paper.world;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.mobs.MobSpawnService;
import com.aincrad.paper.progression.ProgressionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Ravager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Floor boss: The ILFANG. Arena pad far north of town; stepping on the pad
 * summons it. First kill on each floor unlocks the next floor.
 */
public final class FloorBoss implements Listener {

    public static final int ARENA_X = 0, ARENA_Z = -700;
    private static final long SUMMON_COOLDOWN_MS = 30 * 60 * 1000L;
    private static final double BOSS_HP = 640;

    private final AincradPaperPlugin plugin;
    private final Map<Integer, Long> lastSummon = new HashMap<>();
    private final Set<UUID> participants = new HashSet<>();
    private LivingEntity boss;
    private BossBar bar;
    private int bossFloor = 1;
    private boolean phase2;
    private org.bukkit.scheduler.BukkitTask patternTask;
    private int tickCount;
    private int lastPhase;
    private com.aincrad.paper.mobs.MobSpawnService mobService;

    public void setMobService(com.aincrad.paper.mobs.MobSpawnService service) {
        this.mobService = service;
    }

    public FloorBoss(AincradPaperPlugin plugin) {
        this.plugin = plugin;
    }

    static Attribute maxHealthAttr() {
        for (String name : new String[]{"GENERIC_MAX_HEALTH", "MAX_HEALTH"}) {
            try {
                return Attribute.valueOf(name);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    public void buildArena() {
        World w = floorWorld(1);
        if (w == null) return;
        w.getChunkAt(ARENA_X, ARENA_Z).load(true);
        int g = TownKit.G;
        for (int dx = -14; dx <= 14; dx++) {
            for (int dz = -14; dz <= 14; dz++) {
                int r = Math.max(Math.abs(dx), Math.abs(dz));
                for (int y = 1; y <= 9; y++) {
                    w.getBlockAt(ARENA_X + dx, g + y, ARENA_Z + dz).setType(Material.AIR, false);
                }
                if (r == 14) {
                    set(w, ARENA_X + dx, g, ARENA_Z + dz, Material.CRYING_OBSIDIAN);
                } else if (r == 0) {
                    set(w, ARENA_X + dx, g, ARENA_Z + dz, Material.RED_NETHER_BRICKS);
                } else {
                    set(w, ARENA_X + dx, g, ARENA_Z + dz, r % 2 == 0
                            ? Material.NETHER_BRICKS : Material.CRACKED_NETHER_BRICKS);
                }
            }
        }
        for (int dx = -10; dx <= 10; dx += 20) {
            for (int dz = -10; dz <= 10; dz += 20) {
                for (int y = 1; y <= 7; y++) {
                    set(w, ARENA_X + dx, g + y, ARENA_Z + dz, Material.OBSIDIAN);
                }
                set(w, ARENA_X + dx, g + 8, ARENA_Z + dz, Material.CANDLE);
            }
        }
        set(w, ARENA_X - 1, g, ARENA_Z + 15, Material.GILDED_BLACKSTONE);
        set(w, ARENA_X, g, ARENA_Z + 15, Material.GILDED_BLACKSTONE);
        set(w, ARENA_X + 1, g, ARENA_Z + 15, Material.GILDED_BLACKSTONE);
        set(w, ARENA_X, g + 1, ARENA_Z + 15, Material.SOUL_FIRE);
    }

    private World floorWorld(int floor) {
        for (World w : plugin.getServer().getWorlds()) {
            if (AincradPaperPlugin.isFloor(floor, w)) return w;
        }
        return null;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null || (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())) {
            return;
        }
        Player player = event.getPlayer();
        if (!AincradPaperPlugin.isFloor(1, player.getWorld())) return;
        if (Math.hypot(player.getX() - ARENA_X, player.getZ() - ARENA_Z) > 13) return;
        if (boss != null && !boss.isDead()) return;
        Long last = lastSummon.get(1);
        long now = System.currentTimeMillis();
        if (last != null && now - last < SUMMON_COOLDOWN_MS) return;
        lastSummon.put(1, now);
        summon(player.getWorld(), 1, player.getLocation());
    }

    public void summon(World w, int floor, Location near) {
        bossFloor = floor;
        phase2 = false;
        Location loc = near.clone().add(0, 1, 5);
        MobSpawnService.suppressSpawnEvents = true;
        try {
            EntityType type = new MobSpawnService.EntityTypeFallback(
                    "PILFERING_HOE", "RAVAGER", "SILVERFISH").resolve();
            if (!(w.spawnEntity(loc, type) instanceof LivingEntity ent)) return;
            boss = ent;
            ent.setCustomNameVisible(true);
            ent.customName(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&4&l☠ The ILFANG &cFloor " + floor + " Boss"));
            Attribute maxHp = maxHealthAttr();
            if (maxHp != null && ent.getAttribute(maxHp) != null) {
                ent.getAttribute(maxHp).setBaseValue(BOSS_HP * floor);
                ent.setHealth(ent.getAttribute(maxHp).getValue());
            }
            ent.getPersistentDataContainer().set(
                    new org.bukkit.NamespacedKey("aincrad", "boss_floor"),
                    PersistentDataType.INTEGER, floor);
        } finally {
            MobSpawnService.suppressSpawnEvents = false;
        }
        bar = Bukkit.createBossBar("§4§l☠ The ILFANG", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1.0);
        for (Player p : w.getPlayers()) {
            bar.addPlayer(p);
        }
        participants.clear();
        broadcast(w, "§4§l☠ The ILFANG awakens! §fFirst kill unlocks §bFloor 2§f.");
        w.playSound(loc, org.bukkit.Sound.ENTITY_WITHER_SPAWN, 1.2f, 0.7f);
        w.playSound(loc, org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.9f, 0.75f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (boss != null) {
                boss.getWorld().playSound(boss.getLocation(),
                        org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 0.85f);
            }
        }, 12L);
        customize();
        startPatterns();
    }

    /** Visual identity: saddle, larger scale, glow, ember aura. */
    private void customize() {
        try {
            Ravager.class.getMethod("setSaddle", boolean.class).invoke(boss, true);
        } catch (Throwable ignored) {
        }
        try {
            boss.setGlowing(true);
        } catch (Throwable ignored) {
        }
        for (String name : new String[]{"GENERIC_SCALE", "SCALE", "GENERIC_SIZE"}) {
            try {
                var a = Attribute.valueOf(name);
                if (boss.getAttribute(a) != null) {
                    boss.getAttribute(a).setBaseValue(1.35);
                    break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private Player nearestPlayer() {
        if (boss == null) return null;
        Player best = null;
        double bd = Double.MAX_VALUE;
        for (Player p : boss.getWorld().getPlayers()) {
            if (!p.isOnline() || p.isDead()) continue;
            double d = p.getLocation().distanceSquared(boss.getLocation());
            if (d < bd) {
                bd = d;
                best = p;
            }
        }
        return best;
    }

    private void startPatterns() {
        tickCount = 0;
        lastPhase = 0;
        if (patternTask != null) {
            patternTask.cancel();
        }
        patternTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (boss == null || boss.isDead()) {
                if (patternTask != null) patternTask.cancel();
                patternTask = null;
                return;
            }
            tickCount++;
            embers();
            double frac = hpFraction();
            int phase = frac <= 0.3 ? 3 : frac <= 0.6 ? 2 : 1;
            if (phase > lastPhase) {
                lastPhase = phase;
                BossFxNet.send(plugin, boss.getLocation(), BossFxNet.PHASE, 1f);
                boss.getWorld().spawnParticle(org.bukkit.Particle.FLASH, boss.getLocation(), 1);
                if (phase == 2) {
                    announce("§c§l☠ \"You leave me no choice...\"",
                            org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 0.8f);
                    buff(PotionEffectType.SPEED, 6000, 1);
                    sfxDelay(org.bukkit.Sound.ENTITY_WARDEN_EMERGE, 0.7f, 0.75f, 8);
                    sfxDelay(org.bukkit.Sound.ENTITY_WARDEN_HEARTBEAT, 1f, 0.9f, 18);
                } else if (phase == 3) {
                    announce("§4§l☠ \"THIS IS MY FOREST!!\"",
                            org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f);
                    buff(PotionEffectType.RESISTANCE, 6000, 1);
                    sfxDelay(org.bukkit.Sound.ENTITY_WARDEN_ROAR, 0.9f, 0.85f, 4);
                    sfxDelay(org.bukkit.Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.15f, 12);
                    if (mobService != null) {
                        for (int i = 0; i < 3; i++) {
                            mobService.spawnDungeonMob(boss.getLocation().add(2 - i * 2, 0, 2), 2);
                        }
                    }
                }
            }
            int cycle = phase == 3 ? 2 : 3;
            if (tickCount % cycle != 0) return;
            switch (tickCount / cycle % 4) {
                case 0 -> cobaltLunge();
                case 1 -> daggerSweep(phase);
                case 2 -> terrorWave();
                default -> {
                    if (phase >= 2) axiomSweep();
                    else blinkStrike();
                }
            }
        }, 40L, 20L);
    }

    private double hpFraction() {
        Attribute maxHp = maxHealthAttr();
        if (maxHp == null || boss.getAttribute(maxHp) == null) return 1;
        return boss.getHealth() / boss.getAttribute(maxHp).getValue();
    }

    private void buff(PotionEffectType type, int ticks, int amp) {
        try {
            boss.addPotionEffect(new PotionEffect(type, ticks, amp, true, false, false));
        } catch (Throwable ignored) {
        }
    }

    private void announce(String msg, org.bukkit.Sound sound, float pitch) {
        broadcast(boss.getWorld(), msg);
        boss.getWorld().playSound(boss.getLocation(), sound, 1f, pitch);
        for (Player p : boss.getWorld().getPlayers()) {
            p.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
        }
    }

    private void sfxNow(org.bukkit.Sound s, float vol, float pitch) {
        if (boss != null) {
            boss.getWorld().playSound(boss.getLocation(), s, vol, pitch);
        }
    }

    private void sfxDelay(org.bukkit.Sound s, float vol, float pitch, int delayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> sfxNow(s, vol, pitch), delayTicks);
    }

    private void embers() {
        boss.getWorld().spawnParticle(org.bukkit.Particle.SOUL_FIRE_FLAME,
                boss.getLocation().clone().add(0, 1.6, 0), 6, 0.9, 0.7, 0.9, 0.01);
        if (tickCount % 9 == 0) {
            boss.getWorld().playSound(boss.getLocation(),
                    org.bukkit.Sound.ENTITY_RAVAGER_AMBIENT, 0.45f, 0.6f);
        } else if (tickCount % 13 == 0) {
            boss.getWorld().playSound(boss.getLocation(),
                    org.bukkit.Sound.ENTITY_EVOKER_AMBIENT, 0.3f, 0.55f);
        }
    }

    /** Rush at the nearest player with a cleave on arrival. */
    private void cobaltLunge() {
        Player t = nearestPlayer();
        if (t == null) return;
        BossFxNet.send(plugin, boss.getLocation(), BossFxNet.LUNGE, 1f);
        boss.getWorld().playSound(boss.getLocation(), org.bukkit.Sound.ENTITY_RAVAGER_ROAR, 1f, 1.3f);
        sfxNow(org.bukkit.Sound.ENTITY_WARDEN_SONIC_CHARGE, 0.6f, 1.3f);
        sfxDelay(org.bukkit.Sound.ENTITY_BREEZE_WIND_BURST, 0.5f, 0.9f, 4);
        boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 3, true, false, false));
        Vector dir = t.getLocation().toVector().subtract(boss.getLocation().toVector());
        dir.setY(0.35).normalize();
        boss.setVelocity(dir.multiply(1.4));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (boss == null || boss.isDead()) return;
            for (Player p : boss.getWorld().getPlayers()) {
                if (p.getLocation().distanceSquared(boss.getLocation()) < 16) {
                    p.damage(7, boss);
                    Vector kb = p.getLocation().toVector().subtract(boss.getLocation().toVector());
                    p.setVelocity(kb.normalize().multiply(1.1).setY(0.5));
                }
            }
            boss.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION, boss.getLocation(), 3, 1, 0.5, 1, 0);
            boss.getWorld().playSound(boss.getLocation(), org.bukkit.Sound.ENTITY_RAVAGER_ATTACK, 1f, 0.8f);
            sfxDelay(org.bukkit.Sound.ENTITY_WARDEN_ATTACK_IMPACT, 0.9f, 1.1f, 1);
        }, 14L);
    }

    /** Fan of spectral daggers fired toward the target. */
    private void daggerSweep(int phase) {
        Player t = nearestPlayer();
        if (t == null) return;
        BossFxNet.send(plugin, boss.getLocation(), BossFxNet.SWEEP, 1f);
        sfxNow(org.bukkit.Sound.ENTITY_EVOKER_CAST_SPELL, 0.7f, 1.05f);
        sfxNow(org.bukkit.Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.5f, 1.15f);
        Location muzzle = boss.getLocation().clone().add(0, 1.8, 0);
        Vector base = t.getLocation().add(0, 1, 0).toVector().subtract(muzzle.toVector()).normalize();
        int shots = phase >= 3 ? 7 : 5;
        for (int i = 0; i < shots; i++) {
            double yaw = (i - (shots - 1) / 2.0) * 0.16;
            double cos = Math.cos(yaw), sin = Math.sin(yaw);
            double bx = base.getX(), bz = base.getZ();
            Vector v = new Vector(bx * cos - bz * sin, base.getY(), bx * sin + bz * cos).normalize();
            Arrow a = boss.getWorld().spawn(muzzle, Arrow.class);
            a.setVelocity(v.multiply(1.6));
            a.setShooter(boss);
            a.setCritical(phase >= 3);
            a.setMetadata("ilfang_dagger", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        }
        boss.getWorld().playSound(muzzle, org.bukkit.Sound.ENTITY_ARROW_SHOOT, 1f, 0.6f);
        boss.getWorld().playSound(muzzle, org.bukkit.Sound.ENTITY_SKELETON_SHOOT, 0.6f, 1.7f);
    }

    /** A wave of dread: blindness and slowness near the boss. */
    private void terrorWave() {
        BossFxNet.send(plugin, boss.getLocation(), BossFxNet.TERROR, 1f);
        boss.getWorld().playSound(boss.getLocation(), org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 0.9f, 1.1f);
        boss.getWorld().playSound(boss.getLocation(),
                org.bukkit.Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.8f, 0.8f);
        sfxDelay(org.bukkit.Sound.ENTITY_WARDEN_HEARTBEAT, 1f, 0.85f, 6);
        sfxDelay(org.bukkit.Sound.ENTITY_GHAST_SCREAM, 0.55f, 0.65f, 12);
        boss.getWorld().spawnParticle(org.bukkit.Particle.SMOKE, boss.getLocation(), 40, 3, 1.5, 3, 0.02);
        for (Player p : boss.getWorld().getPlayers()) {
            if (p.getLocation().distance(boss.getLocation()) < 12) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1, true, false, false));
            }
        }
    }

    /** Expanding ground sweep — jump over it or get out. */
    private void axiomSweep() {
        BossFxNet.send(plugin, boss.getLocation(), BossFxNet.AXIOM, 1f);
        boss.getWorld().playSound(boss.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_LAND, 0.8f, 1.4f);
        sfxNow(org.bukkit.Sound.ENTITY_EVOKER_PREPARE_ATTACK, 0.7f, 0.95f);
        sfxDelay(org.bukkit.Sound.ENTITY_WITHER_BREAK_BLOCK, 0.5f, 0.9f, 8);
        sfxDelay(org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 0.4f, 0.55f, 9);
        Location c = boss.getLocation();
        for (int i = 0; i < 48; i++) {
            double ang = i * (Math.PI * 2 / 48);
            for (double r = 3; r <= 9; r += 2) {
                boss.getWorld().spawnParticle(org.bukkit.Particle.DUST,
                        c.clone().add(Math.cos(ang) * r, 0.15, Math.sin(ang) * r),
                        1, 0, 0, 0, 0,
                        new org.bukkit.Particle.DustOptions(org.bukkit.Color.fromRGB(0xB22222), 1.1f));
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (boss == null || boss.isDead()) return;
            for (Player p : boss.getWorld().getPlayers()) {
                double d = p.getLocation().distance(c);
                boolean low = p.getLocation().getY() <= c.getY() + 1.05 && p.isOnGround();
                if (d < 9.5 && d > 2 && low) {
                    p.damage(6, boss);
                    Vector kb = p.getLocation().toVector().subtract(c.toVector());
                    p.setVelocity(kb.normalize().multiply(1.3).setY(0.9));
                }
            }
        }, 10L);
    }

    /** Short-range blink onto a player. */
    private void blinkStrike() {
        Player t = nearestPlayer();
        if (t == null) return;
        BossFxNet.send(plugin, boss.getLocation(), BossFxNet.BLINK, 1f);
        Location to = t.getLocation().add(0, 0, 1.5);
        boss.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, boss.getLocation(), 30, 0.5, 1, 0.5, 0.2);
        boss.teleport(to);
        boss.getWorld().playSound(to, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.6f);
        sfxNow(org.bukkit.Sound.ENTITY_SHULKER_TELEPORT, 0.6f, 0.8f);
        sfxDelay(org.bukkit.Sound.ENTITY_BREEZE_SHOOT, 0.8f, 1.0f, 1);
        if (t.getLocation().distanceSquared(boss.getLocation()) < 9) {
            t.damage(5, boss);
        }
    }

    @EventHandler
    public void onDaggerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)
                || !arrow.hasMetadata("ilfang_dagger")) return;
        event.setDamage(event.getDamage() + 4);
        event.getEntity().getWorld().spawnParticle(org.bukkit.Particle.CRIT,
                event.getEntity().getLocation(), 8, 0.3, 0.5, 0.3, 0.1);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (boss == null || event.getEntity() != boss) return;
        if (event instanceof EntityDamageByEntityEvent byEnt
                && byEnt.getDamager() instanceof Player p) {
            participants.add(p.getUniqueId());
        }
        Attribute maxHp = maxHealthAttr();
        if (bar != null && maxHp != null && boss.getAttribute(maxHp) != null) {
            double max = boss.getAttribute(maxHp).getValue();
            bar.setProgress((float) Math.max(0, boss.getHealth() / max));
            for (Player p : boss.getWorld().getPlayers()) {
                bar.addPlayer(p);
            }
            if (!phase2 && boss.getHealth() - event.getFinalDamage() <= max * 0.5) {
                phase2 = true;
                broadcast(boss.getWorld(), "§cThe ILFANG calls for his kobold guard!");
                boss.getWorld().playSound(boss.getLocation(),
                        org.bukkit.Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.8f, 0.8f);
                sfxDelay(org.bukkit.Sound.ENTITY_RAVAGER_ROAR, 0.8f, 0.9f, 6);
                for (int i = 0; i < 4; i++) {
                    Location l = boss.getLocation().clone().add(
                            (i - 1.5) * 3, 0, (i % 2 == 0 ? 2 : -2));
                    if (mobService != null) {
                        mobService.spawnDungeonMob(l, bossFloor + 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (boss == null || event.getEntity() != boss) return;
        World w = boss.getWorld();
        Location dloc = boss.getLocation();
        if (event.getEntity().getKiller() != null) {
            participants.add(event.getEntity().getKiller().getUniqueId());
        }
        int floor = bossFloor;
        if (patternTask != null) {
            patternTask.cancel();
            patternTask = null;
        }
        boss = null;
        if (bar != null) {
            bar.removeAll();
            bar = null;
        }
        broadcast(w, "§6§l✦ The ILFANG has been defeated! §a+" + (2500 * floor)
                + " Col, +1000 XP for all participants.");
        w.playSound(w.getSpawnLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 0.8f);
        w.playSound(w.getSpawnLocation(), org.bukkit.Sound.ENTITY_WITHER_DEATH, 0.8f, 0.85f);
        w.playSound(dloc, org.bukkit.Sound.ENTITY_RAVAGER_DEATH, 1f, 0.75f);
        for (UUID id : participants) {
            EconomyManager.getInstance().deposit(id, new Col(2500L * floor), "boss_kill");
            ProgressionManager.getInstance().addXp(id, 1000);
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.sendMessage("§6You received §e" + (2500 * floor) + " Col §6and §e1000 XP §6from the boss.");
            }
        }
        if (floor == 1 && unlockedFloors() < 2) {
            plugin.getConfig().set("unlocked_floors", 2);
            plugin.saveConfig();
            Bukkit.broadcastMessage("§b§l==================================");
            Bukkit.broadcastMessage("§dFloor 2 of Aincrad is now §aUNLOCKED§d!"
                    + " §fUse §b/aincrad floor 2 §fto ascend.");
            Bukkit.broadcastMessage("§b§l==================================");
        }
        participants.clear();
    }

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        if (bar != null) {
            bar.removePlayer(event.getPlayer());
        }
    }

    public static int unlockedFloors() {
        AincradPaperPlugin p = AincradPaperPlugin.getInstance();
        return p == null ? 1 : Math.max(1, p.getConfig().getInt("unlocked_floors", 1));
    }

    private void broadcast(World w, String msg) {
        Component c = LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
        for (Player p : w.getPlayers()) {
            p.sendMessage(c);
        }
    }

    private void set(World w, int x, int y, int z, Material m) {
        w.getBlockAt(x, y, z).setType(m, false);
    }
}
