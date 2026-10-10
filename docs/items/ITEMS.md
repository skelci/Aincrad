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

## 3. Item Types

The `ItemType` enum classifies every item into one of 13 categories.
Eleven are equippable; three are non-equippable.

| Type | Equippable | Slot | Description |
|---|---|---|---|
| `WEAPON` | ✓ | Main Hand | Deals damage; has attack speed and damage. |
| `ARMOR` | ✓ | Off Hand | Reduces damage taken (heavy armor). |
| `SHIELD` | ✓ | Off Hand | Blocks damage; has block chance. |
| `HELMET` | ✓ | Helmet | Head protection. |
| `CHESTPLATE` | ✓ | Chestplate | Torso protection. |
| `LEGGINGS` | ✓ | Leggings | Leg protection. |
| `BOOTS` | ✓ | Boots | Foot protection + movement bonuses. |
| `RING` | ✓ | Ring 1 / Ring 2 | Passive bonuses and affixes. |
| `AMULET` | ✓ | Necklace | Passive bonuses and affixes. |
| `BELT` | ✓ | Belt | Utility + passive bonuses. |
| `CRYSTAL` | ✓ | Crystal Slot | Slotted into crystal equipment. |
| `MATERIAL` | ✗ | — | Crafting component. |
| `CONSUMABLE` | ✗ | — | Single-use buffs and healing. |

### 3.1 Type-Weight Mapping

| Type | Weight Range |
|---|---|
| WEAPON | 10–50 |
| ARMOR | 15–60 |
| SHIELD | 15–40 |
| HELMET | 8–25 |
| CHESTPLATE | 12–35 |
| LEGGINGS | 10–30 |
| BOOTS | 6–20 |
| RING | 3–8 |
| AMULET | 3–8 |
| BELT | 4–10 |
| CRYSTAL | 2–6 |
| MATERIAL | 1–5 per unit |
| CONSUMABLE | 1–3 per unit |

---

## 4. Item Catalog (200+ Items)

The following is the complete item catalog for launch. Each item has a
name, type, rarity, and slot assignment. Enhancement, affixes, and
binding behavior are inherited from the base `AincradItem` class.

### 4.1 Weapons (36 items)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 1 | Crystal Sword | Common | WEAPON | Main Hand | Starting weapon. |
| 2 | Iron Sword | Common | WEAPON | Main Hand | Basic melee. |
| 3 | Steel Sword | Uncommon | WEAPON | Main Hand | +10% damage vs Common. |
| 4 | Knight's Blade | Uncommon | WEAPON | Main Hand | Floor 2+ drop. |
| 5 | Silver Rapier | Rare | WEAPON | Main Hand | Fast attack speed. |
| 6 | Dark Repulser | Rare | WEAPON | Main Hand | Fire element. |
| 7 | Elucidator | Epic | WEAPON | Main Hand | Dual-wield trait. |
| 8 | Dark Repulser MK-II | Epic | WEAPON | Main Hand | Upgraded fire blade. |
| 9 | Excalibur | Legendary | WEAPON | Main Hand | Holy element, AoE. |
| 10 | World Ender | Mythic | WEAPON | Main Hand | Ignores armor. |
| 11 | Crystal Dagger | Common | WEAPON | Main Hand | Fast, low damage. |
| 12 | Iron Dagger | Common | WEAPON | Main Hand | Starting rogue weapon. |
| 13 | Assassin's Knife | Uncommon | WEAPON | Main Hand | +crit chance. |
| 14 | Venom Fang | Rare | WEAPON | Main Hand | Poison on hit. |
| 15 | Phantom Dagger | Epic | WEAPON | Main Hand | Teleport on crit. |
| 16 | Gleam Fang | Legendary | WEAPON | Main Hand | Floor 15 boss drop. |
| 17 | Void Stiletto | Mythic | WEAPON | Main Hand | Shadow damage + lifesteal. |
| 18 | Crystal Bow | Common | WEAPON | Main Hand | Ranged, low power. |
| 19 | Short Bow | Common | WEAPON | Main Hand | Fast draw. |
| 20 | Long Bow | Uncommon | WEAPON | Main Hand | Extended range. |
| 21 | Wind Piercer | Rare | WEAPON | Main Hand | Arrow speed +50%. |
| 22 | Moonlight Bow | Epic | WEAPON | Main Hand | Night damage bonus. |
| 23 | Artemis' Bow | Legendary | WEAPON | Main Hand | Guided arrows. |
| 24 | Storm Bow | Mythic | WEAPON | Main Hand | Chain lightning on hit. |
| 25 | Crystal Staff | Common | WEAPON | Main Hand | Magic starter. |
| 26 | Apprentice Staff | Common | WEAPON | Main Hand | +MP regen. |
| 27 | Ember Staff | Uncommon | WEAPON | Main Hand | Fire spell power. |
| 28 | Frost Staff | Rare | WEAPON | Main Hand | Ice spell power. |
| 29 | Arcane Staff | Epic | WEAPON | Main Hand | All spells +15%. |
| 30 | Staff of Aincrad | Legendary | WEAPON | Main Hand | Mana cost -30%. |
| 31 | Staff of Creation | Mythic | WEAPON | Main Hand | Creative-mode powers in survival. |
| 32 | Crystal Axe | Common | WEAPON | Main Hand | Gathering + combat. |
| 33 | Iron Axe | Common | WEAPON | Main Hand | Woodcutting. |
| 34 | Silver Axe | Uncommon | WEAPON | Main Hand | Faster gathering. |
| 35 | War Axe | Rare | WEAPON | Main Hand | High damage, slow. |
| 36 | Titan's Axe | Epic | WEAPON | Main Hand | Cleave attack. |

