package com.aincrad.armor;

public enum ArmorSet {
    LEATHER("Leather", "Basic starter armor", 1),
    CHAIN("Chain", "Light and flexible", 2),
    IRON("Iron", "Reliable protection", 3),
    STEEL("Steel", "Forged for war", 4),
    MYTHRIL("Mythril", "Elven craftsmanship", 5),
    ADAMANTITE("Adamantite", "Unbreakable ore", 6),
    DRAGON("Dragon", "Scales of the wyrm", 7),
    SHADOW("Shadow", "Woven from darkness", 8),
    HOLY("Holy", "Blessed by the gods", 9),
    VOID("Void", "Touched by the abyss", 10);

    private final String displayName;
    private final String lore;
    private final int tier;

    ArmorSet(String displayName, String lore, int tier) {
        this.displayName = displayName;
        this.lore = lore;
        this.tier = tier;
    }

    public String getDisplayName() { return displayName; }
    public String getLore() { return lore; }
    public int getTier() { return tier; }
}
