# Aincrad — Boss System Design

**Scope:** Per-floor boss mechanics, phases, attack patterns, telegraphs, enrage timers, arena rules, drops, raid bosses (floors 50/75/100), and party-size difficulty scaling.

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

**Party scaling:** HP × `(1 + 0.15 × (n − 1))`, Damage × `(1 + 0.10 × (n − 1))` where `n` = party size (see §7).

---

## 2. Boss Structure

Every floor has **one boss**. Bosses have **3 phases** defined by HP thresholds:

| Phase | HP range | Behavior |
|-------|----------|----------|
| **Phase 1** | 100% – 70% | Basic attacks, slow telegraphs |
| **Phase 2** | 70% – 30% | Adds AoE attacks, faster telegraphs, summons 1–2 adds |
| **Phase 3** | 30% – 0% | Enrage-lite: +30% attack speed, new ultimate attack, desperate AI |

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
| **Ultimate** | Screen shake, boss glows gold | 2.5 s | ×3.0 | 10 m radius | Phase 3 only, 30 s CD |
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

**Soft enrage (party scaling):** if the party is too small for the floor, the boss gains +10% HP per missing player below the recommended party size (see §7).

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

## 7. Raid Bosses (Floors 50, 75, 100)

Raid bosses are **multi-phase, multi-mechanic** encounters designed for **8–16 players**.

| Property | Floor 50 | Floor 75 | Floor 100 |
|----------|----------|----------|----------|
| **Name** | Molten Titan | Frost Emperor | Void Sovereign |
| **HP** | 389,000 × party scaling | 877,000 × party scaling | 1,560,000 × party scaling |
| **Phases** | 3 | 4 | 5 |
| **Phase thresholds** | 100/70/30% | 100/75/50/25% | 100/80/60/40/20% |
| **Adds per phase** | 2 | 3 | 4 |
| **Ultimate per phase** | 1 | 2 | 3 |
| **Enrage timer** | 10 min | 12 min | 15 min |
| **Arena size** | 50 × 50 m | 60 × 60 m | 80 × 80 m |
| **Min party** | 8 | 10 | 12 |
| **Max party** | 16 | 16 | 20 |

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
| **Mount** | 10% | Floor 50: Fire Drake, Floor 75: Frost Wyrm, Floor 100: Void Phoenix |
| **Title** | 100% | "Slayer of the Molten Titan" etc. |

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
| 1 | Goblin Warlord | 500 | 20 | 200 | 1 | 8 min | 30×30 |
| 10 | Forest Spider Queen | 19,950 | 503 | 6,325 | 2 | 8 min | 30×30 |
| 25 | Crystal Golem King | 97,650 | 1,390 | 25,000 | 3 | 8 min | 30×30 |
| 50 | Molten Titan (raid) | 389,000 | 3,730 | 70,700 | 3 | 10 min | 50×50 |
| 75 | Frost Emperor (raid) | 877,000 | 7,150 | 138,000 | 4 | 12 min | 60×60 |
| 100 | Void Sovereign (raid) | 1,560,000 | 11,220 | 223,600 | 5 | 15 min | 80×80 |

---

## 10. Boss Fight Flow

1. **Enter arena** → 5 s countdown → arena locks.
2. **Phase 1:** boss uses basic attacks. Learn telegraphs.
3. **70% HP** → Phase 2: boss roars, summons adds, gains AoE attacks.
4. **30% HP** → Phase 3: boss enrages-lite, gains ultimate attack.
5. **10% HP** → Enrage timer triggers (if not already): boss gains Desperation.
6. **Boss dies** → 10 s loot window → arena unlocks → drops distributed.
7. **Wipe** → boss resets to full HP → 30 s before re-entry allowed.
