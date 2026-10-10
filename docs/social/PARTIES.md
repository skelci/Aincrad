# Parties

**Status:** Planning draft v1
**Backend:** Paper/Purpur plugin (authoritative) + Fabric client UI
**Related:** `PROGRESSION.md` (floor milestones), `QUESTS.md` (shared quest credit), `GUILDS.md` (guild wars)

## 1. Overview

Parties are temporary groups of **2–6 players** for floor clearing. **Raid parties** (4 parties = 24 players) unlock at the floor 25 milestone for high-floor boss content.

## 2. Creation & Management

**Commands:**

| Command | Description |
|---|---|
| `/party create [name]` | Create party (default name: "<leader>'s Party") |
| `/party invite <player>` | Send invite (expires after 60s) |
| `/party accept` | Accept pending invite |
| `/party leave` | Leave party |
| `/party kick <player>` | Kick member (leader) |
| `/party transfer <player>` | Transfer leadership |
| `/party toggle` | Toggle join requests (open / invite-only) |
| `/party list` | List members, levels, roles |
| `/party disband` | Disband (leader; auto-disbands when empty) |

**Constraints:**

- Max 6 members; one party per player
- Anti-spam: max 5 pending invites per player

**Data (plugin-side):** leader UUID, members, join mode, created-at.

## 3. Party XP Share

- **Trigger:** mob kill or quest XP awarded within **128 blocks** of the killer
- **Split:** equal share among online members in range (killer included)
- **Party bonus:** +2% XP per member in party, cap +10%
- Offline members: excluded (no catch-up)

## 4. Party Chat

- `/pc <msg>` or `/pc toggle` — per-member channel; toggle-able mute
- 24-hour log retained for moderation

## 5. Raid Parties (High Floors)

- **Unlock:** floor 25 milestone
- **Composition:** 4 parties = 24 players; raid leader = leader of party 1 (the creator)
- **Formation:** `/raid invite <party-leader>` merges parties into a raid instance, or form via the party finder
- **Instance rules:**
  - Boss arena locks to the raid
  - 7-day boss lockout per player (plugin-side)
  - Cross-party chat channel (all 24)
  - Shared raid quest progress
- **Raid roles:** DPS / Tank / Healer (derived from skill loadout) — used for finder filters and matchmaking

## 6. Loot Rules

Leader sets the rule at party creation; changeable when no roll is active.

| Rule | Behavior |
|---|---|
| Free-for-all | First-come, first-served |
| Round-robin | Items distributed in order; player may pass to next |
| Need-greed | Roll per item (see below) |

**Roll rules (need-greed):**

- **Threshold:** Rare+ rarity or boss/milestone drops go through rolls; Common/Uncommon are FFA
- **Window:** 60 seconds; options: Need (main build) / Greed (off-build, materials) / Pass
- **Resolution:** Need beats Greed; roll 1–100, highest wins; tie → higher roll
- **Bind-on-equip items:** tradable for 2 minutes after raid ends
- **Audit:** all rolls logged; leader override possible (override itself is logged)

## 7. Party Finder

- **UI (Fabric):** list of open parties — floor, type (quest / raid / leveling), current size, minimum level, join mode
- **Filters:** floor range, type
- **Join:** one-click join request; auto-accept if party is in open mode
- **Backend:** LFG service shared with the raid queue (see Open Questions)

## 8. Data Model & Backend

**Plugin:** `PartyManager` (parties, invites, LFG service).

**Fabric client:** party frames, XP share notifications, LFG browser GUI, roll UI.

## 9. Open Questions

- Auto-matchmaking ("quick raid") vs manual finder only?
- Party-only instanced dungeons?
- Cross-party shared kill credit for side quests (currently only XP is shared)?
