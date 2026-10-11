package com.aincrad.item.impl.armor;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class AincradArmor extends Item {
    private final String name;
    private final ItemRarity rarity;
    private final ItemType type;
    private final int tier;
    private final int defense;
    private final int durability;

    public AincradArmor(String name, ItemRarity rarity, ItemType type, int tier, int defense, int durability) {
        super(new Properties()
            .durability(durability)
            .rarity(toVanillaRarity(rarity)));
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.tier = tier;
        this.defense = defense;
        this.durability = durability;
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
    public int getDefense() { return defense; }
    public int getDurability() { return durability; }
}
