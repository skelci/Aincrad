package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class GreaterHpPotion extends AincradConsumable {
    public GreaterHpPotion() {
        super("Greater HP Potion", ItemRarity.UNCOMMON, ItemType.CONSUMABLE, 2, 16, 60, 100);
    }
}
