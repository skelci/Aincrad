# Guilds

**Status:** Planning draft v1
**Backend:** Paper/Purpur plugin (authoritative) + Fabric client UI
**Related:** `PROGRESSION.md` (milestones), `PARTIES.md` (party finder), economy/Col

## 1. Overview

Guilds are long-term player organizations of 10–105 members that persist across all 100 floors. Inspired by SAO guilds (Knights of the Blood, Dragon Knights, Moonlit Black Cats).

**Design pillars:**

- **Social identity** — guild name, tag, and banner shown on nametags and in chat
- **Progression** — guild XP and levels unlock shared bonuses
- **Shared resources** — guild bank with an audit ledger
- **Competitive play** — guild vs guild (GvG) arena wars

**Server-wide unlock schedule:**

| Content | Unlock |
|---|---|
| Basic guilds (create/join/bank) | Character level 15 (≈ floor 10) |
| Guild halls + guild skills | Floor 10 milestone |
| GvG arena wars | Floor 25 milestone |

## 2. Guild Creation

- **Cost:** 100,000 Col one-time, paid by the founder
- **Requirements:**
  - Founder character level ≥ 15
  - Founder not already in a guild
  - 1–5 additional founding members present (2–6 founders total)
  - Unique guild name (2–16 chars) and tag (2–4 chars), case-insensitive

**Commands:**

| Command | Description |
|---|---|
| `/guild create <name> <tag>` | Create guild (pays fee) |
| `/guild invite <player>` | Invite a player (officer+) |
| `/guild leave` | Leave current guild |
| `/guild disband` | Disband guild (master only; requires empty roster or 24h confirm) |

Founders start as officers; the creator becomes guild master.

## 3. Ranks & Permissions

| Rank | Count | Permissions |
|---|---|---|
| Master | 1 | All permissions; transfer leadership; disband |
| Officer | 3 + floor(guild level / 5), cap 7 | Invite, kick, bank deposit + withdraw, edit notice, manage hall, declare GvG, promote/demote |
| Member | Unlimited (within member cap) | Bank deposit, hall access, GvG participation |

**Additional rules:**

- Guild master cannot be kicked; must transfer or disband
- Inactivity kick: configurable; default 30 days offline → 7-day warning in guild log, then auto-kick (member rank only)

## 4. Guild Bank

- **Slots:** 9 + 9 × (level − 1) → L1: 9, L10: 90, L20: 189
- **Permissions:** members deposit; officers deposit + withdraw
- **Ledger:** last 100 transactions stored (timestamp, actor UUID, item, quantity); officers can inspect for dispute resolution
- **Safety:** no death loss, no overflow loss (deposit rejected when full)

## 5. Guild XP & Levels

**XP sources** (all server-validated):

| Activity | XP |
|---|---|
| Floor boss kill (guild member participated) | +50 |
| Main quest completion (guild member) | +100 |
| GvG win | +200 |
| GvG participation | +50 |

**Level curve:** XP(L → L+1) = 250 × L^1.5

| Level | XP to next |
|---|---|
| 1 | 250 |
| 5 | ~2,800 |
| 10 | ~7,900 |
| 15 | ~14,500 |
| 20 | ~22,400 |

**Level cap:** 20.

**Level-up rewards:** +5 member cap, +9 bank slots, new guild passive unlocks (see §7).

## 6. Member Cap

`cap = 10 + 5 × (level − 1)`

| Guild level | Max members |
|---|---|
| 1 | 10 |
| 5 | 30 |
| 10 | 55 |
| 20 | 105 |

Officers: 3 + floor(level / 5), cap 7.

## 7. Guild Skills (Passives)

Fixed unlocks — no slot management. Rank = 1 + floor((guild level − unlock level) / 3), max rank 3. Applied to all members as a passive aura; displayed in the guild GUI.

| Skill | Unlock | Effect per rank |
|---|---|---|
| Battle Training | L3 | +2% melee damage |
| Swift Recovery | L5 | +4% healing / potion effectiveness |
| Col Reserve | L8 | +1% Col gained |
| Iron Will | L12 | −2% damage taken |

## 8. Guild Housing / Territory

- **Guild hall:** instanced 16×16×8 room per guild; portal in the main safe city + `/guild hall`
- **Contents:** guild bank chest, banner stand (name/tag/color), trophy shelf (boss trophies), member list board
- **Upgrades:** cosmetic furniture themes (basic / stone / gold) purchased with Col
- **Territory claims:** out of scope for v1 (see Open Questions)

## 9. Guild vs Guild (GvG)

- **Unlock:** floor 25 milestone; both guilds must be level ≥ 3
- **Declaration:** `/guild war declare <guild> [bet Col]` — 24h notice period; bet range 10,000–500,000 Col; active wars listed publicly
- **Format:** 30-minute arena match; 3 arena maps (colosseum / canyon / ruins); server reserves an instance
- **Mode:** elimination (first to 50 kills) OR point capture (hold central point to 100) — chosen at declaration
- **Rules:**
  - War-flagged members only; PvP damage enabled only inside the arena
  - Both guilds field equal caps (minimum of the two guild caps)
  - No spectator interference
- **Rewards:** winner +200 guild XP + bet; loser +50 participation XP
- **Cooldown:** 1 war per guild per 48h; win/loss record and leaderboard tracked

## 10. Data Model & Backend

**Plugin (Paper/Purpur):** `GuildManager`; storage SQLite (single server) with MySQL option.

Tables: `guilds`, `members`, `bank_ledger`, `wars`.

**Fabric client:** guild GUI (roster, bank, notice, skills, war status); guild tag on nametabs/chat; plugin-messaging sync on join, level-up, bank contents, war status.

**Commands:** see §2–§9 plus `/guild info [guild]`, `/guild list`, `/guild top`, `/guild chat <msg>`.

## 11. Open Questions

- Open-world PvP zones instead of (or in addition to) arena-only GvG?
- Guild taxes / member dues on income?
- Cross-server guild sync (network play)?
