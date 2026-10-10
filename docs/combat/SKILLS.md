# Aincrad — Skill System Design

**Scope:** All player skills — Sword Skills (combo chains), Active skills (cooldown), and Passive skills. Includes unlock requirements, SP economy, and skill leveling.

---

## 1. Skill Categories

| Category | Trigger | Resource | Count |
|----------|---------|----------|-------|
| **Sword Skill** | Weapon-swing sequence (combo) | HP% of skill cost + chain window | 4 per weapon type (28 total) |
| **Active Skill** | Hotkey | MP + cooldown | 16 total |
| **Passive Skill** | Always on / conditional | — | 16 total |

---

## 2. Sword Skills (Sequential Auto-Combos)

A Sword Skill is a **3-hit chain ending in a finisher**. The player swings normally; each swing within the chain window advances the combo. The finisher fires automatically if the third hit lands before the window expires.

**Core rules:**
- **Chain window:** `2.0 s + 0.02 s × AGI` (2.2 s at AGI 10 → 4.0 s at AGI 100).
- Each swing consumes the weapon's normal attack; the combo's damage multiplier applies **on the finisher only**.
- The chain can be **canceled** by the player (sidestep/dodge) — no penalty.
- Missing the window resets the combo to hit 1.
- Overweight (>100% carry) disables Sword Skills entirely (see STATS.md §5).

### 2.1 Sword Skill Chains by Weapon Type

| Weapon | Hit 1 | Hit 2 | Hit 3 | Finisher | Finisher mult |
|--------|-------|-------|-------|----------|---------------|
| One-Handed Sword | Horizontal | Vertical | Sharp Turn | **Crescent Arc** | ×2.8 |
| Two-Handed Sword | Cleave | Smash | Giant's Swing | **Meteor Break** | ×3.4 |
| Rapier | Thrust | Pierce | Vorpal Needle | **Starburst Stream** | ×3.0 |
| Dagger | Stealth Strike | Backstab | Assassinate | **Death From Above** | ×3.2 |
| Axe | Wild Swing | Crushing Blow | Earth Splitter | **Executioner** | ×3.6 |
| Spear | Long Strike | Sweep | Cyclone | **Dragon's Descent** | ×2.9 |
| Katana | Draw Slash | Thousand Leaves | Razor Wind | **Blade Dance** | ×3.1 |
| Bow | Charged Shot | Multi-Shot | Rain of Arrows | **Piercing Light** | ×2.7 |

**Finisher damage formula:**

```
FinisherDamage = weaponATK × finisherMult × (1 + 0.015 × STR) × (1 + 0.02 × weaponRarityBonus)
```

**Chain speed bonus:** each weapon type has a chain-speed modifier (see WEAPONS.md §6). Katana +20% window, Rapier −15% MP cost on thrust hits, etc.

### 2.2 Sword Skill Properties

| Property | Value |
|----------|-------|
| HP cost per chain hit | 2% of max HP (finisher included) |
| MP cost | 0 (Sword Skills are HP-driven) |
| Cooldown | None — limited only by chain window and HP |
| Interruptibility | Staggered on hit ≥ 15% of max HP; finisher causes 0.8 s stun |
| AoE | Finishers hit all enemies within 2.5 m arc (120°) |

---

## 3. Active Skills (Cooldown-Based)

Triggered by hotkeys `1–8` (configurable). Cost MP and trigger a cooldown.

| # | Skill | Type | MP cost | Cooldown | Effect |
|---|-------|------|---------|----------|--------|
| 1 | **Howl** | Buff (self) | 40 | 60 s | +20% ATK, +15% move speed, 10 s. +2% per skill level. |
| 2 | **Armor Break** | Debuff | 50 | 45 s | Target −30% DEF for 8 s. −2% DEF per skill level. |
| 3 | **Guard** | Buff (self) | 30 | 30 s | −50% damage taken, 5 s. +0.5 s per level. |
| 4 | **First Aid** | Heal | 60 | 90 s | Restore 35% max HP over 5 s. +3% per level. |
| 5 | **Sprint Burst** | Mobility | 25 | 20 s | +60% move speed, 3 s. +0.3 s per level. |
| 6 | **Counter Stance** | Buff (self) | 45 | 40 s | Next 3 attacks auto-counter for 150% damage, 6 s. |
| 7 | **Heal Aura** | Heal (party) | 120 | 120 s | Restore 20% max HP to party within 10 m, 8 s. |
| 8 | **Power Strike** | Nuke | 80 | 60 s | Single-target burst: `weaponATK × 2.5 × (1 + 0.02 × INT)`. |

