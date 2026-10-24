#!/usr/bin/env python3
"""Generates ilfang.png (128x128) matching IlfangModel UV regions."""
import random
from PIL import Image

random.seed(1101)
W = H = 128
img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
px = img.load()


def rect(u, v, w, h, base, shade=(0, 0, 0), noise=14, panel=True):
    for y in range(h):
        for x in range(w):
            c = list(base)
            f = y / max(h - 1, 1)
            for i in range(3):
                c[i] = max(0, min(255, int(c[i] * (1.12 - 0.38 * f) + shade[i])))
            c[0] = max(0, min(255, c[0] + random.randint(-noise, noise)))
            c[1] = max(0, min(255, c[1] + random.randint(-noise, noise)))
            c[2] = max(0, min(255, c[2] + random.randint(-noise, noise)))
            px[u + x, v + y] = (c[0], c[1], c[2], 255)
    if panel:
        for x in range(w):
            px[u + x, v] = tuple(min(255, ch + 36) for ch in base) + (255,)
            px[u + x, v + h - 1] = tuple(max(0, ch - 46) for ch in base) + (255,)
        for y in range(h):
            px[u, v + y] = tuple(max(0, ch - 30) for ch in base) + (255,)
            px[u + w - 1, v + y] = tuple(max(0, ch - 30) for ch in base) + (255,)


def rivets(u, v, w, h, col=(200, 190, 150)):
    for (rx, ry) in [(2, 2), (w - 3, 2), (2, h - 3), (w - 3, h - 3)]:
        if 0 <= rx < w and 0 <= ry < h:
            px[u + rx, v + ry] = col + (255,)


def goldband(u, v, w, h, y0=0, hh=2):
    for y in range(y0, min(y0 + hh, h)):
        for x in range(w):
            g = (176 + random.randint(-16, 16), 142 + random.randint(-14, 14), 48 + random.randint(-10, 10))
            px[u + x, v + y] = g + (255,)


# --- armor (iron plates) ---
IRON = (64, 66, 74)
rect(0, 0, 36, 22, IRON, noise=10)          # torso
goldband(0, 8, 36, 22, y0=9, hh=2)          # chest gold stripe
rivets(0, 0, 36, 22)
rect(0, 22, 36, 13, (96, 26, 32), noise=12) # belt: crimson cloth
rect(16, 26, 6, 5, (190, 156, 60))          # gold buckle
rect(0, 36, 22, 18, (52, 54, 60), noise=10) # legs (iron greaves)
rivets(0, 36, 22, 18)
rect(60, 36, 30, 15, IRON, noise=10)        # pauldrons
goldband(60, 36, 30, 15, y0=4, hh=2)
rivets(60, 36, 30, 15)
rect(42, 36, 16, 12, (150, 120, 48), noise=12)  # bracers: bronze gold

# --- kobold flesh: dark olive scales ---
SCALE = (78, 92, 62)
def scales(u, v, w, h, base=SCALE):
    rect(u, v, w, h, base, noise=16, panel=False)
    for y in range(0, h, 3):
        for x in range(1 + (y // 3 % 2) * 2, w, 4):
            if v + y < H and u + x < W:
                r, g, b = base
                px[u + x, v + y] = (max(0, r - 26), max(0, g - 26), max(0, b - 22), 255)

scales(24, 36, 16, 13)   # upper arms
scales(20, 56, 30, 15)   # skull
scales(52, 56, 18, 8)    # snout
scales(72, 56, 16, 6, (66, 78, 54))  # jaw
scales(0, 56, 16, 9, (70, 82, 56))   # neck
scales(0, 72, 20, 10)    # tail1
scales(22, 72, 16, 8)    # tail2

# tusks + horns: bone
rect(90, 56, 4, 4, (214, 204, 176), noise=8, panel=False)
rect(96, 56, 16, 8, (196, 184, 152), noise=9, panel=False)
for y in range(8):
    for x in range(16):
        r, g, b = px[96 + x, 56 + y][:3]
        f = 1.0 - y / 9.0
        px[96 + x, 56 + y] = (int(r * f), int(g * f), int(b * f), 255)

# EYES: glowing red  (uv 0,120)
for y in range(4):
    for x in range(8):
        core = 1.0 if 1 <= x <= 6 and 1 <= y <= 2 else 0.55
        px[x, 120 + y] = (int(255 * core), int(36 * core), int(36 * core), 255)

# --- greatsword ---
rect(86, 72, 8, 17, (176, 182, 192), noise=8)     # blade steel
for y in range(17):                                 # red fuller
    px[86 + 3, 72 + y] = (158, 34, 40, 255)
    px[86 + 4, 72 + y] = (158, 34, 40, 255)
rect(74, 72, 20, 5, (186, 150, 56), noise=10)      # guard gold
rect(64, 72, 8, 8, (58, 42, 32), noise=8)          # grip wrap
for y in range(6):
    px[64 + 2, 72 + y] = (40, 28, 22, 255)         # wrap lines
rect(96, 72, 8, 4, (196, 160, 62), noise=8)        # pommel

# --- cape: tattered crimson banner ---
rect(40, 72, 22, 14, (122, 22, 30), noise=14)
for x in range(22):                                 # ragged bottom
    cut = random.randint(0, 3)
    for y in range(cut):
        if 40 + x < W and 72 + 13 - y < H:
            px[40 + x, 72 + 13 - y] = (0, 0, 0, 0)
goldband(40, 72, 22, 14, y0=0, hh=1)

img.save("/mnt/g/BigProjects/Aincrad/src/main/resources/assets/aincrad/textures/entity/ilfang.png")
print("saved", img.size)
