package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class RegenerationDraught extends AincradConsumable {
    public RegenerationDraught() {
        super("Regeneration Draught", ItemRarity.COMMON, ItemType.CONSUMABLE, 1, 16, 30, 50);
    }
}
