package com.aincrad.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.util.Mth;

public class IlfangModel extends EntityModel<RavagerRenderState> {

    private final ModelPart torso;
    private final ModelPart belt;
    private final ModelPart legL;
    private final ModelPart legR;
    private final ModelPart pauldL;
    private final ModelPart pauldR;
    private final ModelPart armL;
    private final ModelPart armR;
    private final ModelPart foreL;
    private final ModelPart foreR;
    private final ModelPart sword;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart snout;
    private final ModelPart jaw;
    private final ModelPart tuskL;
    private final ModelPart tuskR;
    private final ModelPart hornL;
    private final ModelPart hornR;
    private final ModelPart eyeL;
    private final ModelPart eyeR;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart cape;

    public IlfangModel() {
        super(build());
        this.torso = root().getChild("torso");
        this.belt = torso.getChild("belt");
        this.legL = root().getChild("legL");
        this.legR = root().getChild("legR");
        this.pauldL = torso.getChild("pauldL");
        this.pauldR = torso.getChild("pauldR");
        this.armL = torso.getChild("armL");
        this.armR = torso.getChild("armR");
        this.foreL = armL.getChild("foreL");
        this.foreR = armR.getChild("foreR");
        this.sword = foreR.getChild("sword");
        this.neck = torso.getChild("neck");
        this.head = neck.getChild("head");
        this.snout = head.getChild("snout");
        this.jaw = head.getChild("jaw");
        this.tuskL = jaw.getChild("tuskL");
        this.tuskR = jaw.getChild("tuskR");
        this.hornL = head.getChild("hornL");
        this.hornR = head.getChild("hornR");
        this.eyeL = head.getChild("eyeL");
        this.eyeR = head.getChild("eyeR");
        this.tail1 = torso.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.cape = torso.getChild("cape");
    }

    private static ModelPart build() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();

