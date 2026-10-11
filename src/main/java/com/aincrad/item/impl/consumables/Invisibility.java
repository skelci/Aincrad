package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class Invisibility extends AincradConsumable {
    public Invisibility() {
        super("Invisibility", ItemRarity.COMMON, ItemType.CONSUMABLE, 1, 16, 30, 50);
    }
}
