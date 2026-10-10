# Aincrad — Mob System Design

**Scope:** Field mobs per floor, mob stats, drops, behavior AI, spawning rules, and elite mobs.

---

## 1. Mob Stat Scaling

All mob stats scale with floor number `F` (1–100).

```
HP(F)     = round( 30 × F^1.5 )
Damage(F) = round( 4 × F^1.3 )
XP(F)     = round( 8 × F^1.4 )
```

| Floor | HP | Damage | XP |
|-------|----|--------|-----|
| 1 | 30 | 4 | 8 |
| 10 | 949 | 101 | 202 |
| 25 | 3,750 | 415 | 1,030 |
| 50 | 10,607 | 1,163 | 2,915 |
| 75 | 19,841 | 2,170 | 5,470 |
| 100 | 30,000 | 3,162 | 8,500 |

**Elite multiplier:** ×3 HP, ×2.5 damage, ×5 XP, guaranteed rarity drop (see §6).

---

## 2. Field Mobs by Floor Theme

Each floor has a **theme** that determines mob variety. Below are representative floors; the full 100-floor table follows the same pattern.

### Floor 1 — Verdant Plains (starter)

| Mob | HP | Damage | XP | Drops | Behavior |
|-----|----|--------|-----|-------|----------|
| Green Rabbit | 25 | 3 | 6 | Hide (60%), Carrot (30%) | Passive, flees when hit |
| Wild Boar | 40 | 6 | 10 | Hide (50%), Tusk (20%) | Neutral, charges when approached |
| Goblin Scout | 35 | 8 | 12 | Iron Ore (30%), Coin (40%) | Hostile, melee, packs of 2–3 |
| Goblin Shaman | 30 | 10 | 15 | Herb (40%), Staff Shard (10%) | Hostile, ranged (5 m), heals allies |

### Floor 10 — Dark Forest

| Mob | HP | Damage | XP | Drops | Behavior |
|-----|----|--------|-----|-------|----------|
| Forest Wolf | 900 | 90 | 180 | Wolf Pelt (50%), Fang (25%) | Hostile, packs of 3–5, howl buff |
| Spider | 700 | 110 | 200 | Silk (60%), Venom Sac (15%) | Hostile, web slow (2 s), ambush from trees |
| Treant | 1,500 | 130 | 260 | Wood (70%), Sap (30%) | Neutral, AoE slam, high DEF |
| Goblin King (mini-boss) | 3,000 | 180 | 500 | Iron Sword (100%), Crown (5%) | Hostile, summons 2 scouts, enrage at 30% |

### Floor 25 — Crystal Caverns

| Mob | HP | Damage | XP | Drops | Behavior |
|-----|----|--------|-----|-------|----------|
| Crystal Bat | 3,200 | 380 | 900 | Crystal Shard (40%), Wing (20%) | Hostile, flying, sonic screech (stun 1 s) |
| Rock Golem | 5,500 | 450 | 1,100 | Stone Core (30%), Ore (50%) | Neutral, high DEF, AoE quake |
| Cave Spider | 2,800 | 500 | 1,000 | Silk (70%), Venom (25%) | Hostile, poison DoT (5 s), web |
| Crystal Slime | 4,000 | 350 | 950 | Gel (80%), Crystal (20%) | Passive, splits into 2 small slimes on death |

### Floor 50 — Molten Depths

| Mob | HP | Damage | XP | Drops | Behavior |
|-----|----|--------|-----|-------|----------|
| Fire Elemental | 10,000 | 1,100 | 2,800 | Fire Essence (50%), Ember (30%) | Hostile, ranged fireball (8 m), burn DoT |
| Lava Golem | 15,000 | 1,300 | 3,200 | Magma Core (35%), Obsidian (40%) | Neutral, immune to fire, AoE eruption |
| Imp | 7,000 | 1,500 | 3,000 | Demon Horn (25%), Coin (60%) | Hostile, fast, backstab bonus |
| Flame Dragon (mini-boss) | 25,000 | 1,800 | 6,000 | Dragon Scale (100%), Fire Sword (15%) | Hostile, flying, fire breath cone |

### Floor 75 — Frozen Wastes

| Mob | HP | Damage | XP | Drops | Behavior |
|-----|----|--------|-----|-------|----------|
| Ice Wolf | 18,000 | 2,000 | 5,000 | Frost Pelt (50%), Fang (30%) | Hostile, packs, frostbite slow |
| Frost Giant | 28,000 | 2,400 | 6,000 | Giant Bone (40%), Ice Crystal (35%) | Neutral, AoE freeze (3 s), high HP |
| Snow Harpy | 14,000 | 2,200 | 5,500 | Feather (60%), Talon (20%) | Hostile, flying, dive attack |
| Ice Elemental | 16,000 | 2,600 | 5,800 | Ice Essence (50%), Frost Shard (30%) | Hostile, ranged ice spike, chill DoT |

### Floor 100 — The Final Floor

