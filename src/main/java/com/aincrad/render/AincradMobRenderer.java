package com.aincrad.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

/**
 * Vanilla-style mob renderer with floor-specific tint.
 * Each mob type uses its own texture from aincrad/textures/entity/.
 */
public class AincradMobRenderer extends MobRenderer<MobEntity, EntityModel<MobEntity>> {

    private final String mobType;
    private final float[] tint;

    public AincradMobRenderer(EntityRendererFactory.Context context, String mobType) {
        super(context, new SimpleEntityModel(), 0.5f);
        this.mobType = mobType;
        this.tint = computeMobTint(mobType);
    }

    @Override
    public Identifier getTexture(MobEntity entity) {
        return Identifier.of("aincrad", "textures/entity/" + mobType + ".png");
    }

    /**
     * Returns the RGB tint for this mob type.
     * Used by the renderer to apply a colored overlay.
     */
    public float[] getTint() {
        return tint;
    }

    public String getMobType() {
        return mobType;
    }

    /**
     * Computes a distinct tint color for each mob type.
     */
    private static float[] computeMobTint(String mobType) {
        int hash = mobType.hashCode();
        float hue = (hash * 137.5f) % 360f; // golden angle for spread
        return hsvToRgb(hue, 0.5f, 0.8f);
    }

    private static float[] hsvToRgb(float h, float s, float v) {
        int hi = (int) (h / 60) % 6;
        float f = h / 60 - hi;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);
        return switch (hi) {
            case 0 -> new float[]{v, t, p};
            case 1 -> new float[]{q, v, p};
            case 2 -> new float[]{p, v, t};
            case 3 -> new float[]{p, q, v};
            case 4 -> new float[]{t, p, v};
            default -> new float[]{v, p, q};
        };
    }

    /**
     * Minimal empty entity model for placeholder rendering.
     */
    private static class SimpleEntityModel extends EntityModel<MobEntity> {
        @Override
        public void animateModel(MobEntity entity, float limbAngle, float limbDistance, float tickProgress) {}

        @Override
        public void setAngles(MobEntity entity, float limbAngle, float limbDistance,
                              float animationProgress, float headYaw, float headPitch) {}

        @Override
        public void render(net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
                           net.minecraft.client.render.VertexConsumer vertexConsumer,
                           int light, int overlay, int color) {}
    }
}
