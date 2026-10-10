package com.aincrad.social.guild;

import java.util.EnumSet;
import java.util.Set;

/**
 * Rank hierarchy within an Aincrad guild.
 * Each rank carries an immutable permission set; higher ranks inherit
 * lower-rank permissions implicitly through {@link #can(GuildRank)}.
 */
public enum GuildRank {

    /**
     * Guild leader. Holds every permission and can never be kicked or
     * demoted by another member. Only transfer or disband removes a MASTER.
     */
    MASTER("Master", 3, Permission.values()),

    /**
     * Officer tier. Can invite, kick members, manage the bank and edit
     * the notice. Cannot disband or appoint other officers beyond cap.
     */
    OFFICER("Officer", 2,
            Permission.INVITE, Permission.KICK_MEMBER,
            Permission.BANK_DEPOSIT, Permission.BANK_WITHDRAW,
            Permission.EDIT_NOTICE, Permission.MANAGE_SKILLS),

    /**
     * Standard member. Can deposit into the bank, contribute XP and
     * view guild information. Cannot invite or manage others.
     */
    MEMBER("Member", 1,
            Permission.BANK_DEPOSIT, Permission.VIEW_GUILD);

    /** Human-readable rank label (chat display). */
    private final String displayName;

    /** Hierarchical weight — higher outranks lower. */
    private final int weight;

    /** Immutable permission set granted at this rank. */
    private final Set<Permission> permissions;

    GuildRank(String displayName, int weight, Permission... permissions) {
        this.displayName = displayName;
        this.weight = weight;
        this.permissions = permissions.length == 0
                ? EnumSet.noneOf(Permission.class)
                : EnumSet.copyOf(Set.of(permissions));
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getWeight() {
        return weight;
    }

    /** Unmodifiable view of this rank's granted permissions. */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /** Returns true if this rank is strictly higher than {@code other}. */
    public boolean outranks(GuildRank other) {
        return this.weight > other.weight;
    }

    /** Returns true if {@code permission} is granted at this rank. */
    public boolean has(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * Returns true if this rank can issue orders to {@code target}.
     * A rank may never manage another of equal or greater weight.
     */
    public boolean canManage(GuildRank target) {
        return outranks(target);
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Guild permissions used by {@link GuildRank} checks.
     * Each maps to a guarded action in {@link GuildManager} and {@link Guild}.
     */
    public enum Permission {
        /** Create guilds (global, not rank-scoped but stored for completeness). */
        CREATE_GUILD,
        /** Invite new players to the guild. */
        INVITE,
        /** Remove standard members from the guild. */
        KICK_MEMBER,
        /** Deposit items into the guild bank. */
        BANK_DEPOSIT,
        /** Withdraw items from the guild bank. */
        BANK_WITHDRAW,
        /** Edit the guild notice / MOTD. */
        EDIT_NOTICE,
        /** Spend guild resources on skill rank-ups. */
        MANAGE_SKILLS,
        /** View guild roster and stats. */
        VIEW_GUILD
    }
}
