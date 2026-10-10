package com.aincrad.social.friend;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single friend relationship entry.
 * <p>
 * Immutable except for the {@code online} flag, which is updated in real-time
 * as players join and leave the server.
 * </p>
 */
public final class FriendEntry {

    private final UUID playerId;
    private final String name;
    private final Instant friendSince;
    private volatile boolean online;

    /**
     * Creates a new friend entry with the current timestamp.
     *
     * @param playerId the friend's player UUID
     * @param name     the friend's display name
     */
    public FriendEntry(UUID playerId, String name) {
        this(playerId, name, Instant.now(), false);
    }

    /**
     * Creates a new friend entry with explicit fields.
     *
     * @param playerId    the friend's player UUID
     * @param name        the friend's display name
     * @param friendSince the instant the friendship was established
     * @param online      whether the friend is currently online
     */
    public FriendEntry(UUID playerId, String name, Instant friendSince, boolean online) {
        this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.friendSince = Objects.requireNonNull(friendSince, "friendSince must not be null");
        this.online = online;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public Instant getFriendSince() {
        return friendSince;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendEntry that = (FriendEntry) o;
        return online == that.online
                && playerId.equals(that.playerId)
                && name.equals(that.name)
                && friendSince.equals(that.friendSince);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, name, friendSince, online);
    }

    @Override
    public String toString() {
        return "FriendEntry{playerId=%s, name='%s', online=%s, since=%s}"
                .formatted(playerId, name, online, friendSince);
    }
}
