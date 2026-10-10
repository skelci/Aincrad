package com.aincrad.social.guild;

/**
 * Shared constants and formulas for guild progression.
 * Centralizes level/slot/officer scaling so every guild subsystem derives
 * the same numbers and future balance changes stay in one place.
 */
public final class GuildLevel {

    private GuildLevel() {
        // utility class — no instances
    }

    /** Highest guild level attainable. */
    public static final int MAX_LEVEL = 20;

    /** Longest allowed guild display name. */
    public static final int MAX_NAME_LENGTH = 32;

    /** Guild tag length (short prefix shown in chat). */
    public static final int TAG_LENGTH = 4;

    /** Longest allowed guild notice / MOTD. */
    public static final int MAX_NOTICE_LENGTH = 256;

    /** Minimum level at which a guild may be created. */
    public static final int MIN_LEVEL = 1;

    /**
     * Maximum members allowed at a guild level.
     * Formula: {@code 10 + 5 * (level - 1)}.
     *
     * @param level guild level, clamped to {@code 1..MAX_LEVEL}
     * @return inclusive member cap
     */
    public static int memberCap(int level) {
        int l = clamp(level);
        return 10 + 5 * (l - 1);
    }

    /**
     * Number of bank slots at a guild level.
     * Formula: {@code 9 + 9 * (level - 1)}.
     *
     * @param level guild level, clamped to {@code 1..MAX_LEVEL}
     * @return inclusive bank slot count
     */
    public static int bankSlots(int level) {
        int l = clamp(level);
        return 9 + 9 * (l - 1);
    }

    /**
     * Maximum officer slots at a guild level.
     * Formula: {@code 3 + floor(level / 5)}, hard-capped at 7.
     *
     * @param level guild level, clamped to {@code 1..MAX_LEVEL}
     * @return inclusive officer cap (never above 7)
     */
    public static int officerCap(int level) {
        int l = clamp(level);
        int cap = 3 + (l / 5);
        return Math.min(cap, 7);
    }

    /**
     * Total experience required to advance FROM {@code level} to {@code level + 1}.
     * Curve: {@code 250 * L^1.5} where L is the current level.
     *
     * @param level the level being advanced from, clamped to {@code 1..MAX_LEVEL}
     * @return XP required for the next level (negative when already at cap)
     */
    public static long xpForLevel(int level) {
        int l = clamp(level);
        if (l >= MAX_LEVEL) {
            return -1L; // already at cap — no further XP needed
        }
        return Math.round(250.0 * Math.pow(l, 1.5));
    }

    /** Clamps any level into the valid {@code [MIN_LEVEL, MAX_LEVEL]} range. */
    public static int clamp(int level) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }
}
