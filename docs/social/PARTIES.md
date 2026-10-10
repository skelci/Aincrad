# Parties

**Status:** Planning draft v1 — **Partially Implemented** (core classes complete; commands, raid instances, LFG UI pending)
**Backend:** Paper/Purpur plugin (authoritative) + Fabric client UI
**Related:** `PROGRESSION.md` (floor milestones), `QUESTS.md` (shared quest credit), `GUILDS.md` (guild wars)

## 0. Implementation Status

The following Java classes have been implemented in the plugin source tree:

| Class | Package | Description | File Path |
|---|---|---|---|
| `Party` | `com.aincrad.social.party` | Core party data model: leader, members (max 6), loot rule, join mode, XP share tracking | `src/main/java/com/aincrad/social/party/Party.java` |
| `PartyManager` | `com.aincrad.social.party` | Singleton manager: create/disband, invites, joins, kicks, leadership transfer, raid groups | `src/main/java/com/aincrad/social/party/PartyManager.java` |
| `PartyMember` | `com.aincrad.social.party` | Member data: UUID, name, level, role (DPS/TANK/HEALER), join time | `src/main/java/com/aincrad/social/party/PartyMember.java` |
| `PartyLootRule` | `com.aincrad.social.party` | Enum: FREE_FOR_ALL, ROUND_ROBIN, NEED_GREED with description strings | `src/main/java/com/aincrad/social/party/PartyLootRule.java` |
| `PartyFinder` | `com.aincrad.social.party` | LFG service: party search with filters, join requests, request management | `src/main/java/com/aincrad/social/party/PartyFinder.java` |

**Implementation notes:**
- All party classes use defensive copying (`List.copyOf`, `Map.copyOf`) and concurrent data structures.
- `PartyManager` uses double-checked locking singleton with `volatile` instance.
- All shared state in `PartyManager` uses `ConcurrentHashMap`: `parties`, `playerToParty`, `raidGroups`.
- `PartyMember` has value-based equality on `playerId` UUID; identity is immutable, `level`/`role` are mutable.
- `PartyFinder` is a stateless service operating on caller-supplied `Collection<Party>`; uses in-memory `ArrayList` for join requests (not thread-safe — caller must synchronize).

**Still to implement:** Bukkit/Paper command handlers (`/party create`, `/party invite`, etc.), party chat channel, XP share event listeners, raid instance management, Fabric client UI (party frames, LFG browser, roll UI), plugin-messaging channel sync.

## 1. Overview

Parties are temporary groups of **2–6 players** for floor clearing. **Raid parties** (4 parties = 24 players) unlock at the floor 25 milestone for high-floor boss content.

**Implementation status:** Core data model complete (§0). `Party.MAX_MEMBERS = 6`, `PartyManager.RAID_PARTY_COUNT = 4`, `PartyManager.RAID_MAX_PLAYERS = 24` are defined as constants.

## 2. Creation & Management

**Commands:**

| Command | Description |
|---|---|
| `/party create [name]` | Create party (default name: "<leader>'s Party") |
| `/party invite <player>` | Send invite (expires after 60s) |
| `/party accept` | Accept pending invite |
| `/party leave` | Leave party |
| `/party kick <player>` | Kick member (leader) |
| `/party transfer <player>` | Transfer leadership |
| `/party toggle` | Toggle join requests (open / invite-only) |
| `/party list` | List members, levels, roles |
| `/party disband` | Disband (leader; auto-disbands when empty) |

**Constraints:**

- Max 6 members; one party per player
- Anti-spam: max 5 pending invites per player

**Data (plugin-side):** leader UUID, members, join mode, created-at.

**Implementation status:** ✅ `PartyManager` implements: `createParty(uuid, lootRule, joinMode)`, `disbandParty(uuid)`, `invitePlayer(partyId, inviterUuid, targetUuid)`, `joinParty(uuid, member)`, `kickMember(uuid, partyId, actorUuid)`, `transferLeadership(uuid, partyId, actorUuid)`, `getParty(uuid)`, `getPartyForPlayer(uuid)`, `getAllParties()`. `Party` provides: `isFull()`, `isLeader(uuid)`, `hasMember(uuid)`, `transferLeadership(uuid)`, `addMember(member)`, `removeMember(uuid)`. `Party.JoinMode` enum: `OPEN`, `INVITE_ONLY`. Invite expiry (60s) and anti-spam limits still to be implemented in command handler.

## 3. Party XP Share

- **Trigger:** mob kill or quest XP awarded within **128 blocks** of the killer
- **Split:** equal share among online members in range (killer included)
- **Party bonus:** +2% XP per member in party, cap +10%
- Offline members: excluded (no catch-up)

