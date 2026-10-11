package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareIronBow extends AincradWeapon {
    public RareIronBow() {
        super("Rare Iron Bow", ItemRarity.RARE, ItemType.WEAPON, 1, 6, -2.5f, 200);
    }
}
