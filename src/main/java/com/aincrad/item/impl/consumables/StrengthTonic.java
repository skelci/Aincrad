package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class StrengthTonic extends AincradConsumable {
    public StrengthTonic() {
        super("Strength Tonic", ItemRarity.COMMON, ItemType.CONSUMABLE, 1, 16, 30, 50);
    }
}
