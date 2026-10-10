# Aincrad HUD & UI Design

## Overview

The Aincrad HUD replaces vanilla Minecraft's hotbar/health display with an
SAO-inspired interface: blue translucent panels, radial cooldowns, and
contextual menu windows. Every UI element is drawn client-side (Fabric mod)
and styled consistently with SAO's "blue window" aesthetic.

**Design language:** translucent blue (`rgba(0, 80, 180, 0.35)`), thin
lighter-blue borders (`rgba(0, 180, 255, 0.8)`), white text with subtle
shadow, `Minecraft` font family (monospace), hover-highlight in cyan.

---

## 1. Main HUD Layout

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  ┌──────────┐                                    ┌────────────┐ │
│  │ Player   │                                    │ Minimap    │ │
│  │ Status   │                                    │            │ │
│  └──────────┘                                    └────────────┘ │
│                                                                 │
│                        (world view)                             │
│                                                                 │
│                          ┌──────┐                               │
│                          │ Boss │                               │
│                          │ HP   │                               │
│                          └──────┘                               │
│                                                                 │
│  ┌──────────┐          ┌──────────────────┐          ┌────────┐ │
│  │ Party    │          │  Hotbar          │          │Menu    │ │
│  │ Frames   │          │  + Skill Bar     │          │Buttons │ │
│  └──────────┘          └──────────────────┘          └────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Player Status Panel (Top-Left)

Displays the player's own vitals in a translucent blue window.

```
┌─ Kirito ──────────── [Lv. 42] ─┐
│ HP ████████████████████░ 87%   │
│ MP ████████░░░░░░░░░░░░ 45%    │
│ ST ██████████████░░░░░░ 72%    │
│                                │
│ Buffs: [ATK↑ 1:23] [DEF↑ 0:45] │
└────────────────────────────────┘
```

| Element | Behavior |
|---|---|
| **HP bar** | Red-orange gradient. Flashes white when hit. |
| **MP bar** | Blue. Drains on skill use, regenerates slowly. |
| **ST (Stamina) bar** | Green-yellow. Depletes on dodge/attack, regens when idle. |
| **Level badge** | Shows current level; animates on level-up with a glow pulse. |
| **Buff/debuff icons** | 32×32 px icons with remaining time below; click to dismiss (if applicable). |

**Interaction:** Click on the panel to open the **Character Menu** (§9.1).

---

## 3. Skill Bar & Cooldown Radial (Bottom-Center)

Replaces vanilla hotbar display. Shows the player's equipped skills with
radial cooldown overlays.

```
┌────────────────────────────────────────────────────┐
│  [1]    [2]    [3]    [4]    [5]    [6]    [7]     │
│  Sword  Heal    Dash   Fire   Guard  Buff   Swap   │
│  ████████░░░░  (radial sweep)                      │
└────────────────────────────────────────────────────┘
```

| Element | Behavior |
|---|---|
| **Skill slot** | 48×48 px. Shows skill icon. Number = hotkey. |
| **Cooldown overlay** | Dark radial sweep clockwise from top-right. Numeric seconds overlay when >2s. |
| **Keybind** | Number keys 1–7 equip; right-click to use; left-click to assign. |
| **Weapon swap** | Slot 7 is always "Swap Weapon" (swaps main-hand/off-hand loadouts). |
| **Passive indicator** | Passive skills show a soft pulsing border instead of a cooldown. |

**Cooldown display:** The radial is drawn using `drawPie` with the arc
proportional to `remaining / total`. When cooldown is complete, the icon
briefly brightens.

---

## 4. Party Frames (Left Side)

Shows up to 5 party members in stacked frames (§5 Dungeon = 5-player max).

```
┌─ Party (3/5) ─────────────────┐
│ ┌─ Asuna ─────── [Lv. 40] ─┐ │
│ │ HP ██████████░░░░░ 78%   │ │
│ │ MP ██████████████░ 92%   │ │
│ └──────────────────────────┘ │
│ ┌─ Klein ─────── [Lv. 38] ─┐ │
│ │ HP ███████░░░░░░░░░ 55%  │ │
│ │ MP ████████░░░░░░░░ 40%  │ │
│ └──────────────────────────┘ │
│ ┌─ Agil ──────── [Lv. 39] ─┐ │
│ │ HP ██████████████░ 100%  │ │
│ │ MP ████████████░░░░ 70%  │ │
│ └──────────────────────────┘ │
└────────────────────────────────┘
```

