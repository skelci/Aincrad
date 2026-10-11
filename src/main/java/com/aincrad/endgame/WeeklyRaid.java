package com.aincrad.endgame;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a weekly raid boss with rotation schedule and exclusive drops.
 * <p>
 * Each week a different raid boss is available. Players can challenge
 * the raid boss for exclusive rewards that rotate weekly.
 * </p>
 */
public final class WeeklyRaid {

    private final String bossId;
    private final String bossName;
    private final int floor;
    private final DayOfWeek availableDay;
    private final long durationHours;
    private final String[] exclusiveDrops;
    private final int recommendedLevel;
    private final int maxParticipants;

    /**
     * Creates a weekly raid definition.
     *
     * @param bossId the boss identifier
     * @param bossName the display name of the boss
     * @param floor the floor this raid is on
     * @param availableDay the day of week the raid is available
     * @param durationHours how long the raid is available (in hours)
     * @param exclusiveDrops array of exclusive drop item IDs
     * @param recommendedLevel the recommended player level
     * @param maxParticipants maximum number of participants
     */
    public WeeklyRaid(String bossId, String bossName, int floor, DayOfWeek availableDay,
                      long durationHours, String[] exclusiveDrops, int recommendedLevel,
                      int maxParticipants) {
        this.bossId = Objects.requireNonNull(bossId, "bossId");
        this.bossName = Objects.requireNonNull(bossName, "bossName");
        this.floor = floor;
        this.availableDay = Objects.requireNonNull(availableDay, "availableDay");
        this.durationHours = durationHours;
        this.exclusiveDrops = exclusiveDrops != null ? exclusiveDrops.clone() : new String[0];
        this.recommendedLevel = recommendedLevel;
        this.maxParticipants = maxParticipants;
    }

    /**
     * Returns the boss identifier.
     *
     * @return the boss ID
     */
    public String getBossId() {
        return bossId;
    }

    /**
     * Returns the boss display name.
     *
     * @return the boss name
     */
    public String getBossName() {
        return bossName;
    }

    /**
     * Returns the floor this raid is on.
     *
     * @return the floor
     */
    public int getFloor() {
        return floor;
    }

    /**
     * Returns the day of week this raid is available.
     *
     * @return the available day
     */
    public DayOfWeek getAvailableDay() {
        return availableDay;
    }

    /**
     * Returns the duration in hours.
     *
     * @return the duration
     */
    public long getDurationHours() {
        return durationHours;
    }

    /**
     * Returns the exclusive drops.
     *
     * @return array of exclusive drop item IDs
     */
    public String[] getExclusiveDrops() {
        return exclusiveDrops.clone();
    }

    /**
     * Returns the recommended level.
     *
     * @return the recommended level
     */
    public int getRecommendedLevel() {
        return recommendedLevel;
    }

    /**
     * Returns the maximum participants.
     *
     * @return the max participants
     */
    public int getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * Checks whether this raid is currently available.
     *
     * @param now the current time
     * @return {@code true} if the raid is available
     */
    public boolean isAvailable(LocalDateTime now) {
        return now.getDayOfWeek() == availableDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyRaid that = (WeeklyRaid) o;
        return bossId.equals(that.bossId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bossId);
    }

    @Override
    public String toString() {
        return "WeeklyRaid{" + bossName + " (Floor " + floor + "), available " + availableDay + "}";
    }
}
