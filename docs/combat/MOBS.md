# Aincrad — Mob System Design

**Scope:** Field mobs per floor, mob stats, drops, behavior AI, spawning rules, elite mobs, and floor-themed variants. Covers all 100 floors with 200+ unique mob entries.

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
| 2 | 85 | 10 | 21 |
| 3 | 156 | 17 | 37 |
| 5 | 335 | 32 | 76 |
| 10 | 949 | 80 | 201 |
| 15 | 1743 | 135 | 355 |
| 20 | 2683 | 197 | 530 |
| 25 | 3750 | 263 | 725 |
| 30 | 4930 | 333 | 936 |
| 40 | 7589 | 484 | 1400 |
| 50 | 10607 | 647 | 1913 |
| 60 | 13943 | 820 | 2469 |
| 70 | 17570 | 1002 | 3064 |
| 75 | 19486 | 1096 | 3374 |
| 80 | 21466 | 1191 | 3693 |
| 90 | 25614 | 1389 | 4355 |
| 100 | 30000 | 1592 | 5048 |

**Elite multiplier:** ×3 HP, ×2.5 damage, ×5 XP, guaranteed rarity drop (see §8).

---

## 2. MobType Enum Reference

All mobs are assigned one of **9 behavior types** that define their AI, aggro rules, and combat role.

| # | MobType | Description | Aggro Range | Leash | Key Traits |
|---|---------|-------------|-------------|-------|------------|
| 1 | **Passive** | Never attacks; flees when hit | 0 m | — | Drops common materials; no threat |
| 2 | **Neutral** | Attacks only when hit first | 0 m (provoked) | 30 m | High DEF or AoE; territorial |
| 3 | **Hostile** | Aggro on sight; chases player | 12 m | 30 m | Standard combat mob; melee or basic ranged |
| 4 | **Pack** | Groups of 2–5; aggro one = aggro all | 12 m | 30 m | Share 20% damage aura; howl buff |
| 5 | **Ranged** | Keeps 5–8 m distance; fires projectile every 2 s | 15 m | 30 m | Kites player; elemental projectiles |
| 6 | **Ambusher** | Hides (invisible or disguised); attacks when player within 3 m | 3 m | 20 m | High burst damage; stealth approach |
| 7 | **Summoner** | Calls 2–3 adds every 15 s; adds die if summoner dies | 12 m | 30 m | Heals allies; priority target |
| 8 | **Enraged** | Below 30% HP: +50% attack speed, +25% damage, new attack pattern | 12 m | 30 m | Regenerates; desperate AI at low HP |
| 9 | **Boss** | Mini-boss or floor boss; unique mechanics, phases, guaranteed drops | 20 m | Arena | Summons adds; enrage timer; 100% weapon drop |

**Leash rule:** mobs return to spawn point and heal 10%/s if player moves >30 m away. Boss arenas are excluded from leash.

---

## 3. Field Mobs by Floor Theme

Each floor has a **theme** that determines mob variety. Below are all 100 floors with 2 unique mobs per floor, organized by tier.

### Floors 1–10 — Grassland / Plains

*Tutorial floors; gentle mobs, open terrain*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 1 | Green Rabbit | Passive | Hide (60%), Carrot (30%) | Flees when hit; tutorial mob |
| 1 | Wild Boar | Neutral | Hide (50%), Tusk (20%) | Charges when approached |
| 2 | Giant Rat | Hostile | Rat Tail (40%), Cheese (25%) | Fast melee; packs of 2-3 |
| 2 | Field Slime | Passive | Gel (70%), Slime Core (10%) | Splits into 2 small slimes on death |
| 3 | Goblin Scout | Hostile | Iron Ore (30%), Coin (40%) | Melee; packs of 2-3 |
| 3 | Goblin Shaman | Summoner | Herb (40%), Staff Shard (10%) | Ranged (5 m); heals allies |
| 4 | Alpha Wolf | Pack | Wolf Pelt (50%), Fang (25%) | Packs of 3-5; howl buff |
| 4 | Dire Rabbit | Neutral | Hide (55%), Carrot (35%) | Stronger rabbit; charges when hit |
| 5 | Goblin Warlord | Boss | Iron Sword (100%), Crown (5%) | Summons 2 scouts; enrages at 30% |
| 5 | Goblin Archer | Ranged | Arrow (50%), Bow String (20%) | Ranged (8 m); keeps distance |
| 6 | Stone Golem | Neutral | Stone (60%), Core Shard (15%) | High DEF; AoE slam |
| 6 | Rock Crab | Neutral | Shell (45%), Claw (25%) | High DEF; pinches when approached |
| 7 | Harpy | Hostile | Feather (55%), Talon (20%) | Flying; dive attack |
| 7 | Wind Elemental | Ranged | Wind Essence (40%), Feather (30%) | Ranged wind blade; fast |
| 8 | Ogre | Hostile | Ogre Club (30%), Hide (50%) | Slow but heavy hits; enrages at 30% |
| 8 | Ogre Brute | Hostile | Ogre Club (35%), Bone (40%) | Even slower; massive damage |
| 9 | Chimera | Enraged | Chimera Scale (40%), Fang (30%) | Three-headed; enrages at 30% |
| 9 | Chimera Whelp | Hostile | Scale Shard (35%), Fang (20%) | Smaller chimera; fast |
| 10 | Goblin Overlord | Boss | Overlord Blade (100%), Crown (10%) | Summons 3 scouts; enrages at 30% |
| 10 | Goblin Champion | Elite | Champion Sword (80%), Coin (60%) | Elite variant; aura buff |

