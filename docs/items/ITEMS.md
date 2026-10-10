# Aincrad Items & Inventory Design

## Overview

Aincrad's item system extends Minecraft's vanilla items with SAO-inspired
rarities, enhancement tiers, and a weight-based carry limit. The system
is fully data-driven (JSON item definitions) so that new items can be
added without code changes.

All custom items implement a common `AincradItem` interface (Fabric) /
`AincradItemStack` (Paper/Purpur backend) with NBT-persisted metadata for
enhancement tier, affixes, and bound status.

---

## 1. Item Rarities

Six rarities, each with a distinct name color and stat multiplier.

| Rarity | Color | Stat Multiplier | Drop Rate (Standard) | Drop Rate (Boss) |
|---|---|---|---|---|
| **Common** | White | 1.0× | 60% | — |
| **Uncommon** | Green | 1.3× | 25% | 15% |
| **Rare** | Blue | 1.7× | 10% | 30% |
| **Epic** | Purple | 2.2× | 4% | 30% |
| **Legendary** | Gold | 3.0× | 1% | 20% |
| **Mythic** | Red / Rainbow | 4.0× | 0.1% | 5% |

**Rarity display:**
- Item name colored in lore/tooltip.
- Rarity-colored glow effect on item frame.
- `★` suffix on enhanced items (e.g., `Crystal Sword +7 ★★★`).

**Rarity weights are per-loot-table**, not global. A Floor 1 mob won't drop
Legendary gear; a Floor 10 boss has a dedicated loot table with Epic/Legendary
weights.

---

## 2. Equipment Slots

The equipment menu (see `HUD.md` §8.1) has 11 slots, mapped to vanilla
and custom equipment:

| Slot | Vanilla Equivalent | Notes |
|---|---|---|
| **Helmet** | Head | Standard. |
| **Chestplate** | Chest | Standard. |
| **Leggings** | Legs | Standard. |
| **Boots** | Feet | Standard. |
| **Main Hand** | Main hand | Primary weapon or tool. |
| **Off Hand** | Off hand | Shield or secondary weapon. |
| **Ring 1** | — (custom) | Accessory. |
| **Ring 2** | — (custom) | Accessory. |
| **Necklace** | — (custom) | Accessory. |
| **Earring** | — (custom) | Accessory. |
| **Cape / Back** | — (custom) | Cosmetic + minor stats. |

**Total equipment slots: 11**

### 2.1 Slot Rules

- Only one item per slot (no stacking in equipment).
- Rings, necklaces, and earrings are **not** vanilla items — they are
  custom items that only equip in their designated slot.
- The **Cape** slot is cosmetic but can grant a minor passive bonus
  (e.g., +2% movement speed).
- **Shields** go in Off Hand; dual-wielding is possible if the weapon
  has the `Dual Wield` trait (sword-class only).

---

## 3. Item Categories

| Category | Description | Example |
|---|---|---|
| **Weapon** | Deals damage; has attack speed and damage | Sword, Dagger, Bow, Staff |
| **Shield** | Blocks damage; has block chance and durability | Kite Shield, Tower Shield |
| **Armor** | Reduces damage taken; has defense and weight | Plate Mail, Chainmail |
| **Accessory** | Grants passive bonuses or affixes | Ring of Strength, Necklace of Vitality |
| **Material** | Crafting component; not equippable | Crystal Shard, Iron Ingot, Dragon Scale |
| **Consumable** | Single-use; grants buffs or healing | Health Potion, Mana Elixir, Buff Food |
| **Quest** | Quest item; not sellable or droppable | Letter from Heathcliff |
| **Cosmetic** | Visual only; no stats | Titles, dyes, particles |

---

## 4. Crafting Materials

Materials come from three sources: mob drops, boss drops, and gathering
(mining, fishing, herbalism).

### 4.1 Mob Drop Materials

| Material | Source Mob | Drop Rate | Used For |
|---|---|---|---|
| Crystal Shard | Crystal Slime | 40% | Enhancement stones, basic recipes |
| Iron Ore Chunk | Iron Golem | 25% | Armor crafting |
| Beast Hide | Dire Wolf | 30% | Leather armor, accessories |
| Venom Sac | Giant Spider | 20% | Poison weapons, potions |
| Scale Fragment | Drake | 15% | Dragon-scale armor |
| Shadow Essence | Shadow Stalker | 10% | Epic+ enhancement |

### 4.2 Boss Drop Materials

| Material | Boss | Drop Rate | Used For |
|---|---|---|---|
| Dragon's Heart | Floor 1 Boss | 100% | Legendary weapon crafting |
| Nephrite Core | Floor 5 Boss | 100% | Nephrite tier enhancement |
| Gleam Eyes Scale | Floor 15 Boss | 100% | Legendary armor crafting |
| The Gleam Eyes Fang | Floor 15 Boss | 50% | Mythic dagger crafting |
| Phantom Thread | Phantom Weaver | 30% | Cape crafting |

