package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class GreaterMpPotion extends AincradConsumable {
    public GreaterMpPotion() {
        super("Greater MP Potion", ItemRarity.UNCOMMON, ItemType.CONSUMABLE, 2, 16, 60, 100);
    }
}
