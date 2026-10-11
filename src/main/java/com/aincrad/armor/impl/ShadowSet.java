package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class ShadowSet {
    private ShadowSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Shadow Helmet", ItemRarity.LEGENDARY, ItemType.HELMET, 2, 2000,
        "Woven from pure darkness.", ArmorSet.SHADOW, ItemType.HELMET, 60,
        new SetBonus(2, StatType.AGI, 20, "+20 AGI"),
        new SetBonus(4, StatType.CRIT_RATE, 15, "+15% Crit Rate"),
        new SetBonus(6, StatType.EVASION, 12, "+12% Evasion")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Shadow Chestplate", ItemRarity.LEGENDARY, ItemType.CHESTPLATE, 4, 2500,
        "Armor of the void walker.", ArmorSet.SHADOW, ItemType.CHESTPLATE, 110,
        new SetBonus(2, StatType.AGI, 20, "+20 AGI"),
        new SetBonus(4, StatType.CRIT_RATE, 15, "+15% Crit Rate"),
        new SetBonus(6, StatType.EVASION, 12, "+12% Evasion")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Shadow Leggings", ItemRarity.LEGENDARY, ItemType.LEGGINGS, 3, 2200,
        "Silent as the grave.", ArmorSet.SHADOW, ItemType.LEGGINGS, 85,
        new SetBonus(2, StatType.AGI, 20, "+20 AGI"),
        new SetBonus(4, StatType.CRIT_RATE, 15, "+15% Crit Rate"),
        new SetBonus(6, StatType.EVASION, 12, "+12% Evasion")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Shadow Boots", ItemRarity.LEGENDARY, ItemType.BOOTS, 2, 1600,
        "Steps without sound.", ArmorSet.SHADOW, ItemType.BOOTS, 60,
        new SetBonus(2, StatType.AGI, 20, "+20 AGI"),
        new SetBonus(4, StatType.CRIT_RATE, 15, "+15% Crit Rate"),
        new SetBonus(6, StatType.EVASION, 12, "+12% Evasion")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Shadow Shield", ItemRarity.LEGENDARY, ItemType.SHIELD, 4, 1800,
        "A barrier of darkness.", ArmorSet.SHADOW, ItemType.SHIELD, 70,
        new SetBonus(2, StatType.AGI, 20, "+20 AGI"),
        new SetBonus(4, StatType.CRIT_RATE, 15, "+15% Crit Rate"),
        new SetBonus(6, StatType.EVASION, 12, "+12% Evasion")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Shadow Dagger", ItemRarity.LEGENDARY, ItemType.WEAPON, 2, 2100,
        "Strikes from nowhere.", ArmorSet.SHADOW, ItemType.WEAPON, 55,
        new SetBonus(2, StatType.AGI, 20, "+20 AGI"),
        new SetBonus(4, StatType.CRIT_RATE, 15, "+15% Crit Rate"),
        new SetBonus(6, StatType.EVASION, 12, "+12% Evasion")
    ) {};
}
