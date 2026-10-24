package com.aincrad.client;

import com.aincrad.network.StatsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class HudStats {

    public static volatile int level = 1;
    public static volatile double mana = 1.0;
    public static volatile double maxMana = 1.0;
    public static volatile long col = 0L;

    private HudStats() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(StatsPayload.TYPE, StatsPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(StatsPayload.TYPE, (payload, context) -> apply(payload));
    }

    public static double manaRatio() {
        return maxMana <= 0 ? 0 : Math.max(0, Math.min(1, mana / maxMana));
    }

    public static String manaText() {
        return (int) Math.round(mana) + "/" + (int) Math.round(maxMana);
    }

    public static void apply(StatsPayload p) {
        level = Math.max(1, p.level());
        mana = p.mana();
        maxMana = p.maxMana();
        col = p.col();
    }
}
