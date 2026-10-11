package com.aincrad.crystal.craft;

import com.aincrad.crystal.CrystalItem;
import com.aincrad.crystal.CrystalType;
import com.aincrad.crystal.CrystalTier;
import net.minecraft.world.item.Item;

/**
 * Bridge between CrystalItem (domain object) and Minecraft's Item system.
 * Provides one wrapper instance per (type, tier) pair so that
 * ItemStack.isSameItemSameComponents() works correctly in CraftingManager.
 */
public class CrystalItemWrapper extends Item {

    private static final CrystalItemWrapper[] INSTANCES = new CrystalItemWrapper[6 * 5];

    static {
        int i = 0;
        for (CrystalType type : CrystalType.values()) {
            for (CrystalTier tier : CrystalTier.values()) {
                INSTANCES[i++] = new CrystalItemWrapper(new CrystalItem(type, tier));
            }
        }
    }

    private final CrystalItem crystalItem;

    private CrystalItemWrapper(CrystalItem crystalItem) {
        super(new Properties());
        this.crystalItem = crystalItem;
    }

    /**
     * Returns the singleton wrapper for the given crystal type and tier.
     */
    public static CrystalItemWrapper of(CrystalType type, CrystalTier tier) {
        return INSTANCES[type.ordinal() * CrystalTier.values().length + tier.ordinal()];
    }

    public CrystalItem getCrystalItem() { return crystalItem; }
}
