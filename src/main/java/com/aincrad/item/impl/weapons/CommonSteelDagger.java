package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class CommonSteelDagger extends AincradWeapon {
    public CommonSteelDagger() {
        super("Common Steel Dagger", ItemRarity.COMMON, ItemType.WEAPON, 2, 5, -1.2f, 250);
    }
}
