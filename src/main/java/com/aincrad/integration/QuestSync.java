package com.aincrad.integration;

import com.aincrad.paper.persistence.DataStore;
import com.aincrad.paper.quest.Quest;
import com.aincrad.paper.quest.QuestManager;
import com.aincrad.paper.quest.QuestTracker;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Synchronizes quest progress and rewards between the Fabric mod and the Paper plugin.
 */
public class QuestSync {

    private static final String DATA_PREFIX = "quest_progress_";

    /**
     * Immutable quest progress data record for serialization.
     */
    public record QuestProgressData(
            UUID playerUuid,
            List<String> activeQuests,
            List<String> completedQuests,
            Map<String, Integer> completionCounts,
            Map<String, Long> cooldownExpiries,
            long lastUpdated
    ) {
        /**
         * Serializes this quest progress data to a byte array.
         */
        public byte[] serialize() {
            byte[] uuidBytes = playerUuid.toString().getBytes(StandardCharsets.UTF_8);

            int activeTotal = activeQuests.stream()
                    .mapToInt(s -> 4 + s.getBytes(StandardCharsets.UTF_8).length).sum();
            int completedTotal = completedQuests.stream()
                    .mapToInt(s -> 4 + s.getBytes(StandardCharsets.UTF_8).length).sum();
            int countsTotal = completionCounts.entrySet().stream()
                    .mapToInt(e -> 4 + e.getKey().getBytes(StandardCharsets.UTF_8).length + 4).sum();
            int cooldownTotal = cooldownExpiries.entrySet().stream()
                    .mapToInt(e -> 4 + e.getKey().getBytes(StandardCharsets.UTF_8).length + 8).sum();

            int total = 4 + uuidBytes.length + 4 + activeTotal + 4 + completedTotal
                    + 4 + countsTotal + 4 + cooldownTotal + 8;
            ByteBuffer buf = ByteBuffer.allocate(total);

            buf.putInt(uuidBytes.length);
            buf.put(uuidBytes);

            buf.putInt(activeQuests.size());
            for (String q : activeQuests) {
                byte[] b = q.getBytes(StandardCharsets.UTF_8);
                buf.putInt(b.length);
                buf.put(b);
            }

            buf.putInt(completedQuests.size());
            for (String q : completedQuests) {
                byte[] b = q.getBytes(StandardCharsets.UTF_8);
                buf.putInt(b.length);
                buf.put(b);
            }

            buf.putInt(completionCounts.size());
            for (Map.Entry<String, Integer> e : completionCounts.entrySet()) {
                byte[] b = e.getKey().getBytes(StandardCharsets.UTF_8);
                buf.putInt(b.length);
                buf.put(b);
                buf.putInt(e.getValue());
            }

            buf.putInt(cooldownExpiries.size());
            for (Map.Entry<String, Long> e : cooldownExpiries.entrySet()) {
                byte[] b = e.getKey().getBytes(StandardCharsets.UTF_8);
                buf.putInt(b.length);
                buf.put(b);
                buf.putLong(e.getValue());
            }

            buf.putLong(lastUpdated);

            return buf.array();
        }

        /**
         * Deserializes quest progress data from a byte array.
         */
        public static QuestProgressData deserialize(byte[] data) {
            ByteBuffer buf = ByteBuffer.wrap(data);

            int uuidLen = buf.getInt();
            byte[] uuidBytes = new byte[uuidLen];
            buf.get(uuidBytes);
            UUID uuid = UUID.fromString(new String(uuidBytes, StandardCharsets.UTF_8));

            int activeCount = buf.getInt();
            List<String> activeQuests = new ArrayList<>(activeCount);
            for (int i = 0; i < activeCount; i++) {
                int len = buf.getInt();
                byte[] b = new byte[len];
                buf.get(b);
                activeQuests.add(new String(b, StandardCharsets.UTF_8));
            }

            int completedCount = buf.getInt();
            List<String> completedQuests = new ArrayList<>(completedCount);
            for (int i = 0; i < completedCount; i++) {
                int len = buf.getInt();
                byte[] b = new byte[len];
                buf.get(b);
                completedQuests.add(new String(b, StandardCharsets.UTF_8));
            }

            int countsCount = buf.getInt();
            Map<String, Integer> completionCounts = new LinkedHashMap<>(countsCount);
            for (int i = 0; i < countsCount; i++) {
                int len = buf.getInt();
                byte[] b = new byte[len];
                buf.get(b);
                completionCounts.put(new String(b, StandardCharsets.UTF_8), buf.getInt());
            }

            int cooldownCount = buf.getInt();
            Map<String, Long> cooldownExpiries = new LinkedHashMap<>(cooldownCount);
            for (int i = 0; i < cooldownCount; i++) {
                int len = buf.getInt();
                byte[] b = new byte[len];
                buf.get(b);
                cooldownExpiries.put(new String(b, StandardCharsets.UTF_8), buf.getLong());
            }

            long lastUpdated = buf.getLong();

            return new QuestProgressData(uuid, activeQuests, completedQuests,
                    completionCounts, cooldownExpiries, lastUpdated);
        }
    }

