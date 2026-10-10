package com.aincrad.social.trade;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Singleton manager for the auction house.
 * <p>
 * Handles listing creation, cancellation, bidding, and search/filter
 * operations. All listings are tracked by UUID for quick lookup.
 * </p>
 */
public final class AuctionHouse {

    private static class Holder {
        static final AuctionHouse INSTANCE = new AuctionHouse();
    }

    /** All listings keyed by listing UUID. */
    private final Map<UUID, AuctionListing> listings = new ConcurrentHashMap<>();

    /** Maximum listing duration: 7 days. */
    public static final Duration MAX_DURATION = Duration.ofDays(7);

    /** Default listing duration: 24 hours. */
    public static final Duration DEFAULT_DURATION = Duration.ofHours(24);

    /** Minimum starting price in Col. */
    public static final long MIN_STARTING_PRICE = 1L;

    private AuctionHouse() {
    }

    /**
     * Returns the singleton instance of the AuctionHouse.
     *
     * @return the AuctionHouse instance
     */
    public static AuctionHouse getInstance() {
        return Holder.INSTANCE;
    }

    // ── Listing Creation ───────────────────────────────────────────────

    /**
     * Creates a new auction listing.
     *
     * @param sellerId     the seller's UUID
     * @param sellerName   the seller's display name
     * @param itemId       the item ID to sell
     * @param startingPrice the minimum bid in Col
     * @param duration     how long the listing is active
     * @return the created AuctionListing
     */
    public AuctionListing createListing(UUID sellerId, String sellerName, String itemId,
                                        long startingPrice, Duration duration) {
        if (startingPrice < MIN_STARTING_PRICE) {
            throw new IllegalArgumentException("Starting price must be at least %d Col".formatted(MIN_STARTING_PRICE));
        }
        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new IllegalArgumentException("Duration cannot exceed %s".formatted(MAX_DURATION));
        }
        UUID listingId = UUID.randomUUID();
        AuctionListing listing = new AuctionListing(listingId, itemId, sellerId, sellerName, startingPrice, duration);
        listings.put(listingId, listing);
        return listing;
    }

    // ── Listing Cancellation ───────────────────────────────────────────

    /**
     * Cancels a listing.
     *
     * @param listingId  the listing to cancel
     * @param requesterId the player requesting cancellation (must be seller)
     * @return the cancelled AuctionListing
     */
    public AuctionListing cancelListing(UUID listingId, UUID requesterId) {
        AuctionListing listing = getListingOrThrow(listingId);
        listing.cancel(requesterId);
        return listing;
    }

    // ── Bidding ────────────────────────────────────────────────────────

    /**
     * Places a bid on a listing.
     *
     * @param listingId   the listing to bid on
     * @param bidderId    the bidder's UUID
     * @param bidderName  the bidder's display name
     * @param amount      the bid amount in Col
     * @return the updated AuctionListing
     */
    public AuctionListing placeBid(UUID listingId, UUID bidderId, String bidderName, long amount) {
        AuctionListing listing = getListingOrThrow(listingId);
        listing.placeBid(bidderId, bidderName, amount);
        return listing;
    }

    // ── Lookup ─────────────────────────────────────────────────────────

    /**
     * Returns the listing for the given UUID, or {@code null} if not found.
     *
     * @param listingId the listing UUID
     * @return the AuctionListing, or {@code null}
     */
    public AuctionListing getListing(UUID listingId) {
        return listings.get(listingId);
    }

    /**
     * Returns all active (non-cancelled, non-expired) listings.
     *
     * @return list of active AuctionListings, never {@code null}
     */
    public List<AuctionListing> getActiveListings() {
        return listings.values().stream()
                .filter(AuctionListing::isActive)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all listings by a specific seller.
     *
     * @param sellerId the seller's UUID
     * @return list of the seller's active listings
     */
    public List<AuctionListing> getListingsBySeller(UUID sellerId) {
        return listings.values().stream()
                .filter(l -> l.getSellerId().equals(sellerId))
                .filter(AuctionListing::isActive)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all expired listings that have not been cleaned up.
     *
     * @return list of expired AuctionListings
     */
    public List<AuctionListing> getExpiredListings() {
        return listings.values().stream()
                .filter(AuctionListing::isExpired)
                .filter(l -> !l.isCancelled())
                .collect(Collectors.toUnmodifiableList());
    }

    // ── Search & Filter ────────────────────────────────────────────────

    /**
     * Searches active listings by item display name (case-insensitive substring).
     *
     * @param query the search query
     * @return matching active listings
     */
    public List<AuctionListing> searchByItemName(String query) {
        if (query == null || query.isBlank()) return getActiveListings();
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        return listings.values().stream()
                .filter(AuctionListing::isActive)
                .filter(l -> l.getItem().toLowerCase(Locale.ROOT)
                        .contains(lowerQuery))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Filters active listings by a custom predicate.
     *
     * @param predicate the filter condition
     * @return matching active listings
     */
    public List<AuctionListing> filter(Predicate<AuctionListing> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return listings.values().stream()
                .filter(AuctionListing::isActive)
                .filter(predicate)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Filters active listings by price range.
     *
     * @param minPrice minimum bid (inclusive), or null for no lower bound
     * @param maxPrice maximum bid (inclusive), or null for no upper bound
     * @return matching active listings
     */
    public List<AuctionListing> filterByPrice(Long minPrice, Long maxPrice) {
        return filter(l -> {
            if (minPrice != null && l.getCurrentBid() < minPrice) return false;
            if (maxPrice != null && l.getCurrentBid() > maxPrice) return false;
            return true;
        });
    }

    /**
     * Returns active listings sorted by current bid (ascending).
     *
     * @return sorted list of active listings
     */
    public List<AuctionListing> getCheapestFirst() {
        return getActiveListings().stream()
                .sorted(Comparator.comparingLong(AuctionListing::getCurrentBid))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns active listings sorted by expiration (soonest first).
     *
     * @return sorted list of active listings
     */
    public List<AuctionListing> getExpiringSoon() {
        return getActiveListings().stream()
                .sorted(Comparator.comparing(AuctionListing::getExpiresAt))
                .collect(Collectors.toUnmodifiableList());
    }

    // ── Cleanup ────────────────────────────────────────────────────────

    /**
     * Removes all expired and cancelled listings from the manager.
     *
     * @return the number of listings removed
     */
    public int cleanupExpired() {
        int before = listings.size();
        listings.values().removeIf(l -> l.isExpired() || l.isCancelled());
        return before - listings.size();
    }

    /**
     * Returns the total number of listings (active and inactive).
     *
     * @return total listing count
     */
    public int getTotalListingCount() {
        return listings.size();
    }

    // ── Internal Helpers ───────────────────────────────────────────────

    private AuctionListing getListingOrThrow(UUID listingId) {
        AuctionListing listing = listings.get(listingId);
        if (listing == null) {
            throw new IllegalArgumentException("Auction listing %s not found".formatted(listingId));
        }
        return listing;
    }
}
