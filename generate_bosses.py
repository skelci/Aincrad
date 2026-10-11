#!/usr/bin/env python3
"""Generate 80 boss entity files for Aincrad."""

import os

OUTPUT_DIR = "src/main/java/com/aincrad/boss/impl"

# Boss definitions: (floor, name, theme, width, height, description)
BOSSES = [
    # Grassland (1-10)
    (2, "Goblin Scout", "goblin_scout", 0.8, 1.5, "a quick and cunning skirmisher"),
    (3, "Wolf Alpha", "wolf_alpha", 1.2, 1.0, "a fierce pack leader"),
    (4, "Bandit Leader", "bandit_leader", 0.9, 1.9, "a ruthless outlaw"),
    (6, "Giant Rat", "giant_rat", 1.0, 0.8, "an oversized rodent"),
    (7, "Harpy", "harpy", 1.0, 1.8, "a winged predator"),
    (8, "Fungal Brute", "fungal_brute", 1.3, 2.2, "a toxic monstrosity"),
    (9, "Treant Sapling", "treant_sapling", 1.5, 2.5, "a young but deadly tree"),
    # Forest (11-20)
    (11, "Spider Queen", "spider_queen", 1.8, 1.2, "an eight-legged terror"),
    (12, "Owlbear", "owlbear", 1.4, 1.8, "a hybrid of owl and bear"),
    (13, "Thorn Beast", "thorn_beast", 1.3, 1.6, "a plant-based predator"),
    (14, "Forest Wraith", "forest_wraith", 1.0, 2.0, "a spirit of the woods"),
    (16, "Vine Horror", "vine_horror", 1.6, 2.4, "a mass of deadly vines"),
    (17, "Moonlit Prowler", "moonlit_prowler", 1.1, 1.0, "a nocturnal hunter"),
    (18, "Eldritch Growth", "eldritch_growth", 2.0, 2.8, "an aberrant plant"),
    (19, "Canopy Stalker", "canopy_stalker", 1.0, 2.2, "an arboreal ambush predator"),
    # Cave (21-30)
    (21, "Cave Crawler", "cave_crawler", 1.5, 1.0, "a many-legged subterranean horror"),
    (22, "Bat Swarm", "bat_swarm", 1.2, 1.5, "a cloud of bloodthirsty bats"),
    (23, "Rock Elemental", "rock_elemental", 1.6, 2.4, "a living boulder"),
    (24, "Blind Horror", "blind_horror", 1.4, 1.8, "an eyeless subterranean predator"),
    (26, "Crystal Golem", "crystal_golem", 1.5, 2.6, "a gemstone construct"),
    (27, "Deep Dweller", "deep_dweller", 1.3, 1.5, "an abyssal lurker"),
    (28, "Echo Beast", "echo_beast", 1.2, 1.4, "a sound-sensitive hunter"),
    (29, "Shroomling King", "shroomling_king", 1.0, 1.8, "a fungal sovereign"),
    # Lava (31-40)
    (31, "Ember Imp", "ember_imp", 0.7, 1.2, "a small but fiery nuisance"),
    (32, "Flame Atronach", "flake_atronach", 1.0, 2.0, "a fire elemental"),
    (33, "Obsidian Beast", "obsidian_beast", 1.4, 1.8, "a volcanic predator"),
    (34, "Pyroclasm", "pyroclasm", 1.2, 2.2, "a living eruption"),
    (36, "Molten Titan", "molten_titan", 2.0, 3.0, "a colossus of magma"),
    (37, "Infernal Knight", "infernal_knight", 1.0, 2.2, "a demonic warrior"),
    (38, "Magma Serpent", "magma_serpent", 1.5, 1.0, "a slithering river of fire"),
    (39, "Brimstone Lord", "brimstone_lord", 1.3, 2.5, "a sulfurous overlord"),
    # Ice (41-50)
    (41, "Frost Sprite", "frost_sprite", 0.6, 1.0, "a tiny but dangerous ice spirit"),
    (42, "Ice Golem", "ice_golem", 1.5, 2.5, "a frozen construct"),
    (43, "Blizzard Elemental", "blizzard_elemental", 1.2, 2.2, "a living storm"),
    (44, "Frozen Wraith", "frozen_wraith", 1.0, 2.0, "a glacial apparition"),
    (46, "Permafrost Behemoth", "permafrost_behemoth", 1.8, 2.8, "a massive ice beast"),
    (47, "Glacial Spider", "glacial_spider", 1.6, 1.2, "an arctic arachnid"),
    (48, "Snow Stalker", "snow_stalker", 1.1, 1.0, "a white-furred predator"),
    (49, "Avalanche Brute", "avalanche_brute", 1.7, 2.6, "a crushing force of nature"),
    # Desert (51-60)
    (51, "Sand Scorpion", "sand_scorpion", 1.3, 0.8, "a venomous desert dweller"),
    (52, "Dust Devil", "dust_devil", 1.0, 2.0, "a swirling sand vortex"),
    (53, "Mummy Servant", "mummy_servant", 0.9, 1.9, "an undead guardian"),
    (54, "Sun Elemental", "sun_elemental", 1.1, 2.1, "a radiant being"),
    (56, "Dune Stalker", "dune_stalker", 1.2, 1.0, "a sand-swimming predator"),
    (57, "Scarab King", "scarab_king", 1.0, 0.6, "an insectoid monarch"),
    (58, "Mirage Beast", "mirage_beast", 1.3, 1.5, "an illusionary predator"),
    (59, "Desert Colossus", "desert_colossus", 2.0, 3.2, "a towering sand construct"),
    # Swamp (61-70)
    (61, "Bog Leaper", "bog_leaper", 1.0, 1.2, "a frog-like ambush predator"),
    (62, "Venomous Serpent", "venomous_serpent", 1.4, 0.8, "a highly toxic snake"),
    (63, "Mire Fiend", "mire_fiend", 1.2, 1.8, "a swamp-dwelling demon"),
    (64, "Will-o'-the-Mire", "will_o_the_mire", 0.8, 1.5, "a ghostly lantern spirit"),
    (66, "Swamp Thing", "swamp_thing", 1.6, 2.4, "a mass of vegetation"),
    (67, "Leech Matriarch", "leech_matriarch", 1.2, 0.6, "a blood-sucking horror"),
    (68, "Mud Golem", "mud_golem", 1.5, 2.2, "a filthy construct"),
    (69, "Fen Horror", "fen_horror", 1.3, 2.0, "a marsh-dwelling abomination"),
    # Ocean (71-80)
    (71, "Coral Golem", "coral_golem", 1.5, 2.4, "a reef construct"),
    (72, "Tide Elemental", "tide_elemental", 1.2, 2.2, "a water elemental"),
    (73, "Siren's Wrath", "sirens_wrath", 1.0, 1.8, "a deadly songstress"),
    (74, "Deep One", "deep_one", 1.2, 1.6, "an abyssal humanoid"),
    (76, "Jellyfish Matriarch", "jellyfish_matriarch", 1.4, 1.6, "a floating terror"),
    (77, "Leviathan Spawn", "leviathan_spawn", 1.8, 2.5, "a young sea monster"),
    (78, "Brine Beast", "brine_beast", 1.3, 1.4, "a saltwater predator"),
    (79, "Abyssal Tentacle", "abyssal_tentacle", 1.6, 2.0, "a deep-sea horror"),
    # Sky (81-90)
    (81, "Harpy Matriarch", "harpy_matriarch", 1.1, 1.9, "a queen of the harpies"),
    (82, "Wind Elemental", "wind_elemental", 1.0, 2.0, "a living gale"),
    (83, "Cloud Serpent", "cloud_serpent", 1.4, 1.2, "a sky-dwelling snake"),
    (84, "Thunderbird", "thunderbird", 1.3, 1.8, "a storm bird"),
    (86, "Storm Djinn", "storm_djinn", 1.0, 2.2, "a tempest spirit"),
    (87, "Celestial Guardian", "celestial_guardian", 1.2, 2.4, "a divine protector"),
    (88, "Zephyr Lord", "zephyr_lord", 0.9, 1.8, "a master of winds"),
    (89, "Tempest Spawn", "tempest_spawn", 1.1, 2.0, "a child of the storm"),
    # Void (91-100)
    (91, "Void Spawn", "void_spawn", 1.0, 1.6, "a creature from beyond"),
    (92, "Null Beast", "null_beast", 1.2, 1.4, "an absence given form"),
    (93, "Entropy Elemental", "entropy_elemental", 1.1, 2.0, "a being of decay"),
    (94, "Shadow Rift", "shadow_rift", 1.3, 1.8, "a tear in reality"),
    (96, "Oblivion Seeker", "oblivion_seeker", 1.0, 1.9, "a hunter from the void"),
    (97, "Chaos Lord", "chaos_lord", 1.2, 2.2, "a master of disorder"),
    (98, "Void Reaver", "void_reaver", 1.4, 2.4, "a devastating void entity"),
    (99, "Nightmare Incarnate", "nightmare_incarnate", 1.3, 2.6, "a living nightmare"),
]

