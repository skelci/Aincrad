package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class CommonIronBow extends AincradWeapon {
    public CommonIronBow() {
        super("Common Iron Bow", ItemRarity.COMMON, ItemType.WEAPON, 1, 6, -2.5f, 200);
    }
}
