package com.aincrad.item.impl.consumables;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public class GreaterStaminaElixir extends AincradConsumable {
    public GreaterStaminaElixir() {
        super("Greater Stamina Elixir", ItemRarity.UNCOMMON, ItemType.CONSUMABLE, 2, 16, 60, 100);
    }
}
