package com.aincrad.paper.guild;

/**
 * Experience accumulator driving guild level progression.
 * Tracks total XP and resolves level-ups via the 250 * L^1.5 formula.
 */
public final class GuildXP {
    private long totalXp;
    private int level;

    public GuildXP() {
        this.totalXp = 0;
        this.level = GuildLevel.MIN_LEVEL;
    }

    public GuildXP(long totalXp, int level) {
        this.totalXp = totalXp;
        this.level = GuildLevel.clamp(level);
    }

    public int getLevel() { return level; }
    public long getTotalXp() { return totalXp; }

    public long getProgressXp() {
        return totalXp - cumulativeXpFor(level);
    }

    public long getXpToNext() {
        return GuildLevel.xpForLevel(level);
    }

    public int addXp(long amount) {
        if (amount <= 0) return 0;
        totalXp += amount;
        int levelsGained = 0;
        while (level < GuildLevel.MAX_LEVEL && totalXp >= cumulativeXpFor(level + 1)) {
            level++;
            levelsGained++;
        }
        return levelsGained;
    }

    public boolean wouldLevelUp(long amount) {
        return level < GuildLevel.MAX_LEVEL && (totalXp + amount) >= cumulativeXpFor(level + 1);
    }

    public static long cumulativeXpFor(int level) {
        long sum = 0;
        for (int i = GuildLevel.MIN_LEVEL; i < level; i++) {
            long xp = GuildLevel.xpForLevel(i);
            if (xp < 0) break;
            sum += xp;
        }
        return sum;
    }
}
