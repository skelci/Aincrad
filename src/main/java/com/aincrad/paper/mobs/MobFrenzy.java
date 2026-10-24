package com.aincrad.paper.mobs;

import com.aincrad.paper.AincradPaperPlugin;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Floor wildlife outside town is hostile: mobs retarget players, and even
 * animals charge. Town area (radius 80 from 0,0) stays peaceful.
 */
public final class MobFrenzy {

    private final AincradPaperPlugin plugin;
    private final Map<UUID, Long> lastHit = new HashMap<>();

    public MobFrenzy(AincradPaperPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 60L, 10L);
    }

    private void tick() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (!AincradPaperPlugin.isFloorWorld(p.getWorld())) continue;
            Location c = p.getLocation();
            for (Entity e : p.getWorld().getNearbyEntities(c, 16, 12, 16)) {
                if (!(e instanceof LivingEntity le) || le.getHealth() <= 0 || le == p) continue;
                if (le.getPersistentDataContainer().get(MobSpawnService.KEY_MOB,
                        PersistentDataType.STRING) == null) continue;
                Location l = le.getLocation();
                if (Math.hypot(l.getX(), l.getZ()) < 80) continue;
                if (le instanceof Mob mob) {
                    if (mob.getTarget() != p) mob.setTarget(p);
                    continue;
                }
                Vector v = c.toVector().subtract(l.toVector());
                double d = v.length();
                if (d < 14 && d > 1.3) {
                    v.setY(0).normalize().multiply(0.32);
                    le.setVelocity(v);
                }
                if (d < 1.8) {
                    long now = System.currentTimeMillis();
                    Long last = lastHit.get(le.getUniqueId());
                    if (last == null || now - last > 1200) {
                        lastHit.put(le.getUniqueId(), now);
                        p.damage(2.5, le);
                        le.getWorld().playSound(l, Sound.ENTITY_PLAYER_HURT, 0.5f, 1.5f);
                    }
                }
            }
        }
    }
}
