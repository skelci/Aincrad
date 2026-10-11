# Crystal Socketing & Armor Set System — Implementation Plan

> **For Hermes:** Use subagent-driven-development skill to implement this plan task-by-task.

**Goal:** Players socket crystals into weapons/armor for stat bonuses, collect 10 armor sets with 2/4/6-piece set bonuses, and upgrade 3 lower-tier crystals into 1 higher-tier crystal.

**Architecture:**  
- **Socketing** uses a `Socketable` interface + `CrystalSocket` component model. Socketable items expose N slots; inserting a crystal stores it in ItemStack NBT. `CrystalBonusApplier` walks equipped socket items and adds their stat bonuses to `PlayerStats`.  
- **Armor sets** use an `ArmorSet` enum (10 sets) + `ArmorPiece` base class. Each piece carries a `SetBonus` that triggers at 2/4/6 pieces worn.  
- **Crystal crafting** registers `CrystalUpgradeRecipe` entries: 3× Tier-N → 1× Tier-(N+1) via `CraftingManager`.  
- **Crystal drops** hook into `MobEntity` and `BossEntity` loot tables via `CrystalDropHandler`.

**Tech Stack:** Java 25, MC 26.3 unobfuscated (Mojang mappings), Gradle, `net.minecraft.world.item.ItemStack`, `net.minecraft.nbt.CompoundTag`, `net.minecraft.world.entity.player.Player`, `net.minecraft.world.entity.EquipmentSlot`, `net.minecraft.ChatFormatting`, `net.minecraft.network.chat.Component`.

---

## Existing Framework (do NOT recreate)

| Class | Package | Role |
|---|---|---|
| `AincradItem` | `com.aincrad.item` | Abstract base: name, rarity, type, weight, baseValue, description |
| `ItemRarity` | `com.aincrad.item` | 6 tiers: COMMON→MYTHIC, statMultiplier, drop rates |
| `ItemType` | `com.aincrad.item` | 13 categories incl. CRYSTAL, HELMET, CHESTPLATE, LEGGINGS, BOOTS, SHIELD, WEAPON |
| `CrystalItem` | `com.aincrad.crystal` | Crystal with `CrystalType` + `CrystalTier`, `getStatBonus(base)` |
| `CrystalType` | `com.aincrad.crystal` | SWORD, SHIELD, ARMOR, AGILITY, MAGIC, VITALITY |
| `CrystalTier` | `com.aincrad.crystal` | ROUGH(1), POLISHED(2), FLAWLESS(3), PERFECT(4), TRANSCENDENT(5), `getMultiplier()` |
| `StatType` | `com.aincrad.combat.stat` | STR, AGI, VIT, DEX, INT, CRIT_RATE, CRIT_DAMAGE, EVASION, HP_REGEN, MP_REGEN, WEIGHT_LIMIT |
| `PlayerStats` | `com.aincrad.combat.stat` | Base + derived stat maps, `modifyBase()`, `recalculate()`, `snapshot()` |
| `StatCalculator` | `com.aincrad.combat.stat` | Pure formulas for derived stats, `damage()`, `critChance()`, `maxHp()`, etc. |
| `CraftingRecipe` | `com.aincrad.craft` | output, inputs, station, successRate |
| `CraftingManager` | `com.aincrad.craft` | Static registry, `register()`, `craft(inputs, station)` |

---

## File Layout (all new files)

```
src/main/java/com/aincrad/
├── crystal/
│   ├── socket/
│   │   ├── Socketable.java          # Agent A
│   │   ├── CrystalSocket.java       # Agent A
│   │   ├── SocketManager.java        # Agent A
│   │   └── CrystalBonusApplier.java # Agent A
│   ├── craft/
│   │   ├── CrystalUpgradeRecipe.java # Agent C
│   │   └── CrystalCraftingManager.java # Agent C
│   └── drop/
│       └── CrystalDropHandler.java   # Agent D
└── armor/
    ├── ArmorSet.java                 # Agent B
    ├── ArmorPiece.java               # Agent B
    ├── SetBonus.java                 # Agent B
    └── impl/
        ├── LeatherSet.java           # Agent B
        ├── ChainSet.java
        ├── IronSet.java
        ├── SteelSet.java
        ├── MythrilSet.java
        ├── AdamantiteSet.java
        ├── DragonSet.java
        ├── ShadowSet.java
        ├── HolySet.java
        └── VoidSet.java
```

