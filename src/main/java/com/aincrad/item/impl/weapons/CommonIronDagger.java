package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class CommonIronDagger extends AincradWeapon {
    public CommonIronDagger() {
        super("Common Iron Dagger", ItemRarity.COMMON, ItemType.WEAPON, 1, 4, -1.5f, 150);
    }
}