**Cooldown reduction:** `CD × (1 − min(0.25, 0.0025 × INT))` (see STATS.md §2.5).

---

## 4. Passive Skills

Always active once learned. Some are conditional.

| # | Skill | Effect | Condition |
|---|-------|--------|-----------|
| 1 | **HP Boost** | +15% max HP | always |
| 2 | **Evade+** | +8% evasion | always |
| 3 | **Counter** | 10% chance to auto-counter melee for 120% damage | when hit |
| 4 | **Weight Limit Up** | +25% carry limit | always |
| 5 | **Regen Boost** | +30% HP/MP regen | always |
| 6 | **Skill Chain Extension** | +0.5 s chain window | always |
| 7 | **Critical Eye** | +5% crit rate | always |
| 8 | **MP Saver** | −20% MP cost | always |
| 9 | **Berserk** | +25% ATK when HP < 30% | HP < 30% |
| 10 | **Second Wind** | Once per combat, survive a lethal hit at 1 HP (30 s CD) | lethal hit |
| 11 | **Swift** | +10% move speed | always |
| 12 | **Iron Wall** | −15% damage taken | always |
| 13 | **Focus** | +10% skill power | always |
| 14 | **Adrenaline** | +15% attack speed when HP < 50% | HP < 50% |
| 15 | **Loot Hunter** | +10% drop rate | always |
| 16 | **Boss Slayer** | +10% damage vs. bosses/elites | vs. boss/elite |

---

## 5. Unlock Requirements

Skills unlock via **level + SP + quest**. Quest = floor boss kill or NPC quest.

| Skill | Level req. | SP cost | Quest req. |
|-------|-----------|---------|------------|
| Sword Skills (all 4 per weapon) | 1 / 10 / 20 / 30 | 1 each | none |
| Howl | 10 | 2 | Floor 1 boss |
| Armor Break | 15 | 2 | Floor 5 boss |
| Guard | 5 | 1 | none |
| First Aid | 20 | 2 | Floor 3 NPC quest |
| Sprint Burst | 8 | 1 | none |
| Counter Stance | 25 | 3 | Floor 10 boss |
| Heal Aura | 40 | 4 | Floor 25 boss |
| Power Strike | 30 | 3 | Floor 15 boss |
| HP Boost | 5 | 1 | none |
| Evade+ | 12 | 2 | Floor 5 boss |
| Counter | 35 | 3 | Floor 20 boss |
| Weight Limit Up | 10 | 1 | none |
| Regen Boost | 18 | 2 | Floor 8 NPC quest |
| Skill Chain Extension | 45 | 4 | Floor 30 boss |
| Critical Eye | 22 | 2 | Floor 12 boss |
| MP Saver | 15 | 1 | none |
| Berserk | 50 | 4 | Floor 40 boss |
| Second Wind | 60 | 5 | Floor 50 boss |
| Swift | 8 | 1 | none |
| Iron Wall | 28 | 2 | Floor 15 boss |
| Focus | 33 | 2 | Floor 18 NPC quest |
| Adrenrage | 42 | 3 | Floor 28 boss |
| Loot Hunter | 14 | 1 | none |
| Boss Slayer | 55 | 4 | Floor 45 boss |

---

## 6. Skill Point (SP) Economy

- **SP is separate from stat points.** SP buys/levels skills; stat points buy attributes.
- **SP gain:** +2 SP at character creation, **+3 SP per level** → 299 SP at level 100.
- **Total SP cost to unlock everything:** ~260 SP (all skills at level 1). Remaining ~39 SP go to skill leveling.
- **SP respec:** same rules as stat respec (50% refund, Reset Stone for full).

---

## 7. Skill Leveling

Skills level by **use**, not by spending SP.

- Each skill has its own **use counter**. Level up requires `uses = 20 × currentLevel` (level 1→2: 20 uses, 2→3: 40 uses, ...).
- **Max skill level: 5.**
- **Per-level effect:** +2% potency for buffs/debuffs/heals, +0.1 s for durations, +3% damage for damage skills, −2% cooldown for actives.
- Sword Skills level per weapon type (using any chain with that weapon levels all 4 of its skills).
- Use counter persists through respecs.

---

## 8. Skill UI & Controls

- **Sword Skills:** automatic on swing sequence; combo progress shown as 3 pips above the hotbar.
- **Active Skills:** hotkeys `1–8`, cooldown sweep + MP cost on the HUD.
- **Passive Skills:** listed in Menu → Skills & Stats with toggle to inspect.
- **Skill menu:** press `N` — shows SP, skill levels, use progress, and unlock requirements.
