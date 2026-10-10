package com.aincrad.item;

/**
 * Item categories for classification and slot assignment.
 * The 11 equippable slots map to vanilla and custom equipment.
 */
public enum ItemType {
    WEAPON,
    ARMOR,
    SHIELD,
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    RING,
    AMULET,
    BELT,
    CRYSTAL,
    MATERIAL,
    CONSUMABLE;

    /**
     * Whether this type occupies an equipment slot.
     */
    public boolean isEquippable() {
        return switch (this) {
            case WEAPON, ARMOR, SHIELD, HELMET, CHESTPLATE,
                 LEGGINGS, BOOTS, RING, AMULET, BELT -> true;
            default -> false;
        };
    }

    /**
     * Returns the equipment slot name for equippable types,
     * or "None" for non-equippable types.
     */
    public String getSlotName() {
        return switch (this) {
            case WEAPON -> "Main Hand";
            case ARMOR, SHIELD -> "Off Hand";
            case HELMET -> "Helmet";
            case CHESTPLATE -> "Chestplate";
            case LEGGINGS -> "Leggings";
            case BOOTS -> "Boots";
            case RING -> "Ring";
            case AMULET -> "Amulet";
            case BELT -> "Belt";
            default -> "None";
        };
    }
}
