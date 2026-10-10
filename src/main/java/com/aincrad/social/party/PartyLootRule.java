package com.aincrad.social.party;

/**
 * Defines how loot is distributed among party members.
 * <p>
 * Each rule has a human-readable description suitable for display in the UI.
 */
public enum PartyLootRule {

    /** First player to pick up loot keeps it. No restrictions. */
    FREE_FOR_ALL("Free for all — first player to pick up loot keeps it."),

    /** Loot is distributed in turn order among all party members. */
    ROUND_ROBIN("Round robin — loot is distributed in turn order among members."),

    /** Members roll Need or Greed on eligible drops. */
    NEED_GREED("Need/Greed — members roll Need or Greed on eligible drops.");

    private final String description;

    PartyLootRule(String description) {
        this.description = description;
    }

    /**
     * @return a human-readable description of this loot rule
     */
    public String getDescription() {
        return description;
    }
}
