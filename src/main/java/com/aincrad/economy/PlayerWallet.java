package com.aincrad.economy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

/**
 * Per-player Col wallet. Holds the current balance and an in-memory
 * transaction log. Persists to a {@link CompoundTag} so it can be stored
 * on the player's NBT data.
 */
public final class PlayerWallet {

    private static final String NBT_BALANCE = "ColBalance";
    private static final String NBT_LOG = "ColLog";
    private static final String NBT_LOG_AMOUNT = "amount";
    private static final String NBT_LOG_REASON = "reason";
    private static final String NBT_LOG_BALANCE_AFTER = "balanceAfter";

    /** Maximum number of transaction entries kept in memory. */
    private static final int MAX_LOG_ENTRIES = 100;

    private final String ownerUuid;
    private Col balance;
    private final List<TransactionEntry> transactionLog;

    public PlayerWallet(String ownerUuid) {
        this(ownerUuid, Col.ZERO);
    }

    public PlayerWallet(String ownerUuid, Col initialBalance) {
        if (ownerUuid == null || ownerUuid.isBlank()) {
            throw new IllegalArgumentException("ownerUuid must not be null or blank");
        }
        this.ownerUuid = ownerUuid;
        this.balance = initialBalance != null ? initialBalance : Col.ZERO;
        this.transactionLog = new ArrayList<>();
    }

    // ── Balance ──────────────────────────────────────────────────────

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public Col getBalance() {
        return balance;
    }

    /**
     * Adds Col to this wallet and records the transaction.
     *
     * @param amount amount to add (must be non-negative)
     * @param reason human-readable reason for the deposit
     * @return the new balance
     */
    public Col deposit(Col amount, String reason) {
        if (amount == null || amount.isZero()) {
            return balance;
        }
        this.balance = this.balance.add(amount);
        logTransaction(amount, reason);
        return balance;
    }

    /**
     * Removes Col from this wallet and records the transaction.
     *
     * @param amount amount to remove (must be non-negative)
     * @param reason human-readable reason for the withdrawal
     * @return the new balance
     * @throws IllegalArgumentException if the wallet has insufficient funds
     */
    public Col withdraw(Col amount, String reason) {
        if (amount == null || amount.isZero()) {
            return balance;
        }
        this.balance = this.balance.subtract(amount);
        logTransaction(amount.negate(), reason);
        return balance;
    }

    // ── Transaction log ──────────────────────────────────────────────

    /** Returns an unmodifiable view of the transaction log (most recent first). */
    public List<TransactionEntry> getTransactionLog() {
        return Collections.unmodifiableList(transactionLog);
    }

    private void logTransaction(Col delta, String reason) {
        transactionLog.add(0, new TransactionEntry(delta, reason, balance));
        if (transactionLog.size() > MAX_LOG_ENTRIES) {
            transactionLog.remove(transactionLog.size() - 1);
        }
    }

    // ── NBT persistence ──────────────────────────────────────────────

    /** Saves this wallet's balance to the given tag. */
    public void saveToNBT(CompoundTag tag) {
        tag.putLong(NBT_BALANCE, balance.getAmount());
    }

    /**
     * Loads a wallet from the given tag. The transaction log is not
     * persisted and will be empty on load.
     */
    public static PlayerWallet loadFromNBT(String ownerUuid, CompoundTag tag) {
        long amount = tag.getLong(NBT_BALANCE).orElse(0L);
        return new PlayerWallet(ownerUuid, new Col(amount));
    }

    // ── Transaction record ───────────────────────────────────────────

    /**
     * A single immutable transaction record.
     */
    public static final class TransactionEntry {
        private final Col delta;
        private final String reason;
        private final Col balanceAfter;

        TransactionEntry(Col delta, String reason, Col balanceAfter) {
            this.delta = delta;
            this.reason = reason != null ? reason : "";
            this.balanceAfter = balanceAfter;
        }

        public Col getDelta() {
            return delta;
        }

        public String getReason() {
            return reason;
        }

        public Col getBalanceAfter() {
            return balanceAfter;
        }

        @Override
        public String toString() {
            String sign = delta.getAmount() >= 0 ? "+" : "";
            return sign + delta.format() + " (" + reason + ") -> " + balanceAfter.format();
        }
    }
}
