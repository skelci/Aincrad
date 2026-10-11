#!/usr/bin/env python3
"""Generate all placeholder PNG textures for the Aincrad mod."""

import os
import colorsys
from PIL import Image

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                    "src", "main", "resources", "assets", "aincrad", "textures")

count = 0

def save(name, color, size=16):
    """Save a solid-color PNG. color is (r,g,b) or (r,g,b,a)."""
    global count
    path = os.path.join(BASE, name)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    mode = "RGBA" if len(color) == 4 else "RGB"
    img = Image.new(mode, (size, size), color)
    img.save(path, "PNG")
    count += 1

# ── 1. block/ (5) ────────────────────────────────────────────────────────────
save("block/floor_portal.png", (128, 0, 128))
save("block/crafting_forge.png", (64, 64, 64))
save("block/crafting_alchemy.png", (0, 128, 0))
save("block/crafting_workbench.png", (139, 90, 43))
save("block/crafting_enchanting.png", (0, 0, 255))

# ── 2. item/ (110) ───────────────────────────────────────────────────────────
# 20 mob spawn eggs – distinct colors
spawn_egg_colors = [
    (255, 100, 100), (100, 255, 100), (100, 100, 255),
    (255, 255, 100), (255, 100, 255), (100, 255, 255),
    (255, 150, 50),  (150, 255, 50),  (50, 255, 150),
    (50, 150, 255),  (150, 50, 255),  (255, 50, 150),
    (200, 200, 50),  (50, 200, 200),  (200, 50, 200),
    (180, 120, 60),  (60, 180, 120),  (120, 60, 180),
    (220, 80, 80),   (80, 220, 80),
]
spawn_egg_names = [
    "mob_spawn_egg_passive", "mob_spawn_egg_neutral", "mob_spawn_egg_hostile",
    "mob_spawn_egg_pack", "mob_spawn_egg_ranged", "mob_spawn_egg_ambusher",
    "mob_spawn_egg_summoner", "mob_spawn_egg_enraged", "mob_spawn_egg_elite",
    "mob_spawn_egg_boss", "mob_spawn_egg_raid_boss", "mob_spawn_egg_wolf",
    "mob_spawn_egg_skeleton", "mob_spawn_egg_zombie", "mob_spawn_egg_spider",
    "mob_spawn_egg_creeper", "mob_spawn_egg_enderman", "mob_spawn_egg_blaze",
    "mob_spawn_egg_ghast", "mob_spawn_egg_slime",
]
for name, color in zip(spawn_egg_names, spawn_egg_colors):
    save(f"item/{name}.png", color)

# 50 item icons
item_icons = [
    # Swords (7)
    ("sword_iron", (192, 192, 192)), ("sword_steel", (160, 170, 180)),
    ("sword_mythril", (100, 200, 220)), ("sword_adamantite", (80, 160, 200)),
    ("sword_dragon", (200, 50, 50)), ("sword_shadow", (60, 40, 80)),
    ("sword_void", (30, 10, 50)), ("sword_holy", (255, 240, 180)),
    # Axes (6)
    ("axe_iron", (180, 180, 180)), ("axe_steel", (150, 160, 170)),
    ("axe_mythril", (90, 190, 210)), ("axe_adamantite", (70, 150, 190)),
    ("axe_dragon", (190, 40, 40)), ("axe_shadow", (50, 30, 70)),
    ("axe_void", (20, 5, 40)),
    # Pickaxes (5)
    ("pickaxe_iron", (170, 170, 170)), ("pickaxe_steel", (140, 150, 160)),
    ("pickaxe_mythril", (80, 180, 200)), ("pickaxe_adamantite", (60, 140, 180)),
    ("pickaxe_dragon", (180, 30, 30)),
    # Armor (32)
    ("armor_iron_helmet", (192, 192, 192)), ("armor_iron_chestplate", (185, 185, 185)),
    ("armor_iron_leggings", (178, 178, 178)), ("armor_iron_boots", (170, 170, 170)),
    ("armor_steel_helmet", (160, 170, 180)), ("armor_steel_chestplate", (153, 163, 173)),
    ("armor_steel_leggings", (146, 156, 166)), ("armor_steel_boots", (139, 149, 159)),
    ("armor_mythril_helmet", (100, 200, 220)), ("armor_mythril_chestplate", (93, 193, 213)),
    ("armor_mythril_leggings", (86, 186, 206)), ("armor_mythril_boots", (79, 179, 199)),
    ("armor_adamantite_helmet", (80, 160, 200)), ("armor_adamantite_chestplate", (73, 153, 193)),
    ("armor_adamantite_leggings", (66, 146, 186)), ("armor_adamantite_boots", (59, 139, 179)),
    ("armor_dragon_helmet", (200, 50, 50)), ("armor_dragon_chestplate", (193, 43, 43)),
    ("armor_dragon_leggings", (186, 36, 36)), ("armor_dragon_boots", (179, 29, 29)),
    ("armor_shadow_helmet", (60, 40, 80)), ("armor_shadow_chestplate", (53, 33, 73)),
    ("armor_shadow_leggings", (46, 26, 66)), ("armor_shadow_boots", (39, 19, 59)),
    ("armor_void_helmet", (30, 10, 50)), ("armor_void_chestplate", (23, 3, 43)),
    ("armor_void_leggings", (16, 0, 36)), ("armor_void_boots", (10, 0, 30)),
    ("armor_holy_helmet", (255, 240, 180)), ("armor_holy_chestplate", (248, 233, 173)),
    ("armor_holy_leggings", (241, 226, 166)), ("armor_holy_boots", (234, 219, 159)),
    ("armor_leather_helmet", (139, 90, 43)), ("armor_leather_chestplate", (132, 83, 36)),
    ("armor_leather_leggings", (125, 76, 29)), ("armor_leather_boots", (118, 69, 22)),
]
for name, color in item_icons:
    save(f"item/{name}.png", color)

