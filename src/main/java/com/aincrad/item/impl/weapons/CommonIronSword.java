package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class CommonIronSword extends AincradWeapon {
    public CommonIronSword() {
        super("Common Iron Sword", ItemRarity.COMMON, ItemType.WEAPON, 1, 6, -2.4f, 250);
    }
}
