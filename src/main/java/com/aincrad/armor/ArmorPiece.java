package com.aincrad.armor;

import com.aincrad.item.AincradItem;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public abstract class ArmorPiece extends AincradItem {
    private final ArmorSet set;
    private final ItemType slotType;
    private final int defense;
    private final SetBonus bonus2;
    private final SetBonus bonus4;
    private final SetBonus bonus6;

    protected ArmorPiece(String name, ItemRarity rarity, ItemType type,
                         int weight, int baseValue, String description,
                         ArmorSet set, ItemType slotType, int defense,
                         SetBonus bonus2, SetBonus bonus4, SetBonus bonus6) {
        super(name, rarity, type, weight, baseValue, description);
        this.set = set;
        this.slotType = slotType;
        this.defense = defense;
        this.bonus2 = bonus2;
        this.bonus4 = bonus4;
        this.bonus6 = bonus6;
    }

    public ArmorSet getSet() { return set; }
    public ItemType getSlotType() { return slotType; }
    public int getDefense() { return defense; }
    public SetBonus getBonus2() { return bonus2; }
    public SetBonus getBonus4() { return bonus4; }
    public SetBonus getBonus6() { return bonus6; }
}
