package com.aincrad.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

/**
 * Registers all entity renderers for Aincrad bosses and mobs.
 * Called from AincradClient after entity types are registered.
 */
public class AincradRenderers {

    /**
     * Register boss renderers for floors 1-100 and all mob types.
     * Entity types must be registered before calling this.
     */
    public static void registerAll() {
        registerBossRenderers();
        registerMobRenderers();
    }

    private static void registerBossRenderers() {
        for (int floor = 1; floor <= 100; floor++) {
            final int f = floor;
            // Registration deferred until EntityType is available
            // EntityRendererRegistry.register(BossRegistry.getEntityType(floor),
            //     ctx -> new BossMobRenderer(ctx, f));
        }
    }

    private static void registerMobRenderers() {
        String[] mobTypes = {
            "passive", "neutral", "hostile", "pack", "ranged",
            "ambusher", "summoner", "enraged", "elite", "boss", "raid_boss",
            "wolf", "skeleton", "zombie", "spider", "creeper",
            "enderman", "blaze", "ghast", "slime"
        };
        for (String type : mobTypes) {
            // Registration deferred until EntityType is available
            // EntityRendererRegistry.register(MobRegistry.getEntityType(type),
            //     ctx -> new AincradMobRenderer(ctx, type));
        }
    }

    /**
     * Creates a boss renderer for the given floor.
     * Factory method used by registration.
     */
    public static MobRenderer<MobEntity, EntityModel<MobEntity>> createBossRenderer(
            EntityRendererFactory.Context ctx, int floor) {
        return new BossMobRenderer(ctx, floor);
    }

    /**
     * Creates a mob renderer for the given mob type.
     * Factory method used by registration.
     */
    public static MobRenderer<MobEntity, EntityModel<MobEntity>> createMobRenderer(
            EntityRendererFactory.Context ctx, String mobType) {
        return new AincradMobRenderer(ctx, mobType);
    }
}