### 4.2 Armor (30 items)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 37 | Crystal Plate | Common | ARMOR | Off Hand | Light protection. |
| 38 | Leather Tunic | Common | ARMOR | Off Hand | Basic defense. |
| 39 | Chainmail | Uncommon | ARMOR | Off Hand | +physical resist. |
| 40 | Iron Plate | Uncommon | ARMOR | Off Hand | Standard heavy armor. |
| 41 | Steel Plate | Rare | ARMOR | Off Hand | +10% physical resist. |
| 42 | Knight's Plate | Rare | ARMOR | Off Hand | Floor 3+ drop. |
| 43 | Shadow Armor | Epic | ARMOR | Off Hand | +stealth bonus. |
| 44 | Dragon Scale Mail | Legendary | ARMOR | Off Hand | Fire immune. |
| 45 | Aegis of Light | Mythic | ARMOR | Off Hand | Reflect 20% damage. |
| 46 | Crystal Helm | Common | HELMET | Helmet | +5% defense. |
| 47 | Leather Cap | Common | HELMET | Helmet | Starting headgear. |
| 48 | Iron Helm | Uncommon | HELMET | Helmet | +10% defense. |
| 49 | Silver Helm | Rare | HELMET | Helmet | +magic resist. |
| 50 | Helm of Wisdom | Rare | HELMET | Helmet | +MP regen. |
| 51 | Crown of Kings | Epic | HELMET | Helmet | +all stats. |
| 52 | Dragon Helm | Legendary | HELMET | Helmet | Fire resist. |
| 53 | Crown of Aincrad | Mythic | HELMET | Helmet | +20% all stats. |
| 54 | Crystal Chestplate | Common | CHESTPLATE | Chestplate | +10% defense. |
| 55 | Leather Shirt | Common | CHESTPLATE | Chestplate | Basic torso. |
| 56 | Chain Vest | Uncommon | CHESTPLATE | Chestplate | +15% defense. |
| 57 | Iron Chestplate | Uncommon | CHESTPLATE | Chestplate | Standard. |
| 58 | Steel Breastplate | Rare | CHESTPLATE | Chestplate | +20% defense. |
| 59 | Shadow Garb | Epic | CHESTPLATE | Chestplate | +stealth. |
| 60 | Dragon Chestplate | Legendary | CHESTPLATE | Chestplate | Fire immune. |
| 61 | Aegis of the Gods | Mythic | CHESTPLATE | Chestplate | +30% defense. |
| 62 | Crystal Leggings | Common | LEGGINGS | Leggings | +5% defense. |
| 63 | Leather Pants | Common | LEGGINGS | Leggings | Basic legs. |
| 64 | Chain Leggings | Uncommon | LEGGINGS | Leggings | +10% defense. |
| 65 | Iron Leggings | Uncommon | LEGGINGS | Leggings | Standard. |
| 66 | Steel Leggings | Rare | LEGGINGS | Leggings | +15% defense. |
| 67 | Shadow Leggings | Epic | LEGGINGS | Leggings | +stealth. |
| 68 | Dragon Leggings | Legendary | LEGGINGS | Leggings | Fire resist. |

