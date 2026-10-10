# Progression

**Status:** Planning draft v1 — **Implementation in progress**
**Backend:** Paper/Purpur plugin (authoritative) + Fabric client UI
**Related:** `QUESTS.md` (quest rewards), `GUILDS.md` (guild levels), `PARTIES.md` (raid parties)

---

## 0. Implementation Status

The following Java classes have been created under `src/main/java/com/aincrad/`:

| Class | Type | Location | Status |
|---|---|---|---|
| `Milestone` | enum | `progression/Milestone.java` | ✅ Complete |
| `PlayerProgression` | final class | `progression/PlayerProgression.java` | ✅ Complete |
| `ProgressionManager` | final class (singleton) | `progression/ProgressionManager.java` | ✅ Complete |

**Package:** `com.aincrad.progression`

**What's implemented:**
- Full XP curve (50 × L^1.5) with level-up processing (multi-level support)
- Level cap at 100
- Skill points and stat points income per level
- Milestone tracking (FLOOR_10 / 25 / 50 / 75 / 100)
- NG+ (prestige) system: reset to level 60, stacking +5% XP/Col bonus (cap +25%)
- Thread-safe singleton manager with concurrent map operations

**What's still planning:**
- Persistence layer (save/load progression state)
- Fabric client UI (skill tree GUI, level bar, milestone toasts)
- Skill tree spending logic (separate doc)
- Equipment tier integration
- Floor level-gating enforcement hooks

---

## 1. Overview

Level-gated floor progression across 100 floors, equipment tiers, skill points, milestone unlocks, and post-game endgame.

## 2. Character Levels

- **Cap:** 100
- **XP curve:** XP(L → L+1) = 50 × L^1.5

| Level | XP to next |
|---|---|
| 1 | 50 |
| 10 | ~1,600 |
| 25 | ~6,250 |
| 50 | ~17,700 |
| 75 | ~32,500 |
| 99 | ~49,000 |

**Implementation note:** `ProgressionManager.xpForLevel(int)` and `xpForNextLevel(int)` implement the curve. Constants: `XP_COEFFICIENT = 50.0`, `XP_EXPONENT = 1.5`. The `addXp(UUID, double)` method handles XP gain and calls `processLevelUps()` which supports multi-level-ups from a single XP grant.

`PlayerProgression` tracks `level`, `xp` (double), `skillPoints`, `statPoints`, `milestonesAchieved`, and `ngPlusCount`. Constants: `MAX_LEVEL = 100`, `NG_PLUS_RESET_LEVEL = 60`, `SKILL_POINTS_PER_LEVEL = 3`, `STAT_POINTS_PER_LEVEL = 5`.

**Sources:** mobs (scaled by floor), quests (see `QUESTS.md`), boss kills, dailies.
Server-authoritative; Fabric renders the XP bar and level display.

## 3. Floor Level-Gating

- **Recommended level = floor number**

| Floors | Recommended level |
|---|---|
| 1–10 | 1–10 |
| 11–25 | 11–25 |
| 26–50 | 26–50 |
| 51–75 | 51–75 |
| 76–100 | 76–100 |

**Under-leveled** (player level < floor −5):

- +5% damage taken per level below (cap +50%)
- −2% XP per level below (floor −50%)

**Over-leveled** (player level > floor +10):

- XP ×0.25, Col ×0.5 (anti-farming low floors)

**Access gates:**

- Boss arena: recommended level −5
- Quest acceptance: recommended level −10

**Status:** 🔲 Enforcement hooks not yet wired to floor/boss/quest access events.

## 4. Equipment Tiers

Tiers 1–10 unlock at floors 1 / 10 / 20 / … / 90.

| Tier | Unlock floor | Stat multiplier |
|---|---|---|
| 1 | 1 | ×1.00 |
| 2 | 10 | ×1.35 |
| 3 | 20 | ×1.82 |
| 5 | 40 | ×3.32 |
| 10 | 90 | ×15.6 |

- Multiplier applies to weapon damage, armor, and tool speed
- Crafting requires materials from the floor decade (e.g., T4 gear ← F31–40 materials)
- Milestone items (main quest 10/25/50/75/100): +10% above tier norm, tradeable

**Status:** 🔲 Equipment system not yet implemented.

## 5. Skill Points

- **Income:** +2 per level (200 at cap) + 10 per milestone (5 × 10 = 50) → **250 total**
- **Spending:** combat / passive skills via a skill tree (separate doc); points are per-character
- **Loadouts:** 1 by default; 2nd unlocked at floor 50 milestone
- **Respec:** `/skills respec` — cost 10,000 Col × times used (cap 100,000); resets SP only, milestones kept

**Implementation note:** `PlayerProgression` tracks `skillPoints` and `statPoints` with `SKILL_POINTS_PER_LEVEL = 3` and `STAT_POINTS_PER_LEVEL = 5`. The `addSkillPoints()` and `addStatPoints()` mutators are package-private, called by `ProgressionManager.processLevelUps()`. Skill tree spending logic is not yet implemented.

## 6. Milestones (10 / 25 / 50 / 75 / 100)

| Floor | Unlock |
|---|---|
| 10 | Guild halls + guild skills + teleport gate network (see `GUILDS.md` §7–8) |
| 25 | Raid parties (4×6) + GvG arenas + party finder (see `PARTIES.md` §5, `GUILDS.md` §9) |
| 50 | Second skill loadout |
| 75 | Weekly raid wing; "Ace" title |
| 100 | Win condition (see §7) |

**Implementation note:** `Milestone` enum defines `FLOOR_10`, `FLOOR_25`, `FLOOR_50`, `FLOOR_75`, `FLOOR_100` with `level` and `description` fields. `Milestone.forLevel(int)` and `isAchievedAt(int)` provide lookups. `ProgressionManager.checkMilestones()` is called on level-up and invokes `PlayerProgression.achieveMilestone()`.

## 7. Endgame

- **Floor 100 clear = win condition:** server-wide event, fireworks, title "Clearer", unlocks post-game content
- **Post-game:**
  - **Weekly rotating raids** — F50 / F75 / F100 scaled difficulty versions, lockout per week
  - **NG+ (prestige):** reset to level 60; keep cosmetics and titles; permanent +5% XP/Col per NG+ (cap +25%)
  - **Leaderboards:** fastest clear, GvG rating, guild ranking
  - **Seasons:** guild ranks reset each season with rewards
- **Server-wide tracking:** % of players cleared per floor; "Aincrad Liberation" event on the first floor-100 clear

**Implementation note:** `ProgressionManager.initiateNgPlus(UUID)` resets the player to level 60, increments `ngPlusCount`, and applies the stacking bonus. Constants: `NG_PLUS_BONUS_PER_CYCLE = 0.05`, `NG_PLUS_MAX_BONUS = 0.25`. `getXpMultiplier(int)` returns the NG+ multiplier (1.0 + min(ngPlusCount × 0.05, 0.25)).

## 8. Data & Backend

- Plugin authoritative for XP / level / skill points / loadouts / milestones
- Fabric UI: skill tree GUI, level bar, milestone toasts

**Implementation note:** `ProgressionManager` is a singleton (static holder pattern) with a `ConcurrentHashMap<UUID, PlayerProgression>` for thread-safe access. `getAllProgressions()` and `removeProgression(UUID)` are available for persistence integration. Persistence layer is not yet implemented.

## 9. Open Questions

- Multiple characters per account sharing progression? (out of scope)
- Hardcore permadeath mode? (separate doc)
