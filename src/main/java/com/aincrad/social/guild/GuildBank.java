package com.aincrad.social.guild;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Thread-safe vault belonging to a {@link Guild}.
 * Slot count scales with guild level via {@link GuildLevel#bankSlots(int)}.
 * A bounded ledger records the most recent {@value #LEDGER_CAPACITY} transactions
 * for audit / moderator review.
 */
public final class GuildBank {

    /** Maximum number of ledger entries retained. */
    public static final int LEDGER_CAPACITY = 100;

    private final int capacity;
    private final List<Stack> slots;
    private final Deque<LedgerEntry> ledger;

    /**
     * Creates an empty bank sized for the given guild level.
     *
     * @param guildLevel guild level used to compute slot capacity
     * @throws IllegalArgumentException if level is outside 1..{@link GuildLevel#MAX_LEVEL}
     */
    public GuildBank(int guildLevel) {
        this.capacity = GuildLevel.bankSlots(guildLevel);
        this.slots = new ArrayList<>(Collections.nCopies(capacity, null));
        this.ledger = new ArrayDeque<>(LEDGER_CAPACITY);
    }

    public synchronized int getCapacity() {
        return capacity;
    }

    /** Unmodifiable snapshot of live slots (null = empty slot). */
    public synchronized List<Stack> getSlots() {
        return Collections.unmodifiableList(new ArrayList<>(slots));
    }

    /** Unmodifiable ledger snapshot, oldest first. */
    public synchronized List<LedgerEntry> getLedger() {
        return List.copyOf(ledger);
    }

    /**
     * Attempts to store a stack in the first free slot.
     *
     * @param stack item stack to deposit (non-null)
     * @param actor  player name performing the deposit (non-null, for ledger)
     * @return the slot index used, or -1 if the bank is full
     */
    public synchronized int deposit(Stack stack, String actor) {
        Objects.requireNonNull(stack, "stack");
        Objects.requireNonNull(actor, "actor");
        int idx = slots.indexOf(null);
        if (idx < 0) {
            return -1;
        }
        slots.set(idx, stack);
        record(TransactionType.DEPOSIT, actor, stack);
        return idx;
    }

    /**
     * Removes and returns the stack at {@code slot}.
     *
     * @param slot index into {@link #getSlots()}
     * @return the removed stack, or null if the slot was empty
     * @throws IndexOutOfBoundsException if slot is outside capacity
     */
    public synchronized Stack withdraw(int slot, String actor) {
        Objects.requireNonNull(actor, "actor");
        if (slot < 0 || slot >= capacity) {
            throw new IndexOutOfBoundsException(
                    "slot " + slot + " outside 0.." + (capacity - 1));
        }
        Stack removed = slots.get(slot);
        if (removed == null) {
            return null;
        }
        slots.set(slot, null);
        record(TransactionType.WITHDRAW, actor, removed);
        return removed;
    }

    /** Returns true when at least one slot is free. */
    public synchronized boolean hasSpace() {
        return slots.contains(null);
    }

    /** Number of occupied slots. */
    public synchronized int usedSlots() {
        int used = 0;
        for (Stack s : slots) {
            if (s != null) used++;
        }
        return used;
    }

    private void record(TransactionType type, String actor, Stack stack) {
        if (ledger.size() >= LEDGER_CAPACITY) {
            ledger.pollFirst(); // drop oldest
        }
        ledger.offerLast(new LedgerEntry(
                System.currentTimeMillis(), type, actor, stack));
    }

    // -------------------------------------------------------------------------
    // Inner types
    // -------------------------------------------------------------------------

    /** Immutable item-stack placeholder. Replace with Minecraft ItemStack in mod. */
    public record Stack(String itemId, int count) {
        public Stack {
            Objects.requireNonNull(itemId, "itemId");
            if (count < 1) {
                throw new IllegalArgumentException("count must be >= 1");
            }
        }

        @Override
        public String toString() {
            return count + "x " + itemId;
        }
    }

    /** Direction of a bank transaction. */
    public enum TransactionType { DEPOSIT, WITHDRAW }

    /** Immutable audit entry stored in the rolling ledger. */
    public record LedgerEntry(long timestamp, TransactionType type,
                              String actor, Stack stack) {
        public LedgerEntry {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(actor, "actor");
            Objects.requireNonNull(stack, "stack");
            if (timestamp < 0) {
                throw new IllegalArgumentException("timestamp must be >= 0");
            }
        }
    }
}