| Element | Behavior |
|---|---|
| **Member name** | Color-coded by role: Tank = orange, Healer = green, DPS = white. |
| **HP/MP bars** | Mirrors own status panel. |
| **Target indicator** | Click a frame to set as target. Target's frame border turns cyan. |
| **Aggro indicator** | Small red icon on frame if that member has threat on the targeted mob. |
| **Status icons** | Poison, stun, buffs shown as 16×16 px icons in a row. |
| **Role icon** | Small class icon (sword/shield/staff/bow) next to name. |

---

## 5. Boss Health Bar (Top-Center)

Appears when a player targets or is in combat with a named boss. Disappears
on boss death or when combat ends.

```
┌─ Floor 15 Boss: The Gleam Eyes ────────────────────────┐
│ ████████████████████████████████████████████░░░░░░ 78% │
│ Phase 2/3  •  Enrage in 0:42  •  Next: Fire Breath    │
└─────────────────────────────────────────────────────────┘
```

| Element | Behavior |
|---|---|
| **Boss name** | White, centered, slightly larger font. |
| **HP bar** | Wide (400 px), red-purple gradient, segmented by phase. |
| **Phase marker** | Small tick marks at 66% / 33% HP indicating phase transitions. |
| **Enrage timer** | Countdown shown when boss is enraged (turns red). |
| **Next ability hint** | Text showing the boss's next cast (telegraphed 3s before). |
| **DPS meter (optional)** | Toggleable small text showing party DPS. |

---

## 6. Minimap (Top-Round, Circular)

Circular minimap inspired by SAO's menu system. Rendered as a 2D top-down
view with player-centered rotation.

```
        ┌─────────────┐
       /   ·  ·  ·  ·  \
      |  ·  [P] ·  ·   |     P = player (center, arrow)
      | ·  ·  ·  ·  ·  |     · = explored terrain
      |  ·  ·  ·  ·   |     ! = quest marker
       \  ·  ·  !  ·  /      E = enemy (red dot)
        └─────────────┘      N = NPC (blue dot)
```

| Element | Behavior |
|---|---|
| **Size** | 128 px diameter, toggleable to 96 / 128 / 192. |
| **Zoom** | Mouse wheel adjusts zoom level (1×–4×). |
| **Terrain** | Biome-colored pixels; explored chunks only (fog of war). |
| **Entities** | NPCs = blue, enemies = red, party = green, quest = yellow `!`. |
| **Boss indicator** | Skull icon at edge if boss is off-screen. |
| **Compass** | N/E/S/W cardinal markers on the rim. |
| **Coordinates** | `X: 1234, Z: -567` below the minimap. |
| **Floor indicator** | `Floor 15 — The Crystal Caverns` above the minimap. |

**Interaction:** Right-click minimap to open **World Map** (§9.4).

---

## 7. Damage Numbers (Floating)

Combat text that pops above entities when they take or deal damage.

| Type | Color | Example |
|---|---|---|
| **Damage dealt** | White | `1,234` |
| **Damage taken** | Red | `-567` |
| **Critical hit** | Yellow + larger | `2,468!` |
| **Heal received** | Green | `+890` |
| **MP cost** | Blue | `-120` |
| **Status applied** | Purple | `Stunned` |
| **Resist** | Gray | `RESIST` |
| **Absorb (shield)** | Cyan | `Absorbed 300` |

**Behavior:**
- Spawn at entity head position, float upward, fade over 1.2 s.
- Cap: max 20 concurrent numbers; oldest fade instantly.
- Toggleable in settings (`F8` or Settings → HUD → Damage Numbers).
- Crit numbers are 1.5× font size with a slight horizontal jitter.

---

## 8. Menu Windows (SAO-Style)

All menus open as blue translucent windows with a header bar and close
button. Windows can be dragged and snap to screen edges. Only one menu can
be "active" (focused) at a time, but multiple can be open.

### 8.1 Equipment Menu (`C`)

```
┌─ Equipment ───────────────────────────────── [X] ┐
│                                                  │
│  [Helmet]  [Chest]  [Legs]  [Boots]             │
│                                                  │
│  [Main Hand]  [Off Hand]                        │
│                                                  │
│  [Ring 1] [Ring 2] [Necklace] [Earring]         │
│                                                  │
│  ┌─ Item Detail ────────────────────────────┐   │
│  │ Crystal Sword +7                        │   │
│  │ Attack: 342 (+85 from enhancement)       │   │
│  │ Sharpness V, Unbreaking III              │   │
│  │ [Equip] [Unequip] [Repair] [Enhance]    │   │
│  └──────────────────────────────────────────┘   │
└──────────────────────────────────────────────────┘
```

