package com.aincrad.boss.util;

/**
 * Static utility for floor-based boss stat scaling.
 *
 * Formulas (from BOSSES.md):
 *   HP(floor)     = round(500 × F^1.6)
 *   Damage(floor) = round(20 × F^1.35)
 *   XP(floor)     = round(200 × F^1.5)
 *
 * Party scaling:
 *   HP(n)     = HP_base × (1 + 0.15 × (n − 1))
 *   Damage(n) = Damage_base × (1 + 0.10 × (n − 1))
 */
public final class BossStatScaler {

    private BossStatScaler() {
    }

    // ------------------------------------------------------------------
    // Base stat formulas
    // ------------------------------------------------------------------

    /**
     * @param floor the floor number (1–100)
     * @return scaled max HP for a boss on this floor
     */
    public static float scaledHp(int floor) {
        return Math.round(500.0f * (float) Math.pow(floor, 1.6));
    }

    /**
     * @param floor the floor number (1–100)
     * @return scaled base damage for a boss on this floor
     */
    public static float scaledDamage(int floor) {
        return Math.round(20.0f * (float) Math.pow(floor, 1.35));
    }

    /**
     * @param floor the floor number (1–100)
     * @return scaled XP reward for defeating a boss on this floor
     */
    public static int scaledXp(int floor) {
        return Math.round(200.0f * (float) Math.pow(floor, 1.5));
    }

    // ------------------------------------------------------------------
    // Party scaling
    // ------------------------------------------------------------------

    /**
     * @param partySize number of players in the party (1–20)
     * @return HP multiplier for the given party size
     */
    public static float partyHpMultiplier(int partySize) {
        return 1.0f + 0.15f * (partySize - 1);
    }

    /**
     * @param partySize number of players in the party (1–20)
     * @return damage multiplier for the given party size
     */
    public static float partyDamageMultiplier(int partySize) {
        return 1.0f + 0.10f * (partySize - 1);
    }

    // ------------------------------------------------------------------
    // Soft enrage (small party penalty)
    // ------------------------------------------------------------------

    /**
     * @param partySize          actual party size
     * @param recommendedPartySize recommended party size for the floor
     * @return additional HP multiplier (1.0 if party is large enough)
     */
    public static float softEnrageMultiplier(int partySize, int recommendedPartySize) {
        int missing = Math.max(0, recommendedPartySize - partySize);
        return 1.0f + 0.10f * missing;
    }

    // ------------------------------------------------------------------
    // Convenience: combined HP
    // ------------------------------------------------------------------

    /**
     * @param floor               the floor number
     * @param partySize           actual party size
     * @param recommendedPartySize recommended party size for the floor
     * @return final HP after party scaling and soft enrage
     */
    public static float finalHp(int floor, int partySize, int recommendedPartySize) {
        return scaledHp(floor) * partyHpMultiplier(partySize) * softEnrageMultiplier(partySize, recommendedPartySize);
    }

    /**
     * @param floor     the floor number
     * @param partySize actual party size
     * @return final damage after party scaling
     */
    public static float finalDamage(int floor, int partySize) {
        return scaledDamage(floor) * partyDamageMultiplier(partySize);
    }
}
