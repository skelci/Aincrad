package com.aincrad.paper.economy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Process-wide economy facade. Manages all PlayerWallet instances.
 * Thread-safe singleton.
 */
public final class EconomyManager {
    private static final EconomyManager INSTANCE = new EconomyManager();

    private final Map<UUID, PlayerWallet> wallets = new ConcurrentHashMap<>();

    private EconomyManager() {}

    public static EconomyManager getInstance() { return INSTANCE; }

    public PlayerWallet getWallet(UUID playerUuid) {
        return wallets.computeIfAbsent(playerUuid, PlayerWallet::new);
    }

    public Optional<PlayerWallet> findWallet(UUID playerUuid) {
        return Optional.ofNullable(wallets.get(playerUuid));
    }

    public void removeWallet(UUID playerUuid) {
        wallets.remove(playerUuid);
    }

    public Col getBalance(UUID playerUuid) {
        return findWallet(playerUuid).map(PlayerWallet::getBalance).orElse(Col.ZERO);
    }

    public Col deposit(UUID playerUuid, Col amount, String reason) {
        return getWallet(playerUuid).deposit(amount, reason);
    }

    public Col withdraw(UUID playerUuid, Col amount, String reason) {
        return getWallet(playerUuid).withdraw(amount, reason);
    }

    public Col transfer(UUID fromUuid, UUID toUuid, Col amount, String reason) {
        if (fromUuid.equals(toUuid)) throw new IllegalArgumentException("Cannot transfer to self");
        PlayerWallet sender = getWallet(fromUuid);
        sender.withdraw(amount, "transfer to " + toUuid + ": " + reason);
        getWallet(toUuid).deposit(amount, "transfer from " + fromUuid + ": " + reason);
        return sender.getBalance();
    }

    public Collection<PlayerWallet> getAllWallets() {
        return Collections.unmodifiableCollection(wallets.values());
    }

    public int getWalletCount() { return wallets.size(); }

    public void reset() { wallets.clear(); }
}
