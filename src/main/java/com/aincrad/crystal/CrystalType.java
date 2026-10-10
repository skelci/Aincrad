package com.aincrad.crystal;

public enum CrystalType {
    SWORD("Sword"),
    SHIELD("Shield"),
    ARMOR("Armor"),
    AGILITY("Agility"),
    MAGIC("Magic"),
    VITALITY("Vitality");

    private final String displayName;

    CrystalType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
