package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class StaminaElixir extends AincradConsumable {
    public StaminaElixir() {
        super("Stamina Elixir", ItemRarity.COMMON, ItemType.CONSUMABLE, 1, 16, 30, 50);
    }
}
