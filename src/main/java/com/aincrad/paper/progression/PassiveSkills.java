package com.aincrad.paper.progression;

import com.aincrad.paper.AincradPaperPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.UUID;

/** Always-on passive perks: HP regen, MP regen, movement speed. */
public final class PassiveSkills {

    private static final UUID SPEED_MOD_ID =
            UUID.fromString("0a1ac000-0000-4000-8000-000000000002");

    private PassiveSkills() {
    }

    public static double hpRegenPerTwoSeconds(int level) {
        return 0.5 + level * 0.02;
    }

    public static double mpRegenPerSecond(int level) {
        return 0.012 + level * 0.0002;
    }

    public static double speedBonus(int level) {
        return level * 0.0005;
    }

    public static void start(AincradPaperPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.isDead()) continue;
                Double max = ProgressionManager.currentMaxHealth(p);
                if (max == null) continue;
                int level = ProgressionManager.getInstance()
                        .getProgression(p.getUniqueId()).getLevel();
                syncSpeed(p);
                double next = p.getHealth() + hpRegenPerTwoSeconds(level);
                if (next > max) next = max;
                p.setHealth(next);
            }
        }, 40L, 40L);
    }

    public static void syncSpeed(Player player) {
        Attribute speed = null;
        for (String key : new String[]{"movement_speed", "generic.movement_speed", "speed"}) {
            speed = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(key));
            if (speed != null) break;
        }
        if (speed == null) return;
        AttributeInstance inst = player.getAttribute(speed);
        if (inst == null) return;
        for (AttributeModifier m : inst.getModifiers()) {
            if (SPEED_MOD_ID.equals(m.getUniqueId())) inst.removeModifier(m);
        }
        int level = ProgressionManager.getInstance()
                .getProgression(player.getUniqueId()).getLevel();
        double bonus = speedBonus(level);
        if (bonus > 0) {
            inst.addModifier(new AttributeModifier(SPEED_MOD_ID, "aincrad:speed", bonus,
                    AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        }
    }
}
