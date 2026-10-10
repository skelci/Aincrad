package com.aincrad.boss;

/**
 * Represents the phases a boss can transition through during combat.
 * Each phase typically corresponds to a percentage of the boss's max health.
 */
public enum BossPhase {
    PHASE_1(0.80F),  // 100% - 80% health
    PHASE_2(0.60F),  // 79%  - 60% health
    PHASE_3(0.40F),  // 59%  - 40% health
    PHASE_4(0.20F),  // 39%  - 20% health
    PHASE_5(0.05F),  // 19%  - 5%  health
    ENRAGE(0.00F);   // Final enrage state (below 5% or timer-triggered)

    private final float healthThreshold;

    BossPhase(float healthThreshold) {
        this.healthThreshold = healthThreshold;
    }

    /**
     * @return the fraction of max health at or below which this phase activates.
     */
    public float getHealthThreshold() {
        return healthThreshold;
    }

    /**
     * Determines the correct phase for a given health fraction.
     *
     * @param healthFraction current health divided by max health (0.0 - 1.0)
     * @return the appropriate BossPhase
     */
    public static BossPhase fromHealthFraction(float healthFraction) {
        for (BossPhase phase : values()) {
            if (healthFraction <= phase.healthThreshold) {
                return phase;
            }
        }
        return PHASE_1;
    }

    /**
     * @return the next phase in sequence, or ENRAGE if already at the last phase.
     */
    public BossPhase next() {
        int nextOrdinal = this.ordinal() + 1;
        BossPhase[] phases = values();
        return nextOrdinal < phases.length ? phases[nextOrdinal] : ENRAGE;
    }

    /**
     * @return {@code true} if this is the final enrage phase.
     */
    public boolean isEnrage() {
        return this == ENRAGE;
    }
}
