# Dungeon Plan v1.1 — Reality-First Redesign

_Dated 2026-10-25. Supersedes v1.0 for everything marked CHANGED; v1.0 stays
as the long-term dream (instanced 5-man raids, lockouts, finder). This plan
defines what the five world-dungeons on floor 1 actually become NOW, for a
1–5 player friend server, no instancing._

## 0. Current state (audited)

- `FloorDungeons.build()` = **hut + ladder shaft + one long hall + tunnel**. Full stop.
- Builders for `mobRoom`, `bossRoom`, `lootRoom`, `corridor` **already exist in code but are never called** — the dungeon has no progression, no boss, no real loot.
- `DungeonManager` exists but is an in-memory stub (instance map + lockout map, no teleport, no clear flow). Unused.
- Mob roster: 21 specs tiered floors 1–5 + per-floor Guardian. Dungeon defenders: 4 random mobs + 1 Guardian on first approach (no boss).
- Chests: 2 static loot chests, fixed items, re-lootable forever.
- No clear detection, no rewards, no quest link, no re-entry cost.

## 1. Design pillars (changed from v1.0)

| v1.0 dream | v1.1 reality |
|---|---|
| Instanced, party-checked, 5-man | **Static world dungeons**, walk in solo or with whoever is online |
| Weekly lockouts + bypass fee | **Entry cooldown** (per player, 10 min) + respawn-grind guard |
| Dungeon Finder GUI + matchmaking | **Signs + compass-worthy landmarks**, NPC mentions quest dungeons |
| 50 dungeons across 25 floors | **5 dungeons on floor 1** with distinct identities, designed to be repeatable |
| 3-phase custom-AI bosses | **1 signature boss per dungeon** using vanilla mob + modifiers + bossbar + 1 real mechanic |

Keep from v1.0: the anatomy rhythm (lobby → trash → boss → loot), first-clear
bonus, the five names/themes below, Col reward philosophy (dungeons are the
main PvE income besides quests).

## 2. Standard layout template (2D)

Every dungeon follows one spine. All builders exist; we just call them.
Scale: `#` wall, `.` air, `E` entrance shaft, `S` safe lobby, `m` mob room,
`B` boss room (red ring), `T` loot room (treasure), `|` corridor (3 wide).

```
 SURFACE                     UNDERGROUND (y = DEEP)
 ┌───────┐                    S ──3w── m1 ──3w── m2 ──3w── [B] ──3w── [T]
 │  hut  │ E ladder            ▲                               │
 │  E ●  │ shaft                │ exit stairs                    │
 └───┬───┘                 spawn pocket                   boss door (gated)
   hole↓                  (lights up,                     opens on
  shaft 60m               doors close                     boss kill
                          while fighting)
```

Rules:
- **Lobby (S)**: safe zone, lit, rest point (respawn anchor glow), chest with
  nothing in it = "clear to fill" placeholder removed; banner + sign naming
  the dungeon and recommended level.
- **Trash rooms (m1, m2)**: 4–6 mobs, respawn if dungeon is re-armed.
- **Boss door**: `#` wall of polished blackstone except a **2-wide RED_CONCRETE
  framed door** that only opens once m1+m2 are cleared (kill counter per
  player-party, 5-min window). Boss room doors seal during the fight.
- **Boss room (B)**: red ring floor (already built), 1 Guardian-class pack +
  **the boss**. Bossbar shows its HP while anyone is in the room.
- **Loot room (T)**: locked until boss dies. Contains the clear chest +
  return staircase/portal back to surface.
- Death in dungeon = respawn at town (unchanged), re-entry allowed anytime
  but defenders re-arm only after the 10-min cooldown.

## 3. The five dungeons

All 5 are on floor 1 for now (existing POINTS), level band 3–14.
Defender pool = floor-1 tier specs (`frenzy_boar, dire_wolf, fury_cow,
blood_fang_pig, razor_hen, mad_fleece, poisoned_fang, bandit_archer`).

### 3.1 The Rusty Sword — (0, -420) — rec. lvl 3 [TUTORIAL]
- Theme: rusty training-ground horror. Mossy/cracked stone, bone lanterns.
- Trash: blood_fang_pig + fury_cow packs (slow, tanky warm-ups).
- **Boss: Illuyong's Whelp** (IRON_GOLEM recolor, 200 HP, Lv 6) — mechanic:
  telegraphed charge (recharges toward nearest player; break line of sight).
- Loot: first Rare weapon drop guaranteed for the clearer; clear chest ~300c.
- Quest hook: Receptionist's "First Blood" chain ends here (already wired-ish).

