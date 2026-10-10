# Aincrad Technical Architecture

## 1. Overview

Aincrad is a two-part system:

- **Aincrad Mod** (Fabric) — client + server, all custom content: mobs, items, blocks, dimensions, rendering, combat.
- **Aincrad Plugin** (Paper/Purpur) — server-side services: guilds, economy, matchmaking, persistence, web integration.

The mod and plugin communicate over the Minecraft protocol's plugin-messaging channel (custom payloads), which is part of the vanilla protocol and works between a Fabric client and a Paper server without any proxy.

## 2. Component Responsibilities

### 2.1 Fabric Mod (client + server)

- Custom content: mobs (GeckoLib animated), items, blocks, entities, particles
- Floor dimensions and chunk generators
- Combat system (skills, combos, weapon types)
- Boss AI and arena logic
- Client rendering: HUD, menus, models, animations
- Networking: plugin-message client ↔ plugin
- World gen: floor themes, boss arenas, town

### 2.2 Paper/Purpur Plugin (server only)

- Guild system (creation, ranks, chat, shared storage)
- Economy (col currency, shops, taxes, auctions)
- Matchmaking (party finder, raid queues, dungeon matchmaking)
- Persistence (MySQL/PostgreSQL: player data, guild data, economy logs)
- Web API (REST for website integration, leaderboards)
- Anti-cheat hooks (optional)
- Cross-server support (if multiple servers behind a proxy)

### 2.3 Why this split

- Custom content (mobs, dimensions, rendering) requires a mod — not possible in a Bukkit plugin.
- Services like guilds/economy/matchmaking benefit from the mature Bukkit ecosystem, easy database access, and web integration.
- Plugin messaging keeps them decoupled: the mod can run on a Fabric server (single-player/LAN) without the plugin, and the plugin can run without the mod (vanilla clients see a normal server).

## 3. Networking Protocol

### 3.1 Transport

Plugin messages (custom payloads) over the Minecraft protocol. Channel namespace: `aincrad:`.

### 3.2 Message types

Client → Server:

- `c2s/guild/create`, `c2s/guild/invite`, `c2s/guild/chat`
- `c2s/economy/transfer`, `c2s/economy/shop_buy`
- `c2s/matchmaking/join_queue`, `c2s/matchmaking/leave_queue`
- `c2s/party/invite`, `c2s/party/accept`

Server → Client:

- `s2c/guild/update`, `s2c/guild/chat`
- `s2c/economy/balance`, `s2c/economy/transaction`
- `s2c/matchmaking/status`, `s2c/matchmaking/found`
- `s2c/party/update`

### 3.3 Serialization

JSON payloads (via Gson) for complex objects; primitive encoding for simple messages. All messages include a `version` field for forward compatibility.

### 3.4 Reliability

Plugin messages are reliable and ordered (they ride on the Minecraft protocol's reliable transport). For critical operations (economy transfers), the plugin is the source of truth and the mod sends a request; the plugin validates and responds.

## 4. Mod Versioning Strategy

### 4.1 Version scheme

`MAJOR.MINOR.PATCH+mcVERSION` e.g. `1.4.2+1.21.1`

- MAJOR: breaking changes (protocol, save format)
- MINOR: new features (backward compatible)
- PATCH: bug fixes

### 4.2 Protocol compatibility

- The plugin-message protocol has its own version field.
- The mod and plugin negotiate on handshake; mismatched major versions are rejected with a user-facing message.
- N-1 compatibility: the plugin accepts messages from the previous minor version of the mod.

### 4.3 Save format

- Player data versioned with a schema version; migrations run on load.
- World data (floor unlocks) stored per-world in the mod's data files.

## 5. Dependencies

### 5.1 Mod (Fabric)

- Fabric API (required)
- GeckoLib (animated mobs/entities)
- Cloth Config + Mod Menu (config UI)
- (Optional) EMI/JEI for recipe viewing

### 5.2 Plugin (Paper)

- Paper API 1.21+
- (Optional) Vault-compatible economy bridge
- MySQL/PostgreSQL JDBC driver (shaded)

### 5.3 Shared

- Gson (JSON) — shaded into both

## 6. Build System

### 6.1 Gradle

- Fabric Loom Gradle plugin for the mod.
- Gradle with Shadow plugin for the plugin.
- Java toolchain: JDK 21+.
- `gradle build` produces:
  - `build/libs/aincrad-mod-<version>.jar` (Fabric mod)
  - `build/libs/aincrad-plugin-<version>.jar` (Paper plugin)

### 6.2 Version catalog

- Gradle version catalog (`gradle/libs.versions.toml`) for centralized dependency versions.

## 7. Multi-Loader Considerations

- Target: Fabric only (client + server).
- Code is structured loader-agnostic where possible: content registration behind interfaces, no direct Fabric API calls in game logic.
- If NeoForge/Quilt support is added later, only the registration layer changes.
- The plugin side is loader-independent (Bukkit API).

## 8. Repository Layout

```
aincrad/
├── aincrad-mod/          # Fabric mod (Gradle subproject)
│   ├── src/main/java/com/aincrad/mod/
│   └── src/main/resources/
├── aincrad-plugin/       # Paper plugin (Gradle subproject)
│   ├── src/main/java/com/aincrad/plugin/
│   └── src/main/resources/
├── docs/
│   ├── tech/
│   └── world/
├── gradle/
└── build.gradle
```