---

## Agent A — Crystal Socketing Framework

### A1: `Socketable` interface

```java
package com.aincrad.crystal.socket;

import net.minecraft.world.item.ItemStack;

public interface Socketable {
    int getMaxSockets(ItemStack stack);
    boolean canSocket(ItemStack stack, ItemStack crystal);
    void insertCrystal(ItemStack stack, int slot, ItemStack crystal);
    ItemStack removeCrystal(ItemStack stack, int slot);
    ItemStack getCrystal(ItemStack stack, int slot);
    boolean isSocketable(ItemStack stack);
}
```

### A2: `CrystalSocket` — NBT-backed socket slot

```java
package com.aincrad.crystal.socket;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class CrystalSocket {
    private static final String SOCKET_TAG = "AincradSockets";
    
    // Stores/reads crystal ItemStacks in NBT ListTag under "AincradSockets"
    // Each entry: { "slot": int, "crystal": CompoundTag (serialized ItemStack) }
    // Methods: static int getSocketCount(ItemStack), static ItemStack getCrystal(ItemStack, int),
    //          static void setCrystal(ItemStack, int, ItemStack), static void removeCrystal(ItemStack, int)
}
```

### A3: `SocketManager` — default `Socketable` implementation

```java
package com.aincrad.crystal.socket;

public class SocketManager implements Socketable {
    // Default: 2 sockets for weapons, 1 for armor pieces, 3 for shields
    // canSocket: checks crystal type matches item type (SWORD crystal → WEAPON item, etc.)
    // insert/remove delegate to CrystalSocket NBT methods
}
```

### A4: `CrystalBonusApplier` — walks equipment, adds bonuses to PlayerStats

```java
package com.aincrad.crystal.socket;

public class CrystalBonusApplier {
    public static void apply(Player player, PlayerStats stats) {
        // Iterate EquipmentSlot.values()
        // For each equipped ItemStack, check Socketable
        // For each filled socket, get CrystalItem, compute bonus, modifyBase()
    }
    
    private static double getBonusForCrystal(CrystalItem crystal, PlayerStats stats) {
        // SWORD → damage bonus, ARMOR → defense, AGI → evasion/speed, 
        // MAGIC → INT, VITALITTY → VIT, SHIELD → damage reduction
    }
}
```

---

## Agent B — Armor Set System (10 sets × 6 pieces)

### B1: `ArmorSet` enum

```java
package com.aincrad.armor;

public enum ArmorSet {
    LEATHER("Leather", "Basic starter armor", 1),
    CHAIN("Chain", "Light and flexible", 2),
    IRON("Iron", "Reliable protection", 3),
    STEEL("Steel", "Forged for war", 4),
    MYTHRIL("Mythril", "Elven craftsmanship", 5),
    ADAMANTITE("Adamantite", "Unbreakable ore", 6),
    DRAGON("Dragon", "Scales of the wyrm", 7),
    SHADOW("Shadow", "Woven from darkness", 8),
    HOLY("Holy", "Blessed by the gods", 9),
    VOID("Void", "Touched by the abyss", 10);
    
    // fields: displayName, lore, tier (1-10)
}
```

### B2: `SetBonus` — 2/4/6 piece bonuses per set

```java
package com.aincrad.armor;

import com.aincrad.combat.stat.StatType;

public record SetBonus(int piecesRequired, StatType stat, double bonus, String description) {
    // 2-piece: minor stat boost (+5 to primary stat)
    // 4-piece: major stat boost (+15) + utility (regen, speed, etc.)
    // 6-piece: powerful effect (lifesteal, thorns, damage aura, etc.)
}
```

### B3: `ArmorPiece` base class

```java
package com.aincrad.armor;

import com.aincrad.item.AincradItem;
import com.aincrad.item.ItemRarity;
import com.aincrad.item.ItemType;

public abstract class ArmorPiece extends AincradItem {
    private final ArmorSet set;
    private final ItemType slotType; // HELMET, CHESTPLATE, LEGGINGS, BOOTS, SHIELD, WEAPON
    private final int defense;
    private final SetBonus bonus2;
    private final SetBonus bonus4;
    private final SetBonus bonus6;
    
    // Constructor: super(name, rarity, type, weight, baseValue, description)
    // Subclasses call super and pass their set + bonuses
}
```

