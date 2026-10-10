# Quests

**Status:** Planning draft v1 — **Implementation in progress**
**Backend:** Paper/Purpur plugin (authoritative state) + Fabric client UI
**Related:** `PROGRESSION.md` (floor gating, milestones), `PARTIES.md` (shared credit), `GUILDS.md` (guild quests — proposed)

---

## 0. Implementation Status

The following Java classes have been created under `src/main/java/com/aincrad/`:

| Class | Type | Location | Status |
|---|---|---|---|
| `QuestType` | enum | `quest/QuestType.java` | ✅ Complete |
| `QuestReward` | final class | `quest/QuestReward.java` | ✅ Complete |
| `QuestObjective` | final class | `quest/QuestObjective.java` | ✅ Complete |
| `Quest` | final class | `quest/Quest.java` | ✅ Complete |
| `QuestTracker` | final class | `quest/QuestTracker.java` | ✅ Complete |
| `QuestManager` | final class (singleton) | `quest/QuestManager.java` | ✅ Complete |

**Package:** `com.aincrad.quest`

**What's implemented:**
- Full quest data model (definitions, objectives, rewards, types)
- Per-player quest tracking with thread-safe concurrent collections
- Quest lifecycle management (accept → track → turn-in → abandon)
- Hot-reload support with progress preservation
- XP scaling logic (anti-farming / catch-up multipliers)
- Cooldown and completion-cap enforcement
- Daily/weekly reset mechanics

**What's still planning:**
- JSON definition loading (currently programmatic registration)
- Fabric client UI (HUD tracker, journal GUI, world markers)
- Party shared-credit integration
- Anti-cheat validation hooks (server-side event listeners)
- Persistence layer (save/load quest state)

---

## 1. Overview

Three quest classes:

| Class | Purpose | Repeatable |
|---|---|---|
| Main | 100-floor clear arc (story) | No |
| Side | Per-floor objectives (kill / collect / defeat) | No |
| Daily | Repeatable tasks for steady rewards | Yes (3/day) |

**Implementation note:** `QuestType` enum defines `MAIN`, `SIDE`, `DAILY`, and `WEEKLY` with a `repeatable` flag and `description`. The `hasCooldown()` and `hasCompletionCap()` methods provide gating logic used by `QuestManager`.

## 2. Data Model

**Quest definitions** (versioned JSON, hot-reloadable):

```
id, name, type (main|side|daily), floor, giver (NPC), prerequisites,
objectives (kill X mobs / collect Y items / defeat mini-boss / talk to NPC),
rewards (XP / Col / items), turn-in (auto|NPC), repeatable
```

**Implementation note:** The `Quest` class is an immutable definition with fields for `id`, `name`, `type`, `floor` (1–100), `giverNpc`, `prerequisites`, `objectives`, `rewards`, `turnInMethod` (AUTO/NPC/UI), and `repeatable`. It includes `getXpMultiplier(playerLevel)` implementing the ×0.25 / ×1.0 / ×1.5 scaling from §7. The `withObjectives()` and `withRewards()` withers support hot-reload.

`QuestObjective` is an immutable progress tracker with a nested `ObjectiveType` enum (`KILL`, `COLLECT`, `DEFEAT`, `TALK`), `target`, `current`, and `required` fields. The `progress(int)` method returns a new instance (immutable update).

`QuestReward` is an immutable value object with `experience`, `col`, `items` (list of `ItemDrop` records), and `achievementPoints`. Factory methods `ofExperience()`, `ofExperienceAndCol()`, and `ofItems()` are provided, plus `add()` for combining rewards.

**Player state** (plugin-side): active quests, per-objective progress, completed set, cooldowns.

**Implementation note:** `QuestTracker` holds per-player state using `LinkedHashMap` for active quests (ordered), `ConcurrentHashSet` for completed quests, and `ConcurrentHashMap` for cooldowns and completion counts. Daily/weekly reset timestamps are `volatile`. All operations are thread-safe.

**Server-authoritative progress:** all kill/collect events validated server-side (mob floor-tag, inventory deltas); rollbacks on disconnect.

**Implementation note:** Validation hooks are not yet wired to server events — this is a remaining integration task.

