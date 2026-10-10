# Aincrad Dungeon Design

## Overview

Dungeons are the core PvE endgame of Aincrad. They are **instanced**,
**5-player** challenges organized by **floor tier**, with scaling difficulty,
exclusive rewards, and weekly lockouts. The system is designed to run
on a Paper/Purpur backend with the Fabric mod as the client.

---

## 1. Dungeon Structure

### 1.1 Floor Tiers

The world of Aincrad has 25 floors. Dungeons are unlocked in tiers:

| Tier | Floors | Dungeon Count | Party Size | Recommended Level |
|---|---|---|---|---|
| **Tier 1** | 1–5 | 2 per floor | 5 | 1–10 |
| **Tier 2** | 6–10 | 2 per floor | 5 | 10–20 |
| **Tier 3** | 11–15 | 2 per floor | 5 | 20–30 |
| **Tier 4** | 16–20 | 2 per floor | 5 | 30–40 |
| **Tier 5** | 21–25 | 2 per floor | 5 | 40–50 |

**Total dungeons: 50** (2 per floor × 25 floors)

### 1.2 Dungeon Anatomy

Each dungeon has a consistent structure:

```
┌─────────────────────────────────────────────────────┐
│  Entrance → Lobby (safe zone)                       │
│      ↓                                              │
│  Corridor → Trash Pack 1 (3–5 mobs)                 │
│      ↓                                              │
│  Mini-boss 1 (mechanics check)                      │
│      ↓                                              │
│  Corridor → Trash Pack 2 (4–6 mobs)                 │
│      ↓                                              │
│  Mini-boss 2 (mechanics check)                      │
│      ↓                                              │
│  Pre-boss area (rest point, 30 s)                   │
│      ↓                                              │
│  Final Boss (multi-phase, enrage timer)             │
│      ↓                                              │
│  Loot Chamber (chest + bonus roll)                  │
└─────────────────────────────────────────────────────┘
```

| Section | Count | Purpose |
|---|---|---|
| **Lobby** | 1 | Party can buff, check gear, ready up. |
| **Trash packs** | 2–3 per dungeon | Warm-up, Col drops, material drops. |
| **Mini-bosses** | 2 per dungeon | Gate mechanics; must both be killed. |
| **Final boss** | 1 | Multi-phase encounter with unique mechanics. |
| **Loot chamber** | 1 | Bonus chest after final boss kill. |

---

## 2. Instancing System

### 2.1 How Instancing Works

Each dungeon run gets its own **isolated instance** — a separate world
copy or a region-locked area that is inaccessible to non-party members.

**Backend approach (Paper/Purpur):**
- When a party enters a dungeon, the server creates an `DungeonInstance`
  object with a unique ID.
- The instance tracks: party members, spawn state, timer, difficulty, and
  loot rolls.
- Mobs and chests are spawned by the plugin, not vanilla spawns — they
  use custom AI and loot tables.
- The instance is destroyed when all members leave or the dungeon is
  completed/failed.

**Instance lifecycle:**
```
CREATE → READY → IN_PROGRESS → COMPLETED / FAILED → DESTROY
```

### 2.2 Instance Limits

| Constraint | Value |
|---|---|
| Max concurrent instances per dungeon | 20 (per shard) |
| Max instances per player per day | 5 |
| Max instances per dungeon per player per week | 3 (lockout, see §5) |
| Instance timeout (in progress) | 2 hours (then auto-fail) |

### 2.3 Sharding

- Dungeons are **per-shard**. A party on Shard 1 cannot join an instance
  on Shard 2.
- If a player disconnects mid-dungeon, the instance is held for **5 minutes**
  before being destroyed. The player can rejoin if the instance still exists.

---

## 3. Difficulty Scaling

### 3.1 Difficulty Tiers

Each dungeon has three difficulty tiers, selected by the party leader at
entrance:

| Tier | Level Modifier | Mob HP Modifier | Mob DMG Modifier | Reward Modifier |
|---|---|---|---|---|
| **Normal** | ±0 levels | 1.0× | 1.0× | 1.0× |
| **Hard** | +5 levels | 1.5× | 1.3× | 1.5× (bonus materials) |
| **Nightmare** | +10 levels | 2.2× | 1.8× | 2.0× (bonus Epic+ drop chance) |

### 3.2 Dynamic Difficulty

In addition to static tiers, the system uses **dynamic scaling** based on
party composition:

| Factor | Effect |
|---|---|
| **Party average level** | Mob stats scale ±10% per level above/below recommended. |
| **Party size** | If fewer than 5 players, mob HP scales down proportionally (min 50%). |
| **Deaths** | Each death increases boss enrage timer by 10 seconds (more enrage = harder). |
| **Gear score** | If average gear score is above recommended, mob stats scale up slightly. |

### 3.3 Difficulty Selection UI

