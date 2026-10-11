package com.aincrad.paper.economy;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable auction listing with mutable bid state.
 */
public final class AuctionListing {
    private final UUID listingId;
    private final UUID sellerId;
    private final String sellerName;
    private final String itemId;
    private final long startingPrice;
    private long currentBid;
    private UUID currentBidderId;
    private String currentBidderName;
    private final Instant createdAt;
    private final Instant expiresAt;
    private boolean cancelled;

    public AuctionListing(UUID sellerId, String sellerName, String itemId,
                          long startingPrice, long durationHours) {
        if (startingPrice < 1) throw new IllegalArgumentException("Starting price must be >= 1");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("ItemId cannot be blank");
        if (durationHours < 1) throw new IllegalArgumentException("Duration must be >= 1 hour");
        this.listingId = UUID.randomUUID();
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.itemId = itemId;
        this.startingPrice = startingPrice;
        this.currentBid = startingPrice;
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plusSeconds(durationHours * 3600);
        this.cancelled = false;
    }

    public UUID getListingId() { return listingId; }
    public UUID getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getItemId() { return itemId; }
    public long getStartingPrice() { return startingPrice; }
    public long getCurrentBid() { return currentBid; }
    public UUID getCurrentBidderId() { return currentBidderId; }
    public String getCurrentBidderName() { return currentBidderName; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isCancelled() { return cancelled; }

    public boolean isActive() { return !cancelled && !isExpired(); }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }

    public boolean placeBid(UUID bidderId, String bidderName, long amount) {
        if (!isActive()) return false;
        if (amount <= currentBid) return false;
        if (bidderId.equals(sellerId)) return false;
        this.currentBid = amount;
        this.currentBidderId = bidderId;
        this.currentBidderName = bidderName;
        return true;
    }

    public boolean cancel(UUID requesterId) {
        if (!requesterId.equals(sellerId)) return false;
        if (currentBidderId != null) return false;
        this.cancelled = true;
        return true;
    }

    @Override
    public String toString() {
        return "Auction[" + itemId + " bid=" + currentBid + " by " + currentBidderName + "]";
    }
}
