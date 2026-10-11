package com.aincrad.integration;

import com.aincrad.paper.AincradPaperPlugin;
import com.aincrad.paper.persistence.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Paper-side bridge implementation.
 * Receives data from the Fabric mod and persists it to YAML via the DataStore.
 * <p>
 * This bridge is used when the Aincrad Paper plugin is running and needs to
 * synchronize game state received from Fabric mod clients.
 */
public class PaperBridge implements Bridge {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-integration");

    private final AincradPaperPlugin plugin;
    private final DataStore dataStore;
    private final PlayerDataSync playerDataSync;
    private final GuildSync guildSync;
    private final PartySync partySync;
    private final QuestSync questSync;
    private final EconomySync economySync;
    private final DungeonSync dungeonSync;

    public PaperBridge(Object paperPlugin) {
        if (!(paperPlugin instanceof AincradPaperPlugin)) {
            throw new IllegalArgumentException(
                    "PaperBridge requires AincradPaperPlugin, got: " +
                            (paperPlugin == null ? "null" : paperPlugin.getClass().getName()));
        }
        this.plugin = (AincradPaperPlugin) paperPlugin;
        this.dataStore = plugin.getDataStore();
        this.playerDataSync = new PlayerDataSync();
        this.guildSync = new GuildSync();
        this.partySync = new PartySync();
        this.questSync = new QuestSync();
        this.economySync = new EconomySync();
        this.dungeonSync = new DungeonSync();
        LOGGER.info("PaperBridge created");
    }

    @Override
    public boolean syncPlayerData(String playerUuid) {
        try {
            byte[] rawData = playerDataSync.fetchRaw(playerUuid);
            if (rawData == null) {
                LOGGER.warn("No raw player data found for {}", playerUuid);
                return false;
            }
            PlayerDataSync.PlayerData data = PlayerDataSync.PlayerData.deserialize(rawData);
            playerDataSync.persist(data, dataStore);
            LOGGER.debug("Persisted player data for {}", playerUuid);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to sync player data for {}", playerUuid, e);
            return false;
        }
    }

    @Override
    public boolean syncGuildData(String guildName) {
        try {
            byte[] rawData = guildSync.fetchRaw(guildName);
            if (rawData == null) {
                LOGGER.warn("No raw guild data found for {}", guildName);
                return false;
            }
            GuildSync.GuildData data = GuildSync.GuildData.deserialize(rawData);
            guildSync.persist(data, dataStore);
            LOGGER.debug("Persisted guild data for {}", guildName);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to sync guild data for {}", guildName, e);
            return false;
        }
    }

    @Override
    public boolean syncPartyData(String partyId) {
        try {
            byte[] rawData = partySync.fetchRaw(partyId);
            if (rawData == null) {
                LOGGER.warn("No raw party data found for {}", partyId);
                return false;
            }
            PartySync.PartyData data = PartySync.PartyData.deserialize(rawData);
            partySync.persist(data, dataStore);
            LOGGER.debug("Persisted party data for {}", partyId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to sync party data for {}", partyId, e);
            return false;
        }
    }

    /**
     * Returns the Paper plugin instance.
     */
    public AincradPaperPlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the DataStore for direct persistence access.
     */
    public DataStore getDataStore() {
        return dataStore;
    }
}
