package com.aincrad.render;

import net.minecraft.world.item.ItemStack;

public final class AincradItemRenderer {
    private AincradItemRenderer() {}

    public static void register() {
        // Client-side: register item color providers for rarity colors
    }

    public static int getRarityColor(ItemStack stack) {
        return 0xFFFFFF;
    }
}
