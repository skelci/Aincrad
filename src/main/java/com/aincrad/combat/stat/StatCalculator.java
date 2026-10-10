package com.aincrad.combat.stat;

/**
 * Pure static formulas for computing derived stats and combat values.
 * No game state is mutated — all inputs come from {@link PlayerStats}.
 */
public final class StatCalculator {

    private StatCalculator() {}

    // ---- Derived stat formulas ----

    /**
     * Calculates the derived value of a stat given the player's base stats.
     * Returns 0 for base stats (they are not derived).
     */
    public static double calculate(StatType type, PlayerStats stats) {
        return switch (type) {
            // base stats have no derived component
            case STR, AGI, VIT, DEX, INT -> 0.0;

            case CRIT_RATE       -> stats.getBase(StatType.DEX) * 0.05;   // 0.05% per DEX
            case CRIT_DAMAGE    -> 150.0 + stats.getBase(StatType.STR) * 0.5; // 150% base + 0.5% per STR
            case EVASION        -> stats.getBase(StatType.AGI) * 0.03;   // 0.03% per AGI

            case HP_REGEN       -> 1.0 + stats.getBase(StatType.VIT) * 0.5;  // 1 HP/s base + 0.5 per VIT
            case MP_REGEN       -> 0.5 + stats.getBase(StatType.INT) * 0.3;  // 0.5 MP/s base + 0.3 per INT

            case WEIGHT_LIMIT   -> 100.0 + stats.getBase(StatType.STR) * 5.0; // 100 base + 5 per STR
        };
    }

    // ---- Combat formulas ----

    /**
     * Melee/magic damage: STR * 2 + weaponATK.
     * For pure magic damage, pass INT * 2 as the first argument instead.
     */
    public static double damage(double attackPower, double weaponATK) {
        return attackPower + weaponATK;
    }

    /**
     * Attack speed multiplier: base * (1 + AGI * 0.01).
     * AGI 0 = 1.0x, AGI 50 = 1.5x, AGI 100 = 2.0x.
     */
    public static double attackSpeedMultiplier(PlayerStats stats) {
        return 1.0 + stats.getBase(StatType.AGI) * 0.01;
    }

    /**
     * Critical hit chance as a 0–1 probability.
     */
    public static double critChance(PlayerStats stats) {
        return Math.min(stats.get(StatType.CRIT_RATE) / 100.0, 1.0);
    }

    /**
     * Evasion chance as a 0–1 probability.
     */
    public static double evasionChance(PlayerStats stats) {
        return Math.min(stats.get(StatType.EVASION) / 100.0, 1.0);
    }

    /**
     * Maximum HP: 100 base + 10 per VIT.
     */
    public static double maxHp(PlayerStats stats) {
        return 100.0 + stats.getBase(StatType.VIT) * 10.0;
    }

    /**
     * Maximum MP: 50 base + 5 per INT.
     */
    public static double maxMp(PlayerStats stats) {
        return 50.0 + stats.getBase(StatType.INT) * 5.0;
    }
}
