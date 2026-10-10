# Guilds

**Status:** Planning draft v1 — **Partially Implemented** (core classes complete; commands, GvG, housing pending)
**Backend:** Paper/Purpur plugin (authoritative) + Fabric client UI
**Related:** `PROGRESSION.md` (milestones), `PARTIES.md` (party finder), economy/Col

## 0. Implementation Status

The following Java classes have been implemented in the plugin source tree:

| Class | Package | Description | File Path |
|---|---|---|---|
| `Guild` | `com.aincrad.social.guild` | Core guild data model: name, tag, owner, members, ranks, bank, XP, skills, notice | `src/main/java/com/aincrad/social/guild/Guild.java` |
| `GuildManager` | `com.aincrad.social.guild` | Singleton manager: create/disband, invites, kicks, rank assignment, ownership transfer, skill training, lookup | `src/main/java/com/aincrad/social/guild/GuildManager.java` |
| `GuildRank` | `com.aincrad.social.guild` | Enum: MASTER, OFFICER, MEMBER with nested `Permission` enum (8 permissions) and `EnumSet` storage; weight-based hierarchy | `src/main/java/com/aincrad/social/guild/GuildRank.java` |
| `GuildSkill` | `com.aincrad.social.guild` | Enum: BATTLE_TRAINING, SWIFT_RECOVERY, COL_RESERVE, IRON_WILL; level-gated unlocks, effect-per-rank scaling, max rank 3 | `src/main/java/com/aincrad/social/guild/GuildSkill.java` |
| `GuildBank` | `com.aincrad.social.guild` | Thread-safe bank with level-scaled slots and 100-entry rolling ledger; `synchronized` methods, immutable `Stack`/`LedgerEntry` records | `src/main/java/com/aincrad/social/guild/GuildBank.java` |
| `GuildXP` | `com.aincrad.social.guild` | XP tracker: `250 * L^1.5` curve, cumulative XP, level-up with overflow carry, max level 20 | `src/main/java/com/aincrad/social/guild/GuildXP.java` |
| `GuildLevel` | `com.aincrad.social.guild` | Static utility: `memberCap(level)`, `bankSlots(level)`, `officerCap(level)`, `xpForLevel(level)`, `clamp(level)` | `src/main/java/com/aincrad/social/guild/GuildLevel.java` |

**Implementation notes:**
- All guild classes are `final` with defensive copying and concurrent data structures (`ConcurrentHashMap`, `EnumMap`).
- `GuildManager` uses eager singleton pattern; `Guild` mutations are guarded by rank/permission checks via `GuildManager` helper methods.
- `GuildBank` uses coarse-grained `synchronized` locking with FIFO eviction for the rolling ledger (capacity 100).
- `GuildXP` implements the exact `250 * L^1.5` curve from §5; level cap 20 enforced by `GuildLevel.clamp()`.
- `GuildLevel.officerCap()` hard-caps at 7 as specified in §3.

**Still to implement:** Bukkit/Paper command handlers (`/guild create`, `/guild invite`, etc.), permission listeners, GvG arena system (§9), guild hall instances (§8), SQLite/MySQL persistence layer, Fabric client GUI, plugin-messaging channel sync.

## 1. Overview

Guilds are long-term player organizations of 10–105 members that persist across all 100 floors. Inspired by SAO guilds (Knights of the Blood, Dragon Knights, Moonlight Black Cats).

**Design pillars:**

- **Social identity** — guild name, tag, and banner shown on nametags and in chat
- **Progression** — guild XP and levels unlock shared bonuses
- **Shared resources** — guild bank with an audit ledger
- **Competitive play** — guild vs guild (GvG) arena wars

**Server-wide unlock schedule:**

| Content | Unlock |
|---|---|
| Basic guilds (create/join/bank) | Character level 15 (≈ floor 10) |
| Guild halls + guild skills | Floor 10 milestone |
| GvG arena wars | Floor 25 milestone |

**Implementation status:** Core data model complete (§0). Unlock schedule gating still to be wired into command handlers.

## 2. Guild Creation

- **Cost:** 100,000 Col one-time, paid by the founder
- **Requirements:**
  - Founder character level ≥ 15
  - Founder not already in a guild
  - 1–5 additional founding members present (2–6 founders total)
  - Unique guild name (2–16 chars) and tag (2–4 chars), case-insensitive

**Commands:**