### 4.3 Shields (18 items)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 69 | Crystal Shield | Common | SHIELD | Off Hand | +5% block. |
| 70 | Wooden Shield | Common | SHIELD | Off Hand | Starting shield. |
| 71 | Iron Shield | Uncommon | SHIELD | Off Hand | +10% block. |
| 72 | Kite Shield | Uncommon | SHIELD | Off Hand | Standard. |
| 73 | Tower Shield | Rare | SHIELD | Off Hand | +20% block, slow. |
| 74 | Silver Shield | Rare | SHIELD | Off Hand | +magic block. |
| 75 | Phoenix Shield | Epic | SHIELD | Off Hand | Fire reflect. |
| 76 | Dragon Shield | Legendary | SHIELD | Off Hand | Fire immune block. |
| 77 | Aegis Shield | Mythic | SHIELD | Off Hand | 50% block, reflect 30%. |
| 78 | Round Shield | Common | SHIELD | Off Hand | Light, fast block. |
| 79 | Buckler | Uncommon | SHIELD | Off Hand | Quick block. |
| 80 | Magic Barrier | Rare | SHIELD | Off Hand | Blocks spells. |
| 81 | Energy Shield | Epic | SHIELD | Off Hand | MP-powered block. |
| 82 | Crystal Buckler | Common | SHIELD | Off Hand | Crystal-tech. |
| 83 | Steel Kite Shield | Rare | SHIELD | Off Hand | Reinforced. |
| 84 | Obsidian Shield | Epic | SHIELD | Off Hand | Dark resist. |
| 85 | Guardian Shield | Legendary | SHIELD | Off Hand | +30% block. |

### 4.4 Boots (16 items)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 86 | Crystal Boots | Common | BOOTS | Boots | +2% move speed. |
| 87 | Leather Boots | Common | BOOTS | Boots | Basic footwear. |
| 88 | Iron Boots | Uncommon | BOOTS | Boots | +5% defense. |
| 89 | Swift Boots | Uncommon | BOOTS | Boots | +10% move speed. |
| 90 | Steel Boots | Rare | BOOTS | Boots | +10% defense. |
| 91 | Shadow Boots | Epic | BOOTS | Boots | +stealth, silent. |
| 92 | Wind Boots | Epic | BOOTS | Boots | +25% move speed. |
| 93 | Dragon Boots | Legendary | BOOTS | Boots | Fire resist. |
| 94 | Aincrad Greaves | Mythic | BOOTS | Boots | +30% move speed, flight. |
| 95 | Sandals of Speed | Rare | BOOTS | Boots | +15% move speed. |
| 96 | Frost Boots | Epic | BOOTS | Boots | Ice walk. |
| 97 | Lava Walkers | Legendary | BOOTS | Boots | Lava immune. |
| 98 | Crystal Greaves | Uncommon | BOOTS | Boots | Crystal-tech. |
| 99 | Titan Greaves | Rare | BOOTS | Boots | +knockback resist. |
| 100 | Phantom Treads | Epic | BOOTS | Boots | Intangibility chance. |

