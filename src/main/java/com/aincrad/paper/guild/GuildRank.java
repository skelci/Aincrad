package com.aincrad.paper.guild;

/**
 * Guild rank hierarchy with permissions.
 * MASTER > OFFICER > MEMBER.
 */
public enum GuildRank {
    MASTER("Master", 3, Permission.values()),
    OFFICER("Officer", 2, Permission.INVITE, Permission.KICK_MEMBER,
            Permission.BANK_DEPOSIT, Permission.BANK_WITHDRAW,
            Permission.EDIT_NOTICE, Permission.MANAGE_SKILLS),
    MEMBER("Member", 1, Permission.BANK_DEPOSIT, Permission.VIEW_GUILD);

    private final String displayName;
    private final int weight;
    private final Permission[] permissions;

    GuildRank(String displayName, int weight, Permission... permissions) {
        this.displayName = displayName;
        this.weight = weight;
        this.permissions = permissions;
    }

    public String getDisplayName() { return displayName; }
    public int getWeight() { return weight; }

    public boolean has(Permission perm) {
        for (Permission p : permissions) {
            if (p == perm) return true;
        }
        return false;
    }

    public boolean outranks(GuildRank other) {
        return this.weight > other.weight;
    }

    public boolean canManage(GuildRank target) {
        return outranks(target);
    }

    public enum Permission {
        CREATE_GUILD, INVITE, KICK_MEMBER, BANK_DEPOSIT,
        BANK_WITHDRAW, EDIT_NOTICE, MANAGE_SKILLS, VIEW_GUILD
    }
}
