package com.aincrad.paper.dungeon;

import java.util.UUID;

/**
 * Per-player dungeon lockout with expiry time.
 */
public final class DungeonLockout {
    private final UUID playerUuid;
    private final String dungeonId;
    private final long expireTime;
    private final boolean bypassed;

    public DungeonLockout(UUID playerUuid, String dungeonId, long expireTime, boolean bypassed) {
        this.playerUuid = playerUuid;
        this.dungeonId = dungeonId;
        this.expireTime = expireTime;
        this.bypassed = bypassed;
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public String getDungeonId() { return dungeonId; }
    public long getExpireTime() { return expireTime; }
    public boolean isBypassed() { return bypassed; }

    public boolean isExpired(long now) {
        return now >= expireTime;
    }
}