### 4.5 Rings (20 items)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 101 | Crystal Ring | Common | RING | Ring 1/2 | +1 STR. |
| 102 | Iron Ring | Common | RING | Ring 1/2 | +1 VIT. |
| 103 | Silver Ring | Uncommon | RING | Ring 1/2 | +2 STR. |
| 104 | Ring of Strength | Uncommon | RING | Ring 1/2 | +3 STR. |
| 105 | Ring of Vitality | Uncommon | RING | Ring 1/2 | +3 VIT. |
| 106 | Ring of Dexterity | Rare | RING | Ring 1/2 | +4 DEX. |
| 107 | Ring of Intelligence | Rare | RING | Ring 1/2 | +4 INT. |
| 108 | Ring of Speed | Rare | RING | Ring 1/2 | +5% move speed. |
| 109 | Ring of Power | Epic | RING | Ring 1/2 | +5 all stats. |
| 110 | Ring of the Phoenix | Epic | RING | Ring 1/2 | Revive once per day. |
| 111 | Ring of Shadows | Legendary | RING | Ring 1/2 | +stealth. |
| 112 | Ring of the World | Mythic | RING | Ring 1/2 | +10 all stats. |
| 113 | Band of Courage | Uncommon | RING | Ring 1/2 | +2 STR, +2 VIT. |
| 114 | Band of Focus | Rare | RING | Ring 1/2 | +3 INT, +3 DEX. |
| 115 | Band of Fortune | Epic | RING | Ring 1/2 | +10% drop rate. |
| 116 | Band of Kings | Legendary | RING | Ring 1/2 | +8 all stats. |
| 117 | Crystal Band | Common | RING | Ring 1/2 | +1 all stats. |
| 118 | Silver Band | Uncommon | RING | Ring 1/2 | +2 all stats. |
| 119 | Gold Band | Rare | RING | Ring 1/2 | +3 all stats. |

### 4.6 Amulets (16 items)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 120 | Crystal Amulet | Common | AMULET | Necklace | +5% HP. |
| 121 | Iron Pendant | Common | AMULET | Necklace | +3% HP. |
| 122 | Silver Amulet | Uncommon | AMULET | Necklace | +8% HP. |
| 123 | Amulet of Vitality | Uncommon | AMULET | Necklace | +10% HP. |
| 124 | Amulet of Strength | Rare | AMULET | Necklace | +5 STR, +5% HP. |
| 125 | Amulet of Wisdom | Rare | AMULET | Necklace | +5 INT, +10% MP. |
| 126 | Amulet of Speed | Epic | AMULET | Necklace | +15% move speed. |
| 127 | Amulet of Power | Epic | AMULET | Necklace | +10 all stats. |
| 128 | Amulet of the Phoenix | Legendary | AMULET | Necklace | Revive once per day. |
| 129 | Heart of Aincrad | Mythic | AMULET | Necklace | +25% HP, +15% all stats. |
| 130 | Crystal Pendant | Common | AMULET | Necklace | Crystal-tech. |
| 131 | Moon Amulet | Rare | AMULET | Necklace | Night bonus. |
| 132 | Sun Amulet | Epic | AMULET | Necklace | Day bonus. |
| 133 | Star Amulet | Legendary | AMULET | Necklace | +all stats, +drop rate. |
| 134 | Pendant of Kings | Legendary | AMULET | Necklace | +10 all stats. |

### 4.7 Belts (12 items)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 135 | Crystal Belt | Common | BELT | Belt | +5% carry weight. |
| 136 | Leather Belt | Common | BELT | Belt | +3% carry weight. |
| 137 | Iron Belt | Uncommon | BELT | Belt | +8% carry weight. |
| 138 | Utility Belt | Uncommon | BELT | Belt | +2 inventory slots. |
| 139 | Belt of Strength | Rare | BELT | Belt | +5 STR. |
| 140 | Belt of Haste | Rare | BELT | Belt | +10% attack speed. |
| 141 | Belt of the Titan | Epic | BELT | Belt | +10 STR, +knockback resist. |
| 142 | Belt of the Wind | Epic | BELT | Belt | +20% move speed. |
| 143 | Belt of the King | Legendary | BELT | Belt | +10 all stats, +carry. |
| 144 | Aincrad Belt | Mythic | BELT | Belt | +15 all stats, +50% carry. |
| 145 | Steel Belt | Uncommon | BELT | Belt | +5% defense. |
| 146 | Swift Belt | Rare | BELT | Belt | +15% move speed. |

### 4.8 Crystals (30 items — 6 types × 5 tiers)

