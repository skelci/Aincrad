package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class HolySet {
    private HolySet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Holy Helmet", ItemRarity.LEGENDARY, ItemType.HELMET, 3, 2500,
        "Blessed by the divine.", ArmorSet.HOLY, ItemType.HELMET, 70,
        new SetBonus(2, StatType.VIT, 22, "+22 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 8, "+8 HP/s"),
        new SetBonus(6, StatType.MP_REGEN, 8, "+8 MP/s")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Holy Chestplate", ItemRarity.LEGENDARY, ItemType.CHESTPLATE, 5, 3000,
        "Sacred plate of the gods.", ArmorSet.HOLY, ItemType.CHESTPLATE, 130,
        new SetBonus(2, StatType.VIT, 22, "+22 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 8, "+8 HP/s"),
        new SetBonus(6, StatType.MP_REGEN, 8, "+8 MP/s")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Holy Leggings", ItemRarity.LEGENDARY, ItemType.LEGGINGS, 4, 2700,
        "Consecrated greaves.", ArmorSet.HOLY, ItemType.LEGGINGS, 100,
        new SetBonus(2, StatType.VIT, 22, "+22 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 8, "+8 HP/s"),
        new SetBonus(6, StatType.MP_REGEN, 8, "+8 MP/s")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Holy Boots", ItemRarity.LEGENDARY, ItemType.BOOTS, 3, 2000,
        "Boots of the righteous.", ArmorSet.HOLY, ItemType.BOOTS, 70,
        new SetBonus(2, StatType.VIT, 22, "+22 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 8, "+8 HP/s"),
        new SetBonus(6, StatType.MP_REGEN, 8, "+8 MP/s")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Holy Shield", ItemRarity.LEGENDARY, ItemType.SHIELD, 5, 2300,
        "Aegis of the gods.", ArmorSet.HOLY, ItemType.SHIELD, 80,
        new SetBonus(2, StatType.VIT, 22, "+22 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 8, "+8 HP/s"),
        new SetBonus(6, StatType.MP_REGEN, 8, "+8 MP/s")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Holy Mace", ItemRarity.LEGENDARY, ItemType.WEAPON, 4, 2600,
        "Smite the wicked.", ArmorSet.HOLY, ItemType.WEAPON, 75,
        new SetBonus(2, StatType.VIT, 22, "+22 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 8, "+8 HP/s"),
        new SetBonus(6, StatType.MP_REGEN, 8, "+8 MP/s")
    ) {};
}
