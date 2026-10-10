# Aincrad — Weapon System Design

**Scope:** Weapon types, damage tiers, rarity colors, crafting/upgrading, durability, and weapon-specific skill modifiers.

---

## 1. Weapon Types

| Type | Weight (kg) | Base ATK | Attack speed | Range | Sword Skill chain | Notes |
|------|------------|----------|--------------|-------|-------------------|-------|
| One-Handed Sword | 4 | 12 | 1.6/s | 2.5 m | Horizontal → Vertical → Sharp Turn → Crescent Arc | Balanced; can shield |
| Two-Handed Sword | 8 | 22 | 0.9/s | 3.0 m | Cleave → Smash → Giant's Swing → Meteor Break | Highest burst; slow |
| Rapier | 3 | 10 | 1.8/s | 2.5 m | Thrust → Pierce → Vorpal Needle → Starburst Stream | Fastest chain; low per-hit |
| Dagger | 2 | 8 | 2.2/s | 2.0 m | Stealth Strike → Backstab → Assassinate → Death From Above | Highest crit synergy |
| Axe | 7 | 20 | 1.0/s | 2.8 m | Wild Swing → Crushing Blow → Earth Splitter → Executioner | High raw dmg; armor pierce |
| Spear | 5 | 14 | 1.4/s | 3.5 m | Long Strike → Sweep → Cyclone → Dragon's Descent | Longest reach |
| Bow | 3 | 11 | 1.2/s | 30 m | Charged Shot → Multi-Shot → Rain of Arrows → Piercing Light | Ranged; uses arrows |
| Katana | 4 | 13 | 1.7/s | 2.5 m | Draw Slash → Thousand Leaves → Razor Wind → Blade Dance | Fastest chain window |

**Damage formula (melee):**

```
Damage = weaponATK × (1 + 0.02 × STR) × (1 + 0.015 × STR) [on finisher] × rarityMultiplier
```

**Damage formula (bow):**

```
Damage = bowATK × (1 + 0.025 × DEX) × rarityMultiplier × (1 + 0.1 × chargeLevel)
```

---

## 2. Damage Tiers

Weapons come in **8 tiers**, each tied to a floor range. Tier determines base ATK and material.

| Tier | Floor range | Base ATK range | Primary material | Craft cost (iron) |
|------|-------------|----------------|------------------|-------------------|
| 1 | 1–10 | 8–14 | Iron | 20 |
| 2 | 11–20 | 15–22 | Steel | 45 |
| 3 | 21–30 | 23–32 | Mithril | 90 |
| 4 | 31–40 | 33–45 | Orichalcum | 160 |
| 5 | 41–50 | 46–60 | Adamantite | 280 |
| 6 | 51–65 | 61–78 | Dragonbone | 450 |
| 7 | 66–80 | 79–100 | Crystal | 700 |
| 8 | 81–100 | 101–130 | Aincrad Alloy | 1000 |

**Tier scaling:** each tier ≈ +35% base ATK over the previous. A tier-8 weapon at +10 with orange rarity hits ~×4.5 of a tier-1 gray weapon.

---

## 3. Rarity System (SAO-Style Colors)

| Rarity | Color | Multiplier | Drop source | Upgrade success bonus |
|--------|-------|------------|-------------|----------------------|
| Common | Gray | ×1.00 | Crafting, field mobs | — |
| Uncommon | Green | ×1.15 | Field mobs (15%), crafting (10%) | +5% |
| Rare | Blue | ×1.35 | Elites (25%), crafting (5%) | +10% |
| Epic | Purple | ×1.60 | Bosses (30%), crafting (1%) | +15% |
| Legendary | Orange | ×2.00 | Bosses (8%), raid bosses (25%) | +20% |
| Unique/Relic | Red | ×2.50 | Raid bosses (5%), special quests | +25% |

**Rarity affects:** base ATK multiplier, upgrade success rate, and drop table weighting. Rarity is rolled at craft/drop time; it cannot be changed after.

---

## 4. Crafting

