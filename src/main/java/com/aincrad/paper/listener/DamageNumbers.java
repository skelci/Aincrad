package com.aincrad.paper.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageNumbers implements Listener {

    private record Shot(UUID display, double base, int prio, long until) {}

    private final Plugin plugin;
    private final Map<Integer, Shot> shots = new HashMap<>();

    public DamageNumbers(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity victim)) return;
        Player p = attacker(e.getDamager());
        if (p == null || victim.equals(p)) return;
        double dmg = e.getDamage();
        if (dmg < 0.5) return;

        int eid = victim.getEntityId();
        long now = System.currentTimeMillis();
        int prio = dmg >= 20 ? 2 : dmg >= 10 ? 1 : 0;

        Shot prev = shots.get(eid);
        boolean merge = prev != null && now - prev.until() < 700 && prio <= prev.prio();
        double shown = merge ? prev.base() + dmg : dmg;

        String color = prio == 2 ? "§6" : prio == 1 ? "§c" : "§f";
        String text = color + (prio == 2 ? "✦ " : "") + fmt(shown);

        Location loc = victim.getEyeLocation().add(0, 0.05, 0)
                .add((Math.random() - 0.5) * 0.9, Math.random() * 0.3, (Math.random() - 0.5) * 0.9);
        Vector v = victim.getVelocity();
        if (v.lengthSquared() > 0.25) v.normalize().multiply(0.22); else v = new Vector(0, 0, 0);

        TextDisplay td = null;
        if (merge) {
            Entity ent = Bukkit.getEntity(prev.display());
            if (ent instanceof TextDisplay existing && !existing.isDead()) td = existing;
        }
        boolean fresh = td == null;
        if (fresh) {
            td = victim.getWorld().spawn(loc, TextDisplay.class, d -> {
                d.setBillboard(Display.Billboard.CENTER);
                d.setTransformation(new Transformation(
                        new Vector3f(-0.5f, 0f, 0f), new Quaternionf(),
                        new Vector3f(1.15f), new Quaternionf()));
                d.setText(text);
                d.setPersistent(true);
            });
            td.setTeleportDuration(5);
        } else {
            td.setText(text);
        }
        td.teleport(loc.clone().add(v).add(0, fresh ? 0.65 : 0.25, 0));

        final UUID id = td.getUniqueId();
        shots.put(eid, new Shot(id, shown, prio, now));
        if (shots.size() > 400) shots.entrySet().removeIf(en -> now > en.getValue().until());
        if (fresh) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Entity ent = Bukkit.getEntity(id);
                if (ent != null) ent.remove();
                Shot s = shots.get(eid);
                if (s != null && s.display().equals(id)) shots.remove(eid);
            }, 26L);
        }
    }

    private static Player attacker(Entity damager) {
        if (damager instanceof Player p) return p;
        if (damager instanceof Projectile pr && pr.getShooter() instanceof Player sp) return sp;
        return null;
    }

    private static String fmt(double d) {
        return d >= 100 ? String.valueOf((int) d) : String.format("%.1f", d);
    }
}
