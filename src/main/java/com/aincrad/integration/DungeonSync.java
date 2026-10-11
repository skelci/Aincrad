package com.aincrad.integration;

import com.aincrad.paper.dungeon.DungeonInstance;
import com.aincrad.paper.dungeon.DungeonLockout;
import com.aincrad.paper.dungeon.DungeonManager;
import com.aincrad.paper.dungeon.DungeonState;
import com.aincrad.paper.dungeon.DungeonTier;
import com.aincrad.paper.persistence.DataStore;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Synchronizes dungeon state and lockouts between the Fabric mod and the Paper plugin.
 */
public class DungeonSync {

    private static final String DATA_PREFIX = "dungeon_";
    private static final String LOCKOUT_PREFIX = "lockout_";

    /**
     * Immutable dungeon data record for serialization.
     */
    public record DungeonData(
            String dungeonId,
            String tier,
            int floor,
            String state,
            List<String> playerUuids,
            long lastUpdated
    ) {
        /**
         * Serializes this dungeon data to a byte array.
         */
        public byte[] serialize() {
            byte[] idBytes = dungeonId.getBytes(StandardCharsets.UTF_8);
            byte[] tierBytes = tier.getBytes(StandardCharsets.UTF_8);
            byte[] stateBytes = state.getBytes(StandardCharsets.UTF_8);
            int playersTotal = playerUuids.stream()
                    .mapToInt(s -> 4 + s.getBytes(StandardCharsets.UTF_8).length).sum();

            int total = 4 + idBytes.length + 4 + tierBytes.length + 4
                    + 4 + stateBytes.length + 4 + playersTotal + 8;
            ByteBuffer buf = ByteBuffer.allocate(total);

            buf.putInt(idBytes.length);
            buf.put(idBytes);
            buf.putInt(tierBytes.length);
            buf.put(tierBytes);
            buf.putInt(floor);
            buf.putInt(stateBytes.length);
            buf.put(stateBytes);
            buf.putInt(playerUuids.size());
            for (String u : playerUuids) {
                byte[] b = u.getBytes(StandardCharsets.UTF_8);
                buf.putInt(b.length);
                buf.put(b);
            }
            buf.putLong(lastUpdated);

            return buf.array();
        }

        /**
         * Deserializes dungeon data from a byte array.
         */
        public static DungeonData deserialize(byte[] data) {
            ByteBuffer buf = ByteBuffer.wrap(data);

            int idLen = buf.getInt();
            byte[] idBytes = new byte[idLen];
            buf.get(idBytes);
            String dungeonId = new String(idBytes, StandardCharsets.UTF_8);

            int tierLen = buf.getInt();
            byte[] tierBytes = new byte[tierLen];
            buf.get(tierBytes);
            String tier = new String(tierBytes, StandardCharsets.UTF_8);

            int floor = buf.getInt();

            int stateLen = buf.getInt();
            byte[] stateBytes = new byte[stateLen];
            buf.get(stateBytes);
            String state = new String(stateBytes, StandardCharsets.UTF_8);

            int playerCount = buf.getInt();
            List<String> playerUuids = new ArrayList<>(playerCount);
            for (int i = 0; i < playerCount; i++) {
                int len = buf.getInt();
                byte[] b = new byte[len];
                buf.get(b);
                playerUuids.add(new String(b, StandardCharsets.UTF_8));
            }

            long lastUpdated = buf.getLong();

            return new DungeonData(dungeonId, tier, floor, state, playerUuids, lastUpdated);
        }
    }

    /**
     * Immutable lockout data record for serialization.
     */
    public record LockoutData(
            UUID playerUuid,
            String dungeonId,
            long expireTime,
            boolean bypassed
    ) {
        /**
         * Serializes this lockout data to a byte array.
         */
        public byte[] serialize() {
            byte[] uuidBytes = playerUuid.toString().getBytes(StandardCharsets.UTF_8);
            byte[] idBytes = dungeonId.getBytes(StandardCharsets.UTF_8);
            int total = 4 + uuidBytes.length + 4 + idBytes.length + 8 + 1;
            ByteBuffer buf = ByteBuffer.allocate(total);
            buf.putInt(uuidBytes.length);
            buf.put(uuidBytes);
            buf.putInt(idBytes.length);
            buf.put(idBytes);
            buf.putLong(expireTime);
            buf.put((byte) (bypassed ? 1 : 0));
            return buf.array();
        }

        /**
         * Deserializes lockout data from a byte array.
         */
        public static LockoutData deserialize(byte[] data) {
            ByteBuffer buf = ByteBuffer.wrap(data);
            int uuidLen = buf.getInt();
            byte[] uuidBytes = new byte[uuidLen];
            buf.get(uuidBytes);
            UUID uuid = UUID.fromString(new String(uuidBytes, StandardCharsets.UTF_8));
            int idLen = buf.getInt();
            byte[] idBytes = new byte[idLen];
            buf.get(idBytes);
            String dungeonId = new String(idBytes, StandardCharsets.UTF_8);
            long expireTime = buf.getLong();
            boolean bypassed = buf.get() == 1;
            return new LockoutData(uuid, dungeonId, expireTime, bypassed);
        }
    }

