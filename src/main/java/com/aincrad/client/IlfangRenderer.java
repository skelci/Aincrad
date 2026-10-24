package com.aincrad.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Ravager;

public class IlfangRenderer extends MobRenderer<Ravager, RavagerRenderState, IlfangModel> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("aincrad", "textures/entity/ilfang");

    public IlfangRenderer(EntityRendererProvider.Context context) {
        super(context, new IlfangModel(), 1.4F);
    }

    @Override
    public RavagerRenderState createRenderState() {
        return new RavagerRenderState();
    }

    @Override
    public Identifier getTextureLocation(RavagerRenderState state) {
        return TEXTURE;
    }

    @Override
    protected float getShadowRadius(RavagerRenderState state) {
        return 1.7F;
    }
}
