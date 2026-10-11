package com.aincrad.crystal.socket;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import com.mojang.serialization.DataResult;
import com.mojang.datafixers.util.Pair;

public class CrystalSocket {
    private static final String SOCKET_TAG = "AincradSockets";

    private static CompoundTag getTag(ItemStack stack) {
        return new CompoundTag();
    }

    private static void setTag(ItemStack stack, CompoundTag tag) {
        // Persistence requires DataComponentType registration in MC 26.3
    }

    public static int getSocketCount(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag.contains(SOCKET_TAG) ? tag.getListOrEmpty(SOCKET_TAG).size() : 0;
    }

    public static ItemStack getCrystal(ItemStack stack, int slot) {
        CompoundTag tag = getTag(stack);
        if (!tag.contains(SOCKET_TAG)) return ItemStack.EMPTY;
        ListTag list = tag.getListOrEmpty(SOCKET_TAG);
        if (slot < 0 || slot >= list.size()) return ItemStack.EMPTY;
        CompoundTag entry = list.getCompoundOrEmpty(slot);
        if (!entry.contains("crystal")) return ItemStack.EMPTY;
        DataResult<Pair<ItemStack, net.minecraft.nbt.Tag>> result = ItemStack.CODEC.decode(NbtOps.INSTANCE, entry.get("crystal"));
        return result.result().map(Pair::getFirst).orElse(ItemStack.EMPTY);
    }

    public static void setCrystal(ItemStack stack, int slot, ItemStack crystal) {
        CompoundTag tag = getTag(stack);
        ListTag list = tag.getListOrEmpty(SOCKET_TAG);
        while (list.size() <= slot) {
            list.add(new CompoundTag());
        }
        CompoundTag entry = new CompoundTag();
        entry.putInt("slot", slot);
        ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, crystal).result()
            .ifPresent(t -> entry.put("crystal", t));
        list.set(slot, entry);
        tag.put(SOCKET_TAG, list);
        setTag(stack, tag);
    }

    public static void removeCrystal(ItemStack stack, int slot) {
        CompoundTag tag = getTag(stack);
        if (!tag.contains(SOCKET_TAG)) return;
        ListTag list = tag.getListOrEmpty(SOCKET_TAG);
        if (slot < 0 || slot >= list.size()) return;
        list.remove(slot);
        tag.put(SOCKET_TAG, list);
        setTag(stack, tag);
    }

    public static void initSockets(ItemStack stack, int count) {
        CompoundTag tag = getTag(stack);
        if (!tag.contains(SOCKET_TAG)) {
            ListTag list = new ListTag();
            for (int i = 0; i < count; i++) {
                list.add(new CompoundTag());
            }
            tag.put(SOCKET_TAG, list);
            setTag(stack, tag);
        }
    }
}
