package com.aincrad.paper.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ItemDefs {

    public enum Rarity {
        COMMON(NamedTextColor.WHITE, 1.0),
        UNCOMMON(NamedTextColor.GREEN, 1.3),
        RARE(NamedTextColor.AQUA, 1.7),
        EPIC(NamedTextColor.LIGHT_PURPLE, 2.2),
        LEGENDARY(NamedTextColor.GOLD, 3.0),
        MYTHIC(NamedTextColor.RED, 4.0);

        public final NamedTextColor color;
        public final double multiplier;

        Rarity(NamedTextColor color, double multiplier) {
            this.color = color;
            this.multiplier = multiplier;
        }
    }

    public enum Slot { HAND, HEAD, CHEST, LEGS, FEET, OTHER }

    public record Def(String id, String name, Material material, Slot slot, double damage, double defense,
                      int levelReq, Rarity rarity) {
    }

    public static final NamespacedKey KEY_ITEM = new NamespacedKey("aincrad", "item");

    private static final Map<String, Def> DEFS = new HashMap<>();

    private ItemDefs() {
    }

    private static void add(String id, String name, Material material, Slot slot, double damage, double defense,
                            int levelReq, Rarity rarity) {
        DEFS.put(id, new Def(id, name, material, slot, damage, defense, levelReq, rarity));
    }

    static {
        add("anneal_blade", "Anneal Blade", Material.IRON_SWORD, Slot.HAND, 7.0, 0, 1, Rarity.COMMON);
        add("knight_sword", "Knight Sword", Material.IRON_SWORD, Slot.HAND, 8.5, 0, 3, Rarity.COMMON);
        add("hunting_knife", "Hunting Knife", Material.STONE_SWORD, Slot.HAND, 5.5, 0, 1, Rarity.COMMON);
        add("reinforced_sword", "Reinforced Blade", Material.IRON_SWORD, Slot.HAND, 10.0, 0, 6, Rarity.UNCOMMON);
        add("flicking_rapier", "Flicking Rapier", Material.DIAMOND_SWORD, Slot.HAND, 10.5, 0, 10, Rarity.RARE);
        add("wind_slayer", "Wind Slayer", Material.DIAMOND_SWORD, Slot.HAND, 12.0, 0, 12, Rarity.RARE);
        add("iron_lord_blade", "Iron Lord Blade", Material.DIAMOND_SWORD, Slot.HAND, 11.5, 0, 11, Rarity.RARE);
        add("demon_crasher", "Demon Crasher", Material.DIAMOND_AXE, Slot.HAND, 13.5, 0, 15, Rarity.EPIC);
        add("beast_stinger", "Beast Stinger", Material.NETHERITE_SWORD, Slot.HAND, 15.0, 0, 18, Rarity.EPIC);
        add("moonlit_blade", "Moonlit Blades", Material.NETHERITE_SWORD, Slot.HAND, 14.5, 0, 17, Rarity.EPIC);
        add("homing_thorn", "Homing Thorn", Material.NETHERITE_SWORD, Slot.HAND, 16.0, 0, 20, Rarity.EPIC);
        add("dark_repulser", "Dark Repulser", Material.NETHERITE_SWORD, Slot.HAND, 17.5, 0, 22, Rarity.LEGENDARY);
        add("transcendent", "Transcendent Blade", Material.NETHERITE_SWORD, Slot.HAND, 21.0, 0, 30, Rarity.MYTHIC);

        add("sprout_coif", "Sprout Coif", Material.CHAINMAIL_HELMET, Slot.HEAD, 0, 2.0, 2, Rarity.COMMON);
        add("sprout_jacket", "Sprout Jacket", Material.CHAINMAIL_CHESTPLATE, Slot.CHEST, 0, 3.5, 2, Rarity.COMMON);
        add("sprout_leggings", "Sprout Leggings", Material.CHAINMAIL_LEGGINGS, Slot.LEGS, 0, 3.0, 2, Rarity.COMMON);
        add("sprout_boots", "Sprout Boots", Material.CHAINMAIL_BOOTS, Slot.FEET, 0, 1.5, 2, Rarity.COMMON);

        add("knight_helm", "Knight's Helm", Material.IRON_HELMET, Slot.HEAD, 0, 3.5, 8, Rarity.UNCOMMON);
        add("knight_plate", "Knight's Plate", Material.IRON_CHESTPLATE, Slot.CHEST, 0, 6.0, 8, Rarity.UNCOMMON);
        add("knight_greaves", "Knight's Greaves", Material.IRON_LEGGINGS, Slot.LEGS, 0, 5.0, 8, Rarity.UNCOMMON);
        add("knight_boots", "Knight's Boots", Material.IRON_BOOTS, Slot.FEET, 0, 2.5, 8, Rarity.UNCOMMON);

        add("arcane_crown", "Arcane Crown", Material.DIAMOND_HELMET, Slot.HEAD, 0, 5.0, 16, Rarity.RARE);
        add("arcane_vest", "Arcane Vest", Material.DIAMOND_CHESTPLATE, Slot.CHEST, 0, 8.5, 16, Rarity.RARE);
        add("arcane_leggings", "Arcane Leggings", Material.DIAMOND_LEGGINGS, Slot.LEGS, 0, 7.0, 16, Rarity.RARE);
        add("arcane_boots", "Arcane Boots", Material.DIAMOND_BOOTS, Slot.FEET, 0, 4.0, 16, Rarity.RARE);

        add("bulwark_shield", "Bulwark Shield", Material.SHIELD, Slot.OTHER, 0, 5.0, 10, Rarity.UNCOMMON);

        add("apple", "Apple", Material.APPLE, Slot.HAND, 0, 0, 1, Rarity.COMMON);
        add("bread", "Bread", Material.BREAD, Slot.HAND, 0, 0, 1, Rarity.COMMON);
        add("baked_potato", "Baked Potato", Material.BAKED_POTATO, Slot.HAND, 0, 0, 1, Rarity.COMMON);
        add("cooked_chicken", "Cooked Chicken", Material.COOKED_CHICKEN, Slot.HAND, 0, 0, 1, Rarity.COMMON);
        add("cooked_beef", "Cooked Beef", Material.COOKED_BEEF, Slot.HAND, 0, 0, 1, Rarity.COMMON);
        add("cooked_mutton", "Cooked Mutton", Material.COOKED_MUTTON, Slot.HAND, 0, 0, 1, Rarity.COMMON);
        add("cooked_salmon", "Cooked Salmon", Material.COOKED_SALMON, Slot.HAND, 0, 0, 1, Rarity.COMMON);
        add("golden_apple", "Golden Apple", Material.GOLDEN_APPLE, Slot.HAND, 0, 0, 5, Rarity.RARE);

        add("teleport_crystal", "Teleport Crystal", Material.END_CRYSTAL, Slot.OTHER, 0, 0, 10, Rarity.RARE);
    }

    private static final Map<String, Long> FOOD_PRICES = Map.of(
            "apple", 15L, "bread", 20L, "baked_potato", 20L,
            "cooked_chicken", 30L, "cooked_beef", 45L,
            "cooked_mutton", 40L, "cooked_salmon", 35L, "golden_apple", 200L);

    public static Long foodPrice(String id) {
        return FOOD_PRICES.get(id);
    }

    public static Def get(String id) {
        return DEFS.get(id);
    }

    public static Map<String, Def> all() {
        return DEFS;
    }

    public static Def resolve(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;
        String id = meta.getPersistentDataContainer().get(KEY_ITEM, PersistentDataType.STRING);
        return id == null ? null : DEFS.get(id);
    }

    public static ItemStack create(String id, int count) {
        Def def = DEFS.get(id);
        if (def == null) return null;
        ItemStack stack = new ItemStack(def.material(), Math.max(1, count));
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return stack;

        meta.displayName(Component.text(def.name(), def.rarity().color)
                .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Aincrad " + def.rarity().name(), def.rarity().color)
                .decoration(TextDecoration.ITALIC, false));
        if (def.damage() > 0) {
            lore.add(Component.text("Damage: " + fmt(def.damage()), NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
        }
        if (def.defense() > 0) {
            lore.add(Component.text("Defense: " + fmt(def.defense()), NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
        }
        if (def.id().equals("teleport_crystal")) {
            lore.add(Component.text("Right-click: teleport to this floor's spawn (60 MP)", NamedTextColor.AQUA)
                    .decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.text("Requires Lv " + def.levelReq(), NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(KEY_ITEM, PersistentDataType.STRING, def.id());
        stack.setItemMeta(meta);
        return stack;
    }

    public static List<String> idsForLevel(int level) {
        List<String> out = new ArrayList<>();
        for (Def d : DEFS.values()) {
            if (d.levelReq() <= level) out.add(d.id());
        }
        return out;
    }

    public static String fancy(String id) {
        Def d = DEFS.get(id);
        if (d == null) return id;
        return LegacyComponentSerializer.legacySection().serialize(
                Component.text(d.name(), d.rarity().color));
    }

    private static String fmt(double v) {
        return v == Math.floor(v) ? String.valueOf((int) v) : String.format(Locale.ROOT, "%.1f", v);
    }
}
