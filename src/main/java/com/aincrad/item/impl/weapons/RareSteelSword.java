package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareSteelSword extends AincradWeapon {
    public RareSteelSword() {
        super("Rare Steel Sword", ItemRarity.RARE, ItemType.WEAPON, 2, 7, -2.4f, 350);
    }
}