### 8.2 Skills Menu (`V`)

Shows all learned skills with detailed info, cooldowns, and keybind
assignment.

```
┌─ Skills ───────────────────────────────────── [X] ┐
│ Tab: [Active] [Passive] [Weapon] [Learned]       │
│                                                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ Starburst│ │  Heal    │ │  Dash    │          │
│  │  CD: 12s │ │  CD: 8s  │ │  CD: 15s │          │
│  │  MP: 40  │ │  MP: 60  │ │  MP: 25  │          │
│  └──────────┘ └──────────┘ └──────────┘          │
│                                                    │
│  ┌─ Starburst Stream ────────────────────────┐   │
│  │ Deal 300% weapon damage to target.        │   │
│  │ 5-hit combo. Each hit reduces cooldown    │   │
│  │ by 0.5s.                                   │   │
│  │ [Assign to Slot _]                        │   │
│  └──────────────────────────────────────────┘   │
└────────────────────────────────────────────────────┘
```

### 8.3 Quest Log (`J`)

```
┌─ Quest Log ─────────────────────────────────── [X] ┐
│ Filter: [All] [Main] [Daily] [Side] [Guild]        │
│                                                      │
│ ► Main: The Front Line                               │
│   "Defeat the Floor 1 boss, True Dragon."           │
│   Progress: 1/1 ✓                                    │
│   Reward: 2,500c, Sword of the Beginning            │
│                                                      │
│ ► Daily: Crystal Harvest                              │
│   "Collect 10 Crystal Shards."                       │
│   Progress: 7/10                                     │
│   Reward: 500c                                       │
│                                                      │
│ ► Side: Klein's Request                               │
│   "Gather 5 Iron Ore for Klein."                     │
│   Progress: 5/5 ✓ (Ready to turn in)                │
│   Reward: 150c, Iron Ingot x20                       │
└──────────────────────────────────────────────────────┘
```

### 8.4 World Map (`M`)

Full-screen map of the current floor. Shows explored areas, points of
interest, quest markers, and party member positions.

### 8.5 Guild Menu (`G`)

Guild info, roster, bank, and management (create/disband, invite, promote).

### 8.6 Party Menu (`P`)

Party invite/leave, role assignment, loot rules, party settings.

### 8.7 Settings Menu (`Esc` → Aincrad Settings)

HUD layout editor (drag elements), keybind config, damage number toggle,
minimap size, language.

---

## 9. Interaction & Keybinds

| Key | Action |
|---|---|
| `C` | Equipment menu |
| `V` | Skills menu |
| `J` | Quest log |
| `M` | World map |
| `G` | Guild menu |
| `P` | Party menu |
| `B` | Inventory (replaces `E` — vanilla inventory is hidden) |
| `T` | Trade (request trade with targeted player) |
| `F` | Interact / talk to NPC |
| `Shift + 1-7` | Use skill from bar |
| `Right-click` | Use skill (if assigned to mouse) |
| `F8` | Toggle damage numbers |
| `F9` | Toggle minimap |
| `F10` | Toggle HUD (clean screen) |
| `Tab` | Scoreboard / party DPS |

**Note:** Vanilla `E` (inventory) is replaced by `B` to avoid conflict
with SAO-style menu layout. Vanilla `Tab` scoreboard is repurposed.

---

## 10. Implementation Notes

- **Client rendering:** All HUD elements are drawn in a custom ` HudOverlay`
  Fabric mixin that overrides `InGameHud.render()`. Uses immediate-mode
  rendering (no retained GUI framework) for performance.
- **Font:** Custom bitmap font via `FontRenderer` with SAO-style glyph
  fallbacks for special icons (swords, shields).
- **Colors:** Defined as constants in `HudPalette` for consistency.
- **Configuration:** HUD element positions saved to `aincrad-hud.json` in
  the Minecraft config directory. Reset button in settings.
- **Performance:** Minimap terrain is cached in a framebuffer and only
  re-rendered when the player crosses a chunk boundary.
- **Accessibility:** All keybinds are remappable. Colorblind mode swaps
  HP/MP/ST bar colors to patterns + colors.

---

*Document version: 1.0 — Last updated 2026-10-10*
