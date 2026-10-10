# Aincrad — Boss System Design

**Scope:** Per-floor boss mechanics, phases, attack patterns, telegraphs, enrage timers, arena rules, drops, raid bosses (floors 25/50/75/100), and party-size difficulty scaling.

---

## 1. Boss Stat Scaling

```
HP(F)     = round( 500 × F^1.6 )
Damage(F) = round( 20 × F^1.35 )
XP(F)     = round( 200 × F^1.5 )
```

| Floor | HP | Damage | XP |
|-------|----|--------|-----|
| 1 | 500 | 20 | 200 |
| 10 | 19,950 | 503 | 6,325 |
| 25 | 97,650 | 1,390 | 25,000 |
| 50 | 389,000 | 3,730 | 70,700 |
| 75 | 877,000 | 7,150 | 138,000 |
| 100 | 1,560,000 | 11,220 | 223,600 |

**Party scaling:** HP × `(1 + 0.15 × (n − 1))`, Damage × `(1 + 0.10 × (n − 1))` where `n` = party size (see §8).

---

## 2. Boss Structure

Every floor has **one boss**. Bosses have **4 phases** defined by HP thresholds:

| Phase | HP range | Behavior |
|-------|----------|----------|
| **Phase 1** | 100% – 75% | Basic attacks, slow telegraphs |
| **Phase 2** | 75% – 50% | Adds AoE attacks, faster telegraphs |
| **Phase 3** | 50% – 25% | Summons additional adds, applies debuffs, moderate attack speed increase |
| **Phase 4** | 25% – 0% | Enrage-lite: +30% attack speed, new ultimate attack, desperate AI |

**Phase transition:** 3 s invulnerability + roar + clear all debuffs on boss. Adds from previous phase are killed.

---

## 3. Attack Patterns & Telegraphs

All attacks have a **telegraph** (visual + audio wind-up) before damage lands.

| Attack | Telegraph | Wind-up | Damage | Range | Notes |
|--------|-----------|---------|--------|-------|-------|
| **Melee swipe** | Weapon glows red | 0.8 s | ×1.0 | 4 m arc | Basic attack, 3 s CD |
| **Charge** | Boss crouches, line indicator | 1.2 s | ×1.5 | 15 m line | Knocks back 5 m |
| **AoE slam** | Ground circle (red) | 1.5 s | ×2.0 | 6 m radius | 2 s stun if hit |
| **Projectile** | Boss raises arm, sound cue | 1.0 s | ×1.2 | 20 m line | 3 projectiles in spread |
| **Summon** | Boss roars, portal opens | 2.0 s | — | — | Spawns 2–3 floor mobs |
| **Ultimate** | Screen shake, boss glows gold | 2.5 s | ×3.0 | 10 m radius | Phase 4 only, 30 s CD |
| **Debuff** | Purple aura | 1.5 s | ×0.5 | 8 m radius | −30% DEF or −50% heal, 10 s |

**Dodge window:** the telegraph wind-up is the dodge window. A well-timed sidestep (0.3 s i-frames) avoids all damage.

---

## 4. Enrage Timer

| Rule | Value |
|------|-------|
| **Enrage trigger** | 8 minutes after boss fight starts, OR boss HP < 10% |
| **Enrage effect** | +50% attack speed, +40% damage, telegraphs 25% faster, new attack: "Desperation" (AoE ×2.5, 15 m radius, 20 s CD) |
| **Enrage duration** | Until boss dies |
| **Enrage warning** | 10 s before: "The boss is enraged!" chat message + sound |

**Soft enrage (party scaling):** if the party is too small for the floor, the boss gains +10% HP per missing player below the recommended party size (see §8).

---

## 5. Arena Rules

| Rule | Value |
|------|-------|
| **Arena entry** | Walking into the arena triggers a 5 s countdown, then the arena locks |
| **Arena lock** | No entry/exit during the fight; players who die are spectated |
| **Arena size** | 30 × 30 m (standard), 50 × 50 m (raid bosses) |
| **Arena floor** | Themed per floor (grass, stone, lava, ice) |
| **Respawn** | Dead players respawn at arena entrance after 30 s; can re-enter if the boss is below 90% HP |
| **Wipe condition** | All players dead → boss resets to full HP, arena unlocks |
| **Boss reset** | If all players leave the arena or die, boss heals 10%/s and resets to Phase 1 |
| **Min party** | 1 player (solo possible but not recommended below floor 25) |
| **Recommended party** | 2–3 players (floors 1–25), 3–5 players (floors 26–50), 4–6 players (floors 51–75), 5–8 players (floors 76–100) |

---

## 6. Boss Drops