### Floors 11–20 — Forest

*Dense trees, wolves, spiders*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 11 | Dire Wolf | Pack | Wolf Pelt (50%), Fang (25%) | Packs of 3-5; howl buff |
| 11 | Forest Spider | Ambusher | Silk (60%), Venom Sac (15%) | Web slow (2 s); ambush from trees |
| 12 | Spider Queen | Boss | Silk (80%), Venom Sac (30%) | Summons 3 broodlings; enrages at 30% |
| 12 | Broodling | Hostile | Silk (40%), Venom (15%) | Small spider; fast |
| 13 | Treant | Neutral | Wood (70%), Sap (30%) | AoE slam; high DEF |
| 13 | Forest Sprite | Passive | Herb (50%), Flower (30%) | Flees when hit; drops healing herbs |
| 14 | Forest Ogre | Hostile | Ogre Club (30%), Hide (50%) | Slow; heavy hits; enrages at 30% |
| 14 | Thorn Beast | Neutral | Thorn (45%), Hide (40%) | Damages attackers; charges when hit |
| 15 | Warg | Pack | Pelt (50%), Fang (25%) | Packs of 3-5; fast |
| 15 | Warg Alpha | Pack | Alpha Pelt (55%), Fang (30%) | Larger; packs of 2-3; howl buff |
| 16 | Spider Matriarch | Boss | Silk (85%), Venom Sac (35%) | Summons 4 broodlings; enrages at 30% |
| 16 | Venom Spider | Ambusher | Silk (65%), Venom (25%) | Poison DoT (5 s); web |
| 17 | Elder Treant | Neutral | Ancient Wood (75%), Sap (35%) | AoE slam; very high DEF |
| 17 | Bark Golem | Neutral | Bark (55%), Core Shard (15%) | High DEF; slow |
| 18 | Banshee | Ranged | Wraith Essence (40%), Cloth (30%) | Sonic screech (stun 1 s); ambush |
| 18 | Wraith | Hostile | Wraith Essence (35%), Cloth (25%) | Fast; passes through walls |
| 19 | Forest Troll | Enraged | Troll Hide (50%), Club (30%) | Regenerates; enrages at 30% |
| 19 | Troll Brute | Hostile | Troll Hide (55%), Bone (40%) | Slower; massive damage |
| 20 | Ancient Treant | Boss | Ancient Wood (80%), Sap (40%) | Summons 2 treants; enrages at 30% |
| 20 | Treant Guardian | Neutral | Wood (65%), Core Shard (20%) | High DEF; AoE slam |

### Floors 21–30 — Cave / Underground

