# Aincrad — Full Boss Implementation Plan

**Branch:** `feature/bosses-full`  
**Scope:** 20 bosses at every 5th floor (5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100)

---

## 1. Existing Bosses (Already Implemented)

| Floor | Boss | Type | File |
|-------|------|------|------|
| 1 | Goblin King | Standard | `impl/Floor1Boss.java` |
| 5 | Stone Golem | Standard | `impl/Floor5Boss.java` |
| 10 | Dark Knight | Standard | `impl/Floor10Boss.java` |
| 25 | Dragon Lord | Raid | `impl/Floor25RaidBoss.java` |
| 50 | Void Emperor | Raid | `impl/Floor50RaidBoss.java` |

## 2. New Bosses to Implement (16)

| Floor | Boss Name | Theme | Type |
|-------|-----------|-------|------|
| 15 | Warg Alpha | Forest | Standard |
| 20 | Ancient Treant | Forest | Standard |
| 30 | Abyssal Spider | Cave | Standard |
| 35 | Obsidian Golem | Lava | Standard |
| 40 | Magma Lord | Lava | Tier Boss |
| 45 | Frost Wolf | Ice | Standard |
| 55 | Desert Wraith | Desert | Standard |
| 60 | Pharaoh's Wrath | Desert | Tier Boss |
| 65 | Will-o'-Wisp | Swamp | Standard |
| 70 | Bog Overlord | Swamp | Tier Boss |
| 75 | Sea Serpent | Ocean | Raid |
| 80 | Kraken | Ocean | Tier Boss |
| 85 | Storm Elemental | Sky | Standard |
| 90 | Storm Lord | Sky | Tier Boss |
| 95 | Ender Spider | Void | Standard |
| 100 | Heathcliff / Administrator | Void | Final Boss (7 phases) |

## 3. Common Utility Classes

### 3.1 `BossStatScaler` (util package)
Static utility for floor-based stat scaling:
- `scaledHp(floor)` — `500 × F^1.6`
- `scaledDamage(floor)` — `20 × F^1.35`
- `scaledXp(floor)` — `200 × F^1.5`
- `partySizeMultiplier(n)` — `1 + 0.15 × (n-1)` for HP

### 3.2 `BossTheme` (theme package)
Enum mapping floor ranges to themes:
- GRASSLAND (1-10), FOREST (11-20), CAVE (21-30), LAVA (31-40), ICE (41-50), DESERT (51-60), SWAMP (61-70), OCEAN (71-80), SKY (81-90), VOID (91-100)
- Each theme has: displayName, primaryColor, ambientParticle

### 3.3 `BossLootTier` (loot package)
Enum for loot tiers:
- TIER_1 (floors 1-25): Purple weapons, basic materials
- TIER_2 (floors 26-50): Orange weapons, mid materials
- TIER_3 (floors 51-75): Red weapons, high materials
- TIER_4 (floors 76-100): Legendary weapons, endgame materials

### 3.4 `BossDrop` (loot package)
Record representing a single loot entry:
- itemName, rarity, minQty, maxQty, dropChance

### 3.5 `BossLootTable` (loot package)
Per-tier loot definitions:
- `getDropsForFloor(floor)` — returns list of BossDrop entries
- Standard drops: boss weapon (100%), materials (100%), WP (100%), XP (100%), coins (100%)
- Bonus drops: protection scroll (15%), reset stone (5%), mount (raid only, 10%)

### 3.6 `BossArenaTemplate` (arena package)
Pre-built arena configs per theme:
- `getTemplateForFloor(floor)` — returns themed arena config
- Each template: arenaSize, floorBlock, wallBlock, ambientEffect, isRaid

### 3.7 `BossPatternFactory` (patterns package)
Factory for pre-configured attack patterns:
- `createMeleeSwipe(name, damage, cooldown)`
- `createAoeSlam(name, damage, radius, cooldown)`
- `createProjectile(name, damage, range, cooldown)`
- `createSummon(name, count, cooldown)`
- `createBuff(name, stat, multiplier, duration, cooldown)`
- `createDebuff(name, stat, reduction, duration, cooldown)`
- `createCharge(name, damage, distance, cooldown)`
- `createTeleport(name, cooldown)`

### 3.8 `BossAttackLibrary` (patterns package)
Reusable pre-built pattern collections:
- `getStandardPatterns(floor)` — basic melee + AoE for standard bosses
- `getRaidPatterns(floor)` — full kit for raid bosses
- `getEnragePatterns(floor)` — desperation attacks

## 4. Boss Implementation Pattern

Each boss follows this structure:
```java
public class FloorNNBoss extends BossEntity {
    // EntityType registration (TYPE_KEY + TYPE)
    // Constructor: set HP, damage from BossStatScaler
    // initPatterns(): use BossPatternFactory
    // getBossName(): display name
    // onPhaseEnter(): stat modifications per phase
    // getLootTable(): return BossLootTable for this floor
    // getArenaTemplate(): return themed arena
}
```

## 5. Delegation Plan

### Agent A: Common Utility Classes
Implements: BossStatScaler, BossTheme, BossLootTier, BossDrop, BossLootTable, BossArenaTemplate, BossPatternFactory, BossAttackLibrary

### Agent B: Floors 15, 20, 30, 35, 40
Implements: Floor15Boss, Floor20Boss, Floor30Boss, Floor35Boss, Floor40Boss

### Agent C: Floors 45, 55, 60, 65, 70
Implements: Floor45Boss, Floor55Boss, Floor60Boss, Floor65Boss, Floor70Boss

### Agent D: Floors 75, 80, 85, 90, 95
Implements: Floor75Boss, Floor80Boss, Floor85Boss, Floor90Boss, Floor95Boss

### Agent E: Floor 100 (Final Boss)
Implements: Floor100Boss with 7 phases, multiple unique mechanics, extended enrage

## 6. Registry Update
All new bosses must be registered in `BossRegistry` during mod init.

## 7. Build & Verification
- Build after each agent completes
- Verify all 20 bosses compile and register correctly
- Run `./gradlew build` to confirm no errors
