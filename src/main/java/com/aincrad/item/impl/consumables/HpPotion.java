package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class HpPotion extends AincradConsumable {
    public HpPotion() {
        super("HP Potion", ItemRarity.COMMON, ItemType.CONSUMABLE, 1, 16, 30, 50);
    }
}
