package com.aincrad.crystal.drop;

import com.aincrad.crystal.CrystalItem;
import com.aincrad.crystal.CrystalType;
import com.aincrad.crystal.CrystalTier;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Random;

public final class CrystalDropHandler {
    private static final Random RANDOM = new Random();

    private CrystalDropHandler() {}

    /**
     * Rolls for a crystal drop. Returns ItemStack.EMPTY if no drop.
     * @param isBoss true if the entity is a boss
     * @param bossTier boss tier (1-10), ignored for non-bosses
     */
    public static ItemStack rollCrystal(boolean isBoss, int bossTier) {
        if (isBoss) {
            return rollBossCrystal(bossTier);
        }
        return rollStandardCrystal();
    }

    private static ItemStack rollStandardCrystal() {
        // 10% chance for standard mobs
        if (RANDOM.nextDouble() > 0.10) return ItemStack.EMPTY;
        CrystalType type = CrystalType.values()[RANDOM.nextInt(CrystalType.values().length)];
        // Weight: ROUGH=50%, POLISHED=30%, FLAWLESS=15%, PERFECT=4%, TRANSCENDENT=1%
        CrystalTier tier = rollTier(new double[]{0.50, 0.30, 0.15, 0.04, 0.01});
        return new ItemStack(new CrystalItem(type, tier));
    }

    private static ItemStack rollBossCrystal(int bossTier) {
        // Bosses: guaranteed crystal, tier scales with bossTier
        CrystalType type = CrystalType.values()[RANDOM.nextInt(CrystalType.values().length)];
        // Higher boss tiers -> higher tier crystals
        // bossTier 1-3: POLISHED-FLAWLESS, 4-6: FLAWLESS-PERFECT, 7-10: PERFECT-TRANSCENDENT
        CrystalTier tier;
        if (bossTier <= 3) {
            tier = rollTier(new double[]{0.10, 0.50, 0.30, 0.08, 0.02});
        } else if (bossTier <= 6) {
            tier = rollTier(new double[]{0.02, 0.10, 0.50, 0.30, 0.08});
        } else {
            tier = rollTier(new double[]{0.01, 0.02, 0.10, 0.50, 0.37});
        }
        return new ItemStack(new CrystalItem(type, tier));
    }

    private static CrystalTier rollTier(double[] weights) {
        double roll = RANDOM.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (roll <= cumulative) {
                return CrystalTier.values()[i];
            }
        }
        return CrystalTier.ROUGH;
    }

    /**
     * Convenience method to hook into mob death.
     * Call this from MobEntity/BossEntity death handlers.
     */
    public static void onEntityDeath(boolean isBoss, int bossTier, List<ItemStack> drops) {
        ItemStack crystal = rollCrystal(isBoss, bossTier);
        if (!crystal.isEmpty()) {
            drops.add(crystal);
        }
    }
}