| # | Item | Rarity | Type | Slot | Notes |
|---|---|---|---|---|---|
| 147 | Rough Power Crystal | Common | CRYSTAL | Crystal Slot | +5% damage. |
| 148 | Polished Power Crystal | Uncommon | CRYSTAL | Crystal Slot | +10% damage. |
| 149 | Flawless Power Crystal | Rare | CRYSTAL | Crystal Slot | +20% damage. |
| 150 | Perfect Power Crystal | Epic | CRYSTAL | Crystal Slot | +35% damage. |
| 151 | Transcendent Power Crystal | Legendary | CRYSTAL | Crystal Slot | +50% damage. |
| 152 | Rough Defense Crystal | Common | CRYSTAL | Crystal Slot | +5% defense. |
| 153 | Polished Defense Crystal | Uncommon | CRYSTAL | Crystal Slot | +10% defense. |
| 154 | Flawless Defense Crystal | Rare | CRYSTAL | Crystal Slot | +20% defense. |
| 155 | Perfect Defense Crystal | Epic | CRYSTAL | Crystal Slot | +35% defense. |
| 156 | Transcendent Defense Crystal | Legendary | CRYSTAL | Crystal Slot | +50% defense. |
| 157 | Rough Vitality Crystal | Common | CRYSTAL | Crystal Slot | +5% HP. |
| 158 | Polished Vitality Crystal | Uncommon | CRYSTAL | Crystal Slot | +10% HP. |
| 159 | Flawless Vitality Crystal | Rare | CRYSTAL | Crystal Slot | +20% HP. |
| 160 | Perfect Vitality Crystal | Epic | CRYSTAL | Crystal Slot | +35% HP. |
| 161 | Transcendent Vitality Crystal | Legendary | CRYSTAL | Crystal Slot | +50% HP. |
| 162 | Rough Speed Crystal | Common | CRYSTAL | Crystal Slot | +3% move speed. |
| 163 | Polished Speed Crystal | Uncommon | CRYSTAL | Crystal Slot | +7% move speed. |
| 164 | Flawless Speed Crystal | Rare | CRYSTAL | Crystal Slot | +12% move speed. |
| 165 | Perfect Speed Crystal | Epic | CRYSTAL | Crystal Slot | +20% move speed. |
| 166 | Transcendent Speed Crystal | Legendary | CRYSTAL | Crystal Slot | +30% move speed. |
| 167 | Rough Mana Crystal | Common | CRYSTAL | Crystal Slot | +5% MP. |
| 168 | Polished Mana Crystal | Uncommon | CRYSTAL | Crystal Slot | +10% MP. |
| 169 | Flawless Mana Crystal | Rare | CRYSTAL | Crystal Slot | +20% MP. |
| 170 | Perfect Mana Crystal | Epic | CRYSTAL | Crystal Slot | +35% MP. |
| 171 | Transcendent Mana Crystal | Legendary | CRYSTAL | Crystal Slot | +50% MP. |
| 172 | Rough Fortune Crystal | Common | CRYSTAL | Crystal Slot | +5% drop rate. |
| 173 | Polished Fortune Crystal | Uncommon | CRYSTAL | Crystal Slot | +10% drop rate. |
| 174 | Flawless Fortune Crystal | Rare | CRYSTAL | Crystal Slot | +20% drop rate. |
| 175 | Perfect Fortune Crystal | Epic | CRYSTAL | Crystal Slot | +35% drop rate. |
| 176 | Transcendent Fortune Crystal | Legendary | CRYSTAL | Crystal Slot | +50% drop rate. |

### 4.9 Materials (30 items)

