package com.aincrad.social.trade;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single auction house listing.
 * <p>
 * Contains the item being sold, seller information, pricing, current bid,
 * and expiration. A listing can be cancelled by the seller before it expires
 * or receives a bid.
 * </p>
 */
public final class AuctionListing {

    private final UUID listingId;
    private final String itemId;
    private final UUID sellerId;
    private final String sellerName;
    private final long startingPrice;
    private long currentBid;
    private UUID currentBidderId;
    private String currentBidderName;
    private final Instant createdAt;
    private final Instant expiresAt;
    private boolean cancelled;

    /**
     * Creates a new auction listing.
     *
     * @param listingId    the unique listing ID
     * @param itemId       the item ID being sold
     * @param sellerId     the seller's UUID
     * @param sellerName   the seller's display name
     * @param startingPrice the minimum bid amount in Col
     * @param duration     how long the listing is active
     * @throws IllegalArgumentException if startingPrice <= 0 or item is empty
     */
    public AuctionListing(UUID listingId, String itemId, UUID sellerId, String sellerName,
                          long startingPrice, Duration duration) {
        this.listingId = Objects.requireNonNull(listingId, "listingId");
        this.itemId = Objects.requireNonNull(itemId, "itemId");
        this.sellerId = Objects.requireNonNull(sellerId, "sellerId");
        this.sellerName = Objects.requireNonNull(sellerName, "sellerName");
        if (startingPrice <= 0) {
            throw new IllegalArgumentException("Starting price must be positive");
        }
        if (itemId.isEmpty()) {
            throw new IllegalArgumentException("Cannot list an empty item");
        }
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.startingPrice = startingPrice;
        this.currentBid = startingPrice;
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plus(duration);
        this.cancelled = false;
    }

    // ── Getters ────────────────────────────────────────────────────────

    public UUID getListingId() {
        return listingId;
    }

    /**
     * Returns the listed item ID.
     *
     * @return the item ID
     */
    public String getItem() {
        return itemId;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public long getStartingPrice() {
        return startingPrice;
    }

    public long getCurrentBid() {
        return currentBid;
    }

    public UUID getCurrentBidderId() {
        return currentBidderId;
    }

    public String getCurrentBidderName() {
        return currentBidderName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    // ── State Queries ──────────────────────────────────────────────────

    /**
     * Checks whether the listing has expired.
     *
     * @return {@code true} if past the expiration time
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks whether the listing is active (not cancelled and not expired).
     *
     * @return {@code true} if the listing can receive bids
     */
    public boolean isActive() {
        return !cancelled && !isExpired();
    }

    /**
     * Returns the remaining time until expiration.
     *
     * @return the remaining duration, or zero if already expired
     */
    public Duration getTimeRemaining() {
        if (isExpired()) return Duration.ZERO;
        return Duration.between(Instant.now(), expiresAt);
    }

    // ── Bid Operations ─────────────────────────────────────────────────

    /**
     * Places a bid on this listing.
     *
     * @param bidderId   the bidder's UUID
     * @param bidderName the bidder's display name
     * @param amount     the bid amount in Col
     * @throws IllegalStateException    if the listing is not active
     * @throws IllegalArgumentException if the bid is not higher than the current bid
     */
    public void placeBid(UUID bidderId, String bidderName, long amount) {
        if (!isActive()) {
            throw new IllegalStateException("Listing is not active");
        }
        if (amount <= currentBid) {
            throw new IllegalArgumentException(
                    "Bid (%d) must be higher than current bid (%d)".formatted(amount, currentBid));
        }
        if (bidderId.equals(sellerId)) {
            throw new IllegalArgumentException("Seller cannot bid on their own listing");
        }
        this.currentBid = amount;
        this.currentBidderId = bidderId;
        this.currentBidderName = bidderName;
    }

    // ── Cancellation ──────────────────────────────────────────────────

    /**
     * Cancels the listing. Only the seller can cancel.
     *
     * @param requesterId the player requesting cancellation
     * @throws IllegalArgumentException if the requester is not the seller
     * @throws IllegalStateException    if the listing has a bid or is inactive
     */
    public void cancel(UUID requesterId) {
        if (!sellerId.equals(requesterId)) {
            throw new IllegalArgumentException("Only the seller can cancel the listing");
        }
        if (currentBidderId != null) {
            throw new IllegalStateException("Cannot cancel a listing that has bids");
        }
        this.cancelled = true;
    }

    @Override
    public String toString() {
        return "AuctionListing{id=%s, item=%s, seller=%s, currentBid=%d, active=%s}"
                .formatted(listingId, itemId, sellerName, currentBid, isActive());
    }
}