*Dark caverns, bats, cave spiders*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 21 | Cave Bat | Hostile | Wing (40%), Fang (20%) | Flying; sonic screech (stun 1 s) |
| 21 | Cave Crawler | Hostile | Shell (45%), Meat (30%) | Fast; packs of 2-3 |
| 22 | Cave Spider | Ambusher | Silk (70%), Venom (25%) | Poison DoT (5 s); web |
| 22 | Web Spinner | Ranged | Silk (60%), Venom Sac (20%) | Ranged web shot; slow |
| 23 | Rock Golem | Neutral | Stone (60%), Ore (50%) | High DEF; AoE quake |
| 23 | Stone Elemental | Neutral | Stone Essence (40%), Ore (45%) | High DEF; ranged rock throw |
| 24 | Giant Scorpion | Ambusher | Chitin (50%), Stinger (25%) | Poison DoT (5 s); ambush |
| 24 | Scorpion Sting | Hostile | Chitin (40%), Stinger (20%) | Smaller; fast; poison |
| 25 | Gloom Stalker | Ambusher | Dark Shard (35%), Claw (25%) | Invisible; attacks when player within 3 m |
| 25 | Shadow Bat | Hostile | Wing (45%), Fang (25%) | Flying; sonic screech (stun 1 s) |
| 26 | Crystal Golem | Neutral | Crystal (55%), Ore (50%) | High DEF; AoE quake |
| 26 | Crystal Slime | Passive | Gel (80%), Crystal (20%) | Splits into 2 small slimes on death |
| 27 | Spider Broodmother | Boss | Silk (85%), Venom Sac (35%) | Summons 5 broodlings; enrages at 30% |
| 27 | Broodling | Hostile | Silk (40%), Venom (15%) | Small spider; fast |
| 28 | Stone Titan | Enraged | Stone (70%), Core Shard (25%) | Massive; enrages at 30% |
| 28 | Boulder Golem | Neutral | Stone (65%), Ore (55%) | High DEF; slow |
| 29 | Cave Wyrm | Hostile | Wyrm Scale (45%), Fang (30%) | Burrows; ambush |
| 29 | Wyrm Spawn | Hostile | Scale Shard (35%), Fang (20%) | Smaller wyrm; fast |
| 30 | Abyssal Spider | Boss | Abyss Silk (90%), Venom Sac (40%) | Summons 6 broodlings; enrages at 30% |
| 30 | Abyssal Broodling | Hostile | Silk (45%), Venom (20%) | Small spider; fast |

### Floors 31–40 — Lava / Volcanic

*Magma, fire mobs, obsidian terrain*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 31 | Magma Slime | Passive | Gel (80%), Magma Core (15%) | Splits into 2 small slimes; fire aura |
| 31 | Fire Bat | Hostile | Wing (45%), Ember (25%) | Flying; fire breath |
| 32 | Fire Imp | Hostile | Demon Horn (25%), Coin (60%) | Fast; backstab bonus |
| 32 | Imp Trickster | Ambusher | Demon Horn (30%), Coin (65%) | Teleports; backstab bonus |
| 33 | Magma Golem | Neutral | Magma Core (35%), Obsidian (40%) | Immune to fire; AoE eruption |
| 33 | Lava Elemental | Ranged | Fire Essence (45%), Ember (35%) | Ranged fireball (8 m); burn DoT |
| 34 | Flame Spider | Ambusher | Silk (65%), Ember (30%) | Fire web; burn DoT |
| 34 | Ember Wasp | Hostile | Stinger (40%), Ember (30%) | Flying; fire sting |
| 35 | Obsidian Golem | Neutral | Obsidian (55%), Magma Core (30%) | High DEF; AoE eruption |
| 35 | Obsidian Shardling | Hostile | Obsidian Shard (40%), Ember (25%) | Small; fast |
| 36 | Fire Elemental | Ranged | Fire Essence (50%), Ember (30%) | Ranged fireball (8 m); burn DoT |
| 36 | Flame Hound | Pack | Hide (50%), Fang (30%) | Packs of 3-5; fire bite |
| 37 | Magma Wyrm | Hostile | Wyrm Scale (45%), Magma Core (35%) | Burrows; fire breath |
| 37 | Wyrm Spawn | Hostile | Scale Shard (35%), Ember (25%) | Smaller wyrm; fast |
| 38 | Inferno Hound | Pack | Hide (55%), Fang (35%) | Packs of 3-5; fire bite |
| 38 | Hellfire Imp | Ranged | Demon Horn (30%), Coin (65%) | Ranged fireball; fast |
| 39 | Volcanic Titan | Enraged | Obsidian (60%), Magma Core (35%) | Massive; enrages at 30% |
| 39 | Lava Golem | Neutral | Magma Core (40%), Obsidian (45%) | Immune to fire; AoE eruption |
| 40 | Magma Lord | Boss | Magma Core (50%), Obsidian Blade (100%) | Summons 3 imps; enrages at 30% |
| 40 | Magma Champion | Elite | Magma Core (45%), Coin (70%) | Elite variant; aura buff |

### Floors 41–50 — Ice / Snow

