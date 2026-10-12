package com.aincrad.mob.balance;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads and provides access to per-theme mob balance multipliers from {@code mob_balance.yml}.
 * <p>
 * The config file is resolved in this order:
 * <ol>
 *   <li>Plugin data folder (for overrides)</li>
 *   <li>Classpath resource (bundled default)</li>
 * </ol>
 * <p>
 * Theme indices are 0-based: 0 = Forest, 1 = Cave, ..., 9 = Final Tower.
 */
public final class MobBalanceConfig {

    private static final String CONFIG_NAME = "mob_balance.yml";
    private static final Logger LOGGER = Logger.getLogger(MobBalanceConfig.class.getSimpleName());

    // ---- Defaults (matching mob_balance.yml) ----
    private static final double DEFAULT_HP_STEP = 0.15;
    private static final double DEFAULT_DMG_STEP = 0.10;
    private static final double DEFAULT_SPD_STEP = 0.02;

    // ---- State ----
    private static volatile MobBalanceConfig instance;

    private final List<ThemeEntry> themes;
    private final double hpStep;
    private final double dmgStep;
    private final double spdStep;

    // ---- Data class for theme multipliers ----
    public record ThemeEntry(String name, double hp, double dmg, double spd) {}

    // ---- Singleton ----

    public static MobBalanceConfig getInstance() {
        if (instance == null) {
            synchronized (MobBalanceConfig.class) {
                if (instance == null) {
                    instance = load();
                }
            }
        }
        return instance;
    }

    /** Reload from disk. Returns the new instance. */
    public static MobBalanceConfig reload() {
        synchronized (MobBalanceConfig.class) {
            instance = load();
        }
        return instance;
    }

    private static MobBalanceConfig load() {
        File dataFile = new File("plugins/Aincrad/" + CONFIG_NAME);
        if (dataFile.exists()) {
            try {
                return new MobBalanceConfig(YamlConfiguration.loadConfiguration(dataFile));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load mob_balance.yml from data folder, using classpath resource", e);
            }
        }

        try (InputStream is = MobBalanceConfig.class.getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is)) {
                    return new MobBalanceConfig(YamlConfiguration.loadConfiguration(reader));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load mob_balance.yml from classpath, using defaults", e);
        }

        return new MobBalanceConfig();
    }

    // ---- Constructors ----

    public MobBalanceConfig(YamlConfiguration config) {
        this.themes = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("themes");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection themeSec = section.getConfigurationSection(key);
                if (themeSec != null) {
                    this.themes.add(new ThemeEntry(
                        key,
                        themeSec.getDouble("hp", 1.0),
                        themeSec.getDouble("dmg", 1.0),
                        themeSec.getDouble("spd", 1.0)
                    ));
                }
            }
        }

        this.hpStep = config.getDouble("floorScaling.hpStep", DEFAULT_HP_STEP);
        this.dmgStep = config.getDouble("floorScaling.dmgStep", DEFAULT_DMG_STEP);
        this.spdStep = config.getDouble("floorScaling.spdStep", DEFAULT_SPD_STEP);
    }

    /** Default constructor with all-default values (no file loading). */
    public MobBalanceConfig() {
        this.themes = new ArrayList<>();
        this.themes.add(new ThemeEntry("Forest", 1.0, 1.0, 1.0));
        this.themes.add(new ThemeEntry("Cave", 1.1, 1.1, 1.0));
        this.themes.add(new ThemeEntry("Ice", 1.2, 1.2, 1.1));
        this.themes.add(new ThemeEntry("Desert", 1.3, 1.3, 1.1));
        this.themes.add(new ThemeEntry("Volcano", 1.4, 1.4, 1.0));
        this.themes.add(new ThemeEntry("Ocean", 1.5, 1.5, 1.2));
        this.themes.add(new ThemeEntry("Mountain", 1.6, 1.6, 1.0));
        this.themes.add(new ThemeEntry("Swamp", 1.7, 1.7, 1.1));
        this.themes.add(new ThemeEntry("Crystal", 1.8, 1.8, 1.2));
        this.themes.add(new ThemeEntry("Final Tower", 2.0, 2.0, 1.2));
        this.hpStep = DEFAULT_HP_STEP;
        this.dmgStep = DEFAULT_DMG_STEP;
        this.spdStep = DEFAULT_SPD_STEP;
    }

    // ==================== GETTERS ====================

    /**
     * Returns the HP multiplier for the given theme index.
     *
     * @param themeIndex 0-based theme index (0 = Forest, 9 = Final Tower)
     * @return HP multiplier; 1.0 if no themes are configured
     */
    public double getHpMultiplier(int themeIndex) {
        ThemeEntry entry = getTheme(themeIndex);
        return entry != null ? entry.hp() : 1.0;
    }

    /**
     * Returns the damage multiplier for the given theme index.
     *
     * @param themeIndex 0-based theme index
     * @return damage multiplier; 1.0 if no themes are configured
     */
    public double getDamageMultiplier(int themeIndex) {
        ThemeEntry entry = getTheme(themeIndex);
        return entry != null ? entry.dmg() : 1.0;
    }

    /**
     * Returns the speed multiplier for the given theme index.
     *
     * @param themeIndex 0-based theme index
     * @return speed multiplier; 1.0 if no themes are configured
     */
    public double getSpeedMultiplier(int themeIndex) {
        ThemeEntry entry = getTheme(themeIndex);
        return entry != null ? entry.spd() : 1.0;
    }

    // ---- Floor scaling steps ----

    public double getHpStep() { return hpStep; }
    public double getDmgStep() { return dmgStep; }
    public double getSpdStep() { return spdStep; }

    // ---- Utility ----

    public int getThemeCount() { return themes.size(); }

    public ThemeEntry getTheme(int index) {
        if (themes.isEmpty()) return null;
        if (index < 0) return themes.get(0);
        if (index >= themes.size()) return themes.get(themes.size() - 1);
        return themes.get(index);
    }
}