| Mob | HP | Damage | XP | Drops | Behavior |
|-----|----|--------|-----|-------|----------|
| Shadow Knight | 28,000 | 3,000 | 8,000 | Dark Ore (40%), Shadow Essence (30%) | Hostile, melee, teleport behind target |
| Void Dragon | 40,000 | 3,500 | 10,000 | Void Scale (100%), Dragon Heart (10%) | Hostile, flying, void breath (AoE) |
| Abyssal Golem | 50,000 | 3,200 | 9,500 | Abyss Core (35%), Dark Crystal (40%) | Neutral, AoE slam, damage reflect 10% |
| Chaos Imp | 22,000 | 3,800 | 9,000 | Chaos Horn (25%), Coin (70%) | Hostile, fast, random teleport |

---

## 3. Mob Behavior AI

| Behavior | Description |
|----------|-------------|
| **Passive** | Never attacks; flees when hit (e.g. Rabbit, Slime) |
| **Neutral** | Attacks only when hit first (e.g. Treant, Golem) |
| **Hostile** | Aggro on sight within 12 m; chases to 30 m leash range |
| **Pack** | Groups of 2–5; aggro one = aggro all; share 20% damage aura |
| **Ranged** | Keeps 5–8 m distance; fires projectile every 2 s |
| **Ambusher** | Hides (invisible or disguised); attacks when player within 3 m |
| **Summoner** | Calls 2–3 adds every 15 s; adds die if summoner dies |
| **Enraged** | Below 30% HP: +50% attack speed, +25% damage, new attack pattern |

**Leash rule:** mobs return to spawn point and heal 10%/s if player moves >30 m away.

---

## 4. Spawning Rules

| Rule | Value |
|------|-------|
| **Spawn cap per floor** | `20 + F` (floor 1: 21, floor 50: 70, floor 100: 120) |
| **Max mobs per player** | 8 (prevents single-player farming overload) |
| **Respawn timer** | 30 s (floor 1–25), 60 s (26–50), 90 s (51–75), 120 s (76–100) |
| **Spawn interval** | 1–3 s per mob, random within cap |
| **Spawn locations** | Predefined spawn points per floor (caves, forests, ruins); min 15 m from player |
| **Elite spawn chance** | 5% per spawn roll |
| **Night spawn bonus** | +50% spawn rate, +20% damage (floors 1–50 only) |
| **Party scaling** | +10% mob HP per additional party member (max +40% at 5 players) |

**Spawn cap enforcement:** if the cap is reached, new spawns queue and fire as slots free. Boss arenas are excluded from the cap.

---

## 5. Drops

| Drop type | Drop rate | Notes |
|-----------|-----------|-------|
| **Common material** | 30–80% | Crafting ingredient (hide, ore, wood) |
| **Rare material** | 10–25% | Upgrade ingredient (crystal, essence) |
| **Weapon** | 2% (field), 15% (elite), 100% (boss) | See WEAPONS.md §8 |
| **Coin** | 40–70% | Currency for NPC shops |
| **Consumable** | 10–20% | Potions, food |
| **Quest item** | 5% | NPC quest turn-in |

**Drop scaling:** higher floors drop higher-tier materials. A floor-50 mob drops tier-5 materials, not tier-1.

**Loot Hunter passive:** +10% drop rate (see SKILLS.md §4).

---

## 6. Elite Mobs

Elites are **rare, enhanced variants** of field mobs.

| Property | Elite value |
|----------|-------------|
| HP | ×3 base |
| Damage | ×2.5 base |
| XP | ×5 base |
| Size | ×1.5 model scale |
| Name prefix | "Elite" (e.g. Elite Forest Wolf) |
| Drop chance | 15% weapon, 50% rare material, 100% WP (10–30) |
| Spawn chance | 5% per spawn roll |
| Behavior | Always hostile, enrages at 50% HP (not 30%) |
| Aura | Nearby mobs (within 10 m) gain +15% damage |

**Elite identification:** purple nameplate, particle effect, distinct sound on spawn. Elites are the primary source of blue/purple rarity weapons outside bosses.

---

## 7. Mob Summary Table (All Floors)

| Floor range | Theme | Mob count | Avg HP | Avg dmg | Avg XP | Elite chance |
|-------------|-------|-----------|--------|---------|--------|--------------|
| 1–10 | Plains / Forest | 3–4 per floor | 30–950 | 4–100 | 6–200 | 5% |
| 11–25 | Forest / Cave | 4–5 per floor | 1,000–3,750 | 100–415 | 200–1,030 | 5% |
| 26–50 | Caverns / Lava | 5–6 per floor | 4,000–10,600 | 400–1,160 | 1,000–2,900 | 5% |
| 51–75 | Volcanic / Ice | 5–6 per floor | 11,000–19,800 | 1,200–2,170 | 3,000–5,500 | 5% |
| 76–100 | Ice / Void | 6–8 per floor | 20,000–30,000 | 2,200–3,160 | 5,500–8,500 | 5% |
| 100 (endgame) | The Abyss | 8 | 30,000 | 3,162 | 8,500 | 5% |
