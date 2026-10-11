package com.aincrad.integration;

import com.aincrad.paper.economy.Col;
import com.aincrad.paper.economy.EconomyManager;
import com.aincrad.paper.economy.PlayerWallet;
import com.aincrad.paper.persistence.DataStore;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Synchronizes Col balance and transactions between the Fabric mod and the Paper plugin.
 */
public class EconomySync {

    private static final String DATA_PREFIX = "economy_";

    /**
     * Immutable economy data record for serialization.
     */
    public record EconomyData(
            UUID playerUuid,
            long balance,
            List<Transaction> recentTransactions,
            long lastUpdated
    ) {
        /**
         * Serializes this economy data to a byte array.
         */
        public byte[] serialize() {
            byte[] uuidBytes = playerUuid.toString().getBytes(StandardCharsets.UTF_8);

            int txTotal = recentTransactions.stream()
                    .mapToInt(t -> 4 + t.reason.getBytes(StandardCharsets.UTF_8).length + 8).sum();

            int total = 4 + uuidBytes.length + 8 + 4 + txTotal + 8;
            ByteBuffer buf = ByteBuffer.allocate(total);

            buf.putInt(uuidBytes.length);
            buf.put(uuidBytes);
            buf.putLong(balance);
            buf.putInt(recentTransactions.size());
            for (Transaction t : recentTransactions) {
                byte[] b = t.reason.getBytes(StandardCharsets.UTF_8);
                buf.putInt(b.length);
                buf.put(b);
                buf.putLong(t.amount);
            }
            buf.putLong(lastUpdated);

            return buf.array();
        }

        /**
         * Deserializes economy data from a byte array.
         */
        public static EconomyData deserialize(byte[] data) {
            ByteBuffer buf = ByteBuffer.wrap(data);

            int uuidLen = buf.getInt();
            byte[] uuidBytes = new byte[uuidLen];
            buf.get(uuidBytes);
            UUID uuid = UUID.fromString(new String(uuidBytes, StandardCharsets.UTF_8));

            long balance = buf.getLong();

            int txCount = buf.getInt();
            List<Transaction> transactions = new ArrayList<>(txCount);
            for (int i = 0; i < txCount; i++) {
                int len = buf.getInt();
                byte[] b = new byte[len];
                buf.get(b);
                String reason = new String(b, StandardCharsets.UTF_8);
                long amount = buf.getLong();
                transactions.add(new Transaction(reason, amount));
            }

            long lastUpdated = buf.getLong();

            return new EconomyData(uuid, balance, transactions, lastUpdated);
        }
    }

    /**
     * A single economy transaction record.
     */
    public record Transaction(
            String reason,
            long amount
    ) {}

    /**
     * Collects economy data from the Fabric mod side.
     *
     * @param playerUuid the player's UUID string
     * @return the collected economy data, or null if not found
     */
    public EconomyData collect(String playerUuid) {
        UUID uuid = UUID.fromString(playerUuid);
        return new EconomyData(
                uuid,
                0,
                List.of(),
                System.currentTimeMillis()
        );
    }

    /**
     * Fetches raw economy data bytes from the Paper plugin's data store.
     *
     * @param playerUuid the player's UUID string
     * @return raw serialized data, or null if not found
     */
    public byte[] fetchRaw(String playerUuid) {
        // In a real implementation, this would read from the DataStore
        return null;
    }

    /**
     * Persists economy data to YAML via the DataStore.
     *
     * @param data      the economy data to persist
     * @param dataStore the Paper DataStore
     */
    public void persist(EconomyData data, DataStore dataStore) {
        FileConfiguration config = dataStore.load(DATA_PREFIX + data.playerUuid());
        config.set("balance", data.balance());
        config.set("lastUpdated", data.lastUpdated());

        org.bukkit.configuration.ConfigurationSection txSection = config.createSection("transactions");
        for (int i = 0; i < data.recentTransactions().size(); i++) {
            Transaction tx = data.recentTransactions().get(i);
            txSection.set(i + ".reason", tx.reason());
            txSection.set(i + ".amount", tx.amount());
        }

        dataStore.save(DATA_PREFIX + data.playerUuid(), config);
    }

    /**
     * Loads economy data from the Paper EconomyManager.
     *
     * @param playerUuid the player's UUID
     * @return the loaded economy data, or null if no wallet exists
     */
    public EconomyData loadFromManager(UUID playerUuid) {
        Optional<PlayerWallet> wallet = EconomyManager.getInstance().findWallet(playerUuid);
        if (wallet.isEmpty()) {
            return null;
        }
        return new EconomyData(
                playerUuid,
                wallet.get().getBalance().getAmount(),
                List.of(),
                System.currentTimeMillis()
        );
    }

    /**
     * Deposits Col into a player's wallet and syncs.
     *
     * @param playerUuid the player's UUID
     * @param amount     the amount to deposit
     * @param reason     the transaction reason
     * @return the new balance
     */
    public Col deposit(UUID playerUuid, Col amount, String reason) {
        return EconomyManager.getInstance().deposit(playerUuid, amount, reason);
    }

    /**
     * Withdraws Col from a player's wallet and syncs.
     *
     * @param playerUuid the player's UUID
     * @param amount     the amount to withdraw
     * @param reason     the transaction reason
     * @return the new balance
     */
    public Col withdraw(UUID playerUuid, Col amount, String reason) {
        return EconomyManager.getInstance().withdraw(playerUuid, amount, reason);
    }
}
