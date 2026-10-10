package com.aincrad.boss;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for all boss entities in Aincrad.
 *
 * Handles:
 * <ul>
 *   <li>Phase transitions driven by health thresholds</li>
 *   <li>Enrage timer (auto-enrage if the fight runs too long)</li>
 *   <li>Arena bounds and player tracking</li>
 *   <li>Attack pattern registration and selection</li>
 * </ul>
 *
 * Subclasses must supply an {@link EntityType}, a display name, and at least
 * one {@link BossAttackPattern} via {@link #initPatterns()}.
 */
public abstract class BossEntity extends Monster {

    // ------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------

    /** Default enrage timer in seconds (5 minutes). */
    public static final int DEFAULT_ENRAGE_SECONDS = 300;

    /** NBT key for the current phase ordinal. */
    private static final String NBT_PHASE = "BossPhase";

    /** NBT key for the remaining enrage time in ticks. */
    private static final String NBT_ENRAGE_TICKS = "BossEnrageTicks";

    /** NBT key for the arena lock state. */
    private static final String NBT_ARENA_LOCKED = "BossArenaLocked";

    // ------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------

    /** Current combat phase. */
    protected BossPhase currentPhase = BossPhase.PHASE_1;

    /** Ticks remaining before the boss enrages. {@code <= 0} means enraged. */
    protected int enrageTicksRemaining = DEFAULT_ENRAGE_SECONDS * 20;

    /** Whether the enrage timer is active. */
    protected boolean enrageEnabled = true;

    /** Arena this boss is bound to, or {@code null} if unbound. */
    protected BossArena arena;

    /** Registered attack patterns. */
    protected final List<BossAttackPattern> patterns = new ArrayList<>();

    /** Cooldown tracker — one entry per pattern, in ticks. */
    protected final List<Integer> patternCooldowns = new ArrayList<>();

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    protected BossEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.initPatterns();
    }

    // ------------------------------------------------------------------
    // Subclass hooks
    // ------------------------------------------------------------------

    /**
     * Populate {@link #patterns} and {@link #patternCooldowns} here.
     * Called once from the constructor. At least one pattern is required.
     */
    protected abstract void initPatterns();

    /**
     * @return the display name shown in the boss health bar.
     */
    public abstract String getBossName();

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            this.tickEnrage();
            this.tickPhase();
            this.tickArena();
            this.tickCooldowns();
        }
    }

    // ------------------------------------------------------------------
    // Phase management
    // ------------------------------------------------------------------

    /**
     * Evaluates the boss's health fraction and advances the phase if needed.
     */
    protected void tickPhase() {
        float fraction = this.getHealth() / this.getMaxHealth();
        BossPhase newPhase = BossPhase.fromHealthFraction(fraction);

        if (newPhase != this.currentPhase) {
            this.advancePhase(newPhase);
        }
    }

    /**
     * Transitions to a new phase, firing the onEnter hook.
     *
     * @param newPhase the phase to enter
     */
    protected void advancePhase(BossPhase newPhase) {
        this.currentPhase = newPhase;
        this.onPhaseEnter(newPhase);
    }

    /**
     * Called every tick while the boss is alive on the server side.
     */
    protected void tickEnrage() {
        if (!this.enrageEnabled || this.currentPhase.isEnrage()) {
            return;
        }

        if (--this.enrageTicksRemaining <= 0) {
            this.enrage();
        }
    }

    /**
     * Forces the boss into the ENRAGE phase.
     */
    public void enrage() {
        this.currentPhase = BossPhase.ENRAGE;
        this.enrageTicksRemaining = 0;
        this.onPhaseEnter(BossPhase.ENRAGE);
    }

    // ------------------------------------------------------------------
    // Arena management
    // ------------------------------------------------------------------

    /**
     * Sets the arena for this boss and locks it if there are players inside.
     *
     * @param arena the arena to bind
     */
    public void setArena(BossArena arena) {
        this.arena = arena;
    }

    /**
     * @return the bound arena, or {@code null}
     */
    public BossArena getArena() {
        return this.arena;
    }

    /**
     * Teleports any players who have left the arena back inside and
     * re-locks the arena while players are present.
     */
    protected void tickArena() {
        if (this.arena == null || this.level().isClientSide()) {
            return;
        }

        boolean anyInside = false;

        for (Player player : this.level().players()) {
            if (this.arena.contains(player.blockPosition())) {
                anyInside = true;
            }
        }

        this.arena.setLocked(anyInside);
    }

    // ------------------------------------------------------------------
    // Attack pattern management
    // ------------------------------------------------------------------

    /**
     * Returns a ready (cooldown-expired) pattern, or {@code null} if none
     * are available.
     */
    protected BossAttackPattern selectPattern() {
        for (int i = 0; i < patterns.size(); i++) {
            int cd = patternCooldowns.get(i);
            if (cd <= 0) {
                return patterns.get(i);
            }
        }
        return null;
    }

    /**
     * Executes a pattern and starts its cooldown.
     *
     * @param pattern the pattern to execute
     */
    protected void executePattern(BossAttackPattern pattern) {
        int index = patterns.indexOf(pattern);
        if (index < 0) {
            return;
        }

        pattern.execute(this, this.level());
        patternCooldowns.set(index, pattern.getCooldown());
    }

    /**
     * Decrements all pattern cooldowns. Called once per tick.
     */
    protected void tickCooldowns() {
        for (int i = 0; i < patternCooldowns.size(); i++) {
            int cd = patternCooldowns.get(i);
            if (cd > 0) {
                patternCooldowns.set(i, cd - 1);
            }
        }
    }

    // ------------------------------------------------------------------
    // Phase enter hook
    // ------------------------------------------------------------------

    /**
     * Called when the boss enters a new phase. Override for custom behaviour
     * (e.g., spawning minions, changing AI speed, broadcasting messages).
     *
     * @param phase the phase that was just entered
     */
    protected void onPhaseEnter(BossPhase phase) {
        // Default: no-op. Subclasses override.
    }

    // ------------------------------------------------------------------
    // NBT serialisation
    // ------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt(NBT_PHASE, this.currentPhase.ordinal());
        output.putInt(NBT_ENRAGE_TICKS, this.enrageTicksRemaining);
        output.putBoolean(NBT_ARENA_LOCKED, this.arena != null && this.arena.isLocked());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);

        int ordinal = input.getInt(NBT_PHASE).orElse(BossPhase.PHASE_1.ordinal());
        BossPhase[] phases = BossPhase.values();
        if (ordinal >= 0 && ordinal < phases.length) {
            this.currentPhase = phases[ordinal];
        }

        this.enrageTicksRemaining = input.getInt(NBT_ENRAGE_TICKS).orElse(DEFAULT_ENRAGE_SECONDS * 20);
    }
}
