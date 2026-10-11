package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class DragonSet {
    private DragonSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Dragon Helmet", ItemRarity.EPIC, ItemType.HELMET, 3, 1200,
        "Crafted from wyrm scales.", ArmorSet.DRAGON, ItemType.HELMET, 50,
        new SetBonus(2, StatType.STR, 18, "+18 STR"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 25, "+25% Crit Dmg"),
        new SetBonus(6, StatType.HP_REGEN, 6, "+6 HP/s")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Dragon Chestplate", ItemRarity.EPIC, ItemType.CHESTPLATE, 5, 1500,
        "Scales of the ancient wyrm.", ArmorSet.DRAGON, ItemType.CHESTPLATE, 90,
        new SetBonus(2, StatType.STR, 18, "+18 STR"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 25, "+25% Crit Dmg"),
        new SetBonus(6, StatType.HP_REGEN, 6, "+6 HP/s")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Dragon Leggings", ItemRarity.EPIC, ItemType.LEGGINGS, 4, 1300,
        "Dragon-scale greaves.", ArmorSet.DRAGON, ItemType.LEGGINGS, 70,
        new SetBonus(2, StatType.STR, 18, "+18 STR"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 25, "+25% Crit Dmg"),
        new SetBonus(6, StatType.HP_REGEN, 6, "+6 HP/s")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Dragon Boots", ItemRarity.EPIC, ItemType.BOOTS, 3, 1000,
        "Talons of the dragon.", ArmorSet.DRAGON, ItemType.BOOTS, 50,
        new SetBonus(2, StatType.STR, 18, "+18 STR"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 25, "+25% Crit Dmg"),
        new SetBonus(6, StatType.HP_REGEN, 6, "+6 HP/s")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Dragon Shield", ItemRarity.EPIC, ItemType.SHIELD, 5, 1100,
        "A shield of dragon bone.", ArmorSet.DRAGON, ItemType.SHIELD, 60,
        new SetBonus(2, StatType.STR, 18, "+18 STR"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 25, "+25% Crit Dmg"),
        new SetBonus(6, StatType.HP_REGEN, 6, "+6 HP/s")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Dragon Greatsword", ItemRarity.EPIC, ItemType.WEAPON, 4, 1400,
        "Forged in dragon fire.", ArmorSet.DRAGON, ItemType.WEAPON, 65,
        new SetBonus(2, StatType.STR, 18, "+18 STR"),
        new SetBonus(4, StatType.CRIT_DAMAGE, 25, "+25% Crit Dmg"),
        new SetBonus(6, StatType.HP_REGEN, 6, "+6 HP/s")
    ) {};
}
