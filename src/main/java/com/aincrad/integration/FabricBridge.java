package com.aincrad.integration;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Fabric-side bridge implementation.
 * Sends data to the Paper server via plugin messages.
 * <p>
 * This bridge is used when the Aincrad Fabric mod is running and needs to
 * synchronize game state with the Paper plugin backend.
 */
public class FabricBridge implements Bridge {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-integration");

    /** Channel identifier for Aincrad integration plugin messages. */
    public static final Identifier CHANNEL_ID =
            Identifier.fromNamespaceAndPath("aincrad", "integration");

    private final PlayerDataSync playerDataSync;
    private final GuildSync guildSync;
    private final PartySync partySync;
    private final QuestSync questSync;
    private final EconomySync economySync;
    private final DungeonSync dungeonSync;

    public FabricBridge() {
        this.playerDataSync = new PlayerDataSync();
        this.guildSync = new GuildSync();
        this.partySync = new PartySync();
        this.questSync = new QuestSync();
        this.economySync = new EconomySync();
        this.dungeonSync = new DungeonSync();
        LOGGER.info("FabricBridge created");
    }

    @Override
    public boolean syncPlayerData(String playerUuid) {
        try {
            PlayerDataSync.PlayerData data = playerDataSync.collect(playerUuid);
            if (data == null) {
                LOGGER.warn("No player data found for {}", playerUuid);
                return false;
            }
            sendPayload(IntegrationPayload.Type.PLAYER_DATA, playerUuid, data.serialize());
            LOGGER.debug("Sent player data sync for {}", playerUuid);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to sync player data for {}", playerUuid, e);
            return false;
        }
    }

    @Override
    public boolean syncGuildData(String guildName) {
        try {
            GuildSync.GuildData data = guildSync.collect(guildName);
            if (data == null) {
                LOGGER.warn("No guild data found for {}", guildName);
                return false;
            }
            sendPayload(IntegrationPayload.Type.GUILD_DATA, guildName, data.serialize());
            LOGGER.debug("Sent guild data sync for {}", guildName);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to sync guild data for {}", guildName, e);
            return false;
        }
    }

    @Override
    public boolean syncPartyData(String partyId) {
        try {
            PartySync.PartyData data = partySync.collect(partyId);
            if (data == null) {
                LOGGER.warn("No party data found for {}", partyId);
                return false;
            }
            sendPayload(IntegrationPayload.Type.PARTY_DATA, partyId, data.serialize());
            LOGGER.debug("Sent party data sync for {}", partyId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to sync party data for {}", partyId, e);
            return false;
        }
    }

    /**
     * Sends a payload to the Paper server via plugin message.
     * In a real deployment, this would send via a proxy or direct plugin message channel.
     */
    private void sendPayload(IntegrationPayload.Type type, String targetId, byte[] data) {
        IntegrationPayload payload = new IntegrationPayload(type, targetId, data);
        LOGGER.debug("Sending integration payload: type={} target={} size={} bytes",
                payload.type(), payload.targetId(), payload.data().length);
    }

    /**
     * Simple payload carrier for Aincrad integration plugin messages.
     */
    public record IntegrationPayload(
            Type type,
            String targetId,
            byte[] data
    ) {
        public enum Type {
            PLAYER_DATA,
            GUILD_DATA,
            PARTY_DATA,
            QUEST_DATA,
            ECONOMY_DATA,
            DUNGEON_DATA
        }
    }
}