        r.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(0, 36)
                .addBox(-2.5F, 0.0F, -3.0F, 5.0F, 12.0F, 6.0F),
                PartPose.offset(3.2F, 12.0F, 0.0F));
        r.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(0, 36)
                .addBox(-2.5F, 0.0F, -3.0F, 5.0F, 12.0F, 6.0F),
                PartPose.offset(-3.2F, 12.0F, 0.0F));

        PartDefinition torso = r.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.0F, -14.0F, -4.0F, 10.0F, 14.0F, 8.0F),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        torso.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(0, 22)
                .addBox(-5.0F, 0.0F, -4.0F, 10.0F, 5.0F, 8.0F),
                PartPose.offset(0.0F, -0.5F, 0.0F));

        torso.addOrReplaceChild("pauldL", CubeListBuilder.create().texOffs(60, 36)
                .addBox(-3.5F, -3.5F, -4.0F, 7.0F, 7.0F, 8.0F),
                PartPose.offset(6.0F, -11.0F, 0.0F));
        torso.addOrReplaceChild("pauldR", CubeListBuilder.create().texOffs(60, 36)
                .addBox(-3.5F, -3.5F, -4.0F, 7.0F, 7.0F, 8.0F),
                PartPose.offset(-6.0F, -11.0F, 0.0F));

        PartDefinition armL = torso.addOrReplaceChild("armL", CubeListBuilder.create().texOffs(24, 36)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F),
                PartPose.offset(6.5F, -10.0F, 0.0F));
        armL.addOrReplaceChild("foreL", CubeListBuilder.create().texOffs(42, 36)
                .addBox(-1.75F, 0.0F, -1.75F, 3.5F, 8.0F, 3.5F),
                PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition armR = torso.addOrReplaceChild("armR", CubeListBuilder.create().texOffs(24, 36)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F),
                PartPose.offset(-6.5F, -10.0F, 0.0F));
        PartDefinition foreR = armR.addOrReplaceChild("foreR", CubeListBuilder.create().texOffs(42, 36)
                .addBox(-1.75F, 0.0F, -1.75F, 3.5F, 8.0F, 3.5F),
                PartPose.offset(0.0F, 9.0F, 0.0F));

        PartDefinition sword = foreR.addOrReplaceChild("sword", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 7.5F, 0.0F, -1.4F, 0.0F, 0.0F));
        sword.addOrReplaceChild("hilt", CubeListBuilder.create().texOffs(64, 72)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        sword.addOrReplaceChild("guard", CubeListBuilder.create().texOffs(74, 72)
                .addBox(-3.5F, -2.0F, -1.5F, 7.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        sword.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(86, 72)
                .addBox(-1.5F, -16.0F, -0.5F, 3.0F, 16.0F, 1.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));
        sword.addOrReplaceChild("pommel", CubeListBuilder.create().texOffs(96, 72)
                .addBox(-1.0F, 6.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 56)
                .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -13.0F, -2.5F, -0.45F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(20, 56)
                .addBox(-3.5F, -4.0F, -4.0F, 7.0F, 7.0F, 8.0F),
                PartPose.offset(0.0F, -1.5F, -1.0F));
        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(52, 56)
                .addBox(-2.5F, -1.5F, -4.0F, 5.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 1.0F, -2.5F));
        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(72, 56)
                .addBox(-2.0F, 0.0F, -4.0F, 4.0F, 2.0F, 4.0F),
                PartPose.offset(0.0F, 2.0F, -2.5F));
        jaw.addOrReplaceChild("tuskL", CubeListBuilder.create().texOffs(90, 56)
                .addBox(-0.5F, -2.5F, -0.5F, 1.0F, 2.5F, 1.0F),
                PartPose.offsetAndRotation(1.4F, 0.5F, -3.4F, 0.35F, 0.0F, 0.0F));
        jaw.addOrReplaceChild("tuskR", CubeListBuilder.create().texOffs(90, 56)
                .addBox(-0.5F, -2.5F, -0.5F, 1.0F, 2.5F, 1.0F),
                PartPose.offsetAndRotation(-1.4F, 0.5F, -3.4F, 0.35F, 0.0F, 0.0F));
        head.addOrReplaceChild("hornL", CubeListBuilder.create().texOffs(96, 56)
                .addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(2.8F, -3.2F, 1.0F, -0.55F, 0.0F, -0.45F));
        head.addOrReplaceChild("hornR", CubeListBuilder.create().texOffs(96, 56)
                .addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(-2.8F, -3.2F, 1.0F, -0.55F, 0.0F, 0.45F));
        head.addOrReplaceChild("eyeL", CubeListBuilder.create().texOffs(0, 120)
                .addBox(-1.0F, -0.6F, -0.6F, 2.0F, 1.2F, 1.2F),
                PartPose.offset(1.7F, -0.5F, -6.3F));
        head.addOrReplaceChild("eyeR", CubeListBuilder.create().texOffs(0, 120)
                .addBox(-1.0F, -0.6F, -0.6F, 2.0F, 1.2F, 1.2F),
                PartPose.offset(-1.7F, -0.5F, -6.3F));

        PartDefinition tail1 = torso.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 72)
                .addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -3.0F, 3.5F, 0.55F, 0.0F, 0.0F));
        tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(22, 72)
                .addBox(-1.2F, -1.2F, 0.0F, 2.4F, 2.4F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.5F, -0.45F, 0.0F, 0.0F));

        torso.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(40, 72)
                .addBox(-5.0F, 0.0F, -1.0F, 10.0F, 13.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -13.0F, 4.0F, 0.18F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128).bakeRoot();
    }

    @Override
    public void setupAnim(RavagerRenderState state) {
        float t = state.ageInTicks;
        float move = Mth.clamp(state.walkAnimationSpeed, 0.0F, 1.0F);
        float pos = state.walkAnimationPos;
        float swing = Mth.sin(pos * 0.7F) * 0.85F * move;
        float breathe = Mth.sin(t * 0.12F) * 0.03F;

        legL.xRot = swing - 0.06F;
        legR.xRot = -swing - 0.06F;

        torso.xRot = 0.12F + breathe;
        torso.yRot = -swing * 0.1F;
        torso.zRot = 0.0F;

        head.xRot = -0.12F + Mth.sin(t * 0.1F) * 0.05F;
        head.yRot = Mth.sin(t * 0.07F) * 0.12F;
        jaw.xRot = 0.06F + Mth.sin(t * 0.15F) * 0.03F;

        tail1.yRot = Mth.sin(t * 0.14F) * 0.3F;
        tail2.yRot = Mth.sin(t * 0.14F - 0.6F) * 0.35F;
        cape.xRot = 0.18F + Mth.sin(t * 0.13F) * 0.07F + move * 0.25F;

        armL.xRot = -swing * 0.6F - 0.15F;
        armR.xRot = swing * 0.6F - 0.15F;
        foreL.xRot = -0.3F;
        foreR.xRot = -0.3F;

        float atk = BossFx.attackProgress();
        if (atk >= 0.0F) {
            float e = atk * atk * (3.0F - 2.0F * atk);
            switch (BossFx.animType) {
                case 0 -> {
                    armL.xRot = -1.5F;
                    armR.xRot = -1.5F;
                    foreL.xRot = -0.1F;
                    foreR.xRot = -0.1F;
                    torso.xRot = 0.12F + 0.45F * e;
                    legL.xRot = 0.5F * e;
                    legR.xRot = 0.5F * e;
                }
                case 1 -> {
                    armR.yRot = 1.8F - 3.6F * e;
                    armR.xRot = -0.45F;
                    foreR.xRot = -0.15F;
                    torso.yRot = 0.45F - 0.9F * e;
                }
                case 2 -> {
                    armL.xRot = -2.6F * e - 0.15F;
                    armR.xRot = -2.6F * e - 0.15F;
                    torso.xRot = 0.12F - 0.4F * e;
                    head.xRot = -0.5F * e - 0.1F;
                    jaw.xRot = 0.55F * e;
                }
                case 3 -> {
                    float up = Math.min(atk * 2.2F, 1.0F);
                    float down = Mth.clamp((atk - 0.45F) / 0.55F, 0.0F, 1.0F);
                    armR.xRot = -2.9F * up + 4.6F * down * down;
                    foreR.xRot = -0.4F + 0.3F * down;
                    torso.xRot = 0.12F - 0.5F * up + 0.9F * down * down;
                }
                case 4 -> {
                    armL.xRot = -1.8F;
                    armR.xRot = -1.8F;
                    torso.xRot = 0.12F - 0.2F * (1.0F - atk);
                }
                case 9 -> {
                    armL.xRot = -2.8F * e;
                    armR.xRot = -2.8F * e;
                    torso.xRot = 0.12F - 0.55F * e;
                    head.xRot = -0.7F * e;
                    jaw.xRot = 0.7F * e;
                    cape.xRot = 0.18F + 0.6F * e;
                }
                default -> {
                }
            }
        }
    }
}
