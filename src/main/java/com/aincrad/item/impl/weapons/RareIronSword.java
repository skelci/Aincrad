package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareIronSword extends AincradWeapon {
    public RareIronSword() {
        super("Rare Iron Sword", ItemRarity.RARE, ItemType.WEAPON, 1, 6, -2.4f, 250);
    }
}