# Theme colors for ChatFormatting
THEME_COLORS = {
    "grassland": "ChatFormatting.GREEN",
    "forest": "ChatFormatting.DARK_GREEN",
    "cave": "ChatFormatting.DARK_GRAY",
    "lava": "ChatFormatting.GOLD",
    "ice": "ChatFormatting.AQUA",
    "desert": "ChatFormatting.YELLOW",
    "swamp": "ChatFormatting.DARK_GREEN",
    "ocean": "ChatFormatting.BLUE",
    "sky": "ChatFormatting.WHITE",
    "void": "ChatFormatting.DARK_PURPLE",
}

# Theme ranges for BossTheme
THEME_RANGES = [
    (1, 10, "GRASSLAND", "ChatFormatting.GREEN"),
    (11, 20, "FOREST", "ChatFormatting.DARK_GREEN"),
    (21, 30, "CAVE", "ChatFormatting.DARK_GRAY"),
    (31, 40, "LAVA", "ChatFormatting.GOLD"),
    (41, 50, "ICE", "ChatFormatting.AQUA"),
    (51, 60, "DESERT", "ChatFormatting.YELLOW"),
    (61, 70, "SWAMP", "ChatFormatting.DARK_GREEN"),
    (71, 80, "OCEAN", "ChatFormatting.BLUE"),
    (81, 90, "SKY", "ChatFormatting.WHITE"),
    (91, 100, "VOID", "ChatFormatting.DARK_PURPLE"),
]

