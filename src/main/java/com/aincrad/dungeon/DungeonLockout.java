package com.aincrad.dungeon;

import java.util.UUID;

public class DungeonLockout {

    private final UUID playerUuid;
    private final String dungeonId;
    private final long expireTime;
    private final boolean isBypassed;

    public DungeonLockout(UUID playerUuid, String dungeonId, long expireTime, boolean isBypassed) {
        this.playerUuid = playerUuid;
        this.dungeonId = dungeonId;
        this.expireTime = expireTime;
        this.isBypassed = isBypassed;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public boolean isBypassed() {
        return isBypassed;
    }

    public boolean isExpired(long now) {
        return now >= expireTime;
    }
}
