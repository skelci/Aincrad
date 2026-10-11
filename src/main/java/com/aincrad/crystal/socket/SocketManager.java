package com.aincrad.crystal.socket;

import net.minecraft.world.item.ItemStack;

public class SocketManager implements Socketable {
    @Override
    public int getMaxSockets(ItemStack stack) {
        return 2;
    }

    @Override
    public boolean canSocket(ItemStack stack, ItemStack crystal) {
        if (CrystalSocket.getSocketCount(stack) >= getMaxSockets(stack)) return false;
        return true;
    }

    @Override
    public void insertCrystal(ItemStack stack, int slot, ItemStack crystal) {
        if (canSocket(stack, crystal)) {
            CrystalSocket.setCrystal(stack, slot, crystal);
        }
    }

    @Override
    public ItemStack removeCrystal(ItemStack stack, int slot) {
        ItemStack crystal = CrystalSocket.getCrystal(stack, slot);
        CrystalSocket.removeCrystal(stack, slot);
        return crystal;
    }

    @Override
    public ItemStack getCrystal(ItemStack stack, int slot) {
        return CrystalSocket.getCrystal(stack, slot);
    }

    @Override
    public boolean isSocketable(ItemStack stack) {
        return true;
    }
}
