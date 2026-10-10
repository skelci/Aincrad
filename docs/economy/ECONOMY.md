# Aincrad Economy Design

## Overview

The economy of Aincrad is designed to feel like a living MMO while respecting
Minecraft's vanilla feel. The primary currency is **Col** (short for "Crystal
Orb"), inspired by SAO's in-world money. The economy is **server-wide** and
**player-driven**, with NPC vendors providing baseline price anchors.

All monetary transactions flow through a central `EconomyService` (backend,
Paper/Purpur) that persists balances to a MySQL/SQLite store. The Fabric mod
communicates with it via plugin messages. Virtual balances are updated
optimistically on the client for responsiveness, then reconciled with the
server every tick.

---

## 1. Currency: Col

| Property | Value |
|---|---|
| Name | Col (c) |
| Type | Server-wide virtual currency (no physical item) |
| Starting balance | 100 Col (enough for one starter sword repair) |
| Display format | Integer only — no decimals |
| Storage | BIGINT in `aincrad_economy (uuid, balance, lifetime_earned, lifetime_spent)` |
| Sync | Balance pushed to client on change + on login; cached client-side |

**Anti-abuse:** No physical Col items exist — it is impossible to dupe via
inventory manipulation, hopper pipes, or `/give`. All mutations go through
the service layer which enforces atomic DB transactions.

**Naming:** Col amounts are written as `1,450c` in chat and GUI (e.g. `§b1,450c§r`).

---

## 2. Earn Sources (Faucets)

Every source is tuned so that a player doing varied content earns roughly
**2,000–4,000 Col per hour** of active play on early floors, scaling with floor
difficulty.

### 2.1 Mob Drops

| Source | Drop Amount | Notes |
|---|---|---|
| Standard mob (Floor 1–10) | 3–12c | Auto-pickup into balance, no item |
| Elite mob (purple name) | 25–60c | 5× spawn rate of standard |
| Mini-boss (named, per zone) | 100–250c | One-time bonus per zone per player per day |
| Raid mob (dungeon) | 10–40c | Paid on kill, shared among party by contribution |

> **Design note:** Col drops are added directly to balance rather than dropped
> as physical items, eliminating lag from thousands of micro-entities and
> preventing item-based dupe exploits.

### 2.2 Quests

| Quest Type | Reward | Cooldown |
|---|---|---|
| Main quest (story) | 500–5,000c | One-time |
| Daily quest | 200–800c | 20 h per quest, rotating pool of 5 |
| Repeatable side quest | 80–200c | None, but reward drops 10% per repeat (resets daily) |
| Weekly guild quest | 2,000–10,000c | Weekly reset |

Quest rewards are paid out instantly via `EconomyService.deposit(uuid, amount, "quest:<id>")`.

### 2.3 Boss Kills

| Boss Tier | Reward | Notes |
|---|---|---|
| Field boss (one per floor) | 1,500–3,000c | Shared to all participants, bonus for killer |
| Dungeon boss | 800–2,500c per boss | Distributed by damage contribution |
| World boss (server event) | 10,000–25,000c | Top 10 damage dealers get a cut; everyone who hit it gets a participation share |
| Raid boss (20-man) | 5,000–15,000c | Guild bank split option |

All boss rewards are gated by a **personal weekly cap** of 50,000c to prevent
hyper-inflation from farming the same boss on alts.

### 2.4 Other Sources

- **Daily login bonus:** 100–500c, escalating over 7-day streak.
- **Achievement milestones:** 250–1,000c each, one-time.
- **Event participation:** Varies; server events pay 1,000–5,000c.
- **Quest item turn-in:** NPC bounties (kill X, collect Y) pay on turn-in.

---

## 3. Spend Sinks (Drains)

Every faucet above has a corresponding drain. Target **faucet : drain ratio ≈ 1.1 : 1**
(slight net positive for active players, mild deflation over time).

### 3.1 Repairs

| Repair Type | Cost | Notes |
|---|---|---|
| Full item repair | `item_base_cost × (durability_lost / max_durability)` | Materials not required — gold-style but cheaper |
| Repair at NPC | As above + 10% service fee | Available in every town |
| Self-repair (with Crafting Kit item) | Material-only | No Col, but consumes a craft kit |
| Bulk repair (inventory button) | Sum of all repairs + 5% fee | Convenience sink |

