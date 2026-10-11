package com.aincrad.paper.economy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Per-player Col wallet. Holds the current balance and an in-memory transaction log.
 */
public final class PlayerWallet {
    private static final int MAX_LOG_ENTRIES = 100;

    private final UUID ownerUuid;
    private Col balance;
    private final List<TransactionEntry> transactionLog;

    public PlayerWallet(UUID ownerUuid) {
        this(ownerUuid, Col.ZERO);
    }

    public PlayerWallet(UUID ownerUuid, Col initialBalance) {
        if (ownerUuid == null) throw new IllegalArgumentException("ownerUuid must not be null");
        this.ownerUuid = ownerUuid;
        this.balance = initialBalance != null ? initialBalance : Col.ZERO;
        this.transactionLog = new ArrayList<>();
    }

    public UUID getOwnerUuid() { return ownerUuid; }
    public Col getBalance() { return balance; }

    public Col deposit(Col amount, String reason) {
        if (amount == null || amount.isZero()) return balance;
        this.balance = this.balance.add(amount);
        logTransaction(amount, reason);
        return balance;
    }

    public Col withdraw(Col amount, String reason) {
        if (amount == null || amount.isZero()) return balance;
        this.balance = this.balance.subtract(amount);
        logTransaction(amount.negate(), reason);
        return balance;
    }

    public List<TransactionEntry> getTransactionLog() {
        return Collections.unmodifiableList(transactionLog);
    }

    private void logTransaction(Col delta, String reason) {
        transactionLog.add(0, new TransactionEntry(delta, reason, balance));
        if (transactionLog.size() > MAX_LOG_ENTRIES) {
            transactionLog.remove(transactionLog.size() - 1);
        }
    }

    public static final class TransactionEntry {
        private final Col delta;
        private final String reason;
        private final Col balanceAfter;

        TransactionEntry(Col delta, String reason, Col balanceAfter) {
            this.delta = delta;
            this.reason = reason != null ? reason : "";
            this.balanceAfter = balanceAfter;
        }

        public Col getDelta() { return delta; }
        public String getReason() { return reason; }
        public Col getBalanceAfter() { return balanceAfter; }

        @Override
        public String toString() {
            String sign = delta.getAmount() >= 0 ? "+" : "";
            return sign + delta.format() + " (" + reason + ") -> " + balanceAfter.format();
        }
    }
}
