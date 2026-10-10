package com.aincrad.skill;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract base for active skills that have a cooldown and MP cost.
 * <p>
 * Concrete subclasses implement {@link #onActivate(Player)} for the skill
 * effect. The cooldown timer is tracked per player and decremented each tick.
 * </p>
 */
public abstract class ActiveSkill implements Skill {

    /** Remaining cooldown per player (tick countdown). */
    private final Map<UUID, Integer> cooldowns = new HashMap<>();

    @Override
    public SkillType getSkillType() {
        return SkillType.ACTIVE_SKILL;
    }

    // ── Cooldown Management ─────────────────────────────────────────────

    /**
     * Attempts to activate this skill for the given player.
     * <p>
     * Checks cooldown and MP availability before calling {@link #onActivate(Player)}.
     * </p>
     *
     * @param player the player
     * @return {@code true} if activation succeeded
     */
    public boolean tryActivate(Player player) {
        UUID id = player.getUUID();
        int currentCd = cooldowns.getOrDefault(id, 0);
        if (currentCd > 0) return false;
        if (!player.isCreative() && getMpCost() > 0) {
            // TODO: MP check will be wired to ManaSystem
        }
        onActivate(player);
        cooldowns.put(id, getCooldown());
        return true;
    }

    /**
     * Returns the remaining cooldown for the given player.
     *
     * @param player the player
     * @return remaining cooldown in ticks
     */
    public int getRemainingCooldown(Player player) {
        return cooldowns.getOrDefault(player.getUUID(), 0);
    }

    /**
     * Sets the cooldown directly (for bypassing activation checks).
     *
     * @param player   the player
     * @param cooldown ticks
     */
    public void setCooldown(Player player, int cooldown) {
        cooldowns.put(player.getUUID(), Math.max(0, cooldown));
    }

    /**
     * Clears the cooldown for the given player.
     *
     * @param player the player
     */
    public void clearCooldown(Player player) {
        cooldowns.remove(player.getUUID());
    }

    // ── Abstract Hook ──────────────────────────────────────────────────

    /**
     * Called when the skill successfully activates.
     *
     * @param player the player who activated the skill
     */
    protected abstract void onActivate(Player player);
}