| # | Item | Rarity | Type | Source |
|---|---|---|---|---|
| 177 | Crystal Shard | Common | MATERIAL | Crystal Slime drop. |
| 178 | Iron Ore Chunk | Common | MATERIAL | Iron Golem drop. |
| 179 | Iron Ingot | Common | MATERIAL | Smelted from ore. |
| 180 | Beast Hide | Common | MATERIAL | Dire Wolf drop. |
| 181 | Venom Sac | Uncommon | MATERIAL | Giant Spider drop. |
| 182 | Scale Fragment | Uncommon | MATERIAL | Drake drop. |
| 183 | Shadow Essence | Rare | MATERIAL | Shadow Stalker drop. |
| 184 | Enhancement Stone | Common | MATERIAL | Mob drop + shop. |
| 185 | Nephrite Core | Epic | MATERIAL | Floor 5 boss. |
| 186 | Dragon's Heart | Legendary | MATERIAL | Floor 1 boss. |
| 187 | Gleam Eyes Scale | Legendary | MATERIAL | Floor 15 boss. |
| 188 | Gleam Fang | Mythic | MATERIAL | Floor 15 boss (50%). |
| 189 | Phantom Thread | Epic | MATERIAL | Phantom Weaver drop. |
| 190 | Healing Herb | Common | MATERIAL | Herbalism node. |
| 191 | Ancient Wood | Common | MATERIAL | Ancient trees. |
| 192 | Crystal Cluster | Uncommon | MATERIAL | Crystal node. |
| 193 | Steel Ingot | Uncommon | MATERIAL | Crafted. |
| 194 | Silver Ingot | Rare | MATERIAL | Crafted. |
| 195 | Mythril Ingot | Epic | MATERIAL | Crafted. |
| 196 | Dragon Scale | Legendary | MATERIAL | Dragon drop. |
| 197 | Void Crystal | Epic | MATERIAL | Void mob drop. |
| 198 | Star Fragment | Legendary | MATERIAL | Star event. |
| 199 | Essence of Fire | Rare | MATERIAL | Fire elemental. |
| 200 | Essence of Ice | Rare | MATERIAL | Ice elemental. |
| 201 | Essence of Lightning | Rare | MATERIAL | Storm elemental. |
| 202 | Essence of Shadow | Epic | MATERIAL | Shadow mob. |
| 203 | Enhancement Shield | Uncommon | CONSUMABLE | Prevents tier loss. |
| 204 | Blessing of the Forge | Uncommon | CONSUMABLE | +10% success rate. |
| 205 | Repair Kit | Common | MATERIAL | Repairs durability. |
| 206 | Binding Scroll | Rare | MATERIAL | Binds item to player. |

### 4.10 Consumables (30 items)

| # | Item | Rarity | Type | Effect |
|---|---|---|---|---|
| 207 | Health Potion I | Common | CONSUMABLE | Heal 25% max HP. |
| 208 | Health Potion II | Uncommon | CONSUMABLE | Heal 50% max HP. |
| 209 | Health Potion III | Rare | CONSUMABLE | Heal 75% max HP. |
| 210 | Mana Potion I | Common | CONSUMABLE | Restore 30% MP. |
| 211 | Mana Potion II | Uncommon | CONSUMABLE | Restore 60% MP. |
| 212 | Mana Potion III | Rare | CONSUMABLE | Restore 100% MP. |
| 213 | Stamina Elixir | Common | CONSUMABLE | Refill ST. |
| 214 | Elixir of Fire | Uncommon | CONSUMABLE | +15% fire damage, 10 min. |
| 215 | Elixir of Sharpness | Uncommon | CONSUMABLE | +10% attack, 10 min. |
| 216 | Elixir of Iron Skin | Uncommon | CONSUMABLE | +20% defense, 10 min. |
| 217 | Elixir of Swiftness | Uncommon | CONSUMABLE | +15% move speed, 10 min. |
| 218 | Bread of Vitality | Common | CONSUMABLE | +5% max HP, 15 min. |
| 219 | Roast Beast | Common | CONSUMABLE | +10% attack, 15 min. |
| 220 | Crystal Cake | Uncommon | CONSUMABLE | +10% MP regen, 15 min. |
| 221 | Sage's Stew | Rare | CONSUMABLE | +5% all stats, 30 min. |
| 222 | Dragon Feast | Epic | CONSUMABLE | +15% all stats, 1 hour. |
| 223 | Elixir of Power | Epic | CONSUMABLE | +20% all stats, 15 min. |
| 224 | Elixir of Shadows | Epic | CONSUMABLE | +stealth, 10 min. |
| 225 | Elixir of Flight | Legendary | CONSUMABLE | Flight, 5 min. |
| 226 | Potion of Invisibility | Rare | CONSUMABLE | Invisible, 2 min. |
| 227 | Potion of Strength | Uncommon | CONSUMABLE | +10 STR, 5 min. |
| 228 | Potion of Regeneration | Rare | CONSUMABLE | Regen 2 HP/s, 5 min. |
| 229 | Potion of Fire Resistance | Uncommon | CONSUMABLE | Fire immune, 10 min. |
| 230 | Potion of Water Breathing | Common | CONSUMABLE | Water breathing, 10 min. |
| 231 | Potion of Night Vision | Common | CONSUMABLE | Night vision, 10 min. |
| 232 | Potion of Leaping | Common | CONSUMABLE | +jump height, 10 min. |
| 233 | Golden Apple | Rare | CONSUMABLE | Heal + regen, 5 min. |
| 234 | Enchanted Golden Apple | Epic | CONSUMABLE | Full heal + buffs. |
| 235 | Elixir of the Phoenix | Legendary | CONSUMABLE | Auto-revive, 1 hour. |
| 236 | Greater Health Potion | Uncommon | CONSUMABLE | Heal 60% max HP. |
| 237 | Greater Mana Potion | Uncommon | CONSUMABLE | Restore 80% MP. |
| 238 | Sage's Elixir | Epic | CONSUMABLE | +10% all stats, 30 min. |