def get_theme_color(floor):
    for lo, hi, _, color in THEME_RANGES:
        if lo <= floor <= hi:
            return color
    return "ChatFormatting.WHITE"

def get_theme_enum(floor):
    for lo, hi, theme, _ in THEME_RANGES:
        if lo <= floor <= hi:
            return theme
    return "GRASSLAND"

def gen_boss(floor, name, theme, width, height, desc):
    color = get_theme_color(floor)
    theme_enum = get_theme_enum(floor)

    # Determine pattern count based on floor
    if floor <= 20:
        pattern_count = 3
    elif floor <= 50:
        pattern_count = 4
    else:
        pattern_count = 5

    # Generate patterns code
    patterns_code = ""

    # Always start with library patterns
    if pattern_count == 3:
        patterns_code += """        // Standard pattern library
        var libPatterns = BossAttackLibrary.getStandardPatterns(FLOOR);
        for (var p : libPatterns) {
            patterns.add(p);
            patternCooldowns.add(0);
        }
"""
    else:
        patterns_code += """        // Tier boss pattern library
        var libPatterns = BossAttackLibrary.getTierBossPatterns(FLOOR);
        for (var p : libPatterns) {
            patterns.add(p);
            patternCooldowns.add(0);
        }
"""

    # Add custom themed patterns
    # Pattern 1: Heavy Strike
    patterns_code += f"""        // {name} Heavy Strike
        patterns.add(BossPatternFactory.createHeavyStrike("HeavyStrike", BASE_DAMAGE * 1.4f, 70));
        patternCooldowns.add(0);
"""

    # Pattern 2: AoE Slam
    patterns_code += f"""        // {name} AoE Slam
        patterns.add(BossPatternFactory.createAoeSlam("AoeSlam", BASE_DAMAGE * 1.5f, 6.0f, 85));
        patternCooldowns.add(0);
"""

    # Pattern 3: Charge (for higher floors)
    if pattern_count >= 4:
        patterns_code += f"""        // {name} Charge
        patterns.add(BossPatternFactory.createCharge("Charge", BASE_DAMAGE * 1.3f, 12.0f, 95));
        patternCooldowns.add(0);
"""

    # Pattern 4: Summon (for highest floors)
    if pattern_count >= 5:
        patterns_code += f"""        // {name} Summon
        patterns.add(BossPatternFactory.createSummon("Summon", 2, BASE_DAMAGE * 0.25f, 160));
        patternCooldowns.add(0);
"""

    # Generate phase enter code
    phase_code = ""
    for phase in [2, 3, 4, 5]:
        msg = f"The {name} grows more dangerous!"
        if phase == 2:
            msg = f"The {name}'s power increases!"
        elif phase == 3:
            msg = f"The {name} enters a frenzy!"
        elif phase == 4:
            msg = f"The {name} unleashes its full might!"
        elif phase == 5:
            msg = f"The {name} is desperate!"

        phase_code += f"""        String msg{phase} = switch (phase) {{
            case PHASE_{phase} -> "{msg}";
            default -> null;
        }};
        if (msg{phase} != null) {{
            final String finalMsg{phase} = msg{phase};
            this.level().players().forEach(p ->
                p.sendSystemMessage(Component.literal({color} + finalMsg{phase})));
        }}
"""

    # Add attribute changes
    attr_code = ""
    for phase in [2, 3, 4, 5]:
        if phase == 2:
            attr_code += f"""            case PHASE_2 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.15f);
"""
        elif phase == 3:
            attr_code += f"""            case PHASE_3 -> this.getAttribute(Attributes.ARMOR).setBaseValue({10 + floor // 2}f);
"""
        elif phase == 4:
            attr_code += f"""            case PHASE_4 -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue({0.3 + floor * 0.005}f);
"""
        elif phase == 5:
            attr_code += f"""            case PHASE_5 -> this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * 1.3f);
"""

    # Build the full Java code
    java = f"""package com.aincrad.boss.impl;

import com.aincrad.boss.BossEntity;
import com.aincrad.boss.BossPhase;
import com.aincrad.boss.BossRegistry;
import com.aincrad.boss.patterns.BossAttackLibrary;
import com.aincrad.boss.patterns.BossPatternFactory;
import com.aincrad.boss.util.BossStatScaler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Floor {floor} boss — {name}.
 * A {theme_enum.lower()}-themed boss with {desc}. {pattern_count} attack patterns.
 */
public class Floor{floor}Boss extends BossEntity {{

    public static final ResourceKey<EntityType<?>> TYPE_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("aincrad", "floor{floor}_boss"));

    public static final EntityType<Floor{floor}Boss> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE, TYPE_KEY,
        EntityType.Builder.of(Floor{floor}Boss::new, MobCategory.MONSTER)
            .sized({width}f, {height}f)
            .build(TYPE_KEY));

    private static final int FLOOR = {floor};
    private static final float BASE_HP = BossStatScaler.scaledHp(FLOOR);
    private static final float BASE_DAMAGE = BossStatScaler.scaledDamage(FLOOR);

    public Floor{floor}Boss(EntityType<? extends Monster> entityType, Level level) {{
        super(entityType, level);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(BASE_HP);
        this.setHealth(BASE_HP);
    }}

    @Override
    protected void initPatterns() {{
{patterns_code}
    }}

    @Override
    public String getBossName() {{
        return "{name}";
    }}

    @Override
    protected void onPhaseEnter(BossPhase phase) {{
        if (this.level().isClientSide()) return;

{phase_code}
        switch (phase) {{
{attr_code}            default -> {{}}
        }}
    }}

    static {{
        BossRegistry.register("floor{floor}_{theme}", Floor{floor}Boss.class, FLOOR);
    }}
}}
"""

    return java

# Main
os.makedirs(OUTPUT_DIR, exist_ok=True)

for floor, name, theme, width, height, desc in BOSSES:
    java = gen_boss(floor, name, theme, width, height, desc)
    filepath = os.path.join(OUTPUT_DIR, f"Floor{floor}Boss.java")
    with open(filepath, "w") as f:
        f.write(java)
    print(f"Generated Floor{floor}Boss.java")

print(f"\nGenerated {len(BOSSES)} boss files.")
