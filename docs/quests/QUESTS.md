# Quests

**Status:** Planning draft v1
**Backend:** Paper/Purpur plugin (authoritative state) + Fabric client UI
**Related:** `PROGRESSION.md` (floor gating, milestones), `PARTIES.md` (shared credit), `GUILDS.md` (guild quests — proposed)

## 1. Overview

Three quest classes:

| Class | Purpose | Repeatable |
|---|---|---|
| Main | 100-floor clear arc (story) | No |
| Side | Per-floor objectives (kill / collect / defeat) | No |
| Daily | Repeatable tasks for steady rewards | Yes (3/day) |

## 2. Data Model

**Quest definitions** (versioned JSON, hot-reloadable):

```
id, name, type (main|side|daily), floor, giver (NPC), prerequisites,
objectives (kill X mobs / collect Y items / defeat mini-boss / talk to NPC),
rewards (XP / Col / items), turn-in (auto|NPC), repeatable
```

**Player state** (plugin-side): active quests, per-objective progress, completed set, cooldowns.

**Server-authoritative progress:** all kill/collect events validated server-side (mob floor-tag, inventory deltas); rollbacks on disconnect.

## 3. Main Questline (100-Floor Arc)

- 100 quests — "Clear Floor N" — one per floor
- Story text (3–5 lines) delivered via NPC dialog in the safe city
- Unlocks boss arena access for that floor
- **Prerequisite:** player has cleared floor N−1 (boss kill recorded server-side)
- **Failure:** none; retry allowed

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

## 5. Daily Quests

- **Slots:** 3 per player per day; reset at 00:00 server time
- **Variants:** kill / collect / defeat, weighted to player's current floor ±5
- **Streak:** +5% XP per consecutive day, cap +50%; resets on a missed day
- **Weekly raid quest:** 1 per week (post-floor-100 content)

## 6. Quest Tracking UI

- **HUD tracker** (keybind toggle): active quest, objective list, progress counters, distance hint
- **Journal GUI:** active quests, history (last 20), filters (main/side/daily), abandon (with confirmation; main quest abandon blocked)
- **World markers:** client-side markers for active objectives (data from server)
- **Party integration:** kill/collect credit shared among party members within 128 blocks

## 7. Rewards & Scaling

- **XP scaling:** player level > floor +10 → ×0.25 (anti-farming); < floor −5 → ×1.5 (catch-up)
- **Col / items:** fixed per quest; items are tradeable (no bind-on-quest)
- **Achievement points:** +10 per side/daily quest, +50 per main quest; redeemable for cosmetics

## 8. Anti-cheat & Backend

- Server validates every progress event (mob floor-tag, item counts from inventory deltas)
- Duplicate turn-in rejected; impossible-quest detection
- Rollbacks on disconnect / relog
- JSON definitions hot-reloadable with a version stamp; client caches definitions

## 9. Open Questions

- Guild-wide quests (progress feeds guild XP — see `GUILDS.md` §5)?
- Branching / choice consequences in the main questline?
- Player-created quests? (v2+)
