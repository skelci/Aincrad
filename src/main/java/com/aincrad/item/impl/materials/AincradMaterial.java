package com.aincrad.item.impl.materials;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class AincradMaterial extends Item {
    private final String name;
    private final ItemRarity rarity;
    private final ItemType type;
    private final int tier;
    private final int maxStackSize;

    public AincradMaterial(String name, ItemRarity rarity, ItemType type, int tier, int maxStackSize) {
        super(new Properties()
            .stacksTo(maxStackSize)
            .rarity(toVanillaRarity(rarity)));
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.tier = tier;
        this.maxStackSize = maxStackSize;
    }

    private static Rarity toVanillaRarity(ItemRarity rarity) {
        return switch (rarity) {
            case COMMON -> Rarity.COMMON;
            case UNCOMMON -> Rarity.UNCOMMON;
            case RARE -> Rarity.RARE;
            case EPIC, LEGENDARY, MYTHIC -> Rarity.EPIC;
        };
    }

    public String getName() { return name; }
    public ItemRarity getRarity() { return rarity; }
    public ItemType getType() { return type; }
    public int getTier() { return tier; }
    public int getMaxStackSize() { return maxStackSize; }
}
