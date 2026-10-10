package com.aincrad.combat.stat;

/**
 * All stat types in the Aincrad combat system.
 * Base stats are set directly; derived stats are computed by {@link StatCalculator}.
 */
public enum StatType {

    // ---- Core attributes ----
    STR,
    AGI,
    VIT,
    DEX,
    INT,

    // ---- Combat derived ----
    CRIT_RATE,
    CRIT_DAMAGE,
    EVASION,

    // ---- Resource derived ----
    HP_REGEN,
    MP_REGEN,

    // ---- Utility derived ----
    WEIGHT_LIMIT,
}
