package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class LeatherSet {
    private LeatherSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Leather Helmet", ItemRarity.COMMON, ItemType.HELMET, 1, 50,
        "A sturdy leather helm.", ArmorSet.LEATHER, ItemType.HELMET, 5,
        new SetBonus(2, StatType.VIT, 5, "+5 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 2, "+2 HP/s"),
        new SetBonus(6, StatType.EVASION, 3, "+3% Evasion")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Leather Chestplate", ItemRarity.COMMON, ItemType.CHESTPLATE, 2, 80,
        "A durable leather chestpiece.", ArmorSet.LEATHER, ItemType.CHESTPLATE, 10,
        new SetBonus(2, StatType.VIT, 5, "+5 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 2, "+2 HP/s"),
        new SetBonus(6, StatType.EVASION, 3, "+3% Evasion")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Leather Leggings", ItemRarity.COMMON, ItemType.LEGGINGS, 2, 70,
        "Flexible leather leggings.", ArmorSet.LEATHER, ItemType.LEGGINGS, 8,
        new SetBonus(2, StatType.VIT, 5, "+5 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 2, "+2 HP/s"),
        new SetBonus(6, StatType.EVASION, 3, "+3% Evasion")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Leather Boots", ItemRarity.COMMON, ItemType.BOOTS, 1, 40,
        "Light leather boots.", ArmorSet.LEATHER, ItemType.BOOTS, 5,
        new SetBonus(2, StatType.VIT, 5, "+5 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 2, "+2 HP/s"),
        new SetBonus(6, StatType.EVASION, 3, "+3% Evasion")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Leather Shield", ItemRarity.COMMON, ItemType.SHIELD, 2, 60,
        "A reinforced leather shield.", ArmorSet.LEATHER, ItemType.SHIELD, 7,
        new SetBonus(2, StatType.VIT, 5, "+5 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 2, "+2 HP/s"),
        new SetBonus(6, StatType.EVASION, 3, "+3% Evasion")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Leather Whip", ItemRarity.COMMON, ItemType.WEAPON, 1, 55,
        "A sturdy leather whip.", ArmorSet.LEATHER, ItemType.WEAPON, 4,
        new SetBonus(2, StatType.VIT, 5, "+5 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 2, "+2 HP/s"),
        new SetBonus(6, StatType.EVASION, 3, "+3% Evasion")
    ) {};
}