# 30 crystal icons: type × tier
crystal_types = ["sword", "shield", "armor", "agility", "magic", "vitality"]
crystal_tiers = ["rough", "polished", "flawless", "perfect", "transcendent"]
crystal_base_hues = {
    "sword": 0, "shield": 220, "armor": 30,
    "agility": 120, "magic": 270, "vitality": 90,
}
for ctype in crystal_types:
    for i, tier in enumerate(crystal_tiers):
        hue = (crystal_base_hues[ctype] + i * 15) % 360
        sat = 0.6 + i * 0.08
        val = 0.5 + i * 0.1
        r, g, b = colorsys.hsv_to_rgb(hue / 360, sat, val)
        color = (int(r * 255), int(g * 255), int(b * 255))
        save(f"item/crystal_{ctype}_{tier}.png", color)

# 10 armor set icons
armor_sets = [
    ("armor_set_iron", (192, 192, 192)), ("armor_set_steel", (160, 170, 180)),
    ("armor_set_mythril", (100, 200, 220)), ("armor_set_adamantite", (80, 160, 200)),
    ("armor_set_dragon", (200, 50, 50)), ("armor_set_shadow", (60, 40, 80)),
    ("armor_set_void", (30, 10, 50)), ("armor_set_holy", (255, 240, 180)),
    ("armor_set_leather", (139, 90, 43)), ("armor_set_chain", (128, 128, 128)),
]
for name, color in armor_sets:
    save(f"item/{name}.png", color)

# ── 3. entity/ (120) ─────────────────────────────────────────────────────────
# 100 boss textures – HSV hue = (floor * 3.6) % 360
for floor in range(1, 101):
    hue = (floor * 3.6) % 360
    r, g, b = colorsys.hsv_to_rgb(hue / 360, 0.85, 0.9)
    color = (int(r * 255), int(g * 255), int(b * 255))
    save(f"entity/boss_floor_{floor}.png", color)

# 20 mob textures
mob_textures = [
    ("mob_passive", (100, 200, 100)), ("mob_neutral", (200, 200, 100)),
    ("mob_hostile", (200, 50, 50)), ("mob_pack", (180, 120, 60)),
    ("mob_ranged", (100, 150, 200)), ("mob_ambusher", (80, 40, 120)),
    ("mob_summoner", (150, 50, 200)), ("mob_enraged", (255, 0, 0)),
    ("mob_elite", (255, 215, 0)), ("mob_boss", (255, 0, 255)),
    ("mob_raid_boss", (128, 0, 128)), ("mob_wolf", (128, 128, 128)),
    ("mob_skeleton", (220, 220, 220)), ("mob_zombie", (50, 120, 50)),
    ("mob_spider", (40, 40, 40)), ("mob_creeper", (0, 200, 0)),
    ("mob_enderman", (20, 20, 20)), ("mob_blaze", (255, 165, 0)),
    ("mob_ghast", (240, 240, 240)), ("mob_slime", (0, 255, 128)),
]
for name, color in mob_textures:
    save(f"entity/{name}.png", color)

# ── 4. gui/ (15) ─────────────────────────────────────────────────────────────
save("gui/hud_background.png", (0, 0, 0, 128))
save("gui/hud_health_bar.png", (255, 0, 0))
save("gui/hud_mana_bar.png", (0, 0, 255))
save("gui/hud_exp_bar.png", (0, 255, 0))
save("gui/hud_skill_slot.png", (128, 128, 128))
save("gui/hud_crystal_slot.png", (128, 0, 128))
save("gui/menu_background.png", (32, 32, 32))
save("gui/menu_button.png", (96, 96, 96))
save("gui/skill_icon_attack.png", (255, 0, 0))
save("gui/skill_icon_defense.png", (0, 0, 255))
save("gui/skill_icon_heal.png", (0, 255, 0))
save("gui/skill_icon_damage.png", (255, 165, 0))
save("gui/skill_icon_buff.png", (255, 255, 0))
save("gui/skill_icon_debuff.png", (128, 0, 128))
save("gui/crystal_slot_overlay.png", (200, 150, 255))

# ── 5. environment/ (11) ─────────────────────────────────────────────────────
save("environment/floor_bg_plains.png", (100, 180, 60))
save("environment/floor_bg_forest.png", (34, 100, 34))
save("environment/floor_bg_cave.png", (100, 100, 100))
save("environment/floor_bg_lava.png", (200, 50, 0))
save("environment/floor_bg_ice.png", (170, 220, 255))
save("environment/floor_bg_desert.png", (220, 200, 140))
save("environment/floor_bg_swamp.png", (80, 100, 50))
save("environment/floor_bg_ocean.png", (30, 80, 180))
save("environment/floor_bg_sky.png", (180, 220, 255))
save("environment/floor_bg_void.png", (20, 0, 40))
save("environment/portal_particle.png", (200, 150, 255))

# ── Summary ──────────────────────────────────────────────────────────────────
print(f"Total files created: {count}")
print(f"Expected: 261")
if count == 261:
    print("✓ All textures generated successfully!")
else:
    print(f"⚠ Mismatch! Created {count}, expected 261")
