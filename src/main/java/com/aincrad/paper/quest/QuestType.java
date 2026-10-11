package com.aincrad.paper.quest;

/**
 * Quest type classification.
 */
public enum QuestType {
    MAIN("Main Story", false),
    SIDE("Side Quest", true),
    DAILY("Daily Quest", true),
    WEEKLY("Weekly Quest", true);

    private final String displayName;
    private final boolean repeatable;

    QuestType(String displayName, boolean repeatable) {
        this.displayName = displayName;
        this.repeatable = repeatable;
    }

    public String getDisplayName() { return displayName; }
    public boolean isRepeatable() { return repeatable; }
}
