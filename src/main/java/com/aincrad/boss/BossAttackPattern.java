package com.aincrad.boss;

import net.minecraft.world.level.Level;

/**
 * Represents a single boss attack pattern (e.g., AoE slam, projectile spread,
 * charge attack).
 *
 * Each pattern defines:
 * <ul>
 *   <li>A telegraph duration (wind-up time before the attack lands)</li>
 *   <li>An {@code execute} method that applies the attack's effect</li>
 *   <li>A cooldown before the pattern can be used again</li>
 * </ul>
 *
 * Patterns are registered in {@link BossEntity#initPatterns()} and selected
 * by {@link BossEntity#selectPattern()}.
 */
public abstract class BossAttackPattern {

    // ------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------

    /** Wind-up time in ticks (20 ticks = 1 second). */
    protected final int telegraphDuration;

    /** Cooldown in ticks before this pattern can be used again. */
    protected final int cooldown;

    /** Human-readable name for debugging / logging. */
    protected final String name;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    /**
     * @param name              display name of the pattern
     * @param telegraphDuration wind-up time in ticks
     * @param cooldown          cooldown in ticks
     */
    protected BossAttackPattern(String name, int telegraphDuration, int cooldown) {
        this.name = name;
        this.telegraphDuration = Math.max(0, telegraphDuration);
        this.cooldown = Math.max(0, cooldown);
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    /**
     * @return the wind-up time in ticks
     */
    public int getTelegraphDuration() {
        return telegraphDuration;
    }

    /**
     * @return the cooldown in ticks
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * @return the display name of this pattern
     */
    public String getName() {
        return name;
    }

    // ------------------------------------------------------------------
    // Execution
    // ------------------------------------------------------------------

    /**
     * Executes the attack. Called when the telegraph finishes.
     *
     * @param boss  the boss performing the attack
     * @param level the server-side level
     */
    public abstract void execute(BossEntity boss, Level level);

    // ------------------------------------------------------------------
    // Telegraph
    // ------------------------------------------------------------------

    /**
     * Called when the telegraph begins (wind-up starts). Override to play
     * sounds, spawn particles, show indicators, etc.
     *
     * @param boss  the boss preparing the attack
     * @param level the server-side level
     */
    public void onTelegraphStart(BossEntity boss, Level level) {
        // Default: no-op
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Spawns a simple damage indicator: damages all players within a radius
     * of the boss's position. Override or ignore if not applicable.
     *
     * @param boss   the attacking boss
     * @param level  the server-side level
     * @param radius damage radius in blocks
     * @param damage amount of damage
     */
    protected void damagePlayersInRadius(BossEntity boss, Level level, float radius, float damage) {
        level.players().stream()
            .filter(p -> p.blockPosition().distSqr(boss.blockPosition()) <= radius * radius)
            .forEach(p -> p.hurt(p.damageSources().mobAttack(boss), damage));
    }

    @Override
    public String toString() {
        return name + " [telegraph=" + telegraphDuration + " ticks, cooldown=" + cooldown + " ticks]";
    }
}
