package com.aincrad.social.friend;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager for friend lists, online status tracking, and blocking.
 * <p>
 * All operations are thread-safe. Friend lists are stored per-player using
 * concurrent collections to allow access from both the main game thread and
 * network threads.
 * </p>
 */
public final class FriendManager {

    private static class Holder {
        static final FriendManager INSTANCE = new FriendManager();
    }

    /** Player UUID → their friend entries. */
    private final Map<UUID, Set<FriendEntry>> friends = new ConcurrentHashMap<>();

    /** Player UUID → set of blocked player UUIDs. */
    private final Map<UUID, Set<UUID>> blocked = new ConcurrentHashMap<>();

    /** Requester UUID → target UUID (pending friend requests). */
    private final Map<UUID, UUID> pendingRequests = new ConcurrentHashMap<>();

    private FriendManager() {
    }

    /**
     * Returns the singleton instance of the FriendManager.
     *
     * @return the FriendManager instance
     */
    public static FriendManager getInstance() {
        return Holder.INSTANCE;
    }

    // ── Friendship Management ──────────────────────────────────────────

    /**
     * Adds a friend to the given player's friend list.
     *
     * @param playerId   the player who owns the friend list
     * @param friendId   the friend's UUID
     * @param friendName the friend's display name
     * @return {@code true} if the friend was added, {@code false} if already present
     * @throws IllegalArgumentException if the player tries to friend themselves
     */
    public boolean addFriend(UUID playerId, UUID friendId, String friendName) {
        validateNotSelf(playerId, friendId);
        FriendEntry entry = new FriendEntry(friendId, friendName);
        return friends.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet())
                .add(entry);
    }

    /**
     * Removes a friend from the given player's friend list.
     *
     * @param playerId the player who owns the friend list
     * @param friendId the friend's UUID
     * @return {@code true} if the friend was removed, {@code false} if not found
     */
    public boolean removeFriend(UUID playerId, UUID friendId) {
        Set<FriendEntry> set = friends.get(playerId);
        if (set == null) return false;
        return set.removeIf(e -> e.getPlayerId().equals(friendId));
    }

    /**
     * Returns an unmodifiable view of the player's friend list.
     *
     * @param playerId the player whose friends to retrieve
     * @return unmodifiable set of FriendEntry, never {@code null}
     */
    public Set<FriendEntry> getFriends(UUID playerId) {
        Set<FriendEntry> set = friends.get(playerId);
        if (set == null) return Collections.emptySet();
        return Collections.unmodifiableSet(set);
    }

    /**
     * Checks whether two players are friends.
     *
     * @param playerId the first player
     * @param friendId the second player
     * @return {@code true} if they are friends
     */
    public boolean isFriend(UUID playerId, UUID friendId) {
        Set<FriendEntry> set = friends.get(playerId);
        if (set == null) return false;
        return set.stream().anyMatch(e -> e.getPlayerId().equals(friendId));
    }

    // ── Blocking ───────────────────────────────────────────────────────

    /**
     * Blocks a player. The blocked player cannot send friend requests.
     *
     * @param playerId  the player who is blocking
     * @param blockedId the player being blocked
     * @return {@code true} if newly blocked, {@code false} if already blocked
     */
    public boolean blockPlayer(UUID playerId, UUID blockedId) {
        validateNotSelf(playerId, blockedId);
        return blocked.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet())
                .add(blockedId);
    }

    /**
     * Unblocks a previously blocked player.
     *
     * @param playerId  the player who is unblocking
     * @param blockedId the player being unblocked
     * @return {@code true} if unblocked, {@code false} if not blocked
     */
    public boolean unblockPlayer(UUID playerId, UUID blockedId) {
        Set<UUID> set = blocked.get(playerId);
        if (set == null) return false;
        return set.remove(blockedId);
    }

    /**
     * Checks whether {@code playerId} has blocked {@code targetId}.
     *
     * @param playerId the potential blocker
     * @param targetId the potentially blocked player
     * @return {@code true} if blocked
     */
    public boolean isBlocked(UUID playerId, UUID targetId) {
        Set<UUID> set = blocked.get(playerId);
        if (set == null) return false;
        return set.contains(targetId);
    }

    // ── Online Status ──────────────────────────────────────────────────

    /**
     * Updates the online status for all FriendEntry records matching the given UUID.
     *
     * @param playerId the player whose status is changing
     * @param online  {@code true} if the player came online, {@code false} if offline
     */
    public void setOnline(UUID playerId, boolean online) {
        for (Set<FriendEntry> set : friends.values()) {
            for (FriendEntry entry : set) {
                if (entry.getPlayerId().equals(playerId)) {
                    entry.setOnline(online);
                }
            }
        }
    }

    /**
     * Returns whether the given player is currently online.
     *
     * @param playerId the player to check
     * @return {@code true} if online
     */
    public boolean isOnline(UUID playerId) {
        for (Set<FriendEntry> set : friends.values()) {
            for (FriendEntry entry : set) {
                if (entry.getPlayerId().equals(playerId)) {
                    return entry.isOnline();
                }
            }
        }
        return false;
    }

    // ── Friend Requests ────────────────────────────────────────────────

    /**
     * Sends a friend request from {@code requesterId} to {@code targetId}.
     *
     * @param requesterId the player sending the request
     * @param targetId   the player receiving the request
     * @return {@code true} if the request was sent, {@code false} if already pending or blocked
     */
    public boolean sendFriendRequest(UUID requesterId, UUID targetId) {
        validateNotSelf(requesterId, targetId);
        if (isBlocked(targetId, requesterId)) return false;
        if (pendingRequests.containsKey(requesterId)) return false;
        pendingRequests.put(requesterId, targetId);
        return true;
    }

    /**
     * Accepts a pending friend request, creating bidirectional friendship.
     *
     * @param requesterId the player who sent the request
     * @param targetId   the player who accepts
     * @return {@code true} if accepted, {@code false} if no pending request
     */
    public boolean acceptFriendRequest(UUID requesterId, UUID targetId) {
        UUID pendingTarget = pendingRequests.get(requesterId);
        if (pendingTarget == null || !pendingTarget.equals(targetId)) return false;
        pendingRequests.remove(requesterId);
        // Bidirectional: both players get a friend entry
        addFriend(requesterId, targetId, "Player" + targetId.toString().substring(0, 4));
        addFriend(targetId, requesterId, "Player" + requesterId.toString().substring(0, 4));
        return true;
    }

    /**
     * Declines a pending friend request.
     *
     * @param requesterId the player who sent the request
     * @param targetId   the player who declines
     * @return {@code true} if declined, {@code false} if no pending request
     */
    public boolean declineFriendRequest(UUID requesterId, UUID targetId) {
        UUID pendingTarget = pendingRequests.get(requesterId);
        if (pendingTarget == null || !pendingTarget.equals(targetId)) return false;
        pendingRequests.remove(requesterId);
        return true;
    }

    /**
     * Returns all pending friend requests targeting the given player.
     *
     * @param targetId the player whose pending requests to retrieve
     * @return map of requester UUID → target UUID (always the given targetId)
     */
    public Map<UUID, UUID> getPendingRequests(UUID targetId) {
        Map<UUID, UUID> result = new HashMap<>();
        pendingRequests.forEach((requester, target) -> {
            if (target.equals(targetId)) {
                result.put(requester, target);
            }
        });
        return result;
    }

    // ── Utility ────────────────────────────────────────────────────────

    private void validateNotSelf(UUID playerId, UUID otherId) {
        if (playerId.equals(otherId)) {
            throw new IllegalArgumentException("Cannot perform friend operation on yourself");
        }
    }
}
