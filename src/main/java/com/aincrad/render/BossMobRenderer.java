package com.aincrad.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

/**
 * Custom boss renderer with floor-specific tint overlay.
 * Each floor gets a distinct hue based on its number.
 */
public class BossMobRenderer extends MobRenderer<MobEntity, EntityModel<MobEntity>> {

    private final int floor;
    private final float[] tint;

    public BossMobRenderer(EntityRendererFactory.Context context, int floor) {
        super(context, new SimpleEntityModel(), 0.5f);
        this.floor = floor;
        this.tint = computeFloorTint(floor);
    }

    @Override
    public Identifier getTexture(MobEntity entity) {
        return Identifier.of("aincrad", "textures/entity/boss_floor_" + floor + ".png");
    }

    /**
     * Returns the RGB tint for this floor's boss.
     * Used by the renderer to apply a colored overlay.
     */
    public float[] getTint() {
        return tint;
    }

    public int getFloor() {
        return floor;
    }

    /**
     * Computes a distinct tint color for each floor using HSV.
     * Hue rotates 3.6 degrees per floor, covering the full spectrum every 100 floors.
     */
    private static float[] computeFloorTint(int floor) {
        float hue = (floor * 3.6f) % 360f;
        return hsvToRgb(hue, 0.7f, 0.9f);
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
     * Replace with actual boss model when available.
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