The Dungeon Finder (§6) shows a dropdown for difficulty. The party leader
selects; all members confirm. The selection is locked once the instance
starts.

---

## 4. Rewards

### 4.1 Loot Distribution

Each boss and mini-boss drops loot using a **Need/Greed/Pass** system:

| Roll | Priority | Behavior |
|---|---|---|
| **Need** | Highest | You need the item for your current spec/class. |
| **Greed** | Medium | You want it for off-spec or for the Col value. |
| **Pass** | Lowest | You decline the item. |

**Rules:**
- Only one player can Need an item. If multiple players Need, a roll
  (1–100) determines the winner.
- Greed players compete only if no one Needed the item.
- If everyone Passes, the item is destroyed (or sent to guild bank if
  configured).

### 4.2 Reward Table

| Kill | Col | Materials | Equipment | Enhancement Stones |
|---|---|---|---|---|
| Trash pack | 20–80c | 1–2 common | — | — |
| Mini-boss 1 | 200–500c | 2–3 (uncommon) | 1 (Rare) | 2–3 |
| Mini-boss 2 | 200–500c | 2–3 (uncommon) | 1 (Rare) | 2–3 |
| Final boss | 1,000–2,500c | 3–5 (rare+) | 2 (Rare–Epic) | 5–8 |
| Loot chamber chest | 500–1,500c | 5–8 (rare+) | 1 (Epic on Hard+) | 3–5 |

### 4.3 First-Clear Bonus

- First time a player clears a dungeon on any difficulty: **2× Col reward**.
- First time a player clears a dungeon on Nightmare: **exclusive title +
  cosmetic reward**.

### 4.4 Bonus Roll

After the final boss, the party gets a **bonus roll** — a shared loot
roll that everyone participates in. This is separate from individual
Need/Greed rolls and always drops something (no "pass" option).

---

## 5. Lockout Timers

### 5.1 Weekly Lockout

Each dungeon has a **weekly lockout** per player:

| Difficulty | Lockout | Reset Day |
|---|---|---|
| Normal | 1× per week | Monday 00:00 server time |
| Hard | 1× per week | Monday 00:00 server time |
| Nightmare | 1× per week | Monday 00:00 server time |

**Lockouts are per-dungeon, per-difficulty.** A player can run the same
dungeon on Normal, Hard, and Nightmare in the same week (3 total runs).

### 5.2 Lockout Bypass

Players can bypass a lockout for **3,000c per reset** (see `ECONOMY.md` §3.5).
This allows farming but at a steep cost, acting as an inflation sink.

### 5.3 Lockout Display

The Dungeon Finder shows lockout status:

```
┌─ Floor 5: Crystal Caverns ─────────────────┐
│ Normal:   ✓ Available                      │
│ Hard:     ✗ Locked (resets in 3d 14h)      │
│ Nightmare: ✓ Available                     │
│                                            │
│ [Enter Normal] [Bypass Hard (3,000c)]      │
└────────────────────────────────────────────┘
```

### 5.4 Lockout Persistence

Lockouts are stored in the database (`aincrad_dungeon_lockouts`) with
`(uuid, dungeon_id, difficulty, reset_time)`. The reset is checked
lazily — when a player queries the Dungeon Finder, the system compares
`reset_time` to the current time and clears expired lockouts.

---

## 6. Dungeon Finder

### 6.1 How It Works

The Dungeon Finder is a GUI-based matchmaking tool. It replaces vanilla
`/tp` with a structured queue system.

### 6.2 Party Requirements

| Role | Required | Count |
|---|---|---|
| Tank | Yes | 1 |
| Healer | Yes | 1 |
| DPS | Yes | 3 |

Parties must have exactly 5 players with the correct role composition to
queue. Players can queue solo (the system fills the party) or as a
pre-made group.

### 6.3 Queue Flow

```
1. Party leader opens Dungeon Finder (GUI or /dfinder command).
2. Selects dungeon + difficulty.
3. System checks lockouts for all members.
4. If all pass, party is queued.
5. Once 5 players are matched (or pre-made party enters), countdown starts.
6. All members confirm teleport.
7. Instance is created; party is teleported to dungeon lobby.
```

### 6.4 Queue Times

| Dungeon Type | Expected Queue Time (Peak) | Expected Queue Time (Off-Peak) |
|---|---|---|
| Tank | 2–5 min | 0–1 min |
| Healer | 1–3 min | 0–1 min |
| DPS | 5–15 min | 1–3 min |

### 6.5 Anti-Abuse

- Players cannot queue for a dungeon they are locked out of (without paying
  the bypass fee).
- Players who leave mid-run (without disconnect) are flagged with a
  **30-minute deserter debuff** and cannot queue again until it expires.
- Repeated deserter flags (3+ in 24 h) result in a 24-hour queue ban.

### 6.6 Cross-Faction / Cross-Guild

- Players from different guilds can queue together.
- Cross-faction queueing is **not** supported (SAO has no factions; this
  is a PvE-only system).