*Slippery terrain, strays, ice mobs*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 41 | Ice Slime | Passive | Gel (80%), Ice Crystal (20%) | Splits into 2 small slimes; chill aura |
| 41 | Frost Bat | Hostile | Wing (45%), Ice Shard (25%) | Flying; frostbite slow |
| 42 | Stray Chief | Hostile | Bone (50%), Arrow (40%) | Ranged bone throw; packs of 2-3 |
| 42 | Stray Archer | Ranged | Bone (45%), Arrow (35%) | Ranged (10 m); keeps distance |
| 43 | Frost Spider | Ambusher | Silk (65%), Ice Crystal (25%) | Ice web; chill DoT |
| 43 | Ice Web Spinner | Ranged | Silk (60%), Ice Crystal (20%) | Ranged ice web shot; slow |
| 44 | Ice Golem | Neutral | Ice (55%), Crystal (40%) | High DEF; AoE freeze (3 s) |
| 44 | Frost Elemental | Ranged | Ice Essence (45%), Frost Shard (35%) | Ranged ice spike; chill DoT |
| 45 | Frost Wolf | Pack | Frost Pelt (50%), Fang (30%) | Packs of 3-5; frostbite slow |
| 45 | Ice Wolf Alpha | Pack | Alpha Pelt (55%), Fang (35%) | Larger; packs of 2-3; howl buff |
| 46 | Snow Wraith | Ambusher | Wraith Essence (40%), Ice Crystal (30%) | Invisible; attacks when player within 3 m |
| 46 | Blizzard Bat | Hostile | Wing (45%), Ice Shard (25%) | Flying; frostbite slow |
| 47 | Ice Elemental | Ranged | Ice Essence (50%), Frost Shard (30%) | Ranged ice spike; chill DoT |
| 47 | Frost Giant | Neutral | Giant Bone (40%), Ice Crystal (35%) | AoE freeze (3 s); high HP |
| 48 | Frost Giant | Neutral | Giant Bone (45%), Ice Crystal (40%) | AoE freeze (3 s); very high HP |
| 48 | Ice Titan | Enraged | Ice (60%), Crystal (35%) | Massive; enrages at 30% |
| 49 | Blizzard Harpy | Hostile | Feather (55%), Talon (25%) | Flying; dive attack; chill |
| 49 | Snow Harpy | Hostile | Feather (50%), Talon (20%) | Flying; dive attack |
| 50 | Frost Wyrm | Boss | Frost Scale (50%), Ice Blade (100%) | Summons 3 frost wolves; enrages at 30% |
| 50 | Frost Champion | Elite | Frost Scale (45%), Coin (70%) | Elite variant; aura buff |

### Floors 51–60 — Desert

*Sand, husks, cacti, temples*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 51 | Desert Bandit | Pack | Coin (60%), Dagger (30%) | Packs of 2-3; fast |
| 51 | Sand Slime | Passive | Gel (80%), Sand Crystal (15%) | Splits into 2 small slimes |
| 52 | Sand Slime | Passive | Gel (80%), Sand Crystal (20%) | Splits into 2 small slimes |
| 52 | Sand Scorpion | Ambusher | Chitin (50%), Stinger (25%) | Poison DoT (5 s); ambush |
| 53 | Mummy | Enraged | Bandage (50%), Gold (30%) | Regenerates; enrages at 30% |
| 53 | Tomb Spider | Ambusher | Silk (65%), Venom (25%) | Poison DoT (5 s); web |
| 54 | Sand Scorpion | Ambusher | Chitin (55%), Stinger (30%) | Poison DoT (5 s); ambush |
| 54 | Desert Viper | Hostile | Venom Sac (40%), Fang (30%) | Fast; poison bite |
| 55 | Desert Wraith | Ambusher | Wraith Essence (40%), Sand Crystal (30%) | Invisible; attacks when player within 3 m |
| 55 | Sand Elemental | Ranged | Sand Essence (45%), Crystal (35%) | Ranged sand blast; slow |
| 56 | Sand Golem | Neutral | Sandstone (55%), Crystal (40%) | High DEF; AoE quake |
| 56 | Desert Golem | Neutral | Sandstone (60%), Crystal (45%) | High DEF; AoE quake |
| 57 | Tomb Guardian | Enraged | Gold (45%), Artifact (20%) | Regenerates; enrages at 30% |
| 57 | Scarab Swarm | Pack | Shell (40%), Gold (25%) | Packs of 4-6; fast |
| 58 | Sand Wyrm | Hostile | Wyrm Scale (45%), Fang (30%) | Burrows; ambush |
| 58 | Wyrm Spawn | Hostile | Scale Shard (35%), Fang (20%) | Smaller wyrm; fast |
| 59 | Desert Titan | Enraged | Sandstone (60%), Crystal (35%) | Massive; enrages at 30% |
| 59 | Sand Golem | Neutral | Sandstone (65%), Crystal (50%) | High DEF; slow |
| 60 | Pharaoh's Wrath | Boss | Gold (60%), Pharaoh Blade (100%) | Summons 3 mummies; enrages at 30% |
| 60 | Mummy Lord | Summoner | Bandage (55%), Gold (40%) | Summons 2 scarabs; regenerates |

