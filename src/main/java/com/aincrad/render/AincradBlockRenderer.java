package com.aincrad.render;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.world.level.block.Block;

/**
 * Registers block render layers for Aincrad blocks.
 * Floor portal uses cutout layer for transparency effects.
 */
public class AincradBlockRenderer {

    /**
     * Registers render layers for Aincrad blocks.
     * Call after block registration is complete.
     *
     * @param floorPortalBlock the floor portal block instance
     */
    public static void registerAll(Block floorPortalBlock) {
        // Floor portal uses cutout render layer for transparency
        if (floorPortalBlock != null) {
            BlockRenderLayerMap.INSTANCE.putBlock(floorPortalBlock, RenderLayer.getCutout());
        }
    }

    /**
     * Registers a single block's render layer.
     */
    public static void registerBlockLayer(Block block, RenderLayer layer) {
        if (block != null) {
            BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
        }
    }
}
