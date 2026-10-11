package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class CommonIronShield extends AincradWeapon {
    public CommonIronShield() {
        super("Common Iron Shield", ItemRarity.COMMON, ItemType.SHIELD, 1, 3, 0.0f, 250);
    }
}
