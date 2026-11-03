package com.aincrad.paper.progression;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for all player progression.
 * Uses an exponential XP curve: base 1000 XP at level 2, 20% more per level.
 * Level-up stats compound by 10% per level.
 */
public final class ProgressionManager {

    private static class Holder {
        static final ProgressionManager INSTANCE = new ProgressionManager();
    }

    private static final double XP_BASE = 1000.0;
    private static final double XP_GROWTH = 1.20;
    private static final double STAT_GROWTH = 1.10;
    private static final double NG_PLUS_BONUS_PER_CYCLE = 0.05;
    private static final double NG_PLUS_MAX_BONUS = 0.25;

    private final Map<UUID, PlayerProgression> progressions = new ConcurrentHashMap<>();

    private ProgressionManager() {}

    public static ProgressionManager getInstance() { return Holder.INSTANCE; }

    public static double xpForLevel(int level) {
        if (level < 1) throw new IllegalArgumentException("Level must be >= 1");
        return XP_BASE * (Math.pow(XP_GROWTH, level - 1) - 1.0) / (XP_GROWTH - 1.0);
    }

    public static double xpForNextLevel(int currentLevel) {
        if (currentLevel < 1) throw new IllegalArgumentException("Level must be >= 1");
        return xpForLevel(currentLevel + 1) - xpForLevel(currentLevel);
    }

    /** Largest level that the given accumulated XP reaches. */
    public static int levelForXp(double xp) {
        if (xp < xpForLevel(2)) return 1;
        int lo = 1;
        int hi = PlayerProgression.MAX_LEVEL;
        while (lo < hi) {
            int mid = (lo + hi + 1) / 2;
            if (xpForLevel(mid) <= xp) lo = mid;
            else hi = mid - 1;
        }
        return lo;
    }

    public PlayerProgression getProgression(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return progressions.computeIfAbsent(playerId, PlayerProgression::new);
    }

    public boolean hasProgression(UUID playerId) {
        return progressions.containsKey(playerId);
    }

    public int addXp(UUID playerId, double baseAmount) {
        if (baseAmount < 0) throw new IllegalArgumentException("XP amount must be non-negative");
        PlayerProgression progression = getProgression(playerId);
        double multiplier = getXpMultiplier(progression.getNgPlusCount());
        double adjustedAmount = baseAmount * multiplier;
        progression.addXp(adjustedAmount);
        int gained = processLevelUps(progression);
        org.bukkit.entity.Player online = org.bukkit.Bukkit.getPlayer(playerId);
        if (online != null) syncActionBar(online);
        return gained;
    }

    public void syncActionBar(org.bukkit.entity.Player player) {
        PlayerProgression progression = getProgression(player.getUniqueId());
        player.setLevel(progression.getLevel());
        player.setExp((float) progression.getProgressToNextLevel());
        syncStats(player);
    }

    /** Level-up stats compound by 10% per level. */
    private static double compoundBonus(double baseGain, int level) {
        return baseGain * (Math.pow(STAT_GROWTH, level) - 1.0) / (STAT_GROWTH - 1.0);
    }

    public static double statBonus(double baseGain, int level) {
        return compoundBonus(baseGain, level);
    }

    public static int strengthOf(int level) {
        return (int) Math.round(compoundBonus(2.0, level));
    }

    public static double bonusHealth(int level) {
        return compoundBonus(1.2, level);
    }

    public static double levelDefense(int level) {
        return compoundBonus(1.0, level);
    }

    private static org.bukkit.attribute.AttributeInstance healthInstance(
            org.bukkit.entity.Player player) {
        for (String key : new String[]{"generic.max_health", "max_health"}) {
            org.bukkit.attribute.Attribute a = org.bukkit.Registry.ATTRIBUTE.get(
                    org.bukkit.NamespacedKey.minecraft(key));
            if (a != null) {
                var inst = player.getAttribute(a);
                if (inst != null) return inst;
            }
        }
        return null;
    }

    /** Actual in-game max HP (attribute value), null if attribute unavailable. */
    public static Double currentMaxHealth(org.bukkit.entity.Player p) {
        var inst = healthInstance(p);
        return inst == null ? null : inst.getValue();
    }

    private static final java.util.UUID HP_MOD_ID =
            java.util.UUID.fromString("0a1ac000-0000-4000-8000-000000000001");