| Drop | Rarity | Rate | Notes |
|------|--------|------|-------|
| **Boss weapon** | Purple (floor 1–25), Orange (26–75), Red (76–100) | 100% | 1 weapon, boss-themed, +0 |
| **Boss material** | — | 100% | 3–5 crafting materials (scale, core, essence) |
| **WP** | — | 100% | 10–50 WP (scales with floor) |
| **Protection Scroll** | — | 15% | Prevents upgrade break |
| **Reset Stone** | — | 5% | Full stat/SP respec |
| **XP** | — | 100% | See §1 table |
| **Coin** | — | 100% | 100–10,000 coins (scales with floor) |

**Boss weapon properties:** always the floor's tier, always a unique model, +10% damage vs. mobs of the same floor theme. Cannot be crafted — only dropped.

---

## 7. Raid Bosses (Floors 25, 50, 75, 100)

Raid bosses are **multi-phase, multi-mechanic** encounters designed for **8–16 players**.

| Property | Floor 25 | Floor 50 | Floor 75 | Floor 100 |
|----------|----------|----------|----------|----------|
| **Name** | Crystal Golem King | Molten Titan | Frost Emperor | Void Sovereign |
| **HP** | 97,650 × party scaling | 389,000 × party scaling | 877,000 × party scaling | 1,560,000 × party scaling |
| **Phases** | 5 | 5 | 6 | 7 |
| **Phase thresholds** | 100/80/60/40/20% | 100/80/60/40/20% | 100/83/66/50/33/16% | 100/85/70/55/40/25/10% |
| **Adds per phase** | 2 | 3 | 4 | 5 |
| **Ultimate per phase** | 1 | 2 | 3 | 4 |
| **Enrage timer** | 8 min | 10 min | 12 min | 15 min |
| **Arena size** | 40 × 40 m | 50 × 50 m | 60 × 60 m | 80 × 80 m |
| **Min party** | 6 | 8 | 10 | 12 |
| **Max party** | 12 | 16 | 16 | 20 |

**Raid boss mechanics (examples):**

| Mechanic | Description |
|----------|-------------|
| **Tank swap** | Boss applies a stacking debuff (+20% damage taken per stack, 10 s). Tanks must swap at 3 stacks. |
| **Positioning** | Boss alternates between frontal cone and rear cleave; DPS must move behind. |
| **Add priority** | Adds cast a heal on the boss (+5% HP if not killed in 10 s). |
| **Phase transition AoE** | At each phase threshold, a 15 m AoE deals ×2.5 damage; players must spread out. |
| **Ultimate chain** | Phase 3+: ultimates fire in sequence with 5 s gaps; each requires a different dodge direction. |
| **Debuff cleanse** | Boss applies a healing-reduction debuff; healers must cleanse within 5 s or the tank dies. |

**Raid boss drops:**

| Drop | Rate | Notes |
|------|------|-------|
| **Raid weapon** | 100% | 2 weapons, red rarity, +15% damage vs. bosses |
| **Raid material** | 100% | 10–20 materials for tier-8 crafting |
| **WP** | 100% | 100–300 WP |
| **Reset Stone** | 25% | Full respec |
| **Mount** | 10% | Floor 25: Crystal Stallion, Floor 50: Fire Drake, Floor 75: Frost Wyrm, Floor 100: Void Phoenix |
| **Title** | 100% | "Slayer of the Crystal Golem King" etc. |

---

## 8. Party-Size Difficulty Scaling

Boss stats scale with party size `n`:

```
HP(n)     = HP_base × (1 + 0.15 × (n − 1))
Damage(n) = Damage_base × (1 + 0.10 × (n − 1))
```

| Party size | HP multiplier | Damage multiplier |
|------------|---------------|-------------------|
| 1 | ×1.00 | ×1.00 |
| 2 | ×1.15 | ×1.10 |
| 3 | ×1.30 | ×1.20 |
| 4 | ×1.45 | ×1.30 |
| 5 | ×1.60 | ×1.40 |
| 8 | ×2.05 | ×1.70 |
| 12 | ×2.65 | ×2.10 |
| 16 | ×3.25 | ×2.50 |
| 20 | ×3.85 | ×2.90 |

**Soft enrage for small parties:** if `n < recommended party size`, boss gains +10% HP per missing player. Example: floor 50 boss (recommended 4) with 2 players: HP ×1.30 (party) ×1.20 (soft enrage) = ×1.56 total.

**XP is shared equally** among party members (no per-player reduction). Loot is rolled per player.

---

## 9. Boss Summary (Representative Floors)

