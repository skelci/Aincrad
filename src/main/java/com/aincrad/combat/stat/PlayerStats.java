package com.aincrad.combat.stat;

import java.util.EnumMap;
import java.util.Map;

/**
 * Holds a player's current stat values.
 * Base stats are stored directly; derived stats are recalculated via {@link StatCalculator}.
 */
public class PlayerStats {

    private final Map<StatType, Double> baseStats = new EnumMap<>(StatType.class);
    private final Map<StatType, Double> derivedStats = new EnumMap<>(StatType.class);

    public PlayerStats() {
        // sensible defaults so no stat is ever null
        for (StatType type : StatType.values()) {
            baseStats.put(type, 0.0);
            derivedStats.put(type, 0.0);
        }
    }

    // ---- Base stats ----

    public double getBase(StatType type) {
        return baseStats.getOrDefault(type, 0.0);
    }

    public void setBase(StatType type, double value) {
        baseStats.put(type, value);
    }

    public void modifyBase(StatType type, double delta) {
        baseStats.merge(type, delta, Double::sum);
    }

    // ---- Derived stats ----

    public double getDerived(StatType type) {
        return derivedStats.getOrDefault(type, 0.0);
    }

    public void setDerived(StatType type, double value) {
        derivedStats.put(type, value);
    }

    // ---- Effective (base + derived) ----

    public double get(StatType type) {
        return getBase(type) + getDerived(type);
    }

    /**
     * Recalculates all derived stats from current base stats.
     * Called after any base stat change (level-up, equipment, buffs).
     */
    public void recalculate() {
        for (StatType type : StatType.values()) {
            double value = StatCalculator.calculate(type, this);
            derivedStats.put(type, value);
        }
    }

    /** Returns a snapshot copy of all effective stats. */
    public Map<StatType, Double> snapshot() {
        Map<StatType, Double> all = new EnumMap<>(StatType.class);
        for (StatType type : StatType.values()) {
            all.put(type, get(type));
        }
        return all;
    }
}
