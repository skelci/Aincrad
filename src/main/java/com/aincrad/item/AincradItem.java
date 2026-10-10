package com.aincrad.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/**
 * Abstract base class for all Aincrad items.
 * Handles rarity, type, weight, and description.
 * NBT-persisted metadata (enhancement, affixes, binding) is
 * managed by subclasses via the enhancement/affix/binding API.
 */
public abstract class AincradItem {

    protected final String name;
    protected final ItemRarity rarity;
    protected final ItemType type;
    protected final int weight;
    protected final int baseValue;
    protected final String description;

    public AincradItem(String name, ItemRarity rarity, ItemType type,
                       int weight, int baseValue, String description) {
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.weight = weight;
        this.baseValue = baseValue;
        this.description = description;
    }

    public String getName() { return name; }
    public ItemRarity getRarity() { return rarity; }
    public ItemType getType() { return type; }
    public int getWeight() { return weight; }
    public int getBaseValue() { return baseValue; }
    public String getDescription() { return description; }

    /**
     * Returns the display name with rarity color and type label.
     */
    public String getDisplayName() {
        return rarity.getDisplayName() + " " + name + " [" + type.name() + "]";
    }

    /**
     * Returns lore lines for this item.
     */
    public String getLore() {
        return rarity.getDisplayName() + " " + type.name() + "\n"
             + "Weight: " + weight + "\n"
             + description;
    }

    // ── Enhancement NBT Support ─────────────────────────────────────

    /**
     * Gets the enhancement level from NBT.
     */
    public int getEnhancementLevel(ItemStack stack) {
        return 0; // TODO: Fix with DataComponent API
    }

    /**
     * Sets the enhancement level in NBT.
     */
    public void setEnhancementLevel(ItemStack stack, int level) {
        // TODO: Fix with DataComponent API
    }
}
