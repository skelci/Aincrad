package com.aincrad.crystal.socket;

import net.minecraft.world.item.ItemStack;

public interface Socketable {
    int getMaxSockets(ItemStack stack);
    boolean canSocket(ItemStack stack, ItemStack crystal);
    void insertCrystal(ItemStack stack, int slot, ItemStack crystal);
    ItemStack removeCrystal(ItemStack stack, int slot);
    ItemStack getCrystal(ItemStack stack, int slot);
    boolean isSocketable(ItemStack stack);
}