## 3. Main Questline (100-Floor Arc)

- 100 quests — "Clear Floor N" — one per floor
- Story text (3–5 lines) delivered via NPC dialog in the safe city
- Unlocks boss arena access for that floor
- **Prerequisite:** player has cleared floor N−1 (boss kill recorded server-side)
- **Failure:** none; retry allowed

**Implementation note:** Prerequisite validation is handled in `QuestManager.acceptQuest()`, which checks the quest's prerequisite list against the player's completed quests.

**Rewards:**

| Floor | XP | Col | Items |
|---|---|---|---|
| Any | floor × 100 | floor × 500 | — |
| Every 5th floor | — | — | Tier-appropriate gear |
| Milestones (10/25/50/75/100) | — | — | Milestone item (+10% above tier norm, tradeable) |

Milestone rewards defined in `PROGRESSION.md` §5.

## 4. Side Quests (Per Floor)

Each floor has **3–5 side quests** from NPCs and quest boards:

| Type | Objective | Rewards |
|---|---|---|
| Kill | 15–30 floor mobs | XP floor × 50, Col floor × 250 |
| Collect | 8–15 floor materials | XP floor × 50, Col floor × 250 |
| Defeat | Mini-boss at fixed lair (daily respawn) | XP floor × 75, Col floor × 375, 30% item chance |

- Auto-accept from quest boards; turn-in at NPC (talk)
- 20% chance of a random tier-appropriate item on completion
- One-time per character

**Implementation note:** `QuestObjective.ObjectiveType` covers KILL, COLLECT, DEFEAT, and TALK. The `TALK` type supports NPC turn-in quests. One-time completion is enforced by `QuestTracker.completedQuests`.

## 5. Daily Quests

- **Slots:** 3 per player per day; reset at 00:00 server time
- **Variants:** kill / collect / defeat, weighted to player's current floor ±5
- **Streak:** +5% XP per consecutive day, cap +50%; resets on a missed day
- **Weekly raid quest:** 1 per week (post-floor-100 content)

**Implementation note:** `QuestType.DAILY` and `QuestType.WEEKLY` are defined with `repeatable=true`. `QuestTracker.performDailyReset()` and `performWeeklyReset()` handle reset logic. `QuestManager` enforces `dailyCap` and `weeklyCap` limits. Streak tracking is not yet implemented.

## 6. Quest Tracking UI

- **HUD tracker** (keybind toggle): active quest, objective list, progress counters, distance hint
- **Journal GUI:** active quests, history (last 20), filters (main/side/daily), abandon (with confirmation; main quest abandon blocked)
- **World markers:** client-side markers for active objectives (data from server)
- **Party integration:** kill/collect credit shared among party members within 128 blocks

**Status:** 🔲 Not yet implemented — Fabric client UI is pending.

## 7. Rewards & Scaling

- **XP scaling:** player level > floor +10 → ×0.25 (anti-farming); < floor −5 → ×1.5 (catch-up)
- **Col / items:** fixed per quest; items are tradeable (no bind-on-quest)
- **Achievement points:** +10 per side/daily quest, +50 per main quest; redeemable for cosmetics

**Implementation note:** `Quest.getXpMultiplier(playerLevel)` implements the scaling logic. `QuestReward.achievementPoints` tracks AP. The actual reward distribution happens in `QuestManager.turnInQuest()` which returns a `QuestReward`.

## 8. Anti-cheat & Backend

- Server validates every progress event (mob floor-tag, item counts from inventory deltas)
- Duplicate turn-in rejected; impossible-quest detection
- Rollbacks on disconnect / relog
- JSON definitions hot-reloadable with a version stamp; client caches definitions

**Implementation note:** `QuestManager.hotReload()` replaces quest definitions while preserving in-progress player state. `QuestManager.QuestException` is thrown for invalid operations (duplicate turn-in, prerequisite failure, cap exceeded). Server-side event validation hooks are not yet wired.

## 9. Open Questions

- Guild-wide quests (progress feeds guild XP — see `GUILDS.md` §5)?
- Branching / choice consequences in the main questline?
- Player-created quests? (v2+)
