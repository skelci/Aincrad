package com.aincrad.social.party;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single member of a party.
 * <p>
 * Identity is based on {@link #playerId}; two {@code PartyMember} instances
 * are considered equal if they share the same {@link UUID}.
 */
public final class PartyMember {

    /** Combat role within the party. */
    public enum Role {
        DPS, TANK, HEALER
    }

    private final UUID playerId;
    private final String name;
    private int level;
    private Role role;
    private final Instant joinTime;

    /**
     * Creates a new party member.
     *
     * @param playerId the player's unique identifier (non-null)
     * @param name     the player's display name (non-null, non-blank)
     * @param level    the player's current level (≥ 1)
     * @param role     the player's combat role (non-null)
     * @throws IllegalArgumentException if {@code level < 1} or {@code name} is blank
     * @throws NullPointerException     if any required argument is null
     */
    public PartyMember(UUID playerId, String name, int level, Role role) {
        this.playerId = Objects.requireNonNull(playerId, "playerId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        this.level = level;
        this.role = Objects.requireNonNull(role, "role cannot be null");
        this.joinTime = Instant.now();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Role getRole() {
        return role;
    }

    public Instant getJoinTime() {
        return joinTime;
    }

    /**
     * Updates the member's level.
     *
     * @param level the new level (≥ 1)
     * @throws IllegalArgumentException if {@code level < 1}
     */
    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        this.level = level;
    }

    /**
     * Updates the member's combat role.
     *
     * @param role the new role (non-null)
     * @throws NullPointerException if {@code role} is null
     */
    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "role cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartyMember that = (PartyMember) o;
        return playerId.equals(that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public String toString() {
        return "PartyMember[" + name + " (" + playerId + "), level=" + level + ", role=" + role + "]";
    }
}
