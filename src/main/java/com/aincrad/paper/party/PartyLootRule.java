package com.aincrad.paper.party;

/**
 * Party loot distribution rules.
 */
public enum PartyLootRule {
    FREE_FOR_ALL("Free for All", "First to pick up keeps it"),
    ROUND_ROBIN("Round Robin", "Items distributed in turn order"),
    NEED_GREED("Need/Greed", "Roll-based distribution");

    private final String displayName;
    private final String description;

    PartyLootRule(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
