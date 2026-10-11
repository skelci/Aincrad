package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class CommonIronStaff extends AincradWeapon {
    public CommonIronStaff() {
        super("Common Iron Staff", ItemRarity.COMMON, ItemType.WEAPON, 1, 5, -2.0f, 200);
    }
}
