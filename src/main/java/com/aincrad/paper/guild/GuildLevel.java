package com.aincrad.paper.guild;

/**
 * Guild level constants and formulas.
 * Max level 20, XP curve 250 * L^1.5.
 */
public final class GuildLevel {
    private GuildLevel() {}

    public static final int MAX_LEVEL = 20;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_NAME_LENGTH = 32;
    public static final int TAG_LENGTH = 4;
    public static final int MAX_NOTICE_LENGTH = 256;

    public static int memberCap(int level) {
        return 10 + 5 * (clamp(level) - 1);
    }

    public static int bankSlots(int level) {
        return 9 + 9 * (clamp(level) - 1);
    }

    public static int officerCap(int level) {
        return Math.min(3 + clamp(level) / 5, 7);
    }

    public static long xpForLevel(int level) {
        int l = clamp(level);
        if (l >= MAX_LEVEL) return -1L;
        return Math.round(250.0 * Math.pow(l, 1.5));
    }

    public static int clamp(int level) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }
}
