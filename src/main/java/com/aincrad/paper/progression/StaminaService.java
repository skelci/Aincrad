package com.aincrad.paper.progression;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

/** Separate stamina resource. It regenerates only while vanilla hunger is full. */
public final class StaminaService {

    private static final NamespacedKey KEY_STAMINA =
            new NamespacedKey("aincrad", "stamina");

    private StaminaService() {
    }

    public static double maxStamina(int level) {
        return 20.0 + ProgressionManager.statBonus(2.0, level);
    }

    public static double regenPerTwoSeconds(int level) {
        return 0.4 + level * 0.03;
    }

    public static double current(Player player) {
        int level = ProgressionManager.getInstance()
                .getProgression(player.getUniqueId()).getLevel();
        double max = maxStamina(level);
        var pdc = player.getPersistentDataContainer();
        Double value = pdc.get(KEY_STAMINA, PersistentDataType.DOUBLE);
        if (value == null) {
            value = max;
            pdc.set(KEY_STAMINA, PersistentDataType.DOUBLE, value);
        }
        if (value < 0) value = 0.0;
        if (value > max) value = max;
        return value;
    }

    public static void setCurrent(Player player, double value) {
        int level = ProgressionManager.getInstance()
                .getProgression(player.getUniqueId()).getLevel();
        double max = maxStamina(level);
        value = Math.max(0, Math.min(value, max));
        player.getPersistentDataContainer().set(KEY_STAMINA, PersistentDataType.DOUBLE, value);
    }

    public static void ensure(Player player) {
        current(player);
    }

    /** Regenerates stamina only when hunger is full, at a level-scaled rate. */
    public static void tick(Player player) {
        if (player.isDead() || player.getFoodLevel() < 20) return;
        int level = ProgressionManager.getInstance()
                .getProgression(player.getUniqueId()).getLevel();
        double max = maxStamina(level);
        double value = current(player);
        if (value >= max) return;
        setCurrent(player, value + regenPerTwoSeconds(level));
    }
}