Repair costs are intentionally low (10–100c for starter gear, up to 2,000c
for endgame Nephrite gear) — enough to create a constant Col drain without
feeling punishing.

### 3.2 Crafting

| Craft | Cost | Output |
|---|---|---|
| Basic recipe (vanilla-style) | 0c | Vanilla crafting bench |
| Aincrad recipe (mod) | Material cost only | NPC-free crafting via Crafting Table GUI |
| Enhancement attempt | Scaling fee: `50 × 2^current_tier` | See `ITEMS.md` §5 |
| Reroll accessory affixes | 500c + material | One affix reroll |
| Dye / rename | 25c | Cosmetic |

### 3.3 Teleport

| Destination Type | Cost | Cooldown |
|---|---||
| Town to town (unlocked) | 50c | 30 s |
| Town to dungeon entrance | 100c | 10 s |
| Player to player | 150c | 5 min |
| Death return (respawn at town) | 200c | Instant, auto-charged |
| Home point (max 3) | 100c per set | No cooldown |

Teleport costs are kept low so they feel like a convenience tax, not a
progression gate.

### 3.4 Guild Creation & Upkeep

| Action | Cost |
|---|---|
| Create guild | 10,000c + Guild Charter item |
| Guild rename | 5,000c |
| Guild tabard customization | 1,000c |
| Guild hall rental (weekly) | 5,000–20,000c depending on floor |
| Guild bank deposit fee | 1% of deposit amount |

Guild creation is the single largest one-time Col sink and drives meaningful
wealth concentration at the top of the economy.

### 3.5 Other Sinks

- **Listing fee (Auction House):** 5% of asking price, charged on listing, non-refundable.
- **Sale tax (Auction House):** 2% of sale price, deducted from seller payout.
- **Insurance (death):** Optional; 10% of item value to insure against drop on death. If insured, items return to inventory on death.
- **Cosmetic shop:** Titles, pets, particle effects (500–10,000c).
- **Skill point reset:** 2,000c.
- **Dungeon lockout bypass:** 3,000c per reset (once per day per dungeon).

---

## 4. Player Trading

### 4.1 Direct Trade

- Right-click a player → "Trade" request.
- GUI opens with two inventory panels; both players drag items/Col.
- Both click "Ready"; a 3-second countdown; both confirm again.
- Atomic: either both sides commit or neither does. No dupe possible.
- **No fee** on direct trade — pure P2P.
- Trade is **cross-server** if both players are on the same shard.

### 4.2 Auction House (Col-only market)

