package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class ChainSet {
    private ChainSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Chain Helmet", ItemRarity.UNCOMMON, ItemType.HELMET, 1, 100,
        "A flexible chain coif.", ArmorSet.CHAIN, ItemType.HELMET, 10,
        new SetBonus(2, StatType.AGI, 5, "+5 AGI"),
        new SetBonus(4, StatType.EVASION, 4, "+4% Evasion"),
        new SetBonus(6, StatType.WEIGHT_LIMIT, 10, "+10 Weight Limit")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Chain Chestplate", ItemRarity.UNCOMMON, ItemType.CHESTPLATE, 2, 150,
        "Interlocking chain links.", ArmorSet.CHAIN, ItemType.CHESTPLATE, 18,
        new SetBonus(2, StatType.AGI, 5, "+5 AGI"),
        new SetBonus(4, StatType.EVASION, 4, "+4% Evasion"),
        new SetBonus(6, StatType.WEIGHT_LIMIT, 10, "+10 Weight Limit")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Chain Leggings", ItemRarity.UNCOMMON, ItemType.LEGGINGS, 2, 130,
        "Light chain leggings.", ArmorSet.CHAIN, ItemType.LEGGINGS, 15,
        new SetBonus(2, StatType.AGI, 5, "+5 AGI"),
        new SetBonus(4, StatType.EVASION, 4, "+4% Evasion"),
        new SetBonus(6, StatType.WEIGHT_LIMIT, 10, "+10 Weight Limit")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Chain Boots", ItemRarity.UNCOMMON, ItemType.BOOTS, 1, 80,
        "Agile chain boots.", ArmorSet.CHAIN, ItemType.BOOTS, 10,
        new SetBonus(2, StatType.AGI, 5, "+5 AGI"),
        new SetBonus(4, StatType.EVASION, 4, "+4% Evasion"),
        new SetBonus(6, StatType.WEIGHT_LIMIT, 10, "+10 Weight Limit")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Chain Shield", ItemRarity.UNCOMMON, ItemType.SHIELD, 2, 110,
        "A shield reinforced with chain.", ArmorSet.CHAIN, ItemType.SHIELD, 12,
        new SetBonus(2, StatType.AGI, 5, "+5 AGI"),
        new SetBonus(4, StatType.EVASION, 4, "+4% Evasion"),
        new SetBonus(6, StatType.WEIGHT_LIMIT, 10, "+10 Weight Limit")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Chain Flail", ItemRarity.UNCOMMON, ItemType.WEAPON, 1, 105,
        "A spiked chain flail.", ArmorSet.CHAIN, ItemType.WEAPON, 10,
        new SetBonus(2, StatType.AGI, 5, "+5 AGI"),
        new SetBonus(4, StatType.EVASION, 4, "+4% Evasion"),
        new SetBonus(6, StatType.WEIGHT_LIMIT, 10, "+10 Weight Limit")
    ) {};
}
