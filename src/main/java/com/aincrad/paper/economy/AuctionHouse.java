package com.aincrad.paper.economy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton auction house manager.
 * Handles listing creation, bidding, cancellation, search/filter, and cleanup.
 */
public final class AuctionHouse {
    public static final long MAX_DURATION_HOURS = 168; // 7 days
    public static final long DEFAULT_DURATION_HOURS = 24;
    public static final long MIN_STARTING_PRICE = 1;

    private static final AuctionHouse INSTANCE = new AuctionHouse();

    private final Map<UUID, AuctionListing> listings = new ConcurrentHashMap<>();

    private AuctionHouse() {}

    public static AuctionHouse getInstance() { return INSTANCE; }

    public AuctionListing createListing(UUID sellerId, String sellerName, String itemId,
                                         long startingPrice, long durationHours) {
        if (startingPrice < MIN_STARTING_PRICE) throw new IllegalArgumentException("Price too low");
        if (durationHours < 1 || durationHours > MAX_DURATION_HOURS) throw new IllegalArgumentException("Invalid duration");
        AuctionListing listing = new AuctionListing(sellerId, sellerName, itemId, startingPrice, durationHours);
        listings.put(listing.getListingId(), listing);
        return listing;
    }

    public boolean cancelListing(UUID listingId, UUID requesterId) {
        AuctionListing listing = listings.get(listingId);
        if (listing == null) return false;
        return listing.cancel(requesterId);
    }

    public boolean placeBid(UUID listingId, UUID bidderId, String bidderName, long amount) {
        AuctionListing listing = listings.get(listingId);
        if (listing == null) return false;
        return listing.placeBid(bidderId, bidderName, amount);
    }

    public List<AuctionListing> getActiveListings() {
        return listings.values().stream()
                .filter(AuctionListing::isActive)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AuctionListing> getListingsBySeller(UUID sellerId) {
        return listings.values().stream()
                .filter(l -> l.getSellerId().equals(sellerId))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AuctionListing> searchByItemName(String query) {
        String lower = query.toLowerCase();
        return listings.values().stream()
                .filter(l -> l.isActive() && l.getItemId().toLowerCase().contains(lower))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AuctionListing> filterByPrice(long min, long max) {
        return listings.values().stream()
                .filter(l -> l.isActive() && l.getCurrentBid() >= min && l.getCurrentBid() <= max)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AuctionListing> getCheapestFirst() {
        return listings.values().stream()
                .filter(AuctionListing::isActive)
                .sorted(Comparator.comparingLong(AuctionListing::getCurrentBid))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<AuctionListing> getExpiringSoon() {
        return listings.values().stream()
                .filter(AuctionListing::isActive)
                .sorted(Comparator.comparing(AuctionListing::getExpiresAt))
                .collect(Collectors.toUnmodifiableList());
    }

    public void cleanupExpired() {
        listings.entrySet().removeIf(e -> !e.getValue().isActive());
    }

    public AuctionListing getListing(UUID listingId) { return listings.get(listingId); }

    public void reset() { listings.clear(); }
}
