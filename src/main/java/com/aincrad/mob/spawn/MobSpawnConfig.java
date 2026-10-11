package com.aincrad.mob.spawn;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * Loads and provides access to spawn configuration from {@code spawn.yml}.
 * <p>
 * Each theme has 5 mobs with spawn weights and caps. The config is loaded once
 * from the classpath resource and cached.
 */
public final class MobSpawnConfig {

    private static final String CONFIG_NAME = "spawn.yml";
    private static final int MOBS_PER_THEME = 5;

    private final Map<String, ThemeConfig> themes = new LinkedHashMap<>();
    private final Map<String, Integer> settings = new HashMap<>();

    /**
     * Loads spawn.yml from the classpath.
     */
    public MobSpawnConfig() {
        load();
    }

    private void load() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            if (is == null) {
                throw new IllegalStateException("Cannot find " + CONFIG_NAME + " on classpath");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(is);
            if (root == null) {
                throw new IllegalStateException(CONFIG_NAME + " is empty");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> themesMap = (Map<String, Object>) root.get("themes");
            if (themesMap != null) {
                for (Map.Entry<String, Object> entry : themesMap.entrySet()) {
                    String themeKey = entry.getKey();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> themeData = (Map<String, Object>) entry.getValue();
                    parseTheme(themeKey, themeData);
                }
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> settingsMap = (Map<String, Object>) root.get("settings");
            if (settingsMap != null) {
                for (Map.Entry<String, Object> entry : settingsMap.entrySet()) {
                    settings.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + CONFIG_NAME, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void parseTheme(String themeKey, Map<String, Object> themeData) {
        String name = (String) themeData.getOrDefault("name", themeKey);
        List<MobEntry> mobs = new ArrayList<>();

        List<Object> mobsList = (List<Object>) themeData.get("mobs");
        if (mobsList != null) {
            for (Object mobObj : mobsList) {
                Map<String, Object> mobData = (Map<String, Object>) mobObj;
                String id = (String) mobData.get("id");
                String mobClass = (String) mobData.get("mobClass");
                int weight = ((Number) mobData.get("weight")).intValue();
                int cap = ((Number) mobData.get("cap")).intValue();
                mobs.add(new MobEntry(id, mobClass, weight, cap));
            }
        }

        themes.put(themeKey, new ThemeConfig(name, mobs));
    }

    /**
     * Returns the theme key for a given theme index (0-9).
     * Index maps to: 0=forest, 1=cave, 2=ice, 3=desert, 4=volcano,
     *                5=ocean, 6=mountain, 7=swamp, 8=crystal, 9=final_tower
     */
    public String getThemeKey(int themeIndex) {
        return switch (themeIndex) {
            case 0 -> "forest";
            case 1 -> "cave";
            case 2 -> "ice";
            case 3 -> "desert";
            case 4 -> "volcano";
            case 5 -> "ocean";
            case 6 -> "mountain";
            case 7 -> "swamp";
            case 8 -> "crystal";
            case 9 -> "final_tower";
            default -> throw new IllegalArgumentException("Theme index must be 0-9, got: " + themeIndex);
        };
    }

    /**
     * Returns the spawn weight for a specific mob slot in a theme.
     *
     * @param themeIndex Theme index (0-9)
     * @param slot       Mob slot within the theme (0-4)
     * @return Spawn weight (higher = more common)
     */
    public int getSpawnWeight(int themeIndex, int slot) {
        String themeKey = getThemeKey(themeIndex);
        ThemeConfig theme = themes.get(themeKey);
        if (theme == null || slot < 0 || slot >= theme.mobs.size()) {
            return 0;
        }
        return theme.mobs.get(slot).weight();
    }

    /**
     * Returns the mob class name for a specific slot in a theme.
     */
    public String getMobClass(int themeIndex, int slot) {
        String themeKey = getThemeKey(themeIndex);
        ThemeConfig theme = themes.get(themeKey);
        if (theme == null || slot < 0 || slot >= theme.mobs.size()) {
            return null;
        }
        return theme.mobs.get(slot).mobClass();
    }

    /**
     * Returns the cap for a specific mob slot in a theme.
     */
    public int getMobCap(int themeIndex, int slot) {
        String themeKey = getThemeKey(themeIndex);
        ThemeConfig theme = themes.get(themeKey);
        if (theme == null || slot < 0 || slot >= theme.mobs.size()) {
            return 0;
        }
        return theme.mobs.get(slot).cap();
    }

    /**
     * Returns all mob entries for a theme.
     */
    public List<MobEntry> getMobsForTheme(int themeIndex) {
        String themeKey = getThemeKey(themeIndex);
        ThemeConfig theme = themes.get(themeKey);
        if (theme == null) {
            return List.of();
        }
        return List.copyOf(theme.mobs);
    }

    /**
     * Returns the total number of themes.
     */
    public int getThemeCount() {
        return themes.size();
    }

    /**
     * Returns a setting value by key.
     */
    public int getSetting(String key, int defaultValue) {
        return settings.getOrDefault(key, defaultValue);
    }

    /**
     * Returns all theme keys.
     */
    public Set<String> getThemeKeys() {
        return Collections.unmodifiableSet(themes.keySet());
    }

    /**
     * Represents a single mob entry in a theme.
     */
    public record MobEntry(String id, String mobClass, int weight, int cap) {}

    /**
     * Represents a theme's configuration.
     */
    private record ThemeConfig(String name, List<MobEntry> mobs) {}
}
