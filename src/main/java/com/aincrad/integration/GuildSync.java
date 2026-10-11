package com.aincrad.integration;

import com.aincrad.paper.guild.Guild;
import com.aincrad.paper.guild.GuildManager;
import com.aincrad.paper.guild.GuildRank;
import com.aincrad.paper.persistence.DataStore;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Synchronizes guild data (membership, ranks, bank) between the Fabric mod
 * and the Paper plugin.
 * <p>
 * On the Fabric side, guild data is collected from the mod's guild system
 * and sent to Paper. On the Paper side, data is received and persisted to YAML.
 */
public class GuildSync {

    private static final String DATA_PREFIX = "guild_";

    /**
     * Immutable guild data record for serialization.
     */
    public record GuildData(
            String name,
            String tag,
            String ownerUuid,
            int level,
            long totalXp,
            int memberCount,
            String notice,
            long lastUpdated
    ) {
        /**
         * Serializes this guild data to a byte array.
         */
        public byte[] serialize() {
            byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
            byte[] tagBytes = tag.getBytes(StandardCharsets.UTF_8);
            byte[] ownerBytes = ownerUuid.getBytes(StandardCharsets.UTF_8);
            byte[] noticeBytes = notice.getBytes(StandardCharsets.UTF_8);

            int total = 4 + nameBytes.length + 4 + tagBytes.length + 4 + ownerBytes.length
                    + 4 + 8 + 4 + 4 + noticeBytes.length + 8;
            ByteBuffer buf = ByteBuffer.allocate(total);

            buf.putInt(nameBytes.length);
            buf.put(nameBytes);
            buf.putInt(tagBytes.length);
            buf.put(tagBytes);
            buf.putInt(ownerBytes.length);
            buf.put(ownerBytes);
            buf.putInt(level);
            buf.putLong(totalXp);
            buf.putInt(memberCount);
            buf.putInt(noticeBytes.length);
            buf.put(noticeBytes);
            buf.putLong(lastUpdated);

            return buf.array();
        }

        /**
         * Deserializes guild data from a byte array.
         */
        public static GuildData deserialize(byte[] data) {
            ByteBuffer buf = ByteBuffer.wrap(data);

            int nameLen = buf.getInt();
            byte[] nameBytes = new byte[nameLen];
            buf.get(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);

            int tagLen = buf.getInt();
            byte[] tagBytes = new byte[tagLen];
            buf.get(tagBytes);
            String tag = new String(tagBytes, StandardCharsets.UTF_8);

            int ownerLen = buf.getInt();
            byte[] ownerBytes = new byte[ownerLen];
            buf.get(ownerBytes);
            String ownerUuid = new String(ownerBytes, StandardCharsets.UTF_8);

            int level = buf.getInt();
            long totalXp = buf.getLong();
            int memberCount = buf.getInt();

            int noticeLen = buf.getInt();
            byte[] noticeBytes = new byte[noticeLen];
            buf.get(noticeBytes);
            String notice = new String(noticeBytes, StandardCharsets.UTF_8);

            long lastUpdated = buf.getLong();

            return new GuildData(name, tag, ownerUuid, level, totalXp, memberCount, notice, lastUpdated);
        }
    }

    /**
     * Collects guild data from the Fabric mod side.
     *
     * @param guildName the guild name
     * @return the collected guild data, or null if guild not found
     */
    public GuildData collect(String guildName) {
        // In a real implementation, this would read from the Fabric mod's guild system
        // For now, return a placeholder
        return new GuildData(
                guildName,
                guildName.substring(0, Math.min(4, guildName.length())).toUpperCase(),
                UUID.randomUUID().toString(),
                1,      // level
                0,      // totalXp
                1,      // memberCount
                "",     // notice
                System.currentTimeMillis()
        );
    }

    /**
     * Fetches raw guild data bytes from the Paper plugin's data store.
     *
     * @param guildName the guild name
     * @return raw serialized data, or null if not found
     */
    public byte[] fetchRaw(String guildName) {
        // In a real implementation, this would read from the DataStore
        return null;
    }

    /**
     * Persists guild data to YAML via the DataStore.
     *
     * @param data      the guild data to persist
     * @param dataStore the Paper DataStore
     */
    public void persist(GuildData data, DataStore dataStore) {
        FileConfiguration config = dataStore.load(DATA_PREFIX + data.name());
        config.set("name", data.name());
        config.set("tag", data.tag());
        config.set("owner", data.ownerUuid());
        config.set("xp.level", data.level());
        config.set("xp.total", data.totalXp());
        config.set("notice", data.notice());
        config.set("lastUpdated", data.lastUpdated());
        dataStore.save(DATA_PREFIX + data.name(), config);
    }

    /**
     * Loads guild data from the Paper GuildManager.
     *
     * @param guildName the guild name
     * @return the loaded guild data, or null if not found
     */
    public GuildData loadFromManager(String guildName) {
        Guild guild = GuildManager.getInstance().findByName(guildName);
        if (guild == null) {
            return null;
        }
        return new GuildData(
                guild.getName(),
                guild.getTag(),
                guild.getOwnerUuid().toString(),
                guild.getXp().getLevel(),
                guild.getXp().getTotalXp(),
                guild.getMemberCount(),
                guild.getNotice(),
                System.currentTimeMillis()
        );
    }

    /**
     * Synchronizes guild membership from the Paper GuildManager to the DataStore.
     *
     * @param guildName the guild name
     * @return true if sync was successful
     */
    public boolean syncFromManager(String guildName) {
        Guild guild = GuildManager.getInstance().findByName(guildName);
        if (guild == null) {
            return false;
        }
        GuildData data = new GuildData(
                guild.getName(),
                guild.getTag(),
                guild.getOwnerUuid().toString(),
                guild.getXp().getLevel(),
                guild.getXp().getTotalXp(),
                guild.getMemberCount(),
                guild.getNotice(),
                System.currentTimeMillis()
        );
        // Persist via the GuildManager's own save mechanism
        GuildManager.getInstance().saveGuild(guild);
        return true;
    }
}