### Floors 61–70 — Swamp

*Water, slimes, witches, mangroves*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 61 | Swamp Slime | Passive | Gel (80%), Swamp Core (15%) | Splits into 2 small slimes; poison aura |
| 61 | Bog Bat | Hostile | Wing (45%), Fang (25%) | Flying; sonic screech (stun 1 s) |
| 62 | Bog Spider | Ambusher | Silk (65%), Venom (25%) | Poison DoT (5 s); web |
| 62 | Swamp Viper | Hostile | Venom Sac (40%), Fang (30%) | Fast; poison bite |
| 63 | Witch | Summoner | Potion (40%), Hat (15%) | Summons 2 familiars; ranged poison |
| 63 | Witch's Familiar | Hostile | Eye (30%), Wing (20%) | Small; fast |
| 64 | Swamp Golem | Neutral | Mud (55%), Core Shard (20%) | High DEF; AoE slam |
| 64 | Mud Golem | Neutral | Mud (60%), Core Shard (25%) | High DEF; slow |
| 65 | Will-o'-Wisp | Ambusher | Wisp Essence (40%), Glowstone (30%) | Invisible; ranged fire; ambush |
| 65 | Wisp | Ranged | Wisp Essence (35%), Glowstone (25%) | Ranged fireball; fast |
| 66 | Bog Troll | Enraged | Troll Hide (50%), Club (30%) | Regenerates; enrages at 30% |
| 66 | Troll Brute | Hostile | Troll Hide (55%), Bone (40%) | Slower; massive damage |
| 67 | Swamp Wraith | Ambusher | Wraith Essence (40%), Cloth (30%) | Invisible; attacks when player within 3 m |
| 67 | Bog Wraith | Ambusher | Wraith Essence (35%), Cloth (25%) | Invisible; fast |
| 68 | Mangrove Treant | Neutral | Wood (70%), Sap (30%) | AoE slam; high DEF |
| 68 | Swamp Treant | Neutral | Wood (65%), Sap (25%) | AoE slam; high DEF |
| 69 | Swamp Hydra | Enraged | Hydra Scale (45%), Fang (30%) | Three-headed; enrages at 30% |
| 69 | Hydra Head | Hostile | Scale Shard (35%), Fang (20%) | Smaller; fast |
| 70 | Bog Overlord | Boss | Bog Core (50%), Overlord Club (100%) | Summons 3 trolls; enrages at 30% |
| 70 | Bog Champion | Elite | Bog Core (45%), Coin (70%) | Elite variant; aura buff |

### Floors 71–80 — Ocean

