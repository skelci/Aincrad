package com.aincrad.integration;

import com.aincrad.paper.persistence.DataStore;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Synchronizes player data (level, stats, inventory, position) between the
 * Fabric mod and the Paper plugin.
 * <p>
 * On the Fabric side, data is collected from the player entity and sent
 * to Paper. On the Paper side, data is received and persisted to YAML.
 */
public class PlayerDataSync {

    private static final String DATA_PREFIX = "player_";

    /**
     * Immutable player data record for serialization.
     */
    public record PlayerData(
            UUID playerUuid,
            String playerName,
            int level,
            int hp,
            int maxHp,
            int strength,
            int agility,
            int defense,
            double posX,
            double posY,
            double posZ,
            String dimension,
            long lastUpdated
    ) {
        /**
         * Serializes this player data to a byte array.
         */
        public byte[] serialize() {
            byte[] uuidBytes = playerUuid.toString().getBytes(StandardCharsets.UTF_8);
            byte[] nameBytes = playerName.getBytes(StandardCharsets.UTF_8);
            byte[] dimBytes = dimension.getBytes(StandardCharsets.UTF_8);

            int total = 4 + uuidBytes.length + 4 + nameBytes.length + 4 + 4 + 4 + 4 + 4 + 4
                    + 8 + 8 + 8 + 4 + dimBytes.length + 8;
            ByteBuffer buf = ByteBuffer.allocate(total);

            buf.putInt(uuidBytes.length);
            buf.put(uuidBytes);
            buf.putInt(nameBytes.length);
            buf.put(nameBytes);
            buf.putInt(level);
            buf.putInt(hp);
            buf.putInt(maxHp);
            buf.putInt(strength);
            buf.putInt(agility);
            buf.putInt(defense);
            buf.putDouble(posX);
            buf.putDouble(posY);
            buf.putDouble(posZ);
            buf.putInt(dimBytes.length);
            buf.put(dimBytes);
            buf.putLong(lastUpdated);

            return buf.array();
        }

        /**
         * Deserializes player data from a byte array.
         */
        public static PlayerData deserialize(byte[] data) {
            ByteBuffer buf = ByteBuffer.wrap(data);

            int uuidLen = buf.getInt();
            byte[] uuidBytes = new byte[uuidLen];
            buf.get(uuidBytes);
            UUID uuid = UUID.fromString(new String(uuidBytes, StandardCharsets.UTF_8));

            int nameLen = buf.getInt();
            byte[] nameBytes = new byte[nameLen];
            buf.get(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);

            int level = buf.getInt();
            int hp = buf.getInt();
            int maxHp = buf.getInt();
            int strength = buf.getInt();
            int agility = buf.getInt();
            int defense = buf.getInt();
            double posX = buf.getDouble();
            double posY = buf.getDouble();
            double posZ = buf.getDouble();

            int dimLen = buf.getInt();
            byte[] dimBytes = new byte[dimLen];
            buf.get(dimBytes);
            String dimension = new String(dimBytes, StandardCharsets.UTF_8);

            long lastUpdated = buf.getLong();

            return new PlayerData(uuid, name, level, hp, maxHp, strength, agility,
                    defense, posX, posY, posZ, dimension, lastUpdated);
        }
    }

    /**
     * Collects player data from the Fabric mod side.
     * In a real implementation, this would read from the player entity.
     *
     * @param playerUuid the player's UUID string
     * @return the collected player data, or null if player not found
     */
    public PlayerData collect(String playerUuid) {
        // In a real implementation, this would read from the Minecraft player entity
        // For now, return a placeholder that can be populated by the caller
        UUID uuid = UUID.fromString(playerUuid);
        return new PlayerData(
                uuid,
                "Player_" + playerUuid.substring(0, 8),
                1,      // level
                20,     // hp
                20,     // maxHp
                5,      // strength
                5,      // agility
                5,      // defense
                0.0,    // posX
                64.0,   // posY
                0.0,    // posZ
                "minecraft:overworld",
                System.currentTimeMillis()
        );
    }

    /**
     * Fetches raw player data bytes from the Paper plugin's data store.
     *
     * @param playerUuid the player's UUID string
     * @return raw serialized data, or null if not found
     */
    public byte[] fetchRaw(String playerUuid) {
        // In a real implementation, this would read from the DataStore
        // For now, return null to indicate no cached data
        return null;
    }

    /**
     * Persists player data to YAML via the DataStore.
     *
     * @param data      the player data to persist
     * @param dataStore the Paper DataStore
     */
    public void persist(PlayerData data, DataStore dataStore) {
        FileConfiguration config = dataStore.load(DATA_PREFIX + data.playerUuid());
        config.set("name", data.playerName());
        config.set("level", data.level());
        config.set("hp", data.hp());
        config.set("maxHp", data.maxHp());
        config.set("stats.strength", data.strength());
        config.set("stats.agility", data.agility());
        config.set("stats.defense", data.defense());
        config.set("position.x", data.posX());
        config.set("position.y", data.posY());
        config.set("position.z", data.posZ());
        config.set("position.dimension", data.dimension());
        config.set("lastUpdated", data.lastUpdated());
        dataStore.save(DATA_PREFIX + data.playerUuid(), config);
    }

    /**
     * Loads player data from YAML via the DataStore.
     *
     * @param playerUuid the player's UUID string
     * @param dataStore  the Paper DataStore
     * @return the loaded player data, or null if not found
     */
    public PlayerData load(String playerUuid, DataStore dataStore) {
        FileConfiguration config = dataStore.load(DATA_PREFIX + playerUuid);
        if (config.get("name") == null) {
            return null;
        }
        return new PlayerData(
                UUID.fromString(playerUuid),
                config.getString("name", "Unknown"),
                config.getInt("level", 1),
                config.getInt("hp", 20),
                config.getInt("maxHp", 20),
                config.getInt("stats.strength", 5),
                config.getInt("stats.agility", 5),
                config.getInt("stats.defense", 5),
                config.getDouble("position.x", 0.0),
                config.getDouble("position.y", 64.0),
                config.getDouble("position.z", 0.0),
                config.getString("position.dimension", "minecraft:overworld"),
                config.getLong("lastUpdated", System.currentTimeMillis())
        );
    }
}
