package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class AdamantiteSet {
    private AdamantiteSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Adamantite Helmet", ItemRarity.EPIC, ItemType.HELMET, 3, 800,
        "Unbreakable adamantite helm.", ArmorSet.ADAMANTITE, ItemType.HELMET, 40,
        new SetBonus(2, StatType.STR, 15, "+15 STR"),
        new SetBonus(4, StatType.VIT, 12, "+12 VIT"),
        new SetBonus(6, StatType.EVASION, 8, "+8% Evasion")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Adamantite Chestplate", ItemRarity.EPIC, ItemType.CHESTPLATE, 5, 1000,
        "Impenetrable adamantite plate.", ArmorSet.ADAMANTITE, ItemType.CHESTPLATE, 70,
        new SetBonus(2, StatType.STR, 15, "+15 STR"),
        new SetBonus(4, StatType.VIT, 12, "+12 VIT"),
        new SetBonus(6, StatType.EVASION, 8, "+8% Evasion")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Adamantite Leggings", ItemRarity.EPIC, ItemType.LEGGINGS, 4, 850,
        "Massive adamantite greaves.", ArmorSet.ADAMANTITE, ItemType.LEGGINGS, 55,
        new SetBonus(2, StatType.STR, 15, "+15 STR"),
        new SetBonus(4, StatType.VIT, 12, "+12 VIT"),
        new SetBonus(6, StatType.EVASION, 8, "+8% Evasion")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Adamantite Boots", ItemRarity.EPIC, ItemType.BOOTS, 3, 650,
        "Earth-shaking adamantite boots.", ArmorSet.ADAMANTITE, ItemType.BOOTS, 40,
        new SetBonus(2, StatType.STR, 15, "+15 STR"),
        new SetBonus(4, StatType.VIT, 12, "+12 VIT"),
        new SetBonus(6, StatType.EVASION, 8, "+8% Evasion")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Adamantite Shield", ItemRarity.EPIC, ItemType.SHIELD, 5, 750,
        "A fortress of adamantite.", ArmorSet.ADAMANTITE, ItemType.SHIELD, 50,
        new SetBonus(2, StatType.STR, 15, "+15 STR"),
        new SetBonus(4, StatType.VIT, 12, "+12 VIT"),
        new SetBonus(6, StatType.EVASION, 8, "+8% Evasion")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Adamantite Warhammer", ItemRarity.EPIC, ItemType.WEAPON, 4, 820,
        "A devastating warhammer.", ArmorSet.ADAMANTITE, ItemType.WEAPON, 45,
        new SetBonus(2, StatType.STR, 15, "+15 STR"),
        new SetBonus(4, StatType.VIT, 12, "+12 VIT"),
        new SetBonus(6, StatType.EVASION, 8, "+8% Evasion")
    ) {};
}
