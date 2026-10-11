package com.aincrad.crystal.socket;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class CrystalSocket {
    private static final String SOCKET_TAG = "AincradSockets";

    public static int getSocketCount(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains(SOCKET_TAG) ? tag.getList(SOCKET_TAG, 10).size() : 0;
    }

    public static ItemStack getCrystal(ItemStack stack, int slot) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(SOCKET_TAG)) return ItemStack.EMPTY;
        ListTag list = tag.getList(SOCKET_TAG, 10);
        if (slot < 0 || slot >= list.size()) return ItemStack.EMPTY;
        CompoundTag entry = list.getCompound(slot);
        return ItemStack.of(entry.getCompound("crystal"));
    }

    public static void setCrystal(ItemStack stack, int slot, ItemStack crystal) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList(SOCKET_TAG, 10);
        // Ensure list is big enough
        while (list.size() <= slot) {
            list.add(new CompoundTag());
        }
        CompoundTag entry = new CompoundTag();
        entry.putInt("slot", slot);
        entry.put("crystal", crystal.save(new CompoundTag()));
        list.set(slot, entry);
        tag.put(SOCKET_TAG, list);
    }

    public static void removeCrystal(ItemStack stack, int slot) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(SOCKET_TAG)) return;
        ListTag list = tag.getList(SOCKET_TAG, 10);
        if (slot < 0 || slot >= list.size()) return;
        list.remove(slot);
        tag.put(SOCKET_TAG, list);
    }

    public static void initSockets(ItemStack stack, int count) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(SOCKET_TAG)) {
            ListTag list = new ListTag();
            for (int i = 0; i < count; i++) {
                list.add(new CompoundTag());
            }
            tag.put(SOCKET_TAG, list);
        }
    }
}