| Floor | Boss name | HP | Damage | XP | Phases | Enrage | Arena |
|-------|-----------|----|--------|-----|--------|--------|-------|
| 5 | Goblin Warlord | 5,500 | 89 | 1,118 | 4 | 8 min | 30×30 |
| 10 | Forest Spider Queen | 19,950 | 503 | 6,325 | 4 | 8 min | 30×30 |
| 15 | Stone Golem | 47,000 | 1,000 | 13,000 | 4 | 8 min | 30×30 |
| 20 | Shadow Knight | 82,000 | 1,500 | 18,000 | 4 | 8 min | 30×30 |
| 25 | Crystal Golem King (raid) | 97,650 | 1,390 | 25,000 | 5 | 8 min | 40×40 |
| 50 | Molten Titan (raid) | 389,000 | 3,730 | 70,700 | 5 | 10 min | 50×50 |
| 75 | Frost Emperor (raid) | 877,000 | 7,150 | 138,000 | 6 | 12 min | 60×60 |
| 100 | Void Sovereign (raid) | 1,560,000 | 11,220 | 223,600 | 7 | 15 min | 80×80 |

---

## 10. Boss Fight Flow

1. **Enter arena** → 5 s countdown → arena locks.
2. **Phase 1:** boss uses basic attacks. Learn telegraphs.
3. **75% HP** → Phase 2: boss roars, summons adds, gains AoE attacks.
4. **50% HP** → Phase 3: boss summons more adds, applies debuffs.
5. **25% HP** → Phase 4: boss enrages-lite, gains ultimate attack.
6. **10% HP** → Enrage timer triggers (if not already): boss gains Desperation.
7. **Boss dies** → 10 s loot window → arena unlocks → drops distributed.
8. **Wipe** → boss resets to full HP → 30 s before re-entry allowed.

---

## 11. BossPhase Enum Reference

```java
public enum BossPhase {
    PHASE_1,   // 100% – 75% HP: basic attacks
    PHASE_2,   // 75%  – 50% HP: adds + AoE
    PHASE_3,   // 50%  – 25% HP: summon + debuff
    PHASE_4,   // 25%  – 0%  HP: enrage + ultimate
    PHASE_5,   // Raid bosses only: extended enrage
    PHASE_6,   // Raid bosses only: extended enrage
    PHASE_7    // Raid bosses only: extended enrage
}
```

**Phase thresholds (standard bosses):** 100%, 75%, 50%, 25%, 0%

**Phase thresholds (raid bosses):** Vary by floor — see §7 table.

---

## 12. BossAttackPattern Enum Reference

```java
public enum BossAttackPattern {
    MELEE_SWIPE,    // Basic melee attack, 4 m arc, 0.8 s wind-up
    CHARGE,         // Line charge, 15 m, knocks back 5 m
    AOE_SLAM,       // Ground circle AoE, 6 m radius, 2 s stun
    PROJECTILE,     // Ranged projectile spread, 20 m
    SUMMON,         // Spawns 2–3 floor mobs
    ULTIMATE,       // High-damage AoE, 10 m radius, Phase 4+
    DEBUFF,         // Applies −30% DEF or −50% heal, 8 m radius
    DESPERATION     // Enrage-only AoE, 15 m radius, ×2.5 damage
}
```

---

## 13. BossArena Enum Reference

```java
public enum BossArena {
    STANDARD,       // 30 × 30 m, floors 1–24
    RAID_SMALL,     // 40 × 40 m, floor 25
    RAID_MEDIUM,    // 50 × 50 m, floor 50
    RAID_LARGE,     // 60 × 60 m, floor 75
    RAID_FINAL      // 80 × 80 m, floor 100
}
```

**Arena rules per tier:**

| Tier | Size | Floors | Min party | Max party | Special rules |
|------|------|--------|-----------|-----------|---------------|
| **STANDARD** | 30×30 m | 1–24 | 1 | 6 | Basic rules, no raid mechanics |
| **RAID_SMALL** | 40×40 m | 25 | 6 | 12 | Tank swap, add priority |
| **RAID_MEDIUM** | 50×50 m | 50 | 8 | 16 | + Positioning, debuff cleanse |
| **RAID_LARGE** | 60×60 m | 75 | 10 | 16 | + Ultimate chain, phase transition AoE |
| **RAID_FINAL** | 80×80 m | 100 | 12 | 20 | + All mechanics, extended enrage |

---

## 14. Phase Transition Mechanics

| Step | Timing | Effect |
|------|--------|--------|
| **1. Threshold reached** | HP crosses phase boundary | Boss stops current attack |
| **2. Invulnerability** | 3 s | Boss takes no damage, glows white |
| **3. Roar** | 0.5 s into invuln | Audio cue + screen shake |
| **4. Clear debuffs** | 1.0 s into invuln | All debuffs on boss removed |
| **5. Kill adds** | 1.5 s into invuln | All summoned adds die instantly |
| **6. Phase begin** | 3.0 s | New phase attacks unlocked, boss vulnerable |

**Player guidance:** During the 3 s invulnerability window, players should reposition, heal, and prepare for the next phase's mechanics. Do not waste cooldowns on the invulnerable boss.
