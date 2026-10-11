package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareSteelDagger extends AincradWeapon {
    public RareSteelDagger() {
        super("Rare Steel Dagger", ItemRarity.RARE, ItemType.WEAPON, 2, 5, -1.2f, 250);
    }
}