Weapons are crafted at a **Blacksmith NPC** (found in each floor's starting town) or at a player-placed **Anvil**.

**Recipe structure:**

```
Weapon = Base material × tier quantity + Binding material + Rarity catalyst (optional)
```

**Example — Tier 3 One-Handed Sword (Mithril):**
- 12 Mithril Ingots
- 4 Steel Ingots (binding)
- 1 Sharpness Crystal (rarity catalyst, 5% → blue)
- Craft time: 30 s at anvil, 10 s at blacksmith

**Crafting success rate:** 90% base, +5% per tier of the anvil, +10% if the blacksmith NPC does it. Failure consumes half the materials.

**Rarity catalysts:** optional materials that shift the rarity roll. Without a catalyst: 70% gray / 20% green / 8% blue / 2% purple. With a catalyst: 30% green / 40% blue / 20% purple / 8% orange / 2% red.

---

## 5. Upgrading

Weapons can be upgraded **+1 to +10** at an anvil using materials and **Weapon Points (WP)**.

| Upgrade | WP cost | Material cost | Success rate | Failure penalty |
|---------|---------|---------------|--------------|-----------------|
| +1 → +2 | 10 | 2 ingots | 95% | −1 WP |
| +2 → +3 | 20 | 3 ingots | 90% | −2 WP |
| +3 → +4 | 35 | 4 ingots | 85% | −3 WP |
| +4 → +5 | 50 | 5 ingots | 80% | −4 WP |
| +5 → +6 | 70 | 6 ingots | 70% | −5 WP, 10% break |
| +6 → +7 | 100 | 8 ingots | 60% | −7 WP, 20% break |
| +7 → +8 | 140 | 10 ingots | 50% | −10 WP, 35% break |
| +8 → +9 | 200 | 12 ingots | 40% | −15 WP, 50% break |
| +9 → +10 | 300 | 15 ingots | 30% | −20 WP, 75% break |

**WP sources:** dismantling weapons (gray 1 WP, green 3, blue 8, purple 20, orange 50, red 120), boss drops (10–50 WP), daily quests (5 WP).

**Protection items:**
- **Protection Scroll:** prevents break on failure (consumed). Drop: elites 5%, bosses 15%.
- **Blessing Stone:** +10% success rate for one attempt (consumed). Craft: 5 WP + 3 crystals.

**Upgrade effect:** each `+` adds **+5% base ATK** (additive). A +10 weapon is ×1.5 base ATK. This stacks multiplicatively with rarity.

---

## 6. Durability

Every weapon has **durability**. It decreases on hit (melee) or shot (bow).

| Material | Durability | Durability loss per use |
|----------|-----------|------------------------|
| Iron | 500 | 1 |
| Steel | 700 | 1 |
| Mithril | 1000 | 1 |
| Orichalcum | 1400 | 1 |
| Adamantite | 1800 | 1 |
| Dragonbone | 2200 | 1 |
| Crystal | 2800 | 1 |
| Aincrad Alloy | 3500 | 1 |

**Durability states:**
- **> 0:** normal function.
- **0:** weapon breaks — ATK drops to 20%, Sword Skills disabled. Must be repaired.
- **Repair:** at anvil with 1 ingot per 100 durability, or at blacksmith for 1 WP per 50 durability. Full repair restores to max.
- **Death penalty:** −5 durability on equipped weapon per death (see STATS.md §3).

---

## 7. Weapon-Specific Skill Modifiers

Each weapon type modifies the Sword Skill system:

| Weapon | Chain window | MP cost on chain hits | Finisher effect | Special |
|--------|-------------|----------------------|-----------------|---------|
| One-Handed Sword | normal | normal | 0.8 s stun | Can equip shield (+15% DEF) |
| Two-Handed Sword | normal | normal | 1.2 s stun, knockback 3 m | +10% damage vs. large mobs |
| Rapier | normal | −15% | 0.5 s stun, +5% crit on finisher | Thrust hits pierce 1 target |
| Dagger | normal | normal | 0.5 s stun, +10% crit on finisher | Backstab from behind: +50% finisher dmg |
| Axe | normal | normal | 1.0 s stun, ignores 20% DEF | +15% damage vs. armored mobs |
| Spear | normal | normal | 0.8 s stun, pulls target 2 m | Hits all targets in 3.5 m line |
| Bow | normal | normal | 0.5 s stun, pierces 3 targets | Charge level 0–3: +0/10/25/50% dmg |
| Katana | **+20%** | normal | 0.8 s stun, +10% crit on finisher | Draw Slash (hit 1) has 0.3 s wind-up |

---

## 8. Weapon Drops

| Source | Rarity weighting | Notes |
|--------|-----------------|-------|
| Field mobs | 70% gray / 25% green / 5% blue | 2% drop chance |
| Elites | 20% green / 45% blue / 25% purple / 10% orange | 15% drop chance |
| Bosses | 30% blue / 40% purple / 22% orange / 8% red | 100% drop, 1 weapon |
| Raid bosses | 25% purple / 45% orange / 30% red | 100% drop, 2 weapons |
| Crafting | see §4 | Player-chosen type |

Dropped weapons are always **+0** and rolled within the floor's tier range.