*Drowned, guardians, underwater ruins*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 71 | Drowned | Hostile | Flesh (50%), Trident (15%) | Melee; packs of 2-3 |
| 71 | Drowned Sailor | Hostile | Flesh (45%), Coin (40%) | Melee; packs of 2-3 |
| 72 | Guardian | Ranged | Prismarine (45%), Crystal (20%) | Ranged laser (10 m); high DEF |
| 72 | Elder Guardian Spawn | Ranged | Prismarine (50%), Crystal (25%) | Ranged laser (12 m); high DEF |
| 73 | Ocean Slime | Passive | Gel (80%), Ocean Core (15%) | Splits into 2 small slimes |
| 73 | Coral Slime | Passive | Gel (75%), Coral (25%) | Splits into 2 small slimes |
| 74 | Drowned Captain | Summoner | Flesh (55%), Trident (25%) | Summons 2 drowned; melee |
| 74 | Drowned Crew | Pack | Flesh (45%), Coin (35%) | Packs of 3-4; melee |
| 75 | Sea Serpent | Hostile | Serpent Scale (45%), Fang (30%) | Fast; water swim |
| 75 | Serpent Spawn | Hostile | Scale Shard (35%), Fang (20%) | Smaller; fast |
| 76 | Elder Guardian | Ranged | Prismarine (55%), Crystal (30%) | Ranged laser (12 m); very high DEF |
| 76 | Guardian Spawn | Ranged | Prismarine (45%), Crystal (20%) | Ranged laser (10 m); high DEF |
| 77 | Sunken Golem | Neutral | Prismarine (55%), Crystal (40%) | High DEF; AoE slam |
| 77 | Coral Golem | Neutral | Coral (50%), Crystal (35%) | High DEF; AoE slam |
| 78 | Kraken Spawn | Hostile | Tentacle (45%), Ink (30%) | Fast; water swim |
| 78 | Kraken Tentacle | Hostile | Tentacle (40%), Ink (25%) | Fast; water swim |
| 79 | Abyssal Guardian | Ranged | Prismarine (60%), Crystal (35%) | Ranged laser (15 m); very high DEF |
| 79 | Deep Guardian | Ranged | Prismarine (55%), Crystal (30%) | Ranged laser (12 m); high DEF |
| 80 | Kraken | Boss | Kraken Tentacle (60%), Kraken Eye (100%) | Summons 4 tentacles; enrages at 30% |
| 80 | Kraken Spawn | Hostile | Tentacle (45%), Ink (30%) | Fast; water swim |

### Floors 81–90 — Sky / Cloud

*Floating islands, phantoms, shulkers*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 81 | Phantom | Hostile | Membrane (45%), XP Bottle (20%) | Flying; dive attack |
| 81 | Cloud Bat | Hostile | Wing (45%), Cloud Essence (25%) | Flying; sonic screech (stun 1 s) |
| 82 | Cloud Slime | Passive | Gel (80%), Cloud Core (15%) | Splits into 2 small slimes |
| 82 | Sky Slime | Passive | Gel (75%), Sky Crystal (20%) | Splits into 2 small slimes |
| 83 | Sky Harpy | Hostile | Feather (55%), Talon (25%) | Flying; dive attack |
| 83 | Storm Harpy | Hostile | Feather (50%), Talon (20%) | Flying; dive attack; lightning |
| 84 | Shulker | Ranged | Shell (50%), Shulker Bullet (30%) | Ranged (15 m); levitation |
| 84 | Shulker Bullet | Ranged | Shell Shard (40%), Crystal (25%) | Ranged; fast |
| 85 | Storm Elemental | Ranged | Storm Essence (45%), Crystal (35%) | Ranged lightning; fast |
| 85 | Lightning Elemental | Ranged | Lightning Essence (40%), Crystal (30%) | Ranged lightning; very fast |
| 86 | Sky Golem | Neutral | Cloud (55%), Crystal (40%) | High DEF; AoE slam |
| 86 | Cloud Golem | Neutral | Cloud (50%), Crystal (35%) | High DEF; AoE slam |
| 87 | Thunderbird | Hostile | Feather (55%), Talon (30%) | Flying; dive attack; lightning |
| 87 | Storm Bird | Hostile | Feather (50%), Talon (25%) | Flying; dive attack |
| 88 | Cloud Wyrm | Hostile | Wyrm Scale (45%), Fang (30%) | Flying; fire breath |
| 88 | Wyrm Spawn | Hostile | Scale Shard (35%), Fang (20%) | Smaller wyrm; fast |
| 89 | Sky Titan | Enraged | Cloud (60%), Crystal (35%) | Massive; enrages at 30% |
| 89 | Storm Golem | Neutral | Cloud (55%), Crystal (30%) | High DEF; AoE slam |
| 90 | Storm Lord | Boss | Storm Core (50%), Storm Blade (100%) | Summons 3 elementals; enrages at 30% |
| 90 | Storm Champion | Elite | Storm Core (45%), Coin (70%) | Elite variant; aura buff |

### Floors 91–100 — Void / Final

*End-themed, endermen, final boss on 100*