### 4.11 Quest & Special Items (10 items)

| # | Item | Rarity | Type | Notes |
|---|---|---|---|---|
| 239 | Letter from Heathcliff | Rare | CONSUMABLE | Quest item. |
| 240 | Kirito's Jacket | Legendary | ARMOR | Cosmetic + stats. |
| 241 | Asuna's Dress | Legendary | ARMOR | Cosmetic + stats. |
| 242 | Yui's Ribbon | Epic | AMULET | Companion item. |
| 243 | Floor 1 Key | Rare | CONSUMABLE | Unlocks Floor 1 dungeon. |
| 244 | Floor 5 Key | Epic | CONSUMABLE | Unlocks Floor 5 dungeon. |
| 245 | Floor 10 Key | Legendary | CONSUMABLE | Unlocks Floor 10 dungeon. |
| 246 | Gleam Eyes Token | Legendary | MATERIAL | Proof of Floor 15 kill. |
| 247 | Strelitzia | Mythic | WEAPON | Asuna's rapier replica. |
| 248 | Night Sky Sword | Mythic | WEAPON | Kirito's sword replica. |

**Total catalog: 248 items**

---

## 5. Crafting Materials

Materials come from three sources: mob drops, boss drops, and gathering
(mining, fishing, herbalism).

### 5.1 Mob Drop Materials

| Material | Source Mob | Drop Rate | Used For |
|---|---|---|---|
| Crystal Shard | Crystal Slime | 40% | Enhancement stones, basic recipes |
| Iron Ore Chunk | Iron Golem | 25% | Armor crafting |
| Beast Hide | Dire Wolf | 30% | Leather armor, accessories |
| Venom Sac | Giant Spider | 20% | Poison weapons, potions |
| Scale Fragment | Drake | 15% | Dragon-scale armor |
| Shadow Essence | Shadow Stalker | 10% | Epic+ enhancement |

### 5.2 Boss Drop Materials

| Material | Boss | Drop Rate | Used For |
|---|---|---|---|
| Dragon's Heart | Floor 1 Boss | 100% | Legendary weapon crafting |
| Nephrite Core | Floor 5 Boss | 100% | Nephrite tier enhancement |
| Gleam Eyes Scale | Floor 15 Boss | 100% | Legendary armor crafting |
| The Gleam Eyes Fang | Floor 15 Boss | 50% | Mythic dagger crafting |
| Phantom Thread | Phantom Weaver | 30% | Cape crafting |

### 5.3 Gathering Materials

| Material | Source | Tool | Yield |
|---|---|---|---|
| Iron Ingot | Iron ore vein | Iron Pickaxe+ | 1–3 |
| Crystal Cluster | Crystal node | Diamond Pickaxe+ | 1–2 |
| Healing Herb | Herbalism nodes | Any hoe | 1–3 |
| Ancient Wood | Ancient trees | Iron Axe+ | 2–4 |

**Note:** Gathering nodes are custom blocks placed in the world. They
respawn after 15 minutes and are harvested with the right tool type.

---

## 6. Item Enhancement & Upgrade System

### 6.1 Enhancement Tiers

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

### 6.2 Failure Behavior

| Tier Range | On Failure |
|---|---|
| +1 to +5 | Nothing happens — item stays at current tier. |
| +6 to +9 | Item degrades by 1 tier (e.g., +7 → +6). |
| +10 to +15 | Item **breaks** — destroyed, cannot be recovered. |

### 6.3 Safety Mechanics

