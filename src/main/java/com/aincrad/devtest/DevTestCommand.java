package com.aincrad.devtest;

import com.aincrad.boss.BossRegistry;
import com.aincrad.boss.impl.Floor1Boss;
import com.aincrad.boss.impl.Floor5Boss;
import com.aincrad.crystal.CrystalItem;
import com.aincrad.crystal.CrystalTier;
import com.aincrad.crystal.CrystalType;
import com.aincrad.enhance.EnhanceLevel;
import com.aincrad.mob.MobRegistration;
import com.aincrad.skill.Skill;
import com.aincrad.skill.SkillRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Developer testing command for Aincrad.
 * Provides /test subcommands to quickly test all game features.
 *
 * Usage:
 *   /test book      - Give test book
 *   /test area      - Build test arena
 *   /test items     - Give all item types
 *   /test mobs      - Spawn all mobs
 *   /test bosses    - Spawn F1 and F5 bosses
 *   /test skills    - Give all skills
 *   /test crystals   - Give all crystals
 *   /test enhance   - Give enhancement materials
 *   /test warp      - Teleport to floor 1 plaza
 *   /test all       - Run everything
 */
public final class DevTestCommand {

    private DevTestCommand() {
    }

    /**
     * Registers the /test command with all subcommands.
     * Called from AincradMod.onInitialize().
     */
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("adevtest")
            .requires(source -> true)
            .then(Commands.literal("book")
                .executes(ctx -> giveTestBook(ctx)))
            .then(Commands.literal("area")
                .executes(ctx -> buildTestArea(ctx)))
            .then(Commands.literal("items")
                .executes(ctx -> giveAllItems(ctx)))
            .then(Commands.literal("mobs")
                .executes(ctx -> spawnAllMobs(ctx)))
            .then(Commands.literal("bosses")
                .executes(ctx -> spawnBosses(ctx)))
            .then(Commands.literal("skills")
                .executes(ctx -> giveAllSkills(ctx)))
            .then(Commands.literal("crystals")
                .executes(ctx -> giveAllCrystals(ctx)))
            .then(Commands.literal("enhance")
                .executes(ctx -> giveEnhanceMaterials(ctx)))
            .then(Commands.literal("warp")
                .executes(ctx -> warpToPlaza(ctx)))
            .then(Commands.literal("all")
                .executes(ctx -> runAllTests(ctx)))
            .executes(ctx -> {
                ctx.getSource().sendSuccess(() -> Component.literal(
                    ChatFormatting.GREEN + "=== Aincrad Dev Test ===\n" +
                    ChatFormatting.YELLOW + "/test book " + ChatFormatting.GRAY + "- Give test book\n" +
                    ChatFormatting.YELLOW + "/test area " + ChatFormatting.GRAY + "- Build test arena\n" +
                    ChatFormatting.YELLOW + "/test items " + ChatFormatting.GRAY + "- Give all items\n" +
                    ChatFormatting.YELLOW + "/test mobs " + ChatFormatting.GRAY + "- Spawn all mobs\n" +
                    ChatFormatting.YELLOW + "/test bosses " + ChatFormatting.GRAY + "- Spawn F1 & F5 bosses\n" +
                    ChatFormatting.YELLOW + "/test skills " + ChatFormatting.GRAY + "- Give all skills\n" +
                    ChatFormatting.YELLOW + "/test crystals " + ChatFormatting.GRAY + "- Give all crystals\n" +
                    ChatFormatting.YELLOW + "/test enhance " + ChatFormatting.GRAY + "- Give enhance mats\n" +
                    ChatFormatting.YELLOW + "/test warp " + ChatFormatting.GRAY + "- Go to floor 1 plaza\n" +
                    ChatFormatting.YELLOW + "/test all " + ChatFormatting.GRAY + "- Run everything"
                ), false);
                return 1;
            })
        );
    }

    // ── /test book ──────────────────────────────────────────────────

    private static int giveTestBook(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        ItemStack book = DevTestBook.createTestBook();
        player.getInventory().add(book);
        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Given Aincrad Dev Test Book!"), false);
        return 1;
    }

    // ── /test area ──────────────────────────────────────────────────

    private static int buildTestArea(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        ServerLevel level = player.level();
        BlockPos center = player.blockPosition();

        DevTestArena.generate(level, center);
        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Test arena built at " + center.getX() + ", " + center.getZ() + "!"), false);
        return 1;
    }

    // ── /test items ─────────────────────────────────────────────────

    private static int giveAllItems(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        int count = 0;

        // Give weapon types
        String[] weaponTypes = {"Sword", "Axe", "Bow", "Dagger", "Katana", "Rapier", "Spear", "Staff", "Shield"};
        String[] weaponTiers = {"Common", "Rare", "Epic", "Legendary"};
        String[] weaponMaterials = {"Iron", "Steel", "Mythril", "Adamantite"};

        for (String tier : weaponTiers) {
            for (String material : weaponMaterials) {
                for (String type : weaponTypes) {
                    ItemStack stack = new ItemStack(Items.IRON_SWORD);
                    stack.set(DataComponents.CUSTOM_NAME, Component.literal(
                        ChatFormatting.GOLD + tier + " " + material + " " + type));
                    player.getInventory().add(stack);
                    count++;
                }
            }
        }

        // Give armor sets
        String[] armorSets = {"Leather", "Chain", "Iron", "Steel", "Mythril", "Adamantite", "Dragon", "Shadow", "Holy", "Void"};
        String[] armorPieces = {"Helmet", "Chestplate", "Leggings", "Boots", "Amulet", "Ring"};

        for (String set : armorSets) {
            for (String piece : armorPieces) {
                ItemStack stack = new ItemStack(Items.IRON_CHESTPLATE);
                stack.set(DataComponents.CUSTOM_NAME, Component.literal(
                    ChatFormatting.AQUA + set + " " + piece));
                player.getInventory().add(stack);
                count++;
            }
        }

        // Give materials
        String[] materials = {"Iron Ingot", "Steel Ingot", "Mythril Ingot", "Adamantite Ingot",
            "Bone", "Claw", "Crystal Shard", "Diamond", "Emerald", "Essence",
            "Eye", "Fang", "Feather", "Hide", "Iron Dust", "Mythril Dust",
            "Ruby", "Sapphire", "Scale", "Venom"};
        String[] tiers = {"Tier 1", "Tier 2"};

        for (String tier : tiers) {
            for (String mat : materials) {
                ItemStack stack = new ItemStack(Items.IRON_NUGGET);
                stack.set(DataComponents.CUSTOM_NAME, Component.literal(
                    ChatFormatting.GRAY + tier + " " + mat));
                player.getInventory().add(stack);
                count++;
            }
        }

        // Give consumables
        String[] consumables = {"HP Potion", "MP Potion", "Greater HP Potion", "Greater MP Potion",
            "Agility Tonic", "Greater Agility Tonic", "Fire Resistance", "Greater Fire Resistance",
            "Invisibility", "Greater Invisibility", "Night Vision", "Greater Night Vision",
            "Regeneration Draught", "Greater Regeneration Draught", "Stamina Elixir",
            "Greater Stamina Elixir", "Strength Tonic", "Greater Strength Tonic",
            "Water Breathing", "Greater Water Breathing"};

        for (String cons : consumables) {
            ItemStack stack = new ItemStack(Items.POTION);
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(
                ChatFormatting.LIGHT_PURPLE + cons));
            player.getInventory().add(stack);
            count++;
        }

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Given " + finalCount + " test items!"), false);
        return 1;
    }

    // ── /test mobs ──────────────────────────────────────────────────

    private static int spawnAllMobs(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        ServerLevel level = player.level();
        BlockPos center = player.blockPosition();
        int count = 0;

        List<EntityType<?>> mobs = MobRegistration.getAllMobs();
        int radius = 5;

        for (int i = 0; i < mobs.size(); i++) {
            EntityType<?> type = mobs.get(i);
            double angle = 2 * Math.PI * i / mobs.size();
            int x = center.getX() + (int) (radius * Math.cos(angle));
            int z = center.getZ() + (int) (radius * Math.sin(angle));
            int y = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                new BlockPos(x, 64, z)).getY();

            Entity entity = type.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (entity != null) {
                entity.setPos(x + 0.5, y, z + 0.5);
                level.addFreshEntity(entity);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Spawned " + finalCount + " mobs around you!"), false);
        return 1;
    }

    // ── /test bosses ────────────────────────────────────────────────

    private static int spawnBosses(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        ServerLevel level = player.level();
        BlockPos center = player.blockPosition();

        // Spawn Floor 1 Boss
        Entity f1 = Floor1Boss.TYPE.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (f1 != null) {
            f1.setPos(center.getX() + 3.0, center.getY(), center.getZ());
            level.addFreshEntity(f1);
        }

        // Spawn Floor 5 Boss
        Entity f5 = Floor5Boss.TYPE.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (f5 != null) {
            f5.setPos(center.getX() - 3.0, center.getY(), center.getZ());
            level.addFreshEntity(f5);
        }

        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Spawned Floor 1 Boss (Goblin King) and Floor 5 Boss (Stone Golem)!"), false);
        return 1;
    }

    // ── /test skills ────────────────────────────────────────────────

    private static int giveAllSkills(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        int count = 0;

        for (Skill skill : SkillRegistry.getAll()) {
            ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(
                ChatFormatting.BLUE + skill.getName()));
            stack.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(List.of(
                Component.literal(ChatFormatting.GRAY + "ID: " + skill.getId()),
                Component.literal(ChatFormatting.GRAY + "Cooldown: " + skill.getCooldown() + "s"),
                Component.literal(ChatFormatting.GRAY + "Type: " + skill.getSkillType())
            )));
            player.getInventory().add(stack);
            count++;
        }

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Given " + finalCount + " skills!"), false);
        return 1;
    }

    // ── /test crystals ──────────────────────────────────────────────

    private static int giveAllCrystals(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        int count = 0;

        for (CrystalType type : CrystalType.values()) {
            for (CrystalTier tier : CrystalTier.values()) {
                CrystalItem crystal = new CrystalItem(type, tier);
                ItemStack stack = new ItemStack(crystal);
                player.getInventory().add(stack);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Given " + finalCount + " crystals (6 types x 5 tiers)!"), false);
        return 1;
    }

    // ── /test enhance ───────────────────────────────────────────────

    private static int giveEnhanceMaterials(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        int count = 0;

        // Give enhancement materials for each level
        for (EnhanceLevel level : EnhanceLevel.values()) {
            if (level.getLevel() == 0) continue;

            ItemStack stack = new ItemStack(Items.NETHERITE_SCRAP);
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(
                ChatFormatting.GOLD + "Enhancement Stone +" + level.getLevel()));
            stack.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(List.of(
                Component.literal(ChatFormatting.GRAY + "Success: " + (int)(level.getSuccessRate() * 100) + "%"),
                Component.literal(ChatFormatting.GRAY + "Break: " + (int)(level.getBreakChance() * 100) + "%"),
                Component.literal(ChatFormatting.GRAY + "Bonus: " + level.getStatBonusMultiplier() + "x")
            )));
            player.getInventory().add(stack);
            count++;
        }

        // Give protection stones
        ItemStack protect = new ItemStack(Items.NETHERITE_INGOT);
        protect.set(DataComponents.CUSTOM_NAME, Component.literal(
            ChatFormatting.AQUA + "Protection Stone"));
        protect.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(List.of(
            Component.literal(ChatFormatting.GRAY + "Prevents item break on failure")
        )));
        player.getInventory().add(protect);
        count++;

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Given " + finalCount + " enhancement materials!"), false);
        return 1;
    }

    // ── /test warp ──────────────────────────────────────────────────

    private static int warpToPlaza(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal(ChatFormatting.RED + "Must be run by a player!"));
            return 0;
        }
        ServerLevel level = player.level();

        // Teleport to floor 1 plaza (safe zone)
        BlockPos plazaPos = new BlockPos(0, 64, 0);
        player.teleportTo(plazaPos.getX() + 0.5, plazaPos.getY(), plazaPos.getZ() + 0.5);

        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "Warped to Floor 1 Plaza!"), false);
        return 1;
    }

    // ── /test all ───────────────────────────────────────────────────

    private static int runAllTests(CommandContext<CommandSourceStack> ctx) {
        giveTestBook(ctx);
        buildTestArea(ctx);
        giveAllItems(ctx);
        spawnAllMobs(ctx);
        spawnBosses(ctx);
        giveAllSkills(ctx);
        giveAllCrystals(ctx);
        giveEnhanceMaterials(ctx);
        warpToPlaza(ctx);

        ctx.getSource().sendSuccess(() -> Component.literal(
            ChatFormatting.GREEN + "=== All tests executed! ==="), false);
        return 1;
    }
}