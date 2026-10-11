package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class VoidSet {
    private VoidSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Void Helmet", ItemRarity.MYTHIC, ItemType.HELMET, 3, 3500,
        "Touched by the abyss.", ArmorSet.VOID, ItemType.HELMET, 80,
        new SetBonus(2, StatType.INT, 25, "+25 INT"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 30, "+30% Crit Dmg"),
        new SetBonus(6, StatType.EVASION, 15, "+15% Evasion")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Void Chestplate", ItemRarity.MYTHIC, ItemType.CHESTPLATE, 6, 4000,
        "Armor from beyond the abyss.", ArmorSet.VOID, ItemType.CHESTPLATE, 150,
        new SetBonus(2, StatType.INT, 25, "+25 INT"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 30, "+30% Crit Dmg"),
        new SetBonus(6, StatType.EVASION, 15, "+15% Evasion")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Void Leggings", ItemRarity.MYTHIC, ItemType.LEGGINGS, 5, 3700,
        "Greaves that consume light.", ArmorSet.VOID, ItemType.LEGGINGS, 120,
        new SetBonus(2, StatType.INT, 25, "+25 INT"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 30, "+30% Crit Dmg"),
        new SetBonus(6, StatType.EVASION, 15, "+15% Evasion")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Void Boots", ItemRarity.MYTHIC, ItemType.BOOTS, 3, 2800,
        "Walk between worlds.", ArmorSet.VOID, ItemType.BOOTS, 80,
        new SetBonus(2, StatType.INT, 25, "+25 INT"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 30, "+30% Crit Dmg"),
        new SetBonus(6, StatType.EVASION, 15, "+15% Evasion")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Void Shield", ItemRarity.MYTHIC, ItemType.SHIELD, 5, 3200,
        "A portal to nothingness.", ArmorSet.VOID, ItemType.SHIELD, 90,
        new SetBonus(2, StatType.INT, 25, "+25 INT"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 30, "+30% Crit Dmg"),
        new SetBonus(6, StatType.EVASION, 15, "+15% Evasion")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Void Blade", ItemRarity.MYTHIC, ItemType.WEAPON, 4, 3800,
        "A weapon that devours souls.", ArmorSet.VOID, ItemType.WEAPON, 85,
        new SetBonus(2, StatType.INT, 25, "+25 INT"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 30, "+30% Crit Dmg"),
        new SetBonus(6, StatType.EVASION, 15, "+15% Evasion")
    ) {};
}
