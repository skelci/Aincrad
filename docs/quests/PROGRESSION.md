# Progression

**Status:** Planning draft v1
**Backend:** Paper/Purpur plugin (authoritative) + Fabric client UI
**Related:** `QUESTS.md` (quest rewards), `GUILDS.md` (guild levels), `PARTIES.md` (raid parties)

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

## 5. Skill Points

- **Income:** +2 per level (200 at cap) + 10 per milestone (5 × 10 = 50) → **250 total**
- **Spending:** combat / passive skills via a skill tree (separate doc); points are per-character
- **Loadouts:** 1 by default; 2nd unlocked at floor 50 milestone
- **Respec:** `/skills respec` — cost 10,000 Col × times used (cap 100,000); resets SP only, milestones kept

## 6. Milestones (10 / 25 / 50 / 75 / 100)

| Floor | Unlock |
|---|---|
| 10 | Guild halls + guild skills + teleport gate network (see `GUILDS.md` §7–8) |
| 25 | Raid parties (4×6) + GvG arenas + party finder (see `PARTIES.md` §5, `GUILDS.md` §9) |
| 50 | Second skill loadout |
| 75 | Weekly raid wing; "Ace" title |
| 100 | Win condition (see §7) |

## 7. Endgame

- **Floor 100 clear = win condition:** server-wide event, fireworks, title "Clearer", unlocks post-game content
- **Post-game:**
  - **Weekly rotating raids** — F50 / F75 / F100 scaled difficulty versions, lockout per week
  - **NG+ (prestige):** reset to level 60; keep cosmetics and titles; permanent +5% XP/Col per NG+ (cap +25%)
  - **Leaderboards:** fastest clear, GvG rating, guild ranking
  - **Seasons:** guild ranks reset each season with rewards
- **Server-wide tracking:** % of players cleared per floor; "Aincrad Liberation" event on the first floor-100 clear

## 8. Data & Backend

- Plugin authoritative for XP / level / skill points / loadouts / milestones
- Fabric UI: skill tree GUI, level bar, milestone toasts

## 9. Open Questions

- Multiple characters per account sharing progression? (out of scope)
- Hardcore permadeath mode? (separate doc)
