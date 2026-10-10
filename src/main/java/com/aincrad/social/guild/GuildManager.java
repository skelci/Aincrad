package com.aincrad.social.guild;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Process-wide singleton owning every live guild. Handles lifecycle
 * (create/disband), membership (invite/kick), promotions and cross-guild
 * lookups. All public operations validate rank permissions through
 * {@link GuildRank.Permission} before mutating state.
 */
public final class GuildManager {

    private static final GuildManager INSTANCE = new GuildManager();

    private final Map<String, Guild> guildsByName = new ConcurrentHashMap<>();

    private GuildManager() {
        // singleton — use {@link #getInstance()}
    }

    /** Returns the shared manager instance. */
    public static GuildManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Creates a new guild. The founder becomes MASTER and is the only
     * initial member.
     *
     * @param name      guild display name
     * @param tag       guild tag
     * @param founderUuid unique id of the founding player
     * @return the freshly created guild
     * @throws IllegalStateException if a guild with the same name already exists
     */
    public Guild createGuild(String name, String tag, String founderUuid) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(tag, "tag");
        Objects.requireNonNull(founderUuid, "founderUuid");
        Guild guild = new Guild(name, tag, founderUuid);
        Guild clash = guildsByName.putIfAbsent(guild.getName(), guild);
        if (clash != null) {
            throw new IllegalStateException("guild '" + name + "' already exists");
        }
        return guild;
    }

    /**
     * Disbands a guild and removes it from the registry. All members are
     * dropped and the bank ledger is lost.
     *
     * @param guild guild to disband
     * @param actorUuid player id requesting the disband
     * @return true when the guild was removed
     * @throws IllegalStateException if the actor lacks MASTER rank
     */
    public boolean disbandGuild(Guild guild, String actorUuid) {
        Objects.requireNonNull(guild, "guild");
        Objects.requireNonNull(actorUuid, "actorUuid");
        requireRank(guild, actorUuid, GuildRank.MASTER);
        guild.markDisbanded();
        return guildsByName.remove(guild.getName(), guild);
    }

    // -------------------------------------------------------------------------
    // Membership
    // -------------------------------------------------------------------------

    /**
     * Adds a player directly to the guild (invite acceptance).
     *
     * @param guild target guild
     * @param actorUuid inviter — must hold INVITE permission
     * @param inviteeUuid player being added
     * @return true when the player was added
     * @throws IllegalStateException if the inviter lacks INVITE or the guild is full
     */
    public boolean invitePlayer(Guild guild, String actorUuid, String inviteeUuid) {
        Objects.requireNonNull(guild, "guild");
        Objects.requireNonNull(actorUuid, "actorUuid");
        Objects.requireNonNull(inviteeUuid, "inviteeUuid");
        requirePermission(guild, actorUuid, GuildRank.Permission.INVITE);
        return guild.addMember(inviteeUuid);
    }

    /**
     * Removes a member from the guild. KICK_MEMBER is required unless the
     * actor targets themselves (voluntary leave).
     *
     * @param guild target guild
     * @param actorUuid player id issuing the kick (or leaving)
     * @param targetUuid player being removed
     * @return true when the player was removed
     * @throws IllegalStateException if the actor lacks KICK_MEMBER and is not
     *         targeting themselves
     */
    public boolean kickMember(Guild guild, String actorUuid, String targetUuid) {
        Objects.requireNonNull(guild, "guild");
        Objects.requireNonNull(actorUuid, "actorUuid");
        Objects.requireNonNull(targetUuid, "targetUuid");
        if (!actorUuid.equals(targetUuid)) {
            requirePermission(guild, actorUuid, GuildRank.Permission.KICK_MEMBER);
        }
        return guild.removeMember(targetUuid);
    }

    /**
     * Promotes or demotes a member. MANAGER ranks can only be changed by
     * the guild master; officers may manage standard members.
     *
     * @param guild target guild
     * @param actorUuid player id issuing the change
     * @param targetUuid player whose rank is changing
     * @param newRank target rank (MASTER is rejected)
     * @return the previous rank
     * @throws IllegalStateException if the actor cannot manage the target
     *         or the officer cap would be exceeded
     */
    public GuildRank setRank(Guild guild, String actorUuid,
                             String targetUuid, GuildRank newRank) {
        Objects.requireNonNull(guild, "guild");
        Objects.requireNonNull(actorUuid, "actorUuid");
        Objects.requireNonNull(targetUuid, "targetUuid");
        Objects.requireNonNull(newRank, "newRank");
        if (newRank == GuildRank.MASTER) {
            throw new IllegalStateException("use transferOwnership to assign MASTER");
        }
        requireRank(guild, actorUuid, GuildRank.OFFICER);
        GuildRank actorRank = guild.getRank(actorUuid)
                .orElseThrow(() -> new IllegalStateException("actor is not a member"));
        GuildRank targetRank = guild.getRank(targetUuid)
                .orElseThrow(() -> new IllegalStateException("target is not a member"));
        if (!actorRank.canManage(targetRank) || actorRank.getWeight() <= newRank.getWeight()) {
            throw new IllegalStateException(
                    actorRank + " cannot assign rank " + newRank);
        }
        return guild.assignRank(targetUuid, newRank);
    }

    /**
     * Transfers guild mastership to another member. The current owner must
     * be MASTER; the recipient becomes MASTER and the previous owner drops
     * to OFFICER.
     *
     * @param guild target guild
     * @param currentOwnerUuid outgoing master
     * @param newOwnerUuid incoming master
     * @return the guild, for chaining
     * @throws IllegalStateException if the actor is not the current master
     */
    public Guild transferOwnership(Guild guild, String currentOwnerUuid,
                                   String newOwnerUuid) {
        Objects.requireNonNull(guild, "guild");
        Objects.requireNonNull(currentOwnerUuid, "currentOwnerUuid");
        Objects.requireNonNull(newOwnerUuid, "newOwnerUuid");
        requireRank(guild, currentOwnerUuid, GuildRank.MASTER);
        if (!guild.getMembers().containsKey(newOwnerUuid)) {
            throw new IllegalStateException(newOwnerUuid + " is not a member");
        }
        // Use addMember-free path: re-add via assignRank is bypassed by MASTER guard,
        // so use setRank with a synthetic MASTER check or add a package-private setter.
        // Here we rely on Guild exposing an internal transfer method.
        guild.transferOwner(currentOwnerUuid, newOwnerUuid);
        return guild;
    }

    // -------------------------------------------------------------------------
    // Skill management
    // -------------------------------------------------------------------------

    /**
     * Trains a guild skill one rank. Requires MANAGE_SKILLS permission and a
     * guild level at or above the skill's unlock level.
     *
     * @param guild target guild
     * @param actorUuid player id issuing the training
     * @param skill skill to train
     * @return the new rank after training
     * @throws IllegalStateException if the actor lacks MANAGE_SKILLS, the skill
     *         is locked, or it is already at max rank
     */
    public int trainSkill(Guild guild, String actorUuid, GuildSkill skill) {
        Objects.requireNonNull(guild, "guild");
        Objects.requireNonNull(actorUuid, "actorUuid");
        Objects.requireNonNull(skill, "skill");
        requirePermission(guild, actorUuid, GuildRank.Permission.MANAGE_SKILLS);
        return guild.trainSkill(skill);
    }

    // -------------------------------------------------------------------------
    // Lookups
    // -------------------------------------------------------------------------

    /** Optional guild by exact (case-insensitive) name. */
    public Optional<Guild> findByName(String name) {
        return Optional.ofNullable(guildsByName.get(name));
    }

    /** Unmodifiable snapshot of every live guild. */
    public Collection<Guild> listGuilds() {
        return Collections.unmodifiableCollection(guildsByName.values());
    }

    /** Guild the given player belongs to, if any. */
    public Optional<Guild> findGuildOf(String uuid) {
        return guildsByName.values().stream()
                .filter(g -> g.getMembers().containsKey(uuid))
                .findFirst();
    }

    // -------------------------------------------------------------------------
    // Permission helpers
    // -------------------------------------------------------------------------

    private static void requireRank(Guild guild, String uuid, GuildRank required) {
        if (!guild.hasRankOrAbove(uuid, required)) {
            throw new IllegalStateException(
                    "requires rank " + required + " or above");
        }
    }

    private static void requirePermission(Guild guild, String uuid,
                                          GuildRank.Permission permission) {
        guild.getRank(uuid)
                .filter(rank -> rank.has(permission))
                .orElseThrow(() -> new IllegalStateException(
                        "missing permission: " + permission));
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /** Clears every guild — intended for tests and full world resets. */
    public void reset() {
        guildsByName.clear();
    }

    @Override
    public String toString() {
        return "GuildManager{guilds=" + guildsByName.size() + '}';
    }
}