| Floor | Mob | Behavior | Drops | Notes |
|-------|-----|----------|-------|-------|
| 91 | Enderman | Ambusher | Ender Pearl (40%), Obsidian (25%) | Teleports; attacks when looked at |
| 91 | Void Bat | Hostile | Wing (45%), Void Essence (25%) | Flying; sonic screech (stun 1 s) |
| 92 | Void Slime | Passive | Gel (80%), Void Core (15%) | Splits into 2 small slimes; void aura |
| 92 | Void Stalker | Ambusher | Void Essence (40%), Obsidian (30%) | Invisible; attacks when player within 3 m |
| 93 | Void Stalker | Ambusher | Void Essence (45%), Obsidian (35%) | Invisible; attacks when player within 3 m |
| 93 | Void Wisp | Ranged | Void Essence (40%), Crystal (30%) | Ranged void bolt; fast |
| 94 | Void Golem | Neutral | Obsidian (55%), Void Core (35%) | High DEF; AoE slam |
| 94 | Void Elemental | Ranged | Void Essence (45%), Crystal (35%) | Ranged void bolt; fast |
| 95 | Ender Spider | Ambusher | Silk (65%), Ender Pearl (30%) | Teleports; poison DoT |
| 95 | Void Spider | Ambusher | Silk (60%), Void Essence (25%) | Invisible; poison DoT |
| 96 | Void Wraith | Ambusher | Void Essence (45%), Obsidian (35%) | Invisible; attacks when player within 3 m |
| 96 | Void Specter | Ambusher | Void Essence (40%), Crystal (30%) | Invisible; fast |
| 97 | Ender Dragon Spawn | Hostile | Dragon Scale (45%), Ender Pearl (35%) | Flying; fire breath |
| 97 | Dragon Whelp | Hostile | Scale Shard (35%), Ender Pearl (25%) | Smaller; fast |
| 98 | Void Titan | Enraged | Obsidian (60%), Void Core (35%) | Massive; enrages at 30% |
| 98 | Void Golem | Neutral | Obsidian (55%), Void Core (30%) | High DEF; AoE slam |
| 99 | The Gleam Eye | Boss | Gleam Essence (50%), Gleam Core (100%) | Summons 3 void wisps; enrages at 30% |
| 99 | Gleam Spawn | Hostile | Gleam Essence (40%), Crystal (30%) | Small; fast |
| 100 | Heathcliff / Administrator | Boss | Admin Core (100%), Void Blade (100%) | Summons 4 void champions; enrages at 30% |
| 100 | Void Champion | Elite | Void Core (45%), Coin (70%) | Elite variant; aura buff |

---

## 4. Floor-Themed Mob Variants

Each floor tier has **elemental variants** of base mob types. These variants share the same AI behavior but have themed visuals, elemental damage, and tier-specific drops.

| Tier | Theme | Variant Prefix | Elemental Effect | Example Mobs |
|------|-------|---------------|-----------------|--------------|
| 1 | Grassland | — | None (base) | Green Rabbit, Wild Boar |
| 2 | Forest | — | Poison (spiders), Howl (wolves) | Dire Wolf, Forest Spider |
| 3 | Cave | — | Darkness (blind 1 s), Poison | Gloom Stalker, Cave Spider |
| 4 | Lava | Flame / Magma / Inferno / Obsidian / Hellfire | Burn DoT (5 s), Fire immunity (golems) | Fire Elemental, Magma Golem, Flame Hound |
| 5 | Ice | Frost / Ice / Blizzard / Snow | Chill DoT (5 s), Freeze (3 s), Frostbite slow | Ice Elemental, Frost Giant, Blizzard Harpy |
| 6 | Desert | Sand / Tomb / Scarab / Pharaoh | Sand slow, Regen (mummies), Poison (scorpions) | Sand Golem, Mummy, Sand Wyrm |
| 7 | Swamp | Bog / Swamp / Will-o'-Wisp / Mangrove | Poison DoT, Fire (wisps), Regen (trolls) | Swamp Slime, Witch, Bog Troll |
| 8 | Ocean | Drowned / Coral / Kraken / Abyssal | Water swim, Laser (guardians), Ink blind | Drowned, Guardian, Kraken |
| 9 | Sky | Cloud / Storm / Thunder / Lightning | Lightning strike, Levitation (shulker), Dive attack | Phantom, Shulker, Thunderbird |
| 10 | Void | Void / Ender / Gleam / Abyssal | Teleport, Void bolt, Ender pearl drops | Enderman, Void Stalker, The Gleam Eye |

