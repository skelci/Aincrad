package com.aincrad.render;

public final class AincradRenderers {
    private AincradRenderers() {}

    public static void registerAll() {
        BossMobRenderer.register();
        AincradMobRenderer.register();
        AincradItemRenderer.register();
        AincradBlockRenderer.register();
    }
}
