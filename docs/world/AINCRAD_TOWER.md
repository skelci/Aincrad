# The Aincrad Tower Structure

## 1. Overview

The Aincrad tower is a 100-floor vertical structure. Due to Minecraft's world height limit (384 blocks total, Y −64 to 319), a single-world vertical stack is not feasible for 100 floors. Instead, the tower uses **instanced dimensions**: each floor is a separate dimension instance, and players teleport between them via portals.

## 2. Representation: Instanced Dimensions

### 2.1 Why not a single world?

- 100 floors × ~30 blocks height = ~3000 blocks, far exceeding the 384-block world height limit.
- A single world would keep all 100 floors loaded, causing severe memory and tick lag.
- Instanced dimensions allow each floor to be loaded/unloaded independently.

### 2.2 Dimension model

- Each floor is a registered **Dimension** with its own **ChunkGenerator**.
- All floors share the same dimension type (same sky, fog, etc.) but differ in:
  - Chunk generator (theme: terrain, blocks, mobs)
  - Biome source
  - Floor-specific data (boss state, unlock status)
- Floors are registered at startup (100 dimensions) but only the player's current floor is loaded.

### 2.3 Floor lifecycle

- A floor dimension is **created** when a player first enters it (or pre-generated for Floor 1).
- The floor remains loaded while at least one player is present.
- When the last player leaves, the floor is **unloaded** (entities ticked, chunks saved).
- Boss state and unlock progress are persisted to disk so they survive unload.

## 3. The Starting Town (Floor 1 Plaza)

### 3.1 Layout

- Floor 1 is the **hub town**, a 1024 × 1024 block plaza.
- Central plaza (256 × 256) with:
  - Spawn point (where new players arrive)
  - NPCs: quest givers, shopkeepers, guild registrar, matchmaking board
  - Notice board (news, events)
- Surrounding districts:
  - **Market district**: player shops, auction house
  - **Residential district**: player housing plots (optional)
  - **Training district**: practice dummies, tutorial area
  - **Portal district**: portals to unlocked floors

### 3.2 Safe zone

- The entire Floor 1 plaza is a safe zone: no mob spawning, no PvP, no fall damage.
- New players spawn here and cannot leave until they complete the tutorial (optional gate).

### 3.3 Services

- Floor 1 hosts the services provided by the plugin: guild creation, economy shops, matchmaking boards.
- These are accessible via NPCs or physical blocks (shop blocks, matchmaking terminals).

## 4. Safe Zones

### 4.1 Design

- Every floor has a **safe zone** at its entrance (64 × 64 blocks around the spawn portal).
- Safe zones prevent mob spawning and PvP.
- The Floor 1 plaza is a full-floor safe zone.

### 4.2 Visual indication

- Safe zones are marked by a subtle particle boundary or a change in floor block texture.
- A HUD indicator shows when the player enters/leaves a safe zone.

### 4.3 Configurability

- Safe zone size and rules are configurable per floor.
- Some floors may have additional safe zones (e.g., a mid-floor rest area on long floors).

## 5. Death Mechanics

### 5.1 SAO-style death penalty

In SAO, death is permanent. For a Minecraft mod, we implement a scaled penalty:

- **Item loss**: the player drops their entire inventory on death (SAO-style).
- **Respawn**: the player respawns at the last safe zone they visited (Floor 1 plaza or the current floor's entrance).
- **Col penalty**: the player loses 10% of their col (currency).
- **XP penalty**: the player loses 5% of their current level progress (not levels themselves).

### 5.2 Hardcore mode (optional)

- A true **permadeath** mode: on death, the player's character is deleted.
- This is opt-in and clearly warned.
- Hardcore characters are flagged in the UI and leaderboard.

### 5.3 Revival

- A rare **Soul Crystal** item can be consumed to revive in place without item loss (drops from bosses, low chance).
- Revival items are consumed on use.

### 5.4 Death in a boss arena

- If the player dies during a boss fight, the boss resets to full health and the arena resets.
- The player respawns at the floor entrance and can re-enter the arena.

## 6. Floor Portals

### 6.1 Entrance portal

- Each floor has an **entrance portal** at the spawn point, linking from the previous floor (or Floor 1 for Floor 2).
- The portal is inactive until the previous floor's boss is defeated.

### 6.2 Exit portal (stairway)

- After the boss is defeated, an **exit portal** activates at the boss arena center.
- Entering it teleports the player to the next floor's entrance portal.

### 6.3 Hub portal

- A **return portal** in each floor's safe zone teleports the player back to Floor 1 plaza.
- This allows quick return to town for shopping, guild activities, etc.

### 6.4 Portal mechanics

- Portals are custom blocks with a themed particle effect.
- Teleportation is instant (no loading screen in single-player; brief dimension load in multiplayer).
- Portals are one-way (entrance → floor, floor → next floor, floor → hub) to prevent accidental backtracking.

## 7. Visual Identity

- Each tier has a distinct color palette and architectural style:
  - Tier 1: green/brown (grassland)
  - Tier 2: dark green (forest)
  - Tier 3: gray/black (cave)
  - Tier 4: red/orange (lava)
  - Tier 5: white/blue (ice)
  - Tier 6: yellow/tan (desert)
  - Tier 7: dark teal (swamp)
  - Tier 8: deep blue (ocean)
  - Tier 9: light blue/white (sky)
  - Tier 10: black/purple (void)
- The tower exterior (visible from Floor 1 plaza looking up) shows the stacked floors as a massive spire.

## 8. World Height and Build Limits

- Each floor dimension uses the full world height (−64 to 319) for terrain variation.
- The floor's playable area is typically Y 64–128, with bedrock walls extending to the world boundaries.
- This allows for tall ceilings, underground caves, and floating islands within a single floor.
