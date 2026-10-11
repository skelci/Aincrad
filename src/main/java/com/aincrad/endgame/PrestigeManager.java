package com.aincrad.endgame;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for the prestige system.
 * <p>
 * Tracks each player's prestige level, validates prestige requirements,
 * and manages prestige benefits and titles. Prestige is earned by meeting
 * endgame requirements such as clearing high floors and completing NG+
 * cycles.
 * </p>
 */
public final class PrestigeManager {

    private static class Holder {
        static final PrestigeManager INSTANCE = new PrestigeManager();
    }

    /** Player UUID → their prestige level. */
    private final Map<UUID, PrestigeLevel> prestigeLevels = new ConcurrentHashMap<>();

    /** Player UUID → timestamp of last prestige. */
    private final Map<UUID, Long> lastPrestigeTime = new ConcurrentHashMap<>();

    private PrestigeManager() {
    }

    /**
     * Returns the singleton instance of the PrestigeManager.
     *
     * @return the PrestigeManager instance
     */
    public static PrestigeManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── Prestige Access ─────────────────────────────────────────────────

    /**
     * Returns the prestige level for a player, defaulting to {@link PrestigeLevel#NONE}.
     *
     * @param playerId the player's UUID
     * @return the PrestigeLevel (never {@code null})
     */
    public PrestigeLevel getPrestigeLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return prestigeLevels.getOrDefault(playerId, PrestigeLevel.NONE);
    }

    /**
     * Sets the prestige level for a player.
     *
     * @param playerId the player's UUID
     * @param level the prestige level to set
     */
    public void setPrestigeLevel(UUID playerId, PrestigeLevel level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(level, "level");
        prestigeLevels.put(playerId, level);
        lastPrestigeTime.put(playerId, System.currentTimeMillis());
    }

    // ── Requirements ────────────────────────────────────────────────────

    /**
     * Checks whether a player meets the requirements for a prestige level.
     *
     * @param playerId the player's UUID
     * @param level the prestige level to check
     * @param highestFloorCleared the player's highest floor cleared
     * @param ngPlusCycle the player's current NG+ cycle
     * @return {@code true} if all requirements are met
     */
    public static boolean meetsRequirements(UUID playerId, PrestigeLevel level,
                                            int highestFloorCleared, int ngPlusCycle) {
        return switch (level) {
            case NONE -> true;
            case BRONZE -> highestFloorCleared >= PrestigeLevel.BRONZE.getRequiredFloor();
            case SILVER -> highestFloorCleared >= PrestigeLevel.SILVER.getRequiredFloor();
            case GOLD -> highestFloorCleared >= PrestigeLevel.GOLD.getRequiredFloor();
            case PLATINUM -> highestFloorCleared >= PrestigeLevel.PLATINUM.getRequiredFloor()
                    && ngPlusCycle >= 1;
            case DIAMOND -> highestFloorCleared >= PrestigeLevel.DIAMOND.getRequiredFloor()
                    && ngPlusCycle >= 3;
            case AINCRAD -> highestFloorCleared >= PrestigeLevel.AINCRAD.getRequiredFloor()
                    && ngPlusCycle >= 5;
        };
    }

    /**
     * Checks whether a player can prestige to the next level.
     *
     * @param playerId the player's UUID
     * @param highestFloorCleared the player's highest floor cleared
     * @param ngPlusCycle the player's current NG+ cycle
     * @return {@code true} if the player can prestige
     */
    public boolean canPrestige(UUID playerId, int highestFloorCleared, int ngPlusCycle) {
        PrestigeLevel current = getPrestigeLevel(playerId);
        PrestigeLevel next = current.next();
        return next != null && meetsRequirements(playerId, next, highestFloorCleared, ngPlusCycle);
    }

    /**
     * Attempts to prestige a player to the next level.
     *
     * @param playerId the player's UUID
     * @param highestFloorCleared the player's highest floor cleared
     * @param ngPlusCycle the player's current NG+ cycle
     * @return the new prestige level, or current level if requirements not met
     */
    public PrestigeLevel prestige(UUID playerId, int highestFloorCleared, int ngPlusCycle) {
        PrestigeLevel current = getPrestigeLevel(playerId);
        PrestigeLevel next = current.next();
        if (next != null && meetsRequirements(playerId, next, highestFloorCleared, ngPlusCycle)) {
            setPrestigeLevel(playerId, next);
            return next;
        }
        return current;
    }

    // ── Benefits ────────────────────────────────────────────────────────

    /**
     * Returns the Col bonus percentage for a player's prestige level.
     *
     * @param playerId the player's UUID
     * @return the Col bonus percentage (0-50)
     */
    public static int getColBonus(UUID playerId) {
        return getInstance().getPrestigeLevel(playerId).getColBonusPercent();
    }

    /**
     * Returns the title for a player's prestige level.
     *
     * @param playerId the player's UUID
     * @return the title string (empty if NONE)
     */
    public static String getTitle(UUID playerId) {
        return getInstance().getPrestigeLevel(playerId).getTitle();
    }

    /**
     * Returns all benefits for a prestige level as a human-readable list.
     *
     * @param level the prestige level
     * @return list of benefit descriptions
     */
    public static List<String> getBenefits(PrestigeLevel level) {
        List<String> benefits = new ArrayList<>();
        benefits.add("Col bonus: +" + level.getColBonusPercent() + "%");
        if (!level.getTitle().isEmpty()) {
            benefits.add("Title: " + level.getTitle());
        }
        return benefits;
    }
}
