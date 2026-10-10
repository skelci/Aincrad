package com.aincrad.economy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Process-wide economy facade. Manages all {@link PlayerWallet} instances
 * and provides static access to balance operations. Thread-safe.
 */
public final class EconomyManager {

    private static final EconomyManager INSTANCE = new EconomyManager();

    private final Map<String, PlayerWallet> wallets = new ConcurrentHashMap<>();

    private EconomyManager() {
        // singleton — use {@link #getInstance()}
    }

    public static EconomyManager getInstance() {
        return INSTANCE;
    }

    // ── Wallet lifecycle ─────────────────────────────────────────────

    /**
     * Returns the wallet for the given player, creating one with a zero
     * balance if it does not yet exist.
     */
    public PlayerWallet getWallet(String playerUuid) {
        return wallets.computeIfAbsent(playerUuid, PlayerWallet::new);
    }

    /** Returns the wallet for the given player, or empty if none exists. */
    public Optional<PlayerWallet> findWallet(String playerUuid) {
        return Optional.ofNullable(wallets.get(playerUuid));
    }

    /** Removes a player's wallet (e.g. on logout). */
    public void removeWallet(String playerUuid) {
        wallets.remove(playerUuid);
    }

    // ── Balance operations ───────────────────────────────────────────

    /** Returns the player's current balance (zero if no wallet exists). */
    public Col getBalance(String playerUuid) {
        return findWallet(playerUuid)
                .map(PlayerWallet::getBalance)
                .orElse(Col.ZERO);
    }

    /**
     * Deposits Col into the player's wallet.
     *
     * @return the new balance
     */
    public Col deposit(String playerUuid, Col amount, String reason) {
        return getWallet(playerUuid).deposit(amount, reason);
    }

    /**
     * Withdraws Col from the player's wallet.
     *
     * @return the new balance
     * @throws IllegalArgumentException if the player has insufficient funds
     */
    public Col withdraw(String playerUuid, Col amount, String reason) {
        return getWallet(playerUuid).withdraw(amount, reason);
    }

    /**
     * Transfers Col from one player to another atomically.
     *
     * @return the sender's new balance
     * @throws IllegalArgumentException if the sender has insufficient funds
     * @throws IllegalArgumentException if sender and recipient are the same player
     */
    public Col transfer(String fromUuid, String toUuid, Col amount, String reason) {
        if (fromUuid.equals(toUuid)) {
            throw new IllegalArgumentException("cannot transfer to self");
        }
        PlayerWallet sender = getWallet(fromUuid);
        sender.withdraw(amount, "transfer to " + toUuid + ": " + reason);
        getWallet(toUuid).deposit(amount, "transfer from " + fromUuid + ": " + reason);
        return sender.getBalance();
    }

    // ── Bulk access ──────────────────────────────────────────────────

    /** Returns an unmodifiable view of all wallets. */
    public Collection<PlayerWallet> getAllWallets() {
        return Collections.unmodifiableCollection(wallets.values());
    }

    /** Returns the number of tracked wallets. */
    public int getWalletCount() {
        return wallets.size();
    }

    /** Clears all wallets — intended for tests and full world resets. */
    public void reset() {
        wallets.clear();
    }
}
