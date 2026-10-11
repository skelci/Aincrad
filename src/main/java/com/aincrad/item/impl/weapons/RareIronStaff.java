package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareIronStaff extends AincradWeapon {
    public RareIronStaff() {
        super("Rare Iron Staff", ItemRarity.RARE, ItemType.WEAPON, 1, 5, -2.0f, 200);
    }
}
