package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class GreaterNightVision extends AincradConsumable {
    public GreaterNightVision() {
        super("Greater Night Vision", ItemRarity.UNCOMMON, ItemType.CONSUMABLE, 2, 16, 60, 100);
    }
}
