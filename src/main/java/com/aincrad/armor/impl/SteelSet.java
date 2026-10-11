package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class SteelSet {
    private SteelSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Steel Helmet", ItemRarity.RARE, ItemType.HELMET, 2, 300,
        "A war-forged steel helm.", ArmorSet.STEEL, ItemType.HELMET, 20,
        new SetBonus(2, StatType.STR, 10, "+10 STR"),
        new SetBonus(4, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(6, StatType.HP_REGEN, 4, "+4 HP/s")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Steel Chestplate", ItemRarity.RARE, ItemType.CHESTPLATE, 4, 400,
        "Forged steel plate.", ArmorSet.STEEL, ItemType.CHESTPLATE, 35,
        new SetBonus(2, StatType.STR, 10, "+10 STR"),
        new SetBonus(4, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(6, StatType.HP_REGEN, 4, "+4 HP/s")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Steel Leggings", ItemRarity.RARE, ItemType.LEGGINGS, 3, 350,
        "Steel-plated greaves.", ArmorSet.STEEL, ItemType.LEGGINGS, 28,
        new SetBonus(2, StatType.STR, 10, "+10 STR"),
        new SetBonus(4, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(6, StatType.HP_REGEN, 4, "+4 HP/s")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Steel Boots", ItemRarity.RARE, ItemType.BOOTS, 2, 250,
        "Heavy steel boots.", ArmorSet.STEEL, ItemType.BOOTS, 20,
        new SetBonus(2, StatType.STR, 10, "+10 STR"),
        new SetBonus(4, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(6, StatType.HP_REGEN, 4, "+4 HP/s")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Steel Shield", ItemRarity.RARE, ItemType.SHIELD, 4, 320,
        "A towering steel shield.", ArmorSet.STEEL, ItemType.SHIELD, 25,
        new SetBonus(2, StatType.STR, 10, "+10 STR"),
        new SetBonus(4, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(6, StatType.HP_REGEN, 4, "+4 HP/s")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Steel Greatsword", ItemRarity.RARE, ItemType.WEAPON, 3, 340,
        "A massive steel blade.", ArmorSet.STEEL, ItemType.WEAPON, 22,
        new SetBonus(2, StatType.STR, 10, "+10 STR"),
        new SetBonus(4, StatType.VIT, 8, "+8 VIT"),
        new SetBonus(6, StatType.HP_REGEN, 4, "+4 HP/s")
    ) {};
}