| Feature | Detail |
|---|---|
| Currency | Col only — no barter |
| Listing duration | 12 h / 24 h / 48 h |
| Listing fee | 5% of asking price (min 10c) |
| Sale tax | 2% of final price |
| Price history | Per-item 7-day rolling median shown on listing |
| Buyout | Fixed buyout price OR bid-based (buyer's choice at listing) |
| Cancellation | Listing fee forfeited |
| Delivery | Sold items delivered to buyer's mailbox; Col to seller's mailbox |
| Mailbox expiry | 30 days, then returned to seller |

**Anti-manipulation:**
- One player cannot bid on their own auction.
- Rapid relisting of the same item (same item hash) triggers a 5-minute cooldown.
- Price outliers (>5× 7-day median) require a confirmation click and are
  highlighted in the UI as "unusual price."

---

## 5. NPC Shops (Per-Floor Vendors)

Every floor's hub town has a **Shopping District** with the following vendor
types. Stock is per-floor and refreshes on a **weekly reset** (Monday 00:00
server time).

### 5.1 Vendor Types

| Vendor | Sells | Stock Size | Pricing |
|---|---|---|---|
| **Weapon Smith** | Floor-tier weapons (Common–Rare) | 8 items | 200–2,000c |
| **Armor Smith** | Floor-tier armor (Common–Rare) | 8 items | 200–2,000c |
| **Accessory Trader** | Rings, necklaces, earrings | 6 items | 500–3,000c |
| **Potion Brewer** | Consumables: potions, food, buffs | 20 items | 50–500c |
| **Material Merchant** | Crafting materials (mob drops, bars) | 15 items | 10–200c |
| **Dungeon Merchant** | Dungeon-exclusive materials (sells for Col) | 10 items | 100–1,000c |
| **Cosmetic Vendor** | Titles, dyes, pets, particles | 10 items | 500–10,000c |
| **Fence** (buys anything) | Buys any item at 30% of base value | Unlimited | Sink |

### 5.2 Pricing Model

NPC prices use a formula, not hardcoded values:

```
price = base_value × (1 + floor_tier × 0.05) × (1 + rarity_multiplier)

rarity_multiplier = { Common: 1.0, Uncommon: 1.5, Rare: 2.5,
                      Epic: 5.0, Legendary: 12.0 }
```

Stock items are chosen from a weighted loot table per vendor, favoring
lower rarities. Higher floors introduce higher base-value items.

### 5.3 Fence (Buy-Back)

The Fence buys **any** item at **30% of its base value**, creating a universal
Col sink. Players can bulk-sell unwanted gear with one click.

---

## 6. Inflation Control

### 6.1 Philosophy

We accept mild inflation as a feature (it lets late-game players catch up)
but engineer strong sinks to keep the economy healthy. Target inflation
rate: **2–5% per month**.

### 6.2 Levers

| Lever | Mechanism | Strength |
|---|---|---|
| **Guild upkeep** | Weekly Col drain scaled to guild size | Medium |
| **Insurance** | Optional death-protection premium | Low-Medium |
| **Repair costs** | Constant drain scaling with gear tier | Medium |
| **AH fees** | 5% listing + 2% sale = ~7% of all trade volume | High |
| **Cosmetic sink** | Non-functional purchases remove Col permanently | Low-Medium |
| **Dungeon lockout bypass** | 3,000c per reset, once per day | Medium |
| **Weekly reward cap** | 50,000c/week from boss farming caps faucet | High (anti-alt) |
| **Fence bulk-sell** | Universal 30% drain on all unwanted items | High (universal) |

### 6.3 Monitoring & Response

The backend exposes `/economy stats` for admins, showing:
- Total Col in circulation (`SELECT SUM(balance)`).
- Daily faucet total vs. drain total.
- Gini coefficient of balances.
- AH volume and median prices.

If monthly inflation exceeds 8%, the admin can:
1. Increase cosmetic shop prices (easy, no player backlash).
2. Introduce a limited-time "Col-for-points" event.
3. Add a one-time "wealth tax" on balances above 1,000,000c (progressive).
4. Boost daily login bonuses (counterproductive long-term, use sparingly).

### 6.4 Anti-RMT Measures

- Col **cannot** be transferred between accounts via trade if the receiving
  account is <24 h old (smurf filter).
- Large trades (>100,000c) are logged and flagged for admin review.
- No real-money trading hooks exist — Col has no physical form and cannot be
  sold via any in-game mechanism other than the above sinks.

---

## 7. Implementation Notes

- **Backend:** `EconomyService` singleton on the Paper/Purpur side. All
  mutations are `@Transactional` and idempotent (each transaction has a
  UUID to prevent double-apply on retry).
- **Client sync:** Balance is pushed via plugin message
  `aincrad:economy:balance` with a checksum. Client caches and displays;
  authoritative value is always server-side.
- **Persistence:** MySQL in production, SQLite for local dev. Flyway
  migrations in `src/main/resources/db/migration/`.
- **Logging:** Every mutation is logged to `economy_audit` table with
  `(uuid, delta, reason, balance_after, timestamp)`.

---

## 8. Open Questions

1. Should Col be per-shard or global? (Currently global.)
2. Should players be able to gift Col directly to friends (currently only
   via trade GUI)?
3. Is a 50,000c/week boss cap too restrictive for guilds farming content?
4. Should the Fence have a daily purchase cap per item type to prevent
   hyper-farming of specific materials?

---

*Document version: 1.0 — Last updated 2026-10-10*