| Command | Description |
|---|---|
| `/guild create <name> <tag>` | Create guild (pays fee) |
| `/guild invite <player>` | Invite a player (officer+) |
| `/guild leave` | Leave current guild |
| `/guild disband` | Disband guild (master only; requires empty roster or 24h confirm) |

Founders start as officers; the creator becomes guild master.

**Implementation status:** `GuildManager.createGuild(name, tag, ownerUuid)` handles atomic creation via `putIfAbsent` on `guildsByName`. The founder is set as owner with `GuildRank.MASTER`; additional founders are added as `OFFICER`. Col payment, level checks, and presence validation still to be implemented in the command handler. `GuildManager.disbandGuild(guild, actorUuid)` performs permission-gated disband with idempotent `markDisbanded()`.

## 3. Ranks & Permissions

| Rank | Count | Permissions |
|---|---|---|
| Master | 1 | All permissions; transfer leadership; disband |
| Officer | 3 + floor(guild level / 5), cap 7 | Invite, kick, bank deposit + withdraw, edit notice, manage hall, declare GvG, promote/demote |
| Member | Unlimited (within member cap) | Bank deposit, hall access, GvG participation |

**Additional rules:**

- Guild master cannot be kicked; must transfer or disband
- Inactivity kick: configurable; default 30 days offline → 7-day warning in guild log, then auto-kick (member rank only)

**Implementation status:** ✅ `GuildRank` enum implements all three ranks with nested `Permission` enum (8 permissions: `INVITE`, `KICK`, `BANK_DEPOSIT`, `BANK_WITHDRAW`, `EDIT_NOTICE`, `MANAGE_HALL`, `DECLARE_WAR`, `PROMOTE_DEMOTE`). Permissions stored as `GuildRank.getPermissions()` → `EnumSet<Permission>`. Weight-based hierarchy: `GuildRank.outranks(other)` and `GuildRank.canManage(target)` enable rank comparison. `Guild.assignRank(uuid, rank)` and `Guild.hasRankOrAbove(uuid, rank)` are implemented. `GuildManager.requireRank()` / `requirePermission()` helpers enforce checks. Officer cap enforced by `GuildLevel.officerCap(level)` (hard-capped at 7). Inactivity kick still to be implemented.

## 4. Guild Bank

- **Slots:** 9 + 9 × (level − 1) → L1: 9, L10: 90, L20: 189
- **Permissions:** members deposit; officers deposit + withdraw
- **Ledger:** last 100 transactions stored (timestamp, actor UUID, item, quantity); officers can inspect for dispute resolution
- **Safety:** no death loss, no overflow loss (deposit rejected when full)

**Implementation status:** ✅ `GuildBank` is fully implemented. Thread-safe with `synchronized` methods. Key methods: `deposit(Stack, actorUuid)`, `withdraw(slotIndex, actorUuid)`, `hasSpace()`, `usedSlots()`, `getCapacity()`, `getSlots()`, `getLedger()`. Rolling ledger uses `Deque<LedgerEntry>` with `LEDGER_CAPACITY = 100` (FIFO eviction). Nested records: `Stack` (item data), `LedgerEntry` (timestamp, actor, item, quantity, `TransactionType`). `Guild.resizeBankForLevel(level)` handles level-up slot scaling. Permission enforcement still to be wired in command handlers.

## 5. Guild XP & Levels

**XP sources** (all server-validated):

| Activity | XP |
|---|---|
| Floor boss kill (guild member participated) | +50 |
| Main quest completion (guild member) | +100 |
| GvG win | +200 |
| GvG participation | +50 |

**Level curve:** XP(L → L+1) = 250 × L^1.5

| Level | XP to next |
|---|---|
| 1 | 250 |
| 5 | ~2,800 |
| 10 | ~7,900 |
| 15 | ~14,500 |
| 20 | ~22,400 |

**Level cap:** 20.

**Level-up rewards:** +5 member cap, +9 bank slots, new guild passive unlocks (see §7).

**Implementation status:** ✅ `GuildXP` class fully implements the curve. Key methods: `addXp(amount)` (handles overflow carry on level-up), `wouldLevelUp(amount)`, `getXpToNext()`, `getProgressXp()`, `getTotalXp()`, `getLevel()`, static `cumulativeXpFor(level)`. `GuildLevel.xpForLevel(level)` computes `250 * L^1.5`. `Guild.addXp(amount)` delegates to `GuildXP` and triggers `resizeBankForLevel()`. Level cap 20 enforced by `GuildLevel.clamp(level)`. XP award triggers (boss kill, quest completion, GvG) still to be wired into event listeners.