**Variant rules:**
- Elemental variants deal their element's damage type in addition to physical damage.
- Burn DoT: 5% max HP per second for 5 s (fire mobs).
- Chill DoT: 3% max HP per second for 5 s + 20% move speed slow (ice mobs).
- Poison DoT: 4% max HP per second for 5 s (spiders, scorpions, vipers).
- Freeze: 3 s stun, 50% damage taken increase (ice golems, frost giants).
- Sand slow: 30% move speed reduction for 4 s (desert elementals).
- Ink blind: 50% accuracy reduction for 3 s (kraken tentacles).

---

## 5. Mob Behavior AI

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
| **Boss** | Mini-boss or floor boss; unique mechanics, phases, guaranteed drops |

**Leash rule:** mobs return to spawn point and heal 10%/s if player moves >30 m away.

---

## 6. Spawning Rules

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

## 7. Drops

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

## 8. Elite Mobs

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

### Elite Special Abilities

Each elite has a **unique special ability** based on its base mob type:

| Base Type | Elite Special Ability | Effect |
|-----------|----------------------|--------|
| Wolf / Hound | **Alpha Howl** | +30% attack speed to all pack members within 15 m, 10 s CD |
| Spider / Broodling | **Venom Burst** | On death, releases poison cloud (5 m radius, 10 s DoT) |
| Golem / Titan | **Shatter** | On death, explodes for 50% max HP as AoE damage |
| Elemental | **Elemental Nova** | Every 20 s, AoE burst (8 m radius) of elemental damage |
| Imp / Trickster | **Shadow Step** | Teleports behind target every 8 s, next attack +50% damage |
| Troll / Brute | **Regeneration** | Heals 2% max HP per second, doubled below 30% HP |
| Wraith / Specter | **Phase Shift** | Becomes untargetable for 3 s, 15 s CD |
| Wyrm / Serpent | **Burrow Ambush** | Burrows and resurfaces under target, 200% damage on emerge |
| Shulker | **Levitation Field** | All players within 10 m levitate for 4 s, 20 s CD |
| Enderman / Void Stalker | **Void Swap** | Swaps positions with target, 12 s CD |

**Elite identification:** purple nameplate, particle effect, distinct sound on spawn. Elites are the primary source of blue/purple rarity weapons outside bosses.

---

## 9. Mob Summary Table (All 100 Floors)

| Floor range | Theme | Mob count | Key mobs | Avg HP | Avg dmg | Avg XP | Elite chance |
|-------------|-------|-----------|----------|--------|---------|--------|--------------|
| 1–10 | Grassland / Plains | 2 per floor | Green Rabbit, Goblin Scout, Goblin Overlord | 30–949 | 4–80 | 8–201 | 5% |
| 11–20 | Forest | 2 per floor | Dire Wolf, Spider Queen, Ancient Treant | 1094–2683 | 90–197 | 230–530 | 5% |
| 21–30 | Cave / Underground | 2 per floor | Cave Bat, Crystal Golem, Abyssal Spider | 2887–4930 | 209–333 | 568–936 | 5% |
| 31–40 | Lava / Volcanic | 2 per floor | Fire Elemental, Magma Golem, Magma Lord | 5178–7589 | 347–484 | 979–1400 | 5% |
| 41–50 | Ice / Snow | 2 per floor | Frost Wolf, Ice Elemental, Frost Wyrm | 7876–10607 | 500–647 | 1449–1913 | 5% |
| 51–60 | Desert | 2 per floor | Sand Golem, Mummy, Pharaoh's Wrath | 10926–13943 | 664–820 | 1966–2469 | 5% |
| 61–70 | Swamp | 2 per floor | Swamp Slime, Witch, Bog Overlord | 14293–17570 | 838–1002 | 2527–3064 | 5% |
| 71–80 | Ocean | 2 per floor | Drowned, Guardian, Kraken | 17948–21466 | 1020–1191 | 3125–3693 | 5% |
| 81–90 | Sky / Cloud | 2 per floor | Phantom, Shulker, Storm Lord | 21870–25614 | 1211–1389 | 3758–4355 | 5% |
| 91–100 | Void / Final | 2 per floor | Enderman, Void Stalker, Heathcliff / Administrator | 26043–30000 | 1409–1592 | 4423–5048 | 5% |

**Total unique mob entries:** 200 (2 per floor × 100 floors)
**Total unique mob names:** 188 (some names recur as cross-floor variants)
**Behavior types:** 9 (Passive, Neutral, Hostile, Pack, Ranged, Ambusher, Summoner, Enraged, Boss)
**Elite variants:** 1 per floor (5% spawn chance, ×3 HP, ×2.5 damage, ×5 XP)
