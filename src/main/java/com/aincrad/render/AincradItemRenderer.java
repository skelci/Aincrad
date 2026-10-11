package com.aincrad.render;

import com.aincrad.item.AincradItem;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.ItemStack;

/**
 * Registers item color providers for Aincrad items.
 * Rarity-colored tooltips via ItemColorProvider.
 */
public class AincradItemRenderer {

    /**
     * Registers item color providers.
     * Called from AincradClient after items are registered.
     */
    public static void registerAll() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 0 && isAincradItem(stack)) {
                return getRarityColor(stack);
            }
            return 0xFFFFFF;
        });
    }

    /**
     * Returns the rarity color for an Aincrad item stack.
     */
    private static int getRarityColor(ItemStack stack) {
        // Use the stack's display name to determine rarity
        String name = stack.getHoverName().getString();
        if (name.contains("Mythic")) return 0xFF5555;
        if (name.contains("Legendary")) return 0xFFAA00;
        if (name.contains("Epic")) return 0xAA00AA;
        if (name.contains("Rare")) return 0x5555FF;
        if (name.contains("Uncommon")) return 0x55FF55;
        return 0xAAAAAA; // Common
    }

    private static boolean isAincradItem(ItemStack stack) {
        return stack.getItem() instanceof AincradItem ||
               stack.getItem().getCreatorModId(stack) != null &&
               stack.getItem().getCreatorModId(stack).contains("aincrad");
    }
}