### 4.3 Gathering Materials

| Material | Source | Tool | Yield |
|---|---|---|---|
| Iron Ingot | Iron ore vein | Iron Pickaxe+ | 1–3 |
| Crystal Cluster | Crystal node | Diamond Pickaxe+ | 1–2 |
| Healing Herb | Herbalism nodes | Any hoe | 1–3 |
| Ancient Wood | Ancient trees | Iron Axe+ | 2–4 |

**Note:** Gathering nodes are custom blocks placed in the world. They
respawn after 15 minutes and are harvested with the right tool type.

---

## 5. Item Enhancement & Upgrade System

### 5.1 Enhancement Tiers

Items can be enhanced from `+0` to `+15`. Each tier increases base stats
by a percentage.

| Tier | Stat Bonus | Success Rate | Cost (Col) | Material Cost |
|---|---|---|---|---|
| +1 | +5% | 100% | 50c | 1 Enhancement Stone |
| +2 | +10% | 100% | 100c | 1 Enhancement Stone |
| +3 | +15% | 95% | 200c | 2 Enhancement Stones |
| +4 | +20% | 90% | 400c | 2 Enhancement Stones |
| +5 | +25% | 80% | 800c | 3 Enhancement Stones |
| +6 | +30% | 70% | 1,600c | 3 Enhancement Stones |
| +7 | +35% | 60% | 3,200c | 4 Enhancement Stones |
| +8 | +40% | 50% | 6,400c | 4 Enhancement Stones |
| +9 | +45% | 40% | 12,800c | 5 Enhancement Stones |
| +10 | +50% | 30% | 25,600c | 5 Enhancement Stones + 1 Nephrite Core |
| +11 | +60% | 25% | 51,200c | 6 Enhancement Stones + 1 Nephrite Core |
| +12 | +70% | 20% | 102,400c | 6 Enhancement Stones + 2 Nephrite Cores |
| +13 | +80% | 15% | 204,800c | 7 Enhancement Stones + 2 Nephrite Cores |
| +14 | +90% | 10% | 409,600c | 8 Enhancement Stones + 3 Nephrite Cores |
| +15 | +100% | 5% | 819,200c | 10 Enhancement Stones + 5 Nephrite Cores |

### 5.2 Failure Behavior

| Tier Range | On Failure |
|---|---|
| +1 to +5 | Nothing happens — item stays at current tier. |
| +6 to +9 | Item degrades by 1 tier (e.g., +7 → +6). |
| +10 to +15 | Item **breaks** — destroyed, cannot be recovered. |

### 5.3 Safety Mechanics

- **Enhancement Shield** (consumable): Prevents tier loss on failure for one attempt.
- **Blessing of the Forge** (consumable): +10% success rate for one attempt.
- **Forge Master NPC**: Offers a guaranteed success at +10+ for 3× the material cost.

### 5.4 Affix Reroll (Accessories Only)

Accessories (rings, necklaces, earrings) have 1–3 random affixes. Players can
reroll affixes for 500c + 1 Phantom Thread. Affixes are:

| Affix | Range |
|---|---|
| Strength | +1 to +8 |
| Vitality | +1 to +8 |
| Dexterity | +1 to +8 |
| Intelligence | +1 to +8 |
| Critical Rate | +1% to +5% |
| Critical Damage | +5% to +20% |
| HP Regen | +1 to +5 per second |
| MP Regen | +1 to +3 per second |

---

## 6. Consumables

### 6.1 Potions

| Potion | Effect | Duration | Cooldown | Cost |
|---|---|---|---|---|
| Health Potion I | Heal 25% max HP | Instant | 30 s | 50c |
| Health Potion II | Heal 50% max HP | Instant | 60 s | 200c |
| Health Potion III | Heal 75% max HP | Instant | 120 s | 500c |
| Mana Potion I | Restore 30% max MP | Instant | 30 s | 50c |
| Mana Potion II | Restore 60% max MP | Instant | 60 s | 200c |
| Mana Potion III | Restore 100% max MP | Instant | 120 s | 500c |
| Stamina Elixir | Instantly refill ST | Instant | 60 s | 100c |
| Elixir of Fire | +15% fire damage | 10 min | 5 min | 300c |
| Elixir of Sharpness | +10% attack | 10 min | 5 min | 300c |
| Elixir of Iron Skin | +20% defense | 10 min | 5 min | 300c |
| Elixir of Swiftness | +15% move speed | 10 min | 5 min | 300c |

**Cooldowns are global per potion type** — you can't chug 10 Health Potions
instantly. The cooldown is displayed on the hotbar slot.

### 6.2 Food Buffs