**Implementation status:** ✅ `Party.recordXpShare(uuid, amount)` and `Party.getXpShare(uuid)` / `Party.getXpShareSnapshot()` are implemented using a `ConcurrentHashMap<UUID, Double>` tracker. XP share tracking data structure is in place. The actual event listener (mob kill detection, range check, split calculation) still to be implemented.

## 4. Party Chat

- `/pc <msg>` or `/pc toggle` — per-member channel; toggle-able mute
- 24-hour log retained for moderation

**Implementation status:** ❌ Not yet implemented. No chat channel handler or log retention system exists yet.

## 5. Raid Parties (High Floors)

- **Unlock:** floor 25 milestone
- **Composition:** 4 parties = 24 players; raid leader = leader of party 1 (the creator)
- **Formation:** `/raid invite <party-leader>` merges parties into a raid instance, or form via the party finder
- **Instance rules:**
  - Boss arena locks to the raid
  - 7-day boss lockout per player (plugin-side)
  - Cross-party chat channel (all 24)
  - Shared raid quest progress
- **Raid roles:** DPS / Tank / Healer (derived from skill loadout) — used for finder filters and matchmaking

**Implementation status:** ✅ `PartyManager` has raid group support: `createRaidGroup(List<UUID> partyIds)`, `getRaidParties(raidGroupId)`, `disbandRaidGroup(raidGroupId)`, `getAllRaidGroups()`. `RAID_PARTY_COUNT = 4` and `RAID_MAX_PLAYERS = 24` constants defined. `raidGroups` stored as `ConcurrentHashMap<UUID, Set<UUID>>`. Raid instance management (arena locking, boss lockout, cross-party chat) still to be implemented.

## 6. Loot Rules

Leader sets the rule at party creation; changeable when no roll is active.

| Rule | Behavior |
|---|---|
| Free-for-all | First-come, first-served |
| Round-robin | Items distributed in order; player may pass to next |
| Need-greed | Roll per item (see below) |

**Roll rules (need-greed):**

- **Threshold:** Rare+ rarity or boss/milestone drops go through rolls; Common/Uncommon are FFA
- **Window:** 60 seconds; options: Need (main build) / Greed (off-build, materials) / Pass
- **Resolution:** Need beats Greed; roll 1–100, highest wins; tie → higher roll
- **Bind-on-equip items:** tradable for 2 minutes after raid ends
- **Audit:** all rolls logged; leader override possible (override itself is logged)

**Implementation status:** ✅ `PartyLootRule` enum implements all three rules (`FREE_FOR_ALL`, `ROUND_ROBIN`, `NEED_GREED`) with `getDescription()`. `Party.getLootRule()`, `Party.setLootRule(rule)` manage the active rule. Roll mechanics (threshold checks, roll window, resolution, audit log) still to be implemented.

## 7. Party Finder

- **UI (Fabric):** list of open parties — floor, type (quest / raid / leveling), current size, minimum level, join mode
- **Filters:** floor range, type
- **Join:** one-click join request; auto-accept if party is in open mode
- **Backend:** LFG service shared with the raid queue (see Open Questions)

**Implementation status:** ✅ `PartyFinder` class is implemented as a stateless service. Key types: `PartyType` enum (`DUNGEON`, `RAID`, `PVP`, `EXPLORATION`), `JoinRequestStatus` enum (`PENDING`, `ACCEPTED`, `DECLINED`, `CANCELLED`), `JoinRequest` record, `PartyFilter` record. Key methods: `findOpenParties(Collection<Party>, PartyFilter)`, `submitJoinRequest(playerUuid, partyId)`, `respondToJoinRequest(requestId, accept)`, `cancelJoinRequest(requestId)`, `getPendingRequests(partyId)`, `getRequestsByPlayer(playerUuid)`. Fabric client LFG browser UI not yet implemented.

## 8. Data Model & Backend

**Plugin:** `PartyManager` (parties, invites, LFG service).

**Fabric client:** party frames, XP share notifications, LFG browser GUI, roll UI.

**Implementation status:** Core backend classes complete (§0). `PartyManager` provides in-memory storage via concurrent maps. `PartyFinder` provides search and join-request management. Persistence (if needed for party recovery across restarts) not yet implemented. Fabric client UI not yet implemented. Plugin-messaging channel not yet registered. Command handlers not yet registered.

## 9. Open Questions

- Auto-matchmaking ("quick raid") vs manual finder only?
- Party-only instanced dungeons?
- Cross-party shared kill credit for side quests (currently only XP is shared)?