    /**
     * Collects quest progress data from the Fabric mod side.
     *
     * @param playerUuid the player's UUID string
     * @return the collected quest progress data, or null if not found
     */
    public QuestProgressData collect(String playerUuid) {
        UUID uuid = UUID.fromString(playerUuid);
        return new QuestProgressData(
                uuid,
                List.of(),
                List.of(),
                Map.of(),
                Map.of(),
                System.currentTimeMillis()
        );
    }

    /**
     * Fetches raw quest progress data bytes from the Paper plugin's data store.
     *
     * @param playerUuid the player's UUID string
     * @return raw serialized data, or null if not found
     */
    public byte[] fetchRaw(String playerUuid) {
        // In a real implementation, this would read from the DataStore
        return null;
    }

    /**
     * Persists quest progress data to YAML via the DataStore.
     *
     * @param data      the quest progress data to persist
     * @param dataStore the Paper DataStore
     */
    public void persist(QuestProgressData data, DataStore dataStore) {
        FileConfiguration config = dataStore.load(DATA_PREFIX + data.playerUuid());
        config.set("activeQuests", data.activeQuests());
        config.set("completedQuests", data.completedQuests());

        org.bukkit.configuration.ConfigurationSection counts = config.createSection("completionCounts");
        for (Map.Entry<String, Integer> e : data.completionCounts().entrySet()) {
            counts.set(e.getKey(), e.getValue());
        }

        org.bukkit.configuration.ConfigurationSection cooldowns = config.createSection("cooldowns");
        for (Map.Entry<String, Long> e : data.cooldownExpiries().entrySet()) {
            cooldowns.set(e.getKey(), e.getValue());
        }

        config.set("lastUpdated", data.lastUpdated());
        dataStore.save(DATA_PREFIX + data.playerUuid(), config);
    }

    /**
     * Loads quest progress from the Paper QuestManager.
     *
     * @param playerUuid the player's UUID
     * @return the loaded quest progress data, or null if no tracker exists
     */
    public QuestProgressData loadFromManager(UUID playerUuid) {
        QuestManager qm = QuestManager.getInstance();
        if (!qm.hasTracker(playerUuid)) {
            return null;
        }
        QuestTracker tracker = qm.getTracker(playerUuid);

        List<String> active = new ArrayList<>();
        for (Quest q : tracker.activeQuests()) {
            active.add(q.id());
        }

        List<String> completed = new ArrayList<>(tracker.completedQuests());

        Map<String, Integer> counts = new LinkedHashMap<>();
        // QuestTracker doesn't expose completionCounts directly; we track what we can

        Map<String, Long> cooldowns = new LinkedHashMap<>();
        for (Map.Entry<String, java.time.Instant> e : tracker.cooldowns().entrySet()) {
            cooldowns.put(e.getKey(), e.getValue().toEpochMilli());
        }

        return new QuestProgressData(playerUuid, active, completed, counts, cooldowns,
                System.currentTimeMillis());
    }
}