### 3.2 Goblin Warrens — (380, 180) — rec. lvl 6
- Theme: cramped dirt-and-moss Warren, soul-lantern green tint.
- Trash: ZOMBIE pack "Timber Wraith" (f2 spec allowed) + bandit_archer snipers
  on iron-bar balconies (existing m2 deco).
- **Boss: Goblin King** (RAVAGER, 260 HP, Lv 9) — mechanic: roars → summons
  2 frenzy_boar adds every 25% HP.
- Loot: gold-heavy (themed), ~450c, enhancement stones 2–3.

### 3.3 The Flooded Crypt — (-360, 260) — rec. lvl 9
- Theme: water — flood m1 and corridors with 2-deep water (slows movement),
  prismarine/sea-lantern accents.
- Trash: drowned + squid-tinted "cursed" zombies.
- **Boss: Drowned Lord** (DROWNED trident, 300 HP, Lv 12) — mechanic: ranged
  trident volleys; stay-close DPS check.
- Loot: pearls, tide materials, ~600c.

### 3.4 Spider's Lair — (-440, -220) — rec. lvl 11
- Theme: cobweb-filled cave (COBWEB clusters in m1/m2 ceiling), purple tint.
- Trash: poisoned_fang + crag_stalker swarm (fast spawn waves via the
  existing stir/defender trigger).
- **Boss: Broodmother** (CAVE_SPIDER giant scale 2.2×, 340 HP, Lv 13) —
  mechanic: spawns egg sacs (2 spiders each) every 30s; kill sacs first.
- Loot: silk, poison materials (future alch), ~700c.

### 3.5 Crystal Caverns — (0, 460) — rec. lvl 13
- Theme: amethyst/quartz crystal growth, shroomlight bloom.
- Trash: swarm_lancer + venom_fang + hollow_knight (f3 teaser).
- **Boss: Crystal Golem** (IRON_GOLEM + glow, 420 HP, Lv 15) — mechanic:
  becomes invulnerable behind crystal shield until a "resonance" player
  (random target, marked) stands on a plate; soft tank-swap.
- Loot: best floor-1 dungeon — Epic weapon chance, ~900c.

## 4. Clear flow (the missing system)

`DungeonManager` upgrades (keep it in-memory + one YAML file):
1. Entering boss room arms the fight (door seal, bossbar).
2. Boss death → `CLEAR <dungeon> <player(s)>` → loot room door opens,
   broadcast `[Aincrad] skelci cleared The Rusty Sword!`, Col auto-credited
   (party split even), first-clear = 2× + title `[Sword Saint I]`.
3. `data/Aincrad/clears.yml`: `{player: {dungeon: {first: ts, count: n}}}`.
4. Clear chest spawns fresh loot per clear (randomized from tier table),
   never re-lootable for the same clear after opening.
5. Re-arm cooldown per dungeon (10 min, shared global so you can't rerun-
   chain trash rooms).

## 5. Phases

- **A. Structure** (pure build pass): wire the existing room builders into the
  spine per dungeon, themed materials per dungeon, entrance sign with name +
  rec. level, exit stairs from loot room to surface. No gameplay logic.
- **B. Encounters**: trash-room spawn waves tied to clear-state, boss entities
  with bossbar + the 1 mechanic each, boss-door gating, room sealing.
- **C. Rewards**: clear detection + broadcast + Col + clears.yml + first-clear
  titles; loot tables per dungeon (v1.0 §4.2 numbers, halved: no economy
  inflation with 2 players).
- **D. Quest link**: Receptionist offers one quest-chain ending in the 5
  first-clears; QuestWidget shows current dungeon quest.
- **E. Polish** (later): re-arm cooldown UI, death-respawn-at-entrance
  toggle, ambient sounds, floor-2 dungeons reusing the pipeline.

Deferred to the v1.0 dream (NOT now): instancing, party roles, finder,
weekly lockouts, difficulty modes, need/greed.

## 6. Open decisions (pick or default)

1. Party definition for clears: "anyone within 40 blocks of boss when it
   dies shares the clear" — default YES (no party system needed).
2. Trash respawn on re-entry: NO until re-arm cooldown (prevents grinding
   rooms for mats) — default YES as written.
3. First-clear titles: chat prefix or just scoreboard? Default: chat
   broadcast + `/level` footer line.
4. Bosses drop a trophy item (e.g. "Goblin King's Crown" cosmetic/tradeable)
   — default YES, fun + economy sink.
