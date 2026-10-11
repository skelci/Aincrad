package com.aincrad.item.impl.weapons;

import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class AincradWeapon extends Item {
    private final String name;
    private final ItemRarity rarity;
    private final ItemType type;
    private final int tier;
    private final int baseDamage;
    private final float attackSpeed;
    private final int durability;

    public AincradWeapon(String name, ItemRarity rarity, ItemType type, int tier, int baseDamage, float attackSpeed, int durability) {
        super(new Properties()
            .durability(durability)
            .rarity(toVanillaRarity(rarity)));
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.tier = tier;
        this.baseDamage = baseDamage;
        this.attackSpeed = attackSpeed;
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
    public int getBaseDamage() { return baseDamage; }
    public float getAttackSpeed() { return attackSpeed; }
    public int getDurability() { return durability; }
}
