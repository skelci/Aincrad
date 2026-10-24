#!/usr/bin/env python3
"""F1 economy/progression simulation: XP curve, Col flow, skill upgrades, boss TTK.
Mirrors: ProgressionManager (50*L^1.5), MobSpawnService (xp=8+6L, col=6+5L),
PaperSkillService upgrade costs (base*1.7^(r-1)), quest rewards, boss 420 HP."""
import random

random.seed(7)
XP_COEF, XP_EXP = 50.0, 1.5
def xp_cum(level): return XP_COEF * level ** XP_EXP

MOB_LVL = 2                     # F1 mobs lvl 1-3
KILL_XP = 8 + 6 * MOB_LVL
KILL_COL = 6 + 5 * MOB_LVL
KILLS_PER_H = 22                # density + travel time, measured-ish
DEATH_EVERY_H = 1.4             # deaths per hour while learning
DEATH_PENALTY = 0.05

QUESTS = [  # (accept_h, xp, col, name)
    (0.02, 150, 70, "warm_steel"),
    (0.30, 160, 80, "cull_the_foe"),
    (0.55, 200, 110, "ranch_trouble"),
    (0.90, 150, 150, "weeping_stone"),
    (1.20, 180, 120, "broken_seal"),
    (1.55, 220, 60, "hunters_ledger"),
]
BOSS_H = 1.8 * 60          # attempt after ~1.8h
BOSS_HP = 640
BOSS_COL = 2500

SKILL_BASE = 60            # avg of 45..60
UP_MULT, MAX_RANK = 2.0, 5

def sim(hours=2.5, dt=1/60):
    xp = col = 0.0
    dps = 3.2
    level = 1
    rank = 1                 # avg skill rank
    spent = 0
    qi = 0
    boss_done = False
    t = 0.0
    log = []
    while t < hours * 3600:
        t += dt
        h = t / 3600
        # kills
        if random.random() < KILLS_PER_H * dt / 3600 * 60:
            pass
        xp += KILL_XP * KILLS_PER_H * dt / 3600
        col += KILL_COL * KILLS_PER_H * dt / 3600
        while xp >= xp_cum(level + 1):
            level += 1
        # deaths
        if random.random() < DEATH_EVERY_H * dt / 3600:
            col *= (1 - DEATH_PENALTY)
        # quests
        if qi < len(QUESTS) and h >= QUESTS[qi][0]:
            _, qxq, qcol, name = QUESTS[qi]
            xp += qxq; col += qcol; qi += 1
            while xp >= xp_cum(level + 1):
                level += 1
        # upgrade a random skill when affordable (keep <10 min of income in reserve)
        cost = SKILL_BASE * UP_MULT ** (rank - 1)
        if rank < MAX_RANK and col > cost + 150:
            col -= cost; spent += cost; rank += 1
        # boss
        if not boss_done and h >= BOSS_H / 3600 * 60:  # BOSS_H is minutes
            dps = 3.2 + 5 * (1.22 ** (rank - 1)) + level * 0.35
            ttk = BOSS_HP / dps
            col += BOSS_COL; boss_done = True
            log.append((h, f"BOSS DOWN in {ttk:.0f}s dmg/s={dps:.1f}"))
        if int(t) % 600 == 0 and int(t) > 0 and int(t - dt) // 600 != int(t) // 600:
            log.append((h, f"L{level:2d} col={col:6.0f} rank={rank} xp={xp:6.0f}"))
    return log, level, col, rank, spent, boss_done, dps

for hours in (2.5, 4.0):
    log, lvl, col, rank, spent, boss, dps = sim(hours)
    print(f"--- {hours}h session ---")
    for h, msg in log:
        print(f"  {h:5.2f}h {msg}")
    print(f"  end: L{lvl} col={col:.0f} avg_rank={rank} spent={spent:.0f} boss={boss} final_dps={dps:.1f}")

# upgrade cost ladder sanity
print("\nrank cost ladder (per skill):", [round(SKILL_BASE * UP_MULT ** (r - 1)) for r in range(1, 6)],
      "total:", round(sum(SKILL_BASE * UP_MULT ** (r - 1) for r in range(1, 6))))
print("xp per level:", {l: round(xp_cum(l + 1) - xp_cum(l)) for l in (1, 5, 10, 15, 20)})
