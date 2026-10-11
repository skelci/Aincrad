package com.aincrad.endgame;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Singleton manager for weekly raids.
 * <p>
 * Manages the weekly raid rotation, tracks the current raid, and provides
 * schedule information. Each week a different raid boss is available with
 * exclusive drops.
 * </p>
 */
public final class WeeklyRaidManager {

    private static class Holder {
        static final WeeklyRaidManager INSTANCE = new WeeklyRaidManager();
    }

    /** All available weekly raids. */
    private final List<WeeklyRaid> raids = new ArrayList<>();

    /** Player UUID → number of raid completions this week. */
    private final Map<UUID, Integer> weeklyCompletions = new HashMap<>();

    /** The current week's raid index. */
    private int currentRaidIndex = 0;

    /** Last update timestamp for rotation tracking. */
    private long lastRotationTimestamp;

    private WeeklyRaidManager() {
        registerDefaultRaids();
        this.lastRotationTimestamp = System.currentTimeMillis();
    }

    /**
     * Returns the singleton instance of the WeeklyRaidManager.
     *
     * @return the WeeklyRaidManager instance
     */
    public static WeeklyRaidManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── Raid Management ─────────────────────────────────────────────────

    /**
     * Registers a weekly raid.
     *
     * @param raid the raid to register
     */
    public void registerRaid(WeeklyRaid raid) {
        Objects.requireNonNull(raid, "raid");
        raids.add(raid);
    }

    /**
     * Returns all registered raids.
     *
     * @return list of all raids
     */
    public List<WeeklyRaid> getAllRaids() {
        return Collections.unmodifiableList(raids);
    }

    /**
     * Returns the currently active raid.
     *
     * @return the current WeeklyRaid
     * @throws IllegalStateException if no raids are registered
     */
    public WeeklyRaid getCurrentRaid() {
        if (raids.isEmpty()) {
            throw new IllegalStateException("No weekly raids registered");
        }
        return raids.get(currentRaidIndex % raids.size());
    }

    /**
     * Returns the next raid in the rotation.
     *
     * @return the next WeeklyRaid
     */
    public WeeklyRaid getNextRaid() {
        if (raids.isEmpty()) {
            throw new IllegalStateException("No weekly raids registered");
        }
        return raids.get((currentRaidIndex + 1) % raids.size());
    }

    /**
     * Advances to the next raid in the rotation.
     *
     * @return the new current raid
     */
    public WeeklyRaid rotateRaid() {
        currentRaidIndex = (currentRaidIndex + 1) % raids.size();
        lastRotationTimestamp = System.currentTimeMillis();
        weeklyCompletions.clear();
        return getCurrentRaid();
    }

    /**
     * Returns whether the current raid is available now.
     *
     * @param now the current time
     * @return {@code true} if available
     */
    public boolean isRaidAvailable(LocalDateTime now) {
        return getCurrentRaid().isAvailable(now);
    }

    // ── Schedule ────────────────────────────────────────────────────────

    /**
     * Returns the schedule for the current week.
     *
     * @return map of DayOfWeek to available raid name
     */
    public Map<DayOfWeek, String> getWeeklySchedule() {
        Map<DayOfWeek, String> schedule = new EnumMap<>(DayOfWeek.class);
        WeeklyRaid current = getCurrentRaid();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day == current.getAvailableDay()) {
                schedule.put(day, current.getBossName() + " (ACTIVE)");
            } else {
                schedule.put(day, "No Raid");
            }
        }
        return schedule;
    }

    // ── Completions ─────────────────────────────────────────────────────

    /**
     * Records a raid completion for a player.
     *
     * @param playerId the player's UUID
     */
    public void recordCompletion(UUID playerId) {
        weeklyCompletions.merge(playerId, 1, Integer::sum);
    }

    /**
     * Returns the number of completions for a player this week.
     *
     * @param playerId the player's UUID
     * @return the completion count
     */
    public int getCompletionCount(UUID playerId) {
        return weeklyCompletions.getOrDefault(playerId, 0);
    }

    /**
     * Returns the last rotation timestamp.
     *
     * @return the timestamp in milliseconds
     */
    public long getLastRotationTimestamp() {
        return lastRotationTimestamp;
    }

    // ── Default Raids ───────────────────────────────────────────────────

    private void registerDefaultRaids() {
        raids.add(new WeeklyRaid("raid_gleam", "Gleamfang the Eternal", 100,
                DayOfWeek.MONDAY, 48, new String[]{"gleamfang_sword", "gleamfang_armor"},
                80, 20));

        raids.add(new WeeklyRaid("raid_shadow", "Shadowmaw the Devourer", 100,
                DayOfWeek.TUESDAY, 48, new String[]{"shadowmaw_dagger", "shadowmaw_cloak"},
                85, 15));

        raids.add(new WeeklyRaid("raid_frost", "Frostbite the Ancient", 100,
                DayOfWeek.WEDNESDAY, 48, new String[]{"frostbite_staff", "frostbite_robe"},
                90, 15));

        raids.add(new WeeklyRaid("raid_inferno", "Inferno the Worldbreaker", 100,
                DayOfWeek.THURSDAY, 48, new String[]{"inferno_blade", "inferno_plate"},
                95, 10));

        raids.add(new WeeklyRaid("raid_void", "Voidreaper the End", 100,
                DayOfWeek.FRIDAY, 72, new String[]{"voidreaper_scythe", "voidreaper_aura"},
                100, 10));
    }
}