## 6. Member Cap

`cap = 10 + 5 × (level − 1)`

| Guild level | Max members |
|---|---|
| 1 | 10 |
| 5 | 30 |
| 10 | 55 |
| 20 | 105 |

Officers: 3 + floor(level / 5), cap 7.

**Implementation status:** ✅ `GuildLevel.memberCap(level)` and `Guild.getMemberCap()` are implemented. `Guild.addMember(uuid)` checks cap before adding. `Guild.getOfficerCount()` tracks officer count for cap enforcement.

## 7. Guild Skills (Passives)

Fixed unlocks — no slot management. Rank = 1 + floor((guild level − unlock level) / 3), max rank 3. Applied to all members as a passive aura; displayed in the guild GUI.

| Skill | Unlock | Effect per rank |
|---|---|---|
| Battle Training | L3 | +2% melee damage |
| Swift Recovery | L5 | +4% healing / potion effectiveness |
| Col Reserve | L8 | +1% Col gained |
| Iron Will | L12 | −2% damage taken |

**Implementation status:** ✅ `GuildSkill` enum implements all 4 skills with `unlockLevel`, `effectPerRank`, `displayName`, `description`. Key methods: `isUnlockedAt(level)`, `effectAt(rank)`. `Guild.trainSkill(skill)` and `Guild.getSkillRank(skill)` manage per-guild skill ranks stored in `EnumMap<GuildSkill, Integer>`. `Guild.getSkillRanks()` returns unmodifiable map. Passive aura application (actual damage/healing/Col modifiers) still to be implemented in combat/economy listeners.

## 8. Guild Housing / Territory

- **Guild hall:** instanced 16×16×8 room per guild; portal in the main safe city + `/guild hall`
- **Contents:** guild bank chest, banner stand (name/tag/color), trophy shelf (boss trophies), member list board
- **Upgrades:** cosmetic furniture themes (basic / stone / gold) purchased with Col
- **Territory claims:** out of scope for v1 (see Open Questions)

**Implementation status:** ❌ Not yet implemented. No guild hall instance management, portal, or furniture system exists yet.

## 9. Guild vs Guild (GvG)

- **Unlock:** floor 25 milestone; both guilds must be level ≥ 3
- **Declaration:** `/guild war declare <guild> [bet Col]` — 24h notice period; bet range 10,000–500,000 Col; active wars listed publicly
- **Format:** 30-minute arena match; 3 arena maps (colosseum / canyon / ruins); server reserves an instance
- **Mode:** elimination (first to 50 kills) OR point capture (hold central point to 100) — chosen at declaration
- **Rules:**
  - War-flagged members only; PvP damage enabled only inside the arena
  - Both guilds field equal caps (minimum of the two guild caps)
  - No spectator interference
- **Rewards:** winner +200 guild XP + bet; loser +50 participation XP
- **Cooldown:** 1 war per guild per 48h; win/loss record and leaderboard tracked

**Implementation status:** ❌ Not yet implemented. No war declaration, arena instance, or GvG event system exists yet. `GuildRank` has `DECLARE_WAR` permission defined but no logic uses it yet.

## 10. Data Model & Backend

**Plugin (Paper/Purpur):** `GuildManager`; storage SQLite (single server) with MySQL option.

Tables: `guilds`, `members`, `bank_ledger`, `wars`.

**Fabric client:** guild GUI (roster, bank, notice, skills, war status); guild tag on nametabs/chat; plugin-messaging sync on join, level-up, bank contents, war status.

**Commands:** see §2–§9 plus `/guild info [guild]`, `/guild list`, `/guild top`, `/guild chat <msg>`.

**Implementation status:** Core backend classes complete (§0). `GuildManager` provides in-memory storage via `ConcurrentHashMap<String, Guild> guildsByName`. SQLite/MySQL persistence layer not yet implemented. Fabric client GUI not yet implemented. Plugin-messaging channel not yet registered. Command handlers not yet registered.

## 11. Open Questions

- Open-world PvP zones instead of (or in addition to) arena-only GvG?
- Guild taxes / member dues on income?
- Cross-server guild sync (network play)?
