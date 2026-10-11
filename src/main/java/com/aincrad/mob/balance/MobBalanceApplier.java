package com.aincrad.mob.balance;

import com.aincrad.mob.MobEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Applies theme-based and floor-based balance to a {@link MobEntity}.
 * <p>
 * Theme multipliers come from {@link MobBalanceConfig}. Floor scaling is applied
 * on top of the theme multiplier:
 * <ul>
 *   <li>HP:    {@code themeHp * (1 + (floor - 1) * hpStep)}</li>
 *   <li>DMG:   {@code themeDmg * (1 + (floor - 1) * dmgStep)}</li>
 *   <li>SPD:   {@code themeSpd * (1 + (floor - 1) * spdStep)}</li>
 * </ul>
 * <p>
 * Call this after construction and after any {@code setFloorLevel} call, because
 * {@link MobEntity}'s own {@code applyFloorScaling} resets attributes each time.
 */
public final class MobBalanceApplier {

    private MobBalanceApplier() {}

    /**
     * Apply theme + floor balance to the given mob.
     *
     * @param mob   the mob to balance (must not be {@code null})
     * @param floor 1-based floor number
     */
    public static void applyBalance(MobEntity mob, int floor) {
        if (mob == null) return;
        applyBalance(mob, floor, 0);
    }

    /**
     * Apply theme + floor balance to the given mob using the provided theme index.
     *
     * @param mob        the mob to balance (must not be {@code null})
     * @param floor      1-based floor number
     * @param themeIndex 0-based theme index (see {@link MobBalanceConfig})
     */
    public static void applyBalance(MobEntity mob, int floor, int themeIndex) {
        if (mob == null) return;

        MobBalanceConfig config = MobBalanceConfig.getInstance();
        int safeFloor = Math.max(floor, 1);
        int safeTheme = themeIndex;

        double hpTheme = config.getHpMultiplier(safeTheme);
        double dmgTheme = config.getDamageMultiplier(safeTheme);
        double spdTheme = config.getSpeedMultiplier(safeTheme);

        double floorHp = 1.0 + (safeFloor - 1) * config.getHpStep();
        double floorDmg = 1.0 + (safeFloor - 1) * config.getDmgStep();
        double floorSpd = 1.0 + (safeFloor - 1) * config.getSpdStep();

        double finalHpMult = hpTheme * floorHp;
        double finalDmgMult = dmgTheme * floorDmg;
        double finalSpdMult = spdTheme * floorSpd;

        // Capture the mob's base attributes (set by its own applyFloorScaling) as reference points,
        // then apply theme+floor multipliers on top.
        double baseHp = mob.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
        double baseDmg = mob.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
        double baseSpd = mob.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();

        mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHp * finalHpMult);
        mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDmg * finalDmgMult);
        mob.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseSpd * finalSpdMult);

        // Keep current health in sync if the mob has already been spawned.
        double maxHealth = baseHp * finalHpMult;
        if (mob.getHealth() > maxHealth) {
            mob.setHealth((float) maxHealth);
        }
    }
}
