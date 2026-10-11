package com.aincrad.integration;

import com.aincrad.paper.party.Party;
import com.aincrad.paper.party.PartyLootRule;
import com.aincrad.paper.party.PartyManager;
import com.aincrad.paper.persistence.DataStore;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Synchronizes party data (membership, loot rules) between the Fabric mod
 * and the Paper plugin.
 */
public class PartySync {

    private static final String DATA_PREFIX = "party_";

    /**
     * Immutable party data record for serialization.
     */
    public record PartyData(
            String partyId,
            String leaderUuid,
            String lootRule,
            String joinMode,
            int memberCount,
            List<String> memberUuids,
            long lastUpdated
    ) {
        /**
         * Serializes this party data to a byte array.
         */
        public byte[] serialize() {
            byte[] idBytes = partyId.getBytes(StandardCharsets.UTF_8);
            byte[] leaderBytes = leaderUuid.getBytes(StandardCharsets.UTF_8);
            byte[] lootBytes = lootRule.getBytes(StandardCharsets.UTF_8);
            byte[] joinBytes = joinMode.getBytes(StandardCharsets.UTF_8);

            int total = 4 + idBytes.length + 4 + leaderBytes.length
                    + 4 + lootBytes.length + 4 + joinBytes.length
                    + 4 + 4 + memberUuids.size() * 36 + 8;
            ByteBuffer buf = ByteBuffer.allocate(total);

            buf.putInt(idBytes.length);
            buf.put(idBytes);
            buf.putInt(leaderBytes.length);
            buf.put(leaderBytes);
            buf.putInt(lootBytes.length);
            buf.put(lootBytes);
            buf.putInt(joinBytes.length);
            buf.put(joinBytes);
            buf.putInt(memberCount);
            buf.putInt(memberUuids.size());
            for (String uuid : memberUuids) {
                byte[] u = uuid.getBytes(StandardCharsets.UTF_8);
                buf.putInt(u.length);
                buf.put(u);
            }
            buf.putLong(lastUpdated);

            return buf.array();
        }

        /**
         * Deserializes party data from a byte array.
         */
        public static PartyData deserialize(byte[] data) {
            ByteBuffer buf = ByteBuffer.wrap(data);

            int idLen = buf.getInt();
            byte[] idBytes = new byte[idLen];
            buf.get(idBytes);
            String partyId = new String(idBytes, StandardCharsets.UTF_8);

            int leaderLen = buf.getInt();
            byte[] leaderBytes = new byte[leaderLen];
            buf.get(leaderBytes);
            String leaderUuid = new String(leaderBytes, StandardCharsets.UTF_8);

            int lootLen = buf.getInt();
            byte[] lootBytes = new byte[lootLen];
            buf.get(lootBytes);
            String lootRule = new String(lootBytes, StandardCharsets.UTF_8);

            int joinLen = buf.getInt();
            byte[] joinBytes = new byte[joinLen];
            buf.get(joinBytes);
            String joinMode = new String(joinBytes, StandardCharsets.UTF_8);

            int memberCount = buf.getInt();
            int uuidCount = buf.getInt();
            List<String> memberUuids = new ArrayList<>(uuidCount);
            for (int i = 0; i < uuidCount; i++) {
                int uLen = buf.getInt();
                byte[] uBytes = new byte[uLen];
                buf.get(uBytes);
                memberUuids.add(new String(uBytes, StandardCharsets.UTF_8));
            }

            long lastUpdated = buf.getLong();

            return new PartyData(partyId, leaderUuid, lootRule, joinMode,
                    memberCount, memberUuids, lastUpdated);
        }
    }

    /**
     * Collects party data from the Fabric mod side.
     *
     * @param partyId the party ID
     * @return the collected party data, or null if party not found
     */
    public PartyData collect(String partyId) {
        // In a real implementation, this would read from the Fabric mod's party system
        return new PartyData(
                partyId,
                UUID.randomUUID().toString(),
                PartyLootRule.NEED_GREED.name(),
                Party.JoinMode.INVITE_ONLY.name(),
                1,
                List.of(UUID.randomUUID().toString()),
                System.currentTimeMillis()
        );
    }

    /**
     * Fetches raw party data bytes from the Paper plugin's data store.
     *
     * @param partyId the party ID
     * @return raw serialized data, or null if not found
     */
    public byte[] fetchRaw(String partyId) {
        // In a real implementation, this would read from the DataStore
        return null;
    }

    /**
     * Persists party data to YAML via the DataStore.
     *
     * @param data      the party data to persist
     * @param dataStore the Paper DataStore
     */
    public void persist(PartyData data, DataStore dataStore) {
        FileConfiguration config = dataStore.load(DATA_PREFIX + data.partyId());
        config.set("partyId", data.partyId());
        config.set("leader", data.leaderUuid());
        config.set("lootRule", data.lootRule());
        config.set("joinMode", data.joinMode());
        config.set("lastUpdated", data.lastUpdated());

        org.bukkit.configuration.ConfigurationSection members = config.createSection("members");
        for (String uuid : data.memberUuids()) {
            members.set(uuid, "member");
        }

        dataStore.save(DATA_PREFIX + data.partyId(), config);
    }

    /**
     * Loads party data from the Paper PartyManager.
     *
     * @param partyId the party ID
     * @return the loaded party data, or null if not found
     */
    public PartyData loadFromManager(String partyId) {
        Party party = PartyManager.getInstance().getParty(UUID.fromString(partyId));
        if (party == null) {
            return null;
        }
        List<String> memberUuids = new ArrayList<>();
        for (var member : party.getMembers()) {
            memberUuids.add(member.getPlayerId().toString());
        }
        return new PartyData(
                party.getPartyId().toString(),
                party.getLeaderId().toString(),
                party.getLootRule().name(),
                party.getJoinMode().name(),
                party.getMemberCount(),
                memberUuids,
                System.currentTimeMillis()
        );
    }
}
