package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RareIronKatana extends AincradWeapon {
    public RareIronKatana() {
        super("Rare Iron Katana", ItemRarity.RARE, ItemType.WEAPON, 1, 7, -2.0f, 300);
    }
}
