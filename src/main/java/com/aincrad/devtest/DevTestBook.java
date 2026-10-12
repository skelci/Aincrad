package com.aincrad.devtest;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.WrittenBookContent;

/**
 * Generates a BookItem with pages listing ALL test scenarios.
 * Each page covers a different aspect of the game to test.
 */
public final class DevTestBook {

    private DevTestBook() {
    }

    /**
     * Creates a written book with all test information.
     *
     * @return ItemStack containing the written book
     */
    public static ItemStack createTestBook() {
        List<Component> pages = new ArrayList<>();

        // Page 1: Welcome + how to use /test
        pages.add(Component.literal("""
            §l§nAincrad Dev Test Book§r

            Welcome to the Aincrad developer testing system!

            Use /test <command> to run tests:
            • /test book - This book
            • /test area - Build test arena
            • /test items - Give all items
            • /test mobs - Spawn all mobs
            • /test bosses - Spawn F1 & F5 bosses
            • /test skills - Give all skills
            • /test crystals - Give all crystals
            • /test enhance - Give enhance mats
            • /test warp - Go to floor 1 plaza
            • /test all - Run everything

            §lChecklist:§r
            □ Test each feature
            □ Verify no crashes
            □ Check balance
            □ Document bugs
            """));

        // Page 2: Boss testing checklist
        pages.add(Component.literal("""
            §l§nBoss Testing (F1-F100)§r

            Total: 100 bosses across 10 tiers

            §lTiers:§r
            • F1-10: Plains (Tier 1)
            • F11-20: Forest (Tier 2)
            • F21-30: Cave (Tier 3)
            • F31-40: Lava (Tier 4)
            • F41-50: Ice (Tier 5)
            • F51-60: Desert (Tier 6)
            • F61-70: Swamp (Tier 7)
            • F71-80: Ocean (Tier 8)
            • F81-90: Sky (Tier 9)
            • F91-100: Void (Tier 10)

            §lChecklist:§r
            □ Phases trigger correctly
            □ Enrage timer works
            □ Arena lock functions
            □ Loot tables drop
            □ Boss health scales
            □ Attack patterns fire
            □ F25/F50 raid bosses
            """));

        // Page 3: Mob testing checklist
        pages.add(Component.literal("""
            §l§nMob Testing (50 mobs)§r

            §lMob Types:§r
            • Passive, Neutral, Hostile
            • Pack, Ranged, Ambusher
            • Summoner, Enraged, Elite
            • Boss, Raid Boss

            §lChecklist:§r
            □ All 50 mobs spawn
            □ AI goals work correctly
            □ Floor scaling applies
            □ Drops are correct
            □ Health/damage balanced
            □ Spawning rates correct
            □ No pathfinding issues
            □ Ranged mobs shoot
            □ Summoners spawn adds
            □ Elites have bonuses

            §lTest Command:§r
            /test mobs
            """));

        // Page 4: Item testing checklist
        pages.add(Component.literal("""
            §l§nItem Testing (204 items)§r

            §lCategories:§r
            • Weapons (Common/Rare/Epic/Legendary)
            • Armor (10 sets, 6 pieces each)
            • Materials (Tier 1 & Tier 2)
            • Consumables (Potions, Tonics)
            • Crystals (6 types, 5 tiers)

            §lWeapon Types:§r
            Sword, Axe, Bow, Dagger,
            Katana, Rapier, Spear,
            Staff, Shield

            §lArmor Sets:§r
            Leather, Chain, Iron, Steel,
            Mythril, Adamantite, Dragon,
            Shadow, Holy, Void

            §lChecklist:§r
            □ All items obtainable
            □ Stats are correct
            □ Rarity colors show
            □ Crafting recipes work
            □ Enhancement applies
            □ Crystal socketing works
            """));

        // Page 5: Skill testing checklist
        pages.add(Component.literal("""
            §l§nSkill Testing (80+ skills)§r

            §lSkill Types:§r
            • Active (damage, heal, buff)
            • Passive (stat boosts)
            • Ultimate (powerful, long CD)

            §lChecklist:§r
            □ All skills learnable
            □ Cooldowns tick down
            □ MP costs deducted
            □ Effects apply correctly
            □ Damage scales with stats
            □ Buffs/debuffs work
            □ AoE skills hit multiple
            □ Targeting is correct
            □ No skill crashes
            □ Skill combos work

            §lTest Command:§r
            /test skills
            """));

        // Page 6: Crystal testing checklist
        pages.add(Component.literal("""
            §l§nCrystal Testing§r

            §l6 Crystal Types:§r
            • Sword - +Attack
            • Shield - +Defense
            • Armor - +Health
            • Agility - +Speed
            • Magic - +MP/Spell
            • Vitality - +Regen

            §l5 Tiers:§r
            • Rough (10% bonus)
            • Polished (25% bonus)
            • Flawless (50% bonus)
            • Perfect (100% bonus)
            • Transcendent (200% bonus)

            §lChecklist:§r
            □ All 30 crystal combos
            □ Socketing works
            □ Bonuses apply
            □ Upgrade path works
            □ Extract function works
            □ Crystal drops from bosses
            """));

        // Page 7: Enhancement testing
        pages.add(Component.literal("""
            §l§nEnhancement Testing§r

            §lLevels: +0 to +15§r

            §lSuccess Rates:§r
            • +1: 95%  • +2: 90%
            • +3: 85%  • +4: 80%
            • +5: 75%  • +6: 70%
            • +7: 65%  • +8: 60%
            • +9: 55%  • +10: 50%
            • +11: 45% • +12: 40%
            • +13: 35% • +14: 30%
            • +15: 25%

            §lBreak Chance:§r
            • +1-4: 0%
            • +5: 5%  • +6: 10%
            • +7: 15% • +8: 20%
            • +9: 25% • +10: 30%
            • +11: 35% • +12: 40%
            • +13: 45% • +14: 50%
            • +15: 55%

            §lChecklist:§r
            □ Enhancement succeeds
            □ Break destroys item
            □ Stats scale correctly
            □ Materials consumed
            """));

        // Page 8: Economy testing
        pages.add(Component.literal("""
            §l§nEconomy Testing§r

            §lCurrency: Col§r

            §lFeatures:§r
            • Player trading
            • Auction house
            • NPC shops
            • Boss loot sales
            • Quest rewards

            §lChecklist:§r
            □ Col earned from kills
            □ Trading works
            □ Auction house lists
            □ Bids function
            □ NPC shops buy/sell
            □ Prices are balanced
            □ No dupe exploits
            □ Taxes apply
            □ Transaction logs

            §lTest Command:§r
            /test warp (go to plaza)
            """));

        // Page 9: Social testing
        pages.add(Component.literal("""
            §l§nSocial Testing§r

            §lFeatures:§r
            • Guilds (create, join, ranks)
            • Parties (invite, share loot)
            • Quests (daily, weekly, raid)
            • Chat channels
            • Friend system

            §lChecklist:§r
            □ Guild creation works
            □ Guild ranks function
            □ Party invite/join works
            □ Shared XP/loot
            □ Quest tracking works
            □ Daily reset functions
            □ Weekly raid spawns
            □ Chat channels separate
            □ Friend list works
            □ No social exploits

            §lTest Command:§r
            /test all
            """));

        // Page 10: Dungeon testing
        pages.add(Component.literal("""
            §l§nDungeon Testing§r

            §l75 Layouts, 5 Tiers:§r
            • Tier 1: F1-20 (15 layouts)
            • Tier 2: F21-40 (15 layouts)
            • Tier 3: F41-60 (15 layouts)
            • Tier 4: F61-80 (15 layouts)
            • Tier 5: F81-100 (15 layouts)

            §lChecklist:§r
            □ All 75 layouts generate
            □ Rooms connect properly
            □ Loot chests spawn
            □ Traps function
            □ Lockouts work
            □ Timer counts down
            □ Boss room accessible
            □ Exit portal works
            □ Difficulty scales
            □ No generation errors

            §l§nEnd of Test Book§r
            """));

        // Create the book ItemStack
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        List<Filterable<Component>> filterablePages = new ArrayList<>();
        for (Component page : pages) {
            filterablePages.add(Filterable.passThrough(page));
        }
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
            Filterable.passThrough("Aincrad Dev Test"),
            "Aincrad Dev",
            0,
            filterablePages,
            true
        ));
        book.set(DataComponents.CUSTOM_NAME, Component.literal("§6§lAincrad Dev Test Book§r"));

        return book;
    }
}