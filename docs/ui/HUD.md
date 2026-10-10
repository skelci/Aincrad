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

## 11. HUD Element Positioning System

All HUD elements are positioned using a screen-relative coordinate system
with configurable anchor points. This allows the HUD to adapt to different
resolutions and aspect ratios without hard-coded pixel values.

### 11.1 Coordinate System

- **Origin:** Top-left corner of the screen `(0, 0)`.
- **Units:** Scaled pixels (`sp`), where `1 sp = 1 px` at 100% GUI scale.
- **Y-axis:** Increases downward (standard screen coordinates).
- **Safe zone:** Elements are clamped to a 4 px inset from screen edges.

### 11.2 Anchor Points

Each element declares an anchor that determines how its position is
interpreted:

| Anchor | Position Reference | Typical Use |
|---|---|---|
| `TOP_LEFT` | Element's top-left corner at `(x, y)` | Player status, party frames |
| `TOP_CENTER` | Element's top-center at `(x, y)` | Boss bar, damage numbers |
| `TOP_RIGHT` | Element's top-right corner at `(x, y)` | Minimap, menu buttons |
| `CENTER` | Element's center at `(x, y)` | Radial menus, dialogs |
| `BOTTOM_LEFT` | Element's bottom-left corner at `(x, y)` | Chat, system messages |
| `BOTTOM_CENTER` | Element's bottom-center at `(x, y)` | Skill bar, hotbar |
| `BOTTOM_RIGHT` | Element's bottom-right corner at `(x, y)` | Buffs, compass |

### 11.3 Offset & Margin

```json
{
  "element": "player_status",
  "anchor": "TOP_LEFT",
  "offset": { "x": 8, "y": 8 },
  "margin": { "top": 0, "right": 0, "bottom": 0, "left": 0 }
}
```

- **Offset:** Fixed pixel displacement from the anchor point.
- **Margin:** Additional spacing applied after offset (for grouping).
- **Scale factor:** Multiplied by GUI scale setting (0.5×–3.0×).

### 11.4 Responsive Behavior

- Elements re-anchor on window resize.
- Minimum element size enforced (skill slots ≥ 32 px, text ≥ 8 px).
- Overlap detection prevents elements from stacking on smaller screens.
- User-dragged positions are stored as offsets from the nearest anchor.

---

## 12. Animation System

The HUD uses a lightweight tweening engine for smooth transitions. All
animations are time-based (not frame-based) to remain consistent across
frame rates.

### 12.1 Tweening Engine

```java
TweenManager.tween(element)
    .property("opacity", 0.0f, 1.0f)
    .duration(250, TimeUnit.MILLISECONDS)
    .easing(Easing.CUBIC_OUT)
    .onComplete(() -> element.setInteractive(true))
    .start();
```

- **Properties animated:** `x`, `y`, `width`, `height`, `opacity`, `scale`,
  `rotation`, `color` (RGBA interpolation).
- **Duration:** Specified in milliseconds; clamped to `[0, 5000]`.
- **Delay:** Optional start delay in milliseconds.
- **Chaining:** `.then()` for sequential tweens; `.with()` for parallel.

### 12.2 Easing Functions

| Easing | Curve | Use Case |
|---|---|---|
| `LINEAR` | Constant speed | Damage numbers, cooldowns |
| `QUAD_IN` | Accelerate | Element appear, slide-in |
| `QUAD_OUT` | Decelerate | Element disappear, slide-out |
| `CUBIC_IN` | Sharp accelerate | Menu open |
| `CUBIC_OUT` | Sharp decelerate | Menu close, tooltip |
| `ELASTIC_OUT` | Overshoot + settle | Level-up glow, buff apply |
| `BOUNCE_OUT` | Bounce on land | Notification pop |
| `BACK_IN` | Pull back then forward | Drawer open |
| `BACK_OUT` | Overshoot then settle | Drawer close, panel snap |

### 12.3 Keyframe Animations

For complex sequences, keyframes define property values at specific times:

```json
{
  "animation": "boss_spawn",
  "keyframes": [
    { "time": 0,    "opacity": 0, "scale": 0.5 },
    { "time": 200,  "opacity": 1, "scale": 1.1 },
    { "time": 400,  "opacity": 1, "scale": 1.0 }
  ],
  "loop": false,
  "easing": "ELASTIC_OUT"
}
```

- **Interpolation:** Linear between keyframes unless easing is specified
  per-segment.
- **Loop modes:** `NONE`, `LOOP`, `PING_PONG`.
- **Events:** Keyframes can trigger callbacks (sound, particle, etc.).

### 12.4 Animation States

Elements with persistent animations use a state machine:

| State | Trigger | Animation |
|---|---|---|
| `IDLE` | Default | Subtle pulse (passive skills) |
| `HOVER` | Mouse enter | Border brighten + scale 1.02× |
| `ACTIVE` | Click / use | Flash + scale 0.95× |
| `COOLDOWN` | Skill on CD | Radial sweep + desaturate |
| `DAMAGED` | Player hit | Red flash + shake |
| `LEVEL_UP` | XP gain | Gold glow pulse + expand |
| `DEATH` | HP = 0 | Fade to 30% opacity |

---

## 13. Event Handling

The HUD processes mouse and keyboard input through a unified event system
that supports both immediate-mode rendering and interactive widgets.

### 13.1 Mouse Events

| Event | Trigger | Default Behavior |
|---|---|---|
| `MOUSE_ENTER` | Cursor enters element bounds | Highlight border, play hover sound |
| `MOUSE_LEAVE` | Cursor exits element bounds | Remove highlight |
| `MOUSE_CLICK` | Left-click on element | Activate (open menu, use skill) |
| `MOUSE_RIGHT_CLICK` | Right-click on element | Secondary action (assign, dismiss) |
| `MOUSE_DRAG` | Left-click + move | Reposition element (if draggable) |
| `MOUSE_WHEEL` | Scroll wheel over element | Zoom (minimap), scroll (lists) |
| `MOUSE_RELEASE` | Button released after drag | Snap to grid/edge |

**Hit testing:** Elements are tested in reverse draw order (topmost first).
The first element whose bounds contain the cursor receives the event.
Transparent regions (alpha < 0.1) are ignored for hit testing.

### 13.2 Keyboard Events

| Event | Trigger | Handling |
|---|---|---|
| `KEY_PRESS` | Key down | Execute bound action, set key state |
| `KEY_REPEAT` | Key held | Repeat action (text scroll, menu nav) |
| `KEY_RELEASE` | Key up | Clear key state, finish combo |

**Keybind resolution:**
1. Check if a menu is open → route to menu navigation.
2. Check if chat is open → route to chat input.
3. Check global keybinds (F8–F10, etc.).
4. Check skill bar keybinds (1–7).
5. Pass to Minecraft vanilla handler (if not consumed).

**Modifier keys:** `Shift`, `Ctrl`, `Alt` are tracked as bitmask
flags. Combos like `Shift + 1-7` are resolved before single-key binds.

### 13.3 Event Propagation

```
Mouse Click
    │
    ▼
┌─────────────┐
│ Topmost     │ ← Check draw order (highest layer first)
│ Element     │
└─────────────┘
    │ consumed?
    ├── Yes → Stop propagation
    └── No  → Next element below
                  │
                  ▼
           ┌─────────────┐
           │ World / Game │ ← If no HUD element consumed
           └─────────────┘
```

- **Consumed events** are not passed to the game world.
- **Focus:** Only one element has focus at a time (for keyboard input).
- **Tab navigation:** `Tab` cycles focus through interactive elements.

### 13.4 Focus Management

- Clicking an element grants it focus.
- `Esc` releases focus and closes the topmost menu.
- Focused element shows a cyan dashed border.
- Focus is lost when another element is clicked or `Esc` is pressed.

---

## 14. Configuration Persistence

HUD layout, keybinds, and settings are stored in a JSON file that persists
across game sessions.

### 14.1 File Location

```
<game_dir>/config/aincrad-hud.json
```

- **Auto-save:** Changes are written immediately (debounced 500 ms).
- **Backup:** Previous config kept as `aincrad-hud.json.bak`.
- **Reset:** Settings menu → "Reset to Defaults" restores factory values.

### 14.2 JSON Schema

```json
{
  "version": 2,
  "guiScale": 1.0,
  "elements": {
    "player_status": {
      "anchor": "TOP_LEFT",
      "offset": { "x": 8, "y": 8 },
      "visible": true,
      "opacity": 1.0
    },
    "skill_bar": {
      "anchor": "BOTTOM_CENTER",
      "offset": { "x": 0, "y": -12 },
      "visible": true,
      "slotCount": 7
    },
    "minimap": {
      "anchor": "TOP_RIGHT",
      "offset": { "x": -8, "y": 8 },
      "visible": true,
      "size": 128,
      "zoom": 2.0
    }
  },
  "keybinds": {
    "equipment": "C",
    "skills": "V",
    "quest_log": "J",
    "world_map": "M",
    "guild": "G",
    "party": "P",
    "inventory": "B",
    "damage_numbers_toggle": "F8",
    "minimap_toggle": "F9",
    "hud_toggle": "F10"
  },
  "settings": {
    "damageNumbers": true,
    "screenShake": true,
    "colorblindMode": false,
    "language": "en_us",
    "hudOpacity": 0.85
  }
}
```

### 14.3 Save / Load Flow

```
Game Start
    │
    ▼
┌─────────────────┐
│ Read JSON file  │
└─────────────────┘
    │
    ├── File exists → Parse & validate
    │       │
    │       ├── Valid → Apply to HUD
    │       └── Invalid → Load defaults, log warning
    │
    └── No file → Create with defaults
```

**Validation rules:**
- `version` must match current schema version (or be migratable).
- `offset` values clamped to screen bounds.
- `keybinds` checked for conflicts (duplicate keys rejected).
- Unknown fields are ignored (forward compatibility).

### 14.4 Migration

When the schema version changes, a migration function transforms the old
config to the new format:

```java
ConfigMigrator.migrate(config, fromVersion, toVersion);
```

- Migrations are sequential (1 → 2 → 3, never skipping).
- Failed migrations fall back to defaults with a user notification.
- Migration log is written to `aincrad-hud.log`.

---

## 15. Rendering Pipeline

The HUD is rendered in a dedicated overlay pass after the world is drawn
but before vanilla GUI elements. This ensures the HUD appears on top of
the 3D scene but below chat and debug info.

### 15.1 Draw Order (Back to Front)

```
1. World (Minecraft renderer)
2. HUD Background Layer
   ├── Minimap terrain (cached framebuffer)
   ├── Party frames
   └── Player status panel
3. HUD Midground Layer
   ├── Skill bar + cooldowns
   ├── Boss health bar
   └── Buff/debuff icons
4. HUD Foreground Layer
   ├── Damage numbers (floating)
   ├── Tooltips
   └── Cursor-attached elements
5. Menu Windows (SAO-style)
   ├── Equipment, Skills, Quest, etc.
   └── Drag on top of all other menus
6. Vanilla GUI (chat, debug, etc.)
```

### 15.2 Layering System

Each element is assigned a `Layer` enum value:

| Layer | Z-Index | Elements |
|---|---|---|
| `BACKGROUND` | 0 | Minimap terrain, world markers |
| `PANEL` | 10 | Status panels, party frames |
| `BAR` | 20 | Skill bar, boss bar |
| `ICON` | 30 | Buffs, status effects |
| `FLOATING` | 40 | Damage numbers, floating text |
| `TOOLTIP` | 50 | Hover tooltips |
| `MENU` | 100 | All menu windows |
| `MODAL` | 200 | Dialogs, confirmations |
| `CURSOR` | 300 | Cursor-attached UI |

Higher Z-index elements are drawn later (on top) and receive input first.

### 15.3 Framebuffer Caching

To minimize per-frame GPU work, static or slowly-changing elements are
rendered to offscreen framebuffers:

| Element | Cache Strategy | Invalidation Trigger |
|---|---|---|
| Minimap terrain | Full framebuffer | Player crosses chunk boundary |
| Skill icons | Atlas texture | Skill learned/changed |
| Party frames | Per-member framebuffer | HP/MP change > 1% |
| Boss bar | Full framebuffer | Boss targeted/damaged |
| Menu backgrounds | 9-slice texture | Never (static) |

**Framebuffer lifecycle:**
1. Created on first use at element's current size.
2. Re-rendered only when invalidation trigger fires.
3. Destroyed when element is hidden or screen resized.
4. GPU memory budget: 64 MB total for all cached framebuffers.

### 15.4 Performance Budget

| Metric | Target | Maximum |
|---|---|---|
| HUD draw calls | < 50 | 100 |
| HUD vertices | < 5,000 | 10,000 |
| Framebuffer switches | < 5 | 10 |
| HUD render time | < 1.0 ms | 2.0 ms |
| Texture memory | < 32 MB | 64 MB |

**Optimization techniques:**
- Batch draw calls by texture (minimize state changes).
- Skip fully transparent elements (alpha = 0).
- Use `GL_BLEND` only when needed (disable for opaque elements).
- Cull off-screen elements before submitting draw commands.
- Throttle damage number updates to 30 Hz (interpolate visually).

### 15.5 Render Loop Integration

```java
// In HudOverlay.render()
public void render(MatrixStack matrices, float tickDelta) {
    // 1. Update animations
    TweenManager.update(tickDelta);

    // 2. Update dynamic data (HP, MP, cooldowns)
    HudDataCache.update();

    // 3. Draw background layer (cached)
    drawLayer(Layer.BACKGROUND, matrices);

    // 4. Draw midground layer
    drawLayer(Layer.PANEL, matrices);
    drawLayer(Layer.BAR, matrices);

    // 5. Draw foreground layer
    drawLayer(Layer.FLOATING, matrices);

    // 6. Draw menus (if any open)
    drawMenus(matrices);

    // 7. Draw tooltips (if hovering)
    drawTooltips(matrices);
}
```

---

*Document version: 1.0 — Last updated 2026-10-10*
