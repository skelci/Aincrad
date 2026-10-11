package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareIronDagger extends AincradWeapon {
    public RareIronDagger() {
        super("Rare Iron Dagger", ItemRarity.RARE, ItemType.WEAPON, 1, 4, -1.5f, 150);
    }
}