    public void syncStats(org.bukkit.entity.Player player) {
        int level = getProgression(player.getUniqueId()).getLevel();
        org.bukkit.attribute.AttributeInstance instance = healthInstance(player);
        if (instance == null) return;
        for (var m : instance.getModifiers())
            if (HP_MOD_ID.equals(m.getUniqueId())) instance.removeModifier(m);
        double bonus = bonusHealth(level);
        if (bonus > 0) {
            instance.addModifier(new org.bukkit.attribute.AttributeModifier(
                    HP_MOD_ID, "aincrad:level_health", bonus,
                    org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                    org.bukkit.inventory.EquipmentSlotGroup.ANY));
        }
        PassiveSkills.syncSpeed(player);
    }

    private int processLevelUps(PlayerProgression progression) {
        int levelsGained = 0;
        while (!progression.isMaxLevel()
                && progression.getXp() >= xpForLevel(progression.getLevel() + 1)) {
            int oldLevel = progression.getLevel();
            progression.setLevel(oldLevel + 1);
            progression.addSkillPoints(PlayerProgression.SKILL_POINTS_PER_LEVEL);
            progression.addStatPoints(PlayerProgression.STAT_POINTS_PER_LEVEL);
            checkMilestones(progression, oldLevel, progression.getLevel());
            levelsGained++;
            org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(progression.getPlayerId());
            if (p != null) {
                syncStats(p);
                p.sendMessage("§aLevel " + progression.getLevel() + " §7(+2 STR, HP +"
                        + String.format(java.util.Locale.ROOT, "%.2f",
                                bonusHealth(progression.getLevel()) - bonusHealth(oldLevel)) + ")");
            }
        }
        return levelsGained;
    }

    private void checkMilestones(PlayerProgression progression, int oldLevel, int newLevel) {
        for (Milestone milestone : Milestone.values()) {
            if (milestone.getLevel() > oldLevel && milestone.getLevel() <= newLevel) {
                progression.achieveMilestone(milestone);
            }
        }
    }

    public void adminSetLevel(UUID playerId, int level) {
        if (level < 1 || level > PlayerProgression.MAX_LEVEL) {
            throw new IllegalArgumentException("Level must be 1.." + PlayerProgression.MAX_LEVEL);
        }
        PlayerProgression progression = getProgression(playerId);
        progression.resetXp(xpForLevel(level));
        progression.setLevel(level);
        org.bukkit.entity.Player online = org.bukkit.Bukkit.getPlayer(playerId);
        if (online != null) syncActionBar(online);
    }

    public PlayerProgression initiateNgPlus(UUID playerId) {
        PlayerProgression progression = getProgression(playerId);
        if (progression.getLevel() < PlayerProgression.NG_PLUS_RESET_LEVEL) {
            throw new IllegalStateException("Player must be at least level " + PlayerProgression.NG_PLUS_RESET_LEVEL + " to enter NG+");
        }
        progression.incrementNgPlus();
        progression.setLevel(PlayerProgression.NG_PLUS_RESET_LEVEL);
        progression.resetXp(xpForLevel(PlayerProgression.NG_PLUS_RESET_LEVEL));
        return progression;
    }

    public static double getXpMultiplier(int ngPlusCount) {
        double bonus = Math.min(ngPlusCount * NG_PLUS_BONUS_PER_CYCLE, NG_PLUS_MAX_BONUS);
        return 1.0 + bonus;
    }

    public Map<UUID, PlayerProgression> getAllProgressions() {
        return Collections.unmodifiableMap(progressions);
    }

    /** Recompute levels, stat points, skill points, and milestones for the new curve. */
    public void recalculateAll() {
        for (PlayerProgression p : progressions.values()) {
            int newLevel = levelForXp(p.getXp());
            p.setLevel(newLevel);
            p.setSkillPoints((newLevel - 1) * PlayerProgression.SKILL_POINTS_PER_LEVEL);
            p.setStatPoints((newLevel - 1) * PlayerProgression.STAT_POINTS_PER_LEVEL);
            p.clearMilestones();
            for (Milestone m : Milestone.values()) {
                if (m.isAchievedAt(newLevel)) p.achieveMilestone(m);
            }
            org.bukkit.entity.Player online = org.bukkit.Bukkit.getPlayer(p.getPlayerId());
            if (online != null) syncActionBar(online);
        }
    }

    public PlayerProgression removeProgression(UUID playerId) {
        return progressions.remove(playerId);
    }

    public void reset() {
        progressions.clear();
    }
}
