package com.aincrad.paper.skills;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ShopBuyback {

    public record Sale(String itemId, long unitPrice, int remaining, long expiresAt) {
    }

    private static final long WINDOW_MS = 60_000L;
    private static final int MAX = 5;
    private static final Map<UUID, Map<String, Sale>> SALES = new ConcurrentHashMap<>();

    private ShopBuyback() {
    }

    public static void record(Player player, String itemId, long unitPrice, int sold) {
        long now = System.currentTimeMillis();
        long expires = now + WINDOW_MS;
        Map<String, Sale> byItem = SALES.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>());
        byItem.merge(itemId, new Sale(itemId, unitPrice, Math.min(MAX, sold), expires),
                (old, sale) -> {
                    if (old.expiresAt() <= now) return sale;
                    int remaining = Math.min(MAX, old.remaining() + sale.remaining());
                    long exp = Math.max(old.expiresAt(), sale.expiresAt());
                    return new Sale(itemId, old.unitPrice(), remaining, exp);
                });
    }

    public static Sale get(Player player, String itemId) {
        Map<String, Sale> byItem = SALES.get(player.getUniqueId());
        if (byItem == null) return null;
        Sale sale = byItem.get(itemId);
        if (sale == null) return null;
        if (sale.expiresAt() <= System.currentTimeMillis()) {
            byItem.remove(itemId);
            return null;
        }
        return sale;
    }

    public static int consume(Player player, String itemId, int amount) {
        Map<String, Sale> byItem = SALES.get(player.getUniqueId());
        if (byItem == null) return 0;
        Sale sale = byItem.get(itemId);
        if (sale == null || sale.expiresAt() <= System.currentTimeMillis()) {
            byItem.remove(itemId);
            return 0;
        }
        int take = Math.min(amount, sale.remaining());
        if (take <= 0) return 0;
        int left = sale.remaining() - take;
        if (left <= 0) byItem.remove(itemId);
        else byItem.put(itemId, new Sale(itemId, sale.unitPrice(), left, sale.expiresAt()));
        return take;
    }
}