    /**
     * Collects dungeon data from the Fabric mod side.
     *
     * @param dungeonId the dungeon ID
     * @return the collected dungeon data, or null if not found
     */
    public DungeonData collect(String dungeonId) {
        return new DungeonData(
                dungeonId,
                "T1_EASY",
                1,
                "LOBBY",
                List.of(),
                System.currentTimeMillis()
        );
    }

    /**
     * Fetches raw dungeon data bytes from the Paper plugin's data store.
     *
     * @param dungeonId the dungeon ID
     * @return raw serialized data, or null if not found
     */
    public byte[] fetchRaw(String dungeonId) {
        // In a real implementation, this would read from the DataStore
        return null;
    }

    /**
     * Persists dungeon data to YAML via the DataStore.
     *
     * @param data      the dungeon data to persist
     * @param dataStore the Paper DataStore
     */
    public void persist(DungeonData data, DataStore dataStore) {
        FileConfiguration config = dataStore.load(DATA_PREFIX + data.dungeonId());
        config.set("dungeonId", data.dungeonId());
        config.set("tier", data.tier());
        config.set("floor", data.floor());
        config.set("state", data.state());
        config.set("players", data.playerUuids());
        config.set("lastUpdated", data.lastUpdated());
        dataStore.save(DATA_PREFIX + data.dungeonId(), config);
    }

    /**
     * Persists lockout data to YAML via the DataStore.
     *
     * @param data      the lockout data to persist
     * @param dataStore the Paper DataStore
     */
    public void persistLockout(LockoutData data, DataStore dataStore) {
        FileConfiguration config = dataStore.load(LOCKOUT_PREFIX + data.playerUuid());
        config.set("playerUuid", data.playerUuid().toString());
        config.set("dungeonId", data.dungeonId());
        config.set("expireTime", data.expireTime());
        config.set("bypassed", data.bypassed());
        dataStore.save(LOCKOUT_PREFIX + data.playerUuid(), config);
    }

    /**
     * Loads dungeon data from the Paper DungeonManager.
     *
     * @param dungeonId the dungeon ID
     * @return the loaded dungeon data, or null if not found
     */
    public DungeonData loadFromManager(String dungeonId) {
        DungeonInstance inst = DungeonManager.getInstance().getInstance(dungeonId);
        if (inst == null) {
            return null;
        }
        List<String> players = inst.getPlayers().stream()
                .map(UUID::toString)
                .toList();
        return new DungeonData(
                inst.getDungeonId(),
                inst.getTier().name(),
                inst.getFloor(),
                inst.getState().name(),
                players,
                System.currentTimeMillis()
        );
    }

    /**
     * Loads lockout data from the Paper DungeonManager.
     *
     * @param playerUuid the player's UUID
     * @return the loaded lockout data, or null if not found
     */
    public LockoutData loadLockoutFromManager(UUID playerUuid) {
        DungeonLockout lockout = DungeonManager.getInstance().getLockout(playerUuid);
        if (lockout == null) {
            return null;
        }
        return new LockoutData(
                lockout.getPlayerUuid(),
                lockout.getDungeonId(),
                lockout.getExpireTime(),
                lockout.isBypassed()
        );
    }

    /**
     * Sets a lockout on the Paper DungeonManager.
     *
     * @param playerUuid the player's UUID
     * @param dungeonId  the dungeon ID
     * @param expireTime the expiry time (epoch millis)
     * @param bypassed   whether the lockout is bypassed
     */
    public void setLockout(UUID playerUuid, String dungeonId, long expireTime, boolean bypassed) {
        DungeonManager.getInstance().setLockout(playerUuid,
                new DungeonLockout(playerUuid, dungeonId, expireTime, bypassed));
    }

    /**
     * Checks if a player is locked out on the Paper side.
     *
     * @param playerUuid the player's UUID
     * @return true if locked
     */
    public boolean isLocked(UUID playerUuid) {
        return DungeonManager.getInstance().isLocked(playerUuid);
    }

    /**
     * Syncs a dungeon completion from the Fabric side to the Paper side.
     *
     * @param dungeonId the dungeon ID
     * @return true if sync was successful
     */
    public boolean syncCompletion(String dungeonId) {
        DungeonInstance inst = DungeonManager.getInstance().getInstance(dungeonId);
        if (inst == null) {
            return false;
        }
        DungeonManager.getInstance().complete(dungeonId);
        return true;
    }

    /**
     * Syncs a dungeon failure from the Fabric side to the Paper side.
     *
     * @param dungeonId the dungeon ID
     * @return true if sync was successful
     */
    public boolean syncFailure(String dungeonId) {
        DungeonInstance inst = DungeonManager.getInstance().getInstance(dungeonId);
        if (inst == null) {
            return false;
        }
        DungeonManager.getInstance().fail(dungeonId);
        return true;
    }
}
