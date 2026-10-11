package com.aincrad.armor.impl;

import com.aincrad.armor.ArmorPiece;
import com.aincrad.armor.ArmorSet;
import com.aincrad.armor.SetBonus;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;
import com.aincrad.combat.stat.StatType;

public final class MythrilSet {
    private MythrilSet() {}

    public static final ArmorPiece HELMET = new ArmorPiece(
        "Mythril Helmet", ItemRarity.RARE, ItemType.HELMET, 1, 500,
        "Elven-crafted mythril helm.", ArmorSet.MYTHRIL, ItemType.HELMET, 30,
        new SetBonus(2, StatType.AGI, 12, "+12 AGI"),
        new SetBonus(4, StatType.EVASION, 6, "+6% Evasion"),
        new SetBonus(6, StatType.MP_REGEN, 5, "+5 MP/s")
    ) {};

    public static final ArmorPiece CHESTPLATE = new ArmorPiece(
        "Mythril Chestplate", ItemRarity.RARE, ItemType.CHESTPLATE, 3, 650,
        "Light as silk, hard as diamond.", ArmorSet.MYTHRIL, ItemType.CHESTPLATE, 50,
        new SetBonus(2, StatType.AGI, 12, "+12 AGI"),
        new SetBonus(4, StatType.EVASION, 6, "+6% Evasion"),
        new SetBonus(6, StatType.MP_REGEN, 5, "+5 MP/s")
    ) {};

    public static final ArmorPiece LEGGINGS = new ArmorPiece(
        "Mythril Leggings", ItemRarity.RARE, ItemType.LEGGINGS, 2, 550,
        "Graceful mythril leggings.", ArmorSet.MYTHRIL, ItemType.LEGGINGS, 40,
        new SetBonus(2, StatType.AGI, 12, "+12 AGI"),
        new SetBonus(4, StatType.EVASION, 6, "+6% Evasion"),
        new SetBonus(6, StatType.MP_REGEN, 5, "+5 MP/s")
    ) {};

    public static final ArmorPiece BOOTS = new ArmorPiece(
        "Mythril Boots", ItemRarity.RARE, ItemType.BOOTS, 1, 400,
        "Whisper-quiet mythril boots.", ArmorSet.MYTHRIL, ItemType.BOOTS, 30,
        new SetBonus(2, StatType.AGI, 12, "+12 AGI"),
        new SetBonus(4, StatType.EVASION, 6, "+6% Evasion"),
        new SetBonus(6, StatType.MP_REGEN, 5, "+5 MP/s")
    ) {};

    public static final ArmorPiece SHIELD = new ArmorPiece(
        "Mythril Shield", ItemRarity.RARE, ItemType.SHIELD, 2, 480,
        "A shimmering mythril shield.", ArmorSet.MYTHRIL, ItemType.SHIELD, 35,
        new SetBonus(2, StatType.AGI, 12, "+12 AGI"),
        new SetBonus(4, StatType.EVASION, 6, "+6% Evasion"),
        new SetBonus(6, StatType.MP_REGEN, 5, "+5 MP/s")
    ) {};

    public static final ArmorPiece WEAPON = new ArmorPiece(
        "Mythril Rapier", ItemRarity.RARE, ItemType.WEAPON, 1, 520,
        "An elegant mythril blade.", ArmorSet.MYTHRIL, ItemType.WEAPON, 28,
        new SetBonus(2, StatType.AGI, 12, "+12 AGI"),
        new SetBonus(4, StatType.EVASION, 6, "+6% Evasion"),
        new SetBonus(6, StatType.MP_REGEN, 5, "+5 MP/s")
    ) {};
}
