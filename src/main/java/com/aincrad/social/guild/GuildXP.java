package com.aincrad.social.guild;

import java.util.Objects;

/**
 * Experience accumulator driving guild level progression.
 * Uses the {@link GuildLevel#xpForLevel(int)} curve and the level cap
 * {@link GuildLevel#MAX_LEVEL}. Survives level-ups by carrying overflow XP
 * into the next bracket.
 */
public final class GuildXP {

    private long totalXp;
    private int level;

    /**
     * Creates a level-1 guild with zero accumulated XP.
     */
    public GuildXP() {
        this(1, 0L);
    }

    /**
     * Creates a guild XP tracker at a specific level with existing XP.
     *
     * @param level starting level (clamped to 1..{@link GuildLevel#MAX_LEVEL})
     * @param xp    starting XP, must be &gt;= 0
     * @throws IllegalArgumentException if xp is negative
     */
    public GuildXP(int level, long xp) {
        this.level = GuildLevel.clamp(level);
        if (xp < 0) {
            throw new IllegalArgumentException("xp must be >= 0");
        }
        this.totalXp = xp;
    }

    public int getLevel() {
        return level;
    }

    /** Total XP earned since creation (monotonically increasing). */
    public long getTotalXp() {
        return totalXp;
    }

    /**
     * XP already applied toward the NEXT level (resets on level-up).
     * Returns 0 at the level cap.
     */
    public long getProgressXp() {
        long needed = GuildLevel.xpForLevel(level);
        if (needed < 0) {
            return 0;
        }
        long floor = cumulativeXpFor(level);
        return Math.max(0, totalXp - floor);
    }

    /**
     * XP required to advance from the current level to the next.
     * Returns 0 when already at the cap.
     */
    public long getXpToNext() {
        long needed = GuildLevel.xpForLevel(level);
        return needed < 0 ? 0 : needed;
    }

    /**
     * Adds XP and resolves any resulting level-ups in a single call.
     *
     * @param amount XP to add; must be &gt;= 0
     * @return number of levels gained (0 when already at cap or amount is 0)
     * @throws IllegalArgumentException if amount is negative
     */
    public int addXp(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        if (amount == 0 || level >= GuildLevel.MAX_LEVEL) {
            return 0;
        }
        totalXp += amount;
        int gained = 0;
        while (level < GuildLevel.MAX_LEVEL) {
            long needed = GuildLevel.xpForLevel(level);
            if (needed < 0 || totalXp < cumulativeXpFor(level + 1)) {
                break;
            }
            level++;
            gained++;
        }
        return gained;
    }

    /**
     * Returns true if adding {@code amount} would trigger at least one level-up.
     *
     * @param amount XP to test; must be &gt;= 0
     */
    public boolean wouldLevelUp(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        if (level >= GuildLevel.MAX_LEVEL) {
            return false;
        }
        long target = totalXp + amount;
        return target >= cumulativeXpFor(level + 1);
    }

    /**
     * Total XP required to have reached {@code level} (sum of every bracket
     * below it). Used to derive per-level progress from the running total.
     */
    public static long cumulativeXpFor(int level) {
        int l = GuildLevel.clamp(level);
        long sum = 0;
        for (int i = 1; i < l; i++) {
            sum += GuildLevel.xpForLevel(i);
        }
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuildXP other)) return false;
        return totalXp == other.totalXp && level == other.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalXp, level);
    }

    @Override
    public String toString() {
        return "GuildXP{level=" + level + ", totalXp=" + totalXp + '}';
    }
}
