package com.aincrad.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central manager for the Aincrad integration layer.
 * Initializes the bridge between Fabric mod systems and Paper plugin systems.
 * <p>
 * On the Fabric side, {@link FabricBridge} sends data to Paper via plugin messages.
 * On the Paper side, {@link PaperBridge} receives data and persists it to YAML.
 * <p>
 * This class is the single entry point for all cross-platform synchronization.
 */
public final class IntegrationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("aincrad-integration");

    private static Bridge bridge;
    private static boolean paperAvailable;
    private static boolean initialized;

    private IntegrationManager() {
        throw new AssertionError("IntegrationManager is a static utility class");
    }

    /**
     * Initializes the integration layer. Must be called once during mod/plugin startup.
     *
     * @param paperPlugin the Paper plugin instance, or null if running Fabric-only
     */
    public static synchronized void initialize(Object paperPlugin) {
        if (initialized) {
            LOGGER.warn("IntegrationManager already initialized, skipping");
            return;
        }

        if (paperPlugin != null) {
            // Running on Paper side (server)
            bridge = new PaperBridge(paperPlugin);
            paperAvailable = true;
            LOGGER.info("IntegrationManager initialized in Paper mode");
        } else {
            // Running on Fabric side (client)
            bridge = new FabricBridge();
            paperAvailable = false;
            LOGGER.info("IntegrationManager initialized in Fabric mode");
        }

        initialized = true;
    }

    /**
     * Returns the active bridge implementation.
     *
     * @return the bridge, or null if not initialized
     */
    public static Bridge getBridge() {
        return bridge;
    }

    /**
     * Returns the Paper plugin instance when running on the server side.
     *
     * @return the plugin instance, or null if not available
     */
    public static Object getPaperPlugin() {
        return paperAvailable ? bridge : null;
    }

    /**
     * Returns whether the Paper plugin is available in this environment.
     *
     * @return true if Paper integration is active
     */
    public static boolean isPaperAvailable() {
        return paperAvailable;
    }

    /**
     * Returns whether the integration layer has been initialized.
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Resets the integration manager (primarily for testing).
     */
    public static synchronized void reset() {
        bridge = null;
        paperAvailable = false;
        initialized = false;
        LOGGER.info("IntegrationManager reset");
    }
}
