# Aincrad — Stats System Design

**Target platform:** Minecraft Java 26.3 · Fabric client/server + Paper/Purpur backend
**Scope:** Player stat system — attributes, XP curve, leveling, stat allocation, regen, weight.

---

## 1. Core Attributes

Five primary stats. All start at **10** at character creation (level 1).

| Stat | Full name | Primary role |
|------|-----------|--------------|
| **STR** | Strength | Melee damage, carry weight |
| **AGI** | Agility | Attack speed, evasion, move speed, chain windows |
| **VIT** | Vitality | Max HP, HP regen, weight, knockback resist |
| **DEX** | Dexterity | Crit rate, crit damage, bow damage |
| **INT** | Intelligence | Max MP, MP regen, skill power, cooldown reduction |

Stat points come from leveling only (see §3). Gear can grant temporary bonuses on top of allocated points.

---

## 2. Stat Effects & Formulas

### 2.1 STR — Strength

| Effect | Formula | Cap |
|--------|---------|-----|
| Meley damage multiplier | `1 + 0.02 × STR` (i.e. +2% per point) | none |
| Sword Skill damage | `weaponATK × skillMultiplier × (1 + 0.015 × STR)` | none |
| Carry weight bonus | `+3 kg per STR point` | none |

At STR 100: melee damage ×3.00, sword skill bonus ×2.50, +300 kg carry.

### 2.2 AGI — Agility

| Effect | Formula | Cap |
|--------|---------|-----|
| Attack speed | `baseAttackSpeed × (1 + 0.005 × AGI)` | +40% (AGI 80) |
| Move speed | `4.3 m/s × (1 + 0.0015 × AGI)` | +20% (AGI ~133) |
| Evasion (dodge chance) | `AGI / (AGI + 150) × 0.50` | 35% at AGI 105, hard cap 40% |
| Sword Skill chain window | `2.0 s + 0.02 s × AGI` | 4.0 s (AGI 100) |

Evasion formula is hyperbolic so stacking AGI has diminishing returns:
AGI 25 → 7.1%, AGI 50 → 12.5%, AGI 100 → 20.0%, AGI 200 → 28.6%.

### 2.3 VIT — Vitality

| Effect | Formula | Cap |
|--------|---------|-----|
| Max HP | `100 + 10 × VIT` | none |
| HP regen (out of combat) | `1 + 0.05 × VIT` HP/s (starts 8 s after last damage) | none |
| HP regen (in combat) | `0.2 × (1 + 0.05 × VIT)` HP/s | none |
| Carry weight bonus | `+2 kg per VIT point` | none |
| Knockback resistance | `min(0.80, 0.006 × VIT)` | 80% at VIT ~133 |

At VIT 100: 1100 max HP, 6 HP/s out-of-combat regen.

### 2.4 DEX — Dexterity

| Effect | Formula | Cap |
|--------|---------|-----|
| Crit rate | `0.03 + 0.0015 × DEX` | 30% (DEX 180) |
| Crit damage | `1.50 + 0.01 × DEX` (i.e. +1% per point, base 150%) | none |
| Bow damage | `bowBase × (1 + 0.025 × DEX)` | none |
| Skill accuracy (vs. evasion) | `hit chance = 1 + (DEX − targetAGI) × 0.01` | 98% floor, 100% vs. stationary |

At DEX 100: 18% crit, 250% crit damage.

### 2.5 INT — Intelligence

| Effect | Formula | Cap |
|--------|---------|-----|
| Max MP | `50 + 8 × INT` | none |
| MP regen | `1 + 0.04 × INT` MP/s | none |
| Skill power (buffs/debuffs) | `skillBase × (1 + 0.02 × INT)` | none |
| Cooldown reduction | `1 − min(0.25, 0.0025 × INT)` | 25% at INT 100 |

At INT 100: 850 max MP, 5 MP/s regen, skills +100% power, CDs −25%.

---

## 3. XP Curve & Leveling

**Level cap: 100** (one level per floor of Aincrad).

XP required to go from level `L` to `L+1`:

```
XP(L→L+1) = round(50 × L^1.8)
```

Cumulative XP to reach level `L` from level 1:

```
TotalXP(L) = round( Σ_{i=1}^{L-1} 50 × i^1.8 )  ≈  round( 50 × L^2.8 / 2.8 )
```

| Level | XP to next | Cumulative XP |
|-------|-----------|---------------|
| 1 → 2 | 50 | 0 |
| 5 → 6 | 747 | ~2,000 |
| 10 → 11 | 3,174 | ~16,000 |
| 25 → 26 | 21,380 | ~180,000 |
| 50 → 51 | 75,357 | ~1.4 M |
| 75 → 76 | 153,600 | ~4.3 M |
| 99 → 100 | 225,360 | ~8.9 M |

**XP sources:** field mobs (scaled per floor, see MOBS.md), boss kills (large flat bonus + floor multiplier), dungeon quests. Party XP is shared with a 10% bonus per additional member, capped at +40% (5 players).

**Death penalty:** lose 5% of current level XP (never drop below level start), 30 s respawn lockout, durability loss on equipped weapon (−5 durability).

---

## 4. Stat Point Allocation

- **+5 stat points per level** → 500 total at level 100.
- Players allocate via the **Menu → Skills & Stats** window (press `N` by default).
- Reallocation: **50% refund** of spent points per respec, full respec only via a rare "Reset Stone" drop (boss 5% drop, quest reward once per 10 floors).
- **Recommended builds** (level 100, 500 points):
  - DPS swordsman: 200 STR / 200 AGI / 50 VIT / 50 DEX / 0 INT
  - Tank: 100 STR / 50 AGI / 250 VIT / 0 DEX / 100 INT
  - Rapier duelist: 150 STR / 200 AGI / 50 VIT / 100 DEX / 0 INT
  - Archer: 100 STR / 150 AGI / 50 VIT / 200 DEX / 0 INT

---

## 5. Weight System

Every item has a **weight in kg**. Weapons, armor, and stacks of materials all count.

```
Carry Limit (kg) = 20 + 3 × STR + 2 × VIT
```

At STR 100 / VIT 50: 20 + 300 + 100 = **420 kg**.

| Load state | Condition | Penalty |
|------------|-----------|---------|
| Normal | ≤ 70% of limit | none |
| Heavy | 70–100% | −10% move speed, evasion halved, Sprint disabled |
| Overweight | > 100% | Cannot sprint, **Sword Skills disabled**, jump height −50%, move speed −30% |

Overweight is checked continuously; dropping items via the menu is the only escape. Weight is shown in the HUD as a blue bar next to HP/MP.

**Representative weights:** iron sword 4 kg, two-hander 8 kg, full iron armor 22 kg, boss materials 0.2–5 kg each.

---

## 6. Derived Totals (Level 100 Reference)

| Build | HP | MP | Melee dmg mult | Attack speed | Evasion | Crit |
|-------|----|----|----------------|--------------|---------|------|
| DPS (STR/AGI) | 1600 | 450 | ×6.00 | ×2.00 | 20.0% | 18% |
| Tank (VIT/INT) | 3600 | 850 | ×4.00 | ×1.25 | 6.3% | 3% |
| Balanced | 2100 | 650 | ×4.50 | ×1.60 | 12.5% | 10% |

*(Values from formulas above; DEX/INT allocated as listed in §4.)*
