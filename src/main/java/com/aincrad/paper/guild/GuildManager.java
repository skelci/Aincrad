package com.aincrad.paper.guild;

import com.aincrad.paper.persistence.DataStore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton manager for all guilds on the server.
 * Handles lifecycle, membership, permissions, and persistence.
 */
public final class GuildManager {
    private static GuildManager instance;

    private final Map<String, Guild> guildsByName = new ConcurrentHashMap<>();
    private final DataStore dataStore;

    private GuildManager(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public static synchronized GuildManager getInstance(DataStore dataStore) {
        if (instance == null) {
            instance = new GuildManager(dataStore);
        }
        return instance;
    }

    public static GuildManager getInstance() {
        if (instance == null) throw new IllegalStateException("GuildManager not initialized");
        return instance;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────

    public Guild createGuild(String name, String tag, UUID founderUuid) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Guild name cannot be blank");
        if (guildsByName.containsKey(name)) throw new IllegalArgumentException("Guild already exists: " + name);
        Guild guild = new Guild(name, tag, founderUuid);
        guildsByName.put(name, guild);
        saveGuild(guild);
        return guild;
    }

    public boolean disbandGuild(String name, UUID actorUuid) {
        Guild guild = guildsByName.get(name);
        if (guild == null) return false;
        if (!guild.getOwnerUuid().equals(actorUuid)) return false;
        guild.markDisbanded();
        guildsByName.remove(name);
        dataStore.delete("guild_" + name);
        return true;
    }

    // ── Membership ────────────────────────────────────────────────────

    public boolean invitePlayer(String guildName, UUID targetUuid, UUID actorUuid) {
        Guild guild = guildsByName.get(guildName);
        if (guild == null) return false;
        GuildRank actorRank = guild.getRank(actorUuid);
        if (actorRank == null || !actorRank.has(GuildRank.Permission.INVITE)) return false;
        return guild.addMember(targetUuid);
    }

    public boolean kickMember(String guildName, UUID targetUuid, UUID actorUuid) {
        Guild guild = guildsByName.get(guildName);
        if (guild == null) return false;
        GuildRank actorRank = guild.getRank(actorUuid);
        if (actorRank == null || !actorRank.has(GuildRank.Permission.KICK_MEMBER)) return false;
        if (targetUuid.equals(actorUuid)) return false;
        GuildRank targetRank = guild.getRank(targetUuid);
        if (targetRank == null || !actorRank.canManage(targetRank)) return false;
        return guild.removeMember(targetUuid);
    }

    public boolean setRank(String guildName, UUID targetUuid, GuildRank newRank, UUID actorUuid) {
        Guild guild = guildsByName.get(guildName);
        if (guild == null) return false;
        GuildRank actorRank = guild.getRank(actorUuid);
        if (actorRank == null || !actorRank.has(GuildRank.Permission.MANAGE_SKILLS)) return false;
        GuildRank targetRank = guild.getRank(targetUuid);
        if (targetRank == null || !actorRank.canManage(targetRank)) return false;
        return guild.setRank(targetUuid, newRank);
    }

    public boolean transferOwnership(String guildName, UUID from, UUID to) {
        Guild guild = guildsByName.get(guildName);
        if (guild == null) return false;
        if (!guild.getOwnerUuid().equals(from)) return false;
        return guild.transferOwner(from, to);
    }

    // ── Skills ────────────────────────────────────────────────────────

    public boolean trainSkill(String guildName, GuildSkill skill, UUID actorUuid) {
        Guild guild = guildsByName.get(guildName);
        if (guild == null) return false;
        GuildRank actorRank = guild.getRank(actorUuid);
        if (actorRank == null || !actorRank.has(GuildRank.Permission.MANAGE_SKILLS)) return false;
        return guild.trainSkill(skill);
    }

    // ── Lookups ───────────────────────────────────────────────────────

    public Guild findByName(String name) { return guildsByName.get(name); }

    public Guild findGuildOf(UUID uuid) {
        for (Guild guild : guildsByName.values()) {
            if (guild.isMember(uuid)) return guild;
        }
        return null;
    }

    public List<Guild> listGuilds() {
        return List.copyOf(guildsByName.values());
    }

    public List<Guild> searchByName(String query) {
        String lower = query.toLowerCase();
        return guildsByName.values().stream()
                .filter(g -> g.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ── Persistence ───────────────────────────────────────────────────

    public void saveGuild(Guild guild) {
        FileConfiguration config = dataStore.load("guild_" + guild.getName());
        config.set("name", guild.getName());
        config.set("tag", guild.getTag());
        config.set("owner", guild.getOwnerUuid().toString());
        config.set("createdAt", guild.getCreatedAt().toString());
        config.set("notice", guild.getNotice());
        config.set("xp.total", guild.getXp().getTotalXp());
        config.set("xp.level", guild.getXp().getLevel());

        // Members
        ConfigurationSection membersSection = config.createSection("members");
        for (Map.Entry<UUID, GuildRank> entry : guild.getMembers().entrySet()) {
            membersSection.set(entry.getKey().toString(), entry.getValue().name());
        }

        // Skills
        ConfigurationSection skillsSection = config.createSection("skills");
        for (Map.Entry<GuildSkill, Integer> entry : guild.getSkillRanks().entrySet()) {
            skillsSection.set(entry.getKey().name(), entry.getValue());
        }

        dataStore.save("guild_" + guild.getName(), config);
    }

    public void loadAll() {
        // Guilds are loaded on-demand via findByName
        // This method can be called to preload if needed
    }

    public void saveAll() {
        for (Guild guild : guildsByName.values()) {
            saveGuild(guild);
        }
    }

    public void reset() {
        guildsByName.clear();
    }
}