---

## 7. Boss Mechanics

### 7.1 Design Principles

Every boss follows a **3-phase** structure:

| Phase | HP Range | Mechanics |
|---|---|---|
| **Phase 1** | 100%–66% | Basic attacks, one mechanic. |
| **Phase 2** | 66%–33% | Adds a second mechanic, increased damage. |
| **Phase 3** | 33%–0% | Enrage timer (2 min), all mechanics active, highest damage. |

### 7.2 Mechanic Types

| Mechanic | Description | Counter |
|---|---|---|
| **Tank swap** | Boss applies stacking debuff; tanks must swap. | Tanks swap at 3 stacks. |
| **Add spawn** | Boss summons minions. | DPS priority on adds. |
| **Positioning** | Boss charges or cleaves. | Spread out / stack as called. |
| **Interruptable cast** | Boss channels a big spell. | Interrupt with CC skill. |
| **Ground AoE** | Red circles on ground. | Move out. |
| **Debuff spread** | Party-wide debuff. | Healer cleanse. |
| **Enrage** | Timer expires; boss goes berserk. | Kill before timer ends. |

### 7.3 Boss AI

Bosses use a **priority-based AI system** (not vanilla Minecraft AI):
1. Check for mechanic triggers (phase, HP threshold, timer).
2. If mechanic is due, execute it.
3. Otherwise, perform a basic attack (melee or ranged).
4. Repeat.

This is implemented in the Paper/Purpur backend via custom `Entity` classes
that override vanilla AI.

---

## 8. Implementation Notes

### 8.1 Backend (Paper/Purpur)

- **Instance manager:** `DungeonInstanceManager` — singleton that tracks
  all active instances. Handles creation, lookup, and cleanup.
- **Custom mobs:** Each dungeon mob is a `CraftLivingEntity` subclass with
  custom AI goals and loot tables.
- **Custom bosses:** Bosses use a `BossAI` class that manages phase
  transitions, mechanic timers, and enrage.
- **Persistence:** Instance state is serialized to disk on shutdown so
  that instances can be restored on restart (optional; currently
  instances are destroyed on server restart and players are refunded).

### 8.2 Client (Fabric)

- **Dungeon Finder GUI:** Custom screen with queue status, dungeon list,
  and role selection.
- **Instance boundary:** The client receives a plugin message
  `aincrad:dungeon:enter` that hides the vanilla HUD and shows dungeon
  UI (boss bar, timer, party frames).
- **Rejoin:** If a player disconnects and rejoins within 5 minutes, the
  server sends the current instance state (boss HP, phase, timer) and
  the client re-renders.

### 8.3 Performance

- **Mob cap:** Each instance has a hard cap of 30 mobs (including trash,
  adds, and bosses). This prevents lag from excessive spawning.
- **Tick budget:** Boss AI logic runs every 20 ticks (1 second), not every
  tick, to reduce CPU load.
- **Instance cleanup:** A background task runs every 5 minutes to destroy
  stale instances (no players for >5 min, or timeout exceeded).

---

## 9. Dungeon List (Preview)

### Tier 1 (Floors 1–5)

| Floor | Dungeon Name | Final Boss | Mechanic Theme |
|---|---|---|---|
| 1 | The Rusty Sword | True Dragon | Basic melee, tank swap |
| 2 | Goblin Warrens | Goblin King | Add spawn, ground AoE |
| 3 | The Flooded Crypt | Drowned Lord | Positioning, debuff spread |
| 4 | Spider's Lair | Broodmother | Web traps, add spawn |
| 5 | Crystal Caverns | Crystal Golem | Interruptable casts, tank swap |

### Tier 2 (Floors 6–10)

| Floor | Dungeon Name | Final Boss | Mechanic Theme |
|---|---|---|---|
| 6 | The Howling Wastes | Dire Wolf Alpha | Pack mechanics, positioning |
| 7 | Emberforge | Flame Elemental | Ground AoE, debuff spread |
| 8 | The Sunken Temple | Kraken Spawn | Add spawn, interruptable casts |
| 9 | Shadowfell Arena | Shadow Dancer | Positioning, tank swap, adds |
| 10 | Frostbite Depths | Frost Wyrm | Ground AoE, enrage pressure |

*(Tiers 3–5 to be designed as floors 11–25 are implemented.)*

---

## 10. Open Questions

1. Should there be a 10-man or 20-man raid tier for world bosses? (Currently
   planned as separate system, not in dungeons.)
2. Should dungeon lockouts be account-wide or per-character? (Currently
   per-player, since there is one character per account.)
3. Should there be a "Looking For Group" chat channel in addition to the
   GUI-based finder? (Quality-of-life feature, low priority.)
4. Should dungeon difficulty scale with the number of deaths? (Currently
   only enrage timer is affected.)

---

*Document version: 1.0 — Last updated 2026-10-10*
