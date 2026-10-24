package com.aincrad.client;

import com.aincrad.network.VfxPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.world.entity.monster.Ravager;

import java.util.concurrent.ThreadLocalRandom;

public final class BossFx {

    public static final long ANIM_MS = 900L;
    public static volatile long animUntilMs;
    public static volatile int animType;

    private static int auraTick;

    private BossFx() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(VfxPayload.TYPE, VfxPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(VfxPayload.TYPE,
                (payload, context) -> handle(payload));
        ClientTickEvents.END_CLIENT_TICK.register(BossFx::aura);
    }

    public static float attackProgress() {
        long left = animUntilMs - System.currentTimeMillis();
        if (left <= 0L) {
            return -1.0F;
        }
        return 1.0F - left / (float) ANIM_MS;
    }

    private static void handle(VfxPayload p) {
        if (p.effect() < 9) {
            animType = p.effect();
            animUntilMs = System.currentTimeMillis() + ANIM_MS;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        double x = p.x(), y = p.y(), z = p.z();
        ParticleOptions flash = ColorParticleOption.create(ParticleTypes.FLASH, 0xFFFFFFFF);
        ParticleOptions breath = PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.6F);
        DustParticleOptions crimson = new DustParticleOptions(0xFFB22222, 1.1F);
        DustParticleOptions voidDust = new DustParticleOptions(0xFF1A0022, 1.6F);
        DustParticleOptions gold = new DustParticleOptions(0xFFFFD700, 1.0F);
        switch (p.effect()) {
            case 0 -> {
                burst(flash, x, y, z, 1, 0);
                ring(crimson, x, y + 0.2, z, 2.2, 20);
                burst(ParticleTypes.EXPLOSION_EMITTER, x, y + 1, z, 2, 0.6);
            }
            case 1 -> {
                burst(ParticleTypes.SONIC_BOOM, x, y + 1.3, z, 4, 1.2);
                burst(ParticleTypes.CRIT, x, y + 1.2, z, 24, 1.4);
                burst(ParticleTypes.ENCHANTED_HIT, x, y + 1.2, z, 16, 1.2);
            }
            case 2 -> {
                burst(breath, x, y + 1.2, z, 50, 4.0);
                burst(voidDust, x, y + 1.0, z, 40, 5.0);
                burst(ParticleTypes.SOUL, x, y + 1.4, z, 14, 3.0);
            }
            case 3 -> {
                burst(flash, x, y, z, 1, 0);
                ring(crimson, x, y + 0.15, z, 9.0, 48);
                ring(crimson, x, y + 0.15, z, 6.0, 32);
            }
            case 4 -> {
                burst(ParticleTypes.PORTAL, x, y + 1, z, 30, 0.8);
                burst(ParticleTypes.END_ROD, x, y + 1, z, 12, 0.8);
            }
            case 9 -> {
                burst(flash, x, y, z, 1, 0);
                burst(ParticleTypes.SOUL_FIRE_FLAME, x, y + 1.6, z, 70, 1.2);
                for (int i = 0; i < 3; i++) {
                    burst(ParticleTypes.EXPLOSION_EMITTER, x, y + 1, z, 1, 0.4);
                }
                ring(gold, x, y + 0.3, z, 3.0, 26);
            }
            default -> {
            }
        }
    }

    private static void burst(ParticleOptions type, double x, double y, double z, int count, double spread) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            mc.level.addParticle(type,
                    x + rnd.nextDouble(-spread, spread),
                    y + rnd.nextDouble(0, Math.max(spread, 0.1)),
                    z + rnd.nextDouble(-spread, spread),
                    0.0, i == 0 ? 0.0 : 0.02, 0.0);
        }
    }

    private static void ring(ParticleOptions type, double cx, double cy, double cz, double radius, int points) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        for (int i = 0; i < points; i++) {
            double a = i * (Math.PI * 2 / points);
            mc.level.addParticle(type, cx + Math.cos(a) * radius, cy, cz + Math.sin(a) * radius,
                    0.0, 0.0, 0.0);
        }
    }

    private static void aura(Minecraft client) {
        if (client.level == null || client.player == null) {
            return;
        }
        if (++auraTick % 3 != 0) {
            return;
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (Ravager r : client.level.getEntitiesOfClass(Ravager.class,
                client.player.getBoundingBox().inflate(24.0))) {
            double x = r.getX() + rnd.nextDouble(-0.8, 0.8);
            double y = r.getY() + rnd.nextDouble(0.0, 2.4);
            double z = r.getZ() + rnd.nextDouble(-0.8, 0.8);
            client.level.addParticle(new DustParticleOptions(0xFF8B0000, 0.9F), x, y, z, 0, 0.03, 0);
            if (rnd.nextBoolean()) {
                client.level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0, 0.05, 0);
            }
        }
    }
}