| Food | Buff | Duration | Cost |
|---|---|---|---|
| Bread of Vitality | +5% max HP | 15 min | 50c |
| Roast Beast | +10% attack | 15 min | 100c |
| Crystal Cake | +10% MP regen | 15 min | 100c |
| Sage's Stew | +5% all stats | 30 min | 250c |
| Dragon Feast | +15% all stats | 1 hour | 1,000c |

Food buffs **stack** with potion buffs but not with other food buffs (eating
a new food replaces the old buff).

---

## 7. Item Drop Rates

### 7.1 Standard Mob Drops

| Drop | Rate | Notes |
|---|---|---|
| Col (virtual) | 100% | See `ECONOMY.md` |
| Material (common) | 25% | Weighted table |
| Material (uncommon) | 8% | Weighted table |
| Equipment (Common–Uncommon) | 5% | Weighted by floor tier |
| Enhancement Stone | 10% | Common currency for enhancement |
| Consumable (potion/food) | 15% | Weighted table |

### 7.2 Elite Mob Drops (5× spawn rate of standard)

| Drop | Rate |
|---|---|
| Equipment (Uncommon–Rare) | 15% |
| Material (uncommon) | 20% |
| Enhancement Stone ×2 | 25% |
| Col (2× standard) | 100% |

### 7.3 Boss Drops

| Drop | Rate | Notes |
|---|---|---|
| Col | 100% | See `ECONOMY.md` |
| Boss material (unique) | 100% | 1× per kill |
| Equipment (Rare–Legendary) | 1 per participant | Loot roll |
| Enhancement Stones ×3–5 | 100% | Flat amount |
| Consumable (rare) | 30% | e.g., Elixir of Fire |

**Loot distribution:** 5-player dungeons use **Need/Greed/Pass** system.
Each item that drops is offered to the party; players roll Need (highest
priority), Greed (lower priority), or Pass.

### 7.4 Floor Tier Scaling

Drop tables are gated by floor tier. A Floor 1 mob cannot drop Floor 5
equipment. Each floor has its own loot table JSON:

```json
{
  "floor": 1,
  "mobDrops": {
    "materials": [
      { "item": "crystal_shard", "weight": 60 },
      { "item": "iron_ore_chunk", "weight": 30 },
      { "item": "beast_hide", "weight": 10 }
    ],
    "equipment": [
      { "rarity": "common", "weight": 70 },
      { "rarity": "uncommon", "weight": 25 },
      { "rarity": "rare", "weight": 5 }
    ]
  }
}
```

---

## 8. Weight / Carry System

### 8.1 Weight Model

Every item has a weight value. The player has a **carry capacity** based
on their level and equipment.

```
carry_capacity = 100 + (level × 10) + (strength × 5)

Example: Level 42, STR 20 → 100 + 420 + 100 = 620 weight units
```

| Item Category | Weight per Item |
|---|---|
| Weapon | 10–50 (based on tier) |
| Armor | 15–60 |
| Accessory | 5–15 |
| Material | 1–5 per unit |
| Consumable | 1–3 per unit |
| Quest item | 0 (weightless) |

### 8.2 Overweight Penalties

| Weight % | Penalty |
|---|---|
| ≤ 80% | None |
| 80–100% | Movement speed -10% |
| 100–120% | Movement speed -25%, cannot sprint |
| > 120% | Cannot move, must drop items |

### 8.3 Weight Management

- **Inventory UI** shows current weight vs. capacity as a bar.
- **Storage Chest** in town has unlimited capacity (account-bound).
- **Guild Bank** has 500 slots.
- **Item weight is per-stack** — a stack of 64 Crystal Shards (1 each) = 64 weight.

### 8.4 Death & Item Drop

On death:
- **Insured items** (paid 10% of value at NPC): return to inventory.
- **Uninsured equipment**: drop on the ground as a lootable item.
- **Materials & consumables**: 50% chance to drop half the stack.
- **Quest items**: never drop.

---

## 9. Item Binding

| Binding Type | Behavior |
|---|---|
| **Bound on Equip** | Cannot trade, sell to AH, or drop after equipping. Can still be sold to Fence. |
| **Bound on Pickup** | Cannot trade, sell to AH, or drop. |
| **Unbound** | Fully tradeable. |

Most quest rewards and dungeon drops are Bound on Equip. World boss drops
are Unbound (to fuel the player-driven economy).

---

## 10. Implementation Notes

- **Data-driven items:** Item definitions are JSON files in
  `resources/data/aincrad/items/`. Each file defines stats, weight, slot,
  rarity, and crafting recipe.
- **NBT persistence:** Enhancement tier, affixes, and binding status are
  stored in item NBT (`aincrad:enhancement`, `aincrad:affixes`, `aincrad:binding`).
- **Validation:** Server validates every item transaction (equip, trade,
  enhance) against the item definition to prevent client-side spoofing.
- **Performance:** Item lookups use a `ResourceKey` → `ItemDef` cache.
  Loot tables are pre-computed per floor and cached in memory.

---

*Document version: 1.0 — Last updated 2026-10-10*
