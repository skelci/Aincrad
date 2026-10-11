package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareIronShield extends AincradWeapon {
    public RareIronShield() {
        super("Rare Iron Shield", ItemRarity.RARE, ItemType.SHIELD, 1, 3, 0.0f, 250);
    }
}