- **Enhancement Shield** (consumable): Prevents tier loss on failure for one attempt.
- **Blessing of the Forge** (consumable): +10% success rate for one attempt.
- **Forge Master NPC**: Offers a guaranteed success at +10+ for 3× the material cost.

### 6.4 Affix Reroll (Accessories Only)

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

## 7. Consumables

### 7.1 Potions

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

### 7.2 Food Buffs

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

## 8. Item Drop Rates

### 8.1 Standard Mob Drops

| Drop | Rate | Notes |
|---|---|---|
| Col (virtual) | 100% | See `ECONOMY.md` |
| Material (common) | 25% | Weighted table |
| Material (uncommon) | 8% | Weighted table |
| Equipment (Common–Uncommon) | 5% | Weighted by floor tier |
| Enhancement Stone | 10% | Common currency for enhancement |
| Consumable (potion/food) | 15% | Weighted table |

### 8.2 Elite Mob Drops (5× spawn rate of standard)

| Drop | Rate |
|---|---|
| Equipment (Uncommon–Rare) | 15% |
| Material (uncommon) | 20% |
| Enhancement Stone ×2 | 25% |
| Col (2× standard) | 100% |

### 8.3 Boss Drops

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

### 8.4 Floor Tier Scaling

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

## 9. Weight / Carry System

### 9.1 Weight Model

Every item has a weight value. The player has a **carry capacity** based
on their level and equipment.

```
carry_capacity = 100 + (level × 10) + (strength × 5)
Example: Level 42, STR 20 → 100 + 420 + 100 = 620 weight units
```

| Item Type | Weight per Item |
|---|---|
| Weapon | 10–50 (based on tier) |
| Armor | 15–60 |
| Shield | 15–40 |
| Helmet | 8–25 |
| Chestplate | 12–35 |
| Leggings | 10–30 |
| Boots | 6–20 |
| Ring | 3–8 |
| Amulet | 3–8 |
| Belt | 4–10 |
| Crystal | 2–6 |
| Material | 1–5 per unit |
| Consumable | 1–3 per unit |

### 9.2 Overweight Penalties

| Weight % | Penalty |
|---|---|
| ≤ 80% | None |
| 80–100% | Movement speed -10% |
| 100–120% | Movement speed -25%, cannot sprint |
| > 120% | Cannot move, must drop items |

### 9.3 Weight Management

- **Inventory UI** shows current weight vs. capacity as a bar.
- **Storage Chest** in town has unlimited capacity (account-bound).
- **Guild Bank** has 500 slots.
- **Item weight is per-stack** — a stack of 64 Crystal Shards (1 each) = 64 weight.

### 9.4 Death & Item Drop

On death:
- **Insured items** (paid 10% of value at NPC): return to inventory.
- **Uninsured equipment**: drop on the ground as a lootable item.
- **Materials & consumables**: 50% chance to drop half the stack.
- **Quest items**: never drop.

---

## 10. Item Binding

| Binding Type | Behavior |
|---|---|
| **Bound on Equip** | Cannot trade, sell to AH, or drop after equipping. Can still be sold to Fence. |
| **Bound on Pickup** | Cannot trade, sell to AH, or drop. |
| **Unbound** | Fully tradeable. |

Most quest rewards and dungeon drops are Bound on Equip. World boss drops
are Unbound (to fuel the player-driven economy).

---

## 11. Implementation Notes

- **Data-driven items:** Item definitions are JSON files in
  `resources/data/aincrad/items/`. Each file defines stats, weight, slot,
  rarity, and crafting recipe.
- **NBT persistence:** Enhancement tier, affixes, and binding status are
  stored in item NBT (`aincrad:enhancement`, `aincrad:affixes`, `aincrad:binding`).
- **Validation:** Server validates every item transaction (equip, trade,
  enhance) against the item definition to prevent client-side spoofing.
- **Performance:** Item lookups use a `ResourceKey` → `ItemDef` cache.
  Loot tables are pre-computed per floor and cached in memory.
- **Base class:** All items extend `AincradItem`, which provides name,
  rarity, type, weight, and description. The `ItemType` enum defines the
  13 item categories; the `ItemRarity` enum defines the 6 rarity tiers.

---

*Document version: 2.0 — Last updated 2026-10-10*
