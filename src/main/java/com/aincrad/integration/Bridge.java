package com.aincrad.integration;

/**
 * Bridge interface for synchronizing data between the Fabric mod and Paper plugin.
 * <p>
 * Implementations:
 * <ul>
 *   <li>{@link FabricBridge} — sends data from Fabric to Paper via plugin messages</li>
 *   <li>{@link PaperBridge} — receives data from Fabric and persists to YAML</li>
 * </ul>
 */
public interface Bridge {

    /**
     * Synchronizes player data (level, stats, inventory, position) between mod and plugin.
     *
     * @param playerUuid the player's UUID
     * @return true if sync was successful
     */
    boolean syncPlayerData(String playerUuid);

    /**
     * Synchronizes guild data (membership, ranks, bank) between mod and plugin.
     *
     * @param guildName the guild name
     * @return true if sync was successful
     */
    boolean syncGuildData(String guildName);

    /**
     * Synchronizes party data (membership, loot rules) between mod and plugin.
     *
     * @param partyId the party ID
     * @return true if sync was successful
     */
    boolean syncPartyData(String partyId);
}
