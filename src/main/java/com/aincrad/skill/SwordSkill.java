package com.aincrad.skill;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract base for sword skills that participate in combo chains.
 * <p>
 * Each sword skill occupies a position in a combo sequence. When a player
 * lands a hit with a sword skill, they have {@link #comboWindow} ticks to
 * trigger the next skill in the chain. Skills at the end of a chain receive
 * a {@link #finisherMultiplier} damage bonus.
 * </p>
 */
public abstract class SwordSkill implements Skill {

    /** Index of this skill in its combo chain (0-based). */
    protected final int comboIndex;

    /** Ticks allowed between this skill and the next in the chain. */
    protected final int comboWindow;

    /** Damage multiplier applied when this skill finishes a combo. */
    protected final double finisherMultiplier;

    /** Tracks the last combo tick per player for chain validation. */
    private final Map<UUID, Long> lastComboTick = new HashMap<>();

    /** Tracks which combo index each player is currently on. */
    private final Map<UUID, Integer> playerComboIndex = new HashMap<>();

    /**
     * Creates a new sword skill.
     *
     * @param comboIndex       position in the combo chain
     * @param comboWindow      ticks allowed before the chain breaks
     * @param finisherMultiplier damage multiplier when this is the finisher
     */
    protected SwordSkill(int comboIndex, int comboWindow, double finisherMultiplier) {
        this.comboIndex = comboIndex;
        this.comboWindow = comboWindow;
        this.finisherMultiplier = finisherMultiplier;
    }

    @Override
    public SkillType getSkillType() {
        return SkillType.SWORD_SKILL;
    }

    // ── Combo Chain Logic ───────────────────────────────────────────────

    /**
     * Records that this skill was just used by the player.
     *
     * @param player the player
     * @param tick  the current game tick
     */
    public void recordHit(Player player, long tick) {
        lastComboTick.put(player.getUUID(), tick);
        playerComboIndex.put(player.getUUID(), comboIndex);
    }

    /**
     * Checks whether the given skill can chain from this one.
     *
     * @param next the candidate next skill
     * @return {@code true} if chaining is valid
     */
    public boolean canChainTo(SwordSkill next) {
        return next.comboIndex == this.comboIndex + 1;
    }

    /**
     * Checks whether the player is still within the combo window for chaining.
     *
     * @param player the player
     * @param tick   the current game tick
     * @return {@code true} if the combo window has not expired
     */
    public boolean isComboWindowOpen(Player player, long tick) {
        Long last = lastComboTick.get(player.getUUID());
        return last != null && (tick - last) <= comboWindow;
    }

    /**
     * Returns whether this skill is the finisher of its combo chain.
     *
     * @return {@code true} if this is a finisher
     */
    public boolean isFinisher() {
        return finisherMultiplier > 1.0;
    }

    // ── Activation ─────────────────────────────────────────────────────

    @Override
    public void cast(Player player) {
        onActivate(player);
    }

    /**
     * Called when the skill is activated. Subclasses implement damage/effects here.
     *
     * @param player the player who activated the skill
     */
    protected abstract void onActivate(Player player);

    // ── Getters ────────────────────────────────────────────────────────

    public int getComboIndex() {
        return comboIndex;
    }

    public int getComboWindow() {
        return comboWindow;
    }

    public double getFinisherMultiplier() {
        return finisherMultiplier;
    }
}
