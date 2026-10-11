package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class IronSet {
    private IronSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Iron Helmet", ItemRarity.UNCOMMON, ItemType.HELMET, 2, 180,
        "A solid iron helm.", ArmorSet.IRON, ItemType.HELMET, 15,
        new SetBonus(2, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 3, "+3 HP/s"),
        new SetBonus(6, StatType.EVASION, 5, "+5% Evasion")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Iron Chestplate", ItemRarity.UNCOMMON, ItemType.CHESTPLATE, 3, 250,
        "Heavy iron plate armor.", ArmorSet.IRON, ItemType.CHESTPLATE, 25,
        new SetBonus(2, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 3, "+3 HP/s"),
        new SetBonus(6, StatType.EVASION, 5, "+5% Evasion")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Iron Leggings", ItemRarity.UNCOMMON, ItemType.LEGGINGS, 3, 220,
        "Sturdy iron greaves.", ArmorSet.IRON, ItemType.LEGGINGS, 20,
        new SetBonus(2, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 3, "+3 HP/s"),
        new SetBonus(6, StatType.EVASION, 5, "+5% Evasion")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Iron Boots", ItemRarity.UNCOMMON, ItemType.BOOTS, 2, 150,
        "Iron-plated boots.", ArmorSet.IRON, ItemType.BOOTS, 15,
        new SetBonus(2, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 3, "+3 HP/s"),
        new SetBonus(6, StatType.EVASION, 5, "+5% Evasion")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Iron Shield", ItemRarity.UNCOMMON, ItemType.SHIELD, 3, 200,
        "A heavy iron shield.", ArmorSet.IRON, ItemType.SHIELD, 20,
        new SetBonus(2, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 3, "+3 HP/s"),
        new SetBonus(6, StatType.EVASION, 5, "+5% Evasion")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Iron Sword", ItemRarity.UNCOMMON, ItemType.WEAPON, 2, 190,
        "A reliable iron blade.", ArmorSet.IRON, ItemType.WEAPON, 18,
        new SetBonus(2, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 3, "+3 HP/s"),
        new SetBonus(6, StatType.EVASION, 5, "+5% Evasion")
    ) {};
}
