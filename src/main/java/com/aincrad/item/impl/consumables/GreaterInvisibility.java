package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class GreaterInvisibility extends AincradConsumable {
    public GreaterInvisibility() {
        super("Greater Invisibility", ItemRarity.UNCOMMON, ItemType.CONSUMABLE, 2, 16, 60, 100);
    }
}