### B4–B13: 10 set implementation classes (one per set)

Each set class:
- Extends `ArmorPiece`
- Creates 6 pieces: helmet, chestplate, leggings, boots, shield, weapon
- Each piece passes its `ArmorSet`, `ItemType`, defense value, and 3 `SetBonus` records
- Defense scales with set tier: Leather=10, Void=100
- Uses `ItemRarity` scaling: Leather=COMMON, Void=MYTHIC

Example pattern for `LeatherSet.java`:
```java
public class LeatherSet {
    public static final ArmorPiece HELMET = new ArmorPiece(
        "Leather Helmet", ItemRarity.COMMON, ItemType.HELMET, 1, 50, 
        "A sturdy leather helm.", ArmorSet.LEATHER, 5,
        new SetBonus(2, StatType.VIT, 5, "+5 VIT"),
        new SetBonus(4, StatType.HP_REGEN, 2, "+2 HP/s"),
        new SetBonus(6, StatType.EVASION, 3, "+3% Evasion")
    ) {};
    // ... 5 more pieces
}
```

### B14: `ArmorSetBonusApplier` — checks equipped pieces, applies set bonuses

```java
package com.aincrad.armor;

public class ArmorSetBonusApplier {
    public static void apply(Player player, PlayerStats stats) {
        // Count pieces per ArmorSet from player equipment
        // For each set with >=2 pieces, apply 2/4/6 bonuses via modifyBase()
    }
}
```

---

## Agent C — Crystal Crafting & Upgrade System

### C1: `CrystalUpgradeRecipe`

```java
package com.aincrad.crystal.craft;

public class CrystalUpgradeRecipe {
    private final CrystalType type;
    private final CrystalTier fromTier;
    private final CrystalTier toTier; // always fromTier + 1
    
    // 3 crystals of (type, fromTier) → 1 crystal of (type, toTier)
    // Uses CraftingManager.register() with CraftingStation.CRYSTAL_FORGE
}
```

### C2: `CrystalCraftingManager`

```java
package com.aincrad.crystal.craft;

public final class CrystalCraftingManager {
    // Registers all 6 types × 4 upgrade paths = 24 recipes (ROUGH→POLISHED through PERFECT→TRANSCENDENT)
    // Also registers crystal creation recipes (basic crystals from materials)
    // Provides getUpgradePath(CrystalTier) → next tier or null
}
```

---

## Agent D — Crystal Drop Integration

### D1: `CrystalDropHandler`

```java
package com.aincrad.crystal.drop;

public final class CrystalDropHandler {
    // Called from MobEntity and BossEntity death handlers
    // Roll logic:
    //   - Standard mobs: 10% chance, tier ROUGH-POLISHED, random type
    //   - Elites: 25% chance, tier POLISHED-FLAWLESS
    //   - Bosses: 100% chance, tier FLAWLESS-PERFECT, weighted by boss tier
    //   - Raid bosses: guaranteed TRANSCENDENT
    
    public static ItemStack rollCrystal(boolean isBoss, int bossTier) {
        // Returns a CrystalItem ItemStack or ItemStack.EMPTY
    }
    
    public static void registerDrop(MobEntity mob) {
        // Hooks into mob death event, adds crystal to drops
    }
}
```

---

## Build & Verification

After each agent finishes:
```bash
cd /tmp/aincrad-crystal-plan && ./gradlew build
```

Expected: BUILD SUCCESSFUL with no compilation errors.

---

## Execution Order

1. **Agent A** (Socketing) — must complete first; defines interfaces Agent B depends on
2. **Agents B, C, D** — parallel after Agent A commits
3. **Merge & build** — fetch all branches, merge, build

---

## Constraints

- Small files, one class at a time
- Build after each commit
- No large blocks in single responses
- MC 26.3: use `net.minecraft.world.item.ItemStack`, `net.minecraft.nbt.CompoundTag`, `net.minecraft.world.entity.player.Player`, `net.minecraft.world.entity.EquipmentSlot`, `net.minecraft.ChatFormatting`, `net.minecraft.network.chat.Component`, `net.minecraft.resources.ResourceLocation`
