package com.aincrad.paper.party;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Individual party member model.
 */
public final class PartyMember {
    public enum Role { DPS, TANK, HEALER }

    private final UUID playerId;
    private final String name;
    private int level;
    private Role role;
    private final Instant joinTime;

    public PartyMember(UUID playerId, String name, int level) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.name = Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("Name cannot be blank");
        if (level < 1) throw new IllegalArgumentException("Level must be >= 1");
        this.level = level;
        this.role = Role.DPS;
        this.joinTime = Instant.now();
    }

    public UUID getPlayerId() { return playerId; }
    public String getName() { return name; }
    public int getLevel() { return level; }
    public Role getRole() { return role; }
    public Instant getJoinTime() { return joinTime; }

    public void setLevel(int level) {
        if (level < 1) throw new IllegalArgumentException("Level must be >= 1");
        this.level = level;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "role");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return playerId.equals(((PartyMember) o).playerId);
    }

    @Override
    public int hashCode() { return playerId.hashCode(); }

    @Override
    public String toString() {
        return name + " (Lv" + level + " " + role + ")";
    }
}
