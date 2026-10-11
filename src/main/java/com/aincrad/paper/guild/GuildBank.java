package com.aincrad.paper.guild;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Thread-safe vault for a guild, with rolling transaction ledger.
 * Slots sized via GuildLevel.bankSlots(level).
 */
public final class GuildBank {
    public static final int LEDGER_CAPACITY = 100;

    private final int capacity;
    private final List<ItemStack> slots;
    private final Deque<LedgerEntry> ledger;

    public GuildBank(int capacity) {
        this.capacity = capacity;
        this.slots = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            slots.add(null);
        }
        this.ledger = new ArrayDeque<>();
    }

    public synchronized int getCapacity() { return capacity; }

    public synchronized List<ItemStack> getSlots() {
        return new ArrayList<>(slots);
    }

    public synchronized List<LedgerEntry> getLedger() {
        return new ArrayList<>(ledger);
    }

    public synchronized int deposit(ItemStack stack, String actor) {
        for (int i = 0; i < capacity; i++) {
            if (slots.get(i) == null) {
                slots.set(i, stack.clone());
                record(TransactionType.DEPOSIT, actor, stack);
                return i;
            }
        }
        return -1;
    }

    public synchronized ItemStack withdraw(int slot, String actor) {
        if (slot < 0 || slot >= capacity) return null;
        ItemStack stack = slots.get(slot);
        if (stack == null) return null;
        slots.set(slot, null);
        record(TransactionType.WITHDRAW, actor, stack);
        return stack;
    }

    public synchronized boolean hasSpace() {
        for (ItemStack s : slots) {
            if (s == null) return true;
        }
        return false;
    }

    public synchronized int usedSlots() {
        int count = 0;
        for (ItemStack s : slots) {
            if (s != null) count++;
        }
        return count;
    }

    private void record(TransactionType type, String actor, ItemStack stack) {
        ledger.addFirst(new LedgerEntry(System.currentTimeMillis(), type, actor,
                stack.getType().name(), stack.getAmount()));
        while (ledger.size() > LEDGER_CAPACITY) {
            ledger.removeLast();
        }
    }

    public enum TransactionType { DEPOSIT, WITHDRAW }

    public record LedgerEntry(long timestamp, TransactionType type, String actor,
                              String itemId, int count) {}
}
