package com.aincrad.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SkillCastPayload(int slot) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("aincrad", "skill_cast");
    public static final CustomPacketPayload.Type<SkillCastPayload> TYPE =
            new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillCastPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeByte(payload.slot()),
                    buf -> new SkillCastPayload(buf.readByte()));

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(TYPE, CODEC);
    }

    public static void send(int slot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;
        ClientPlayNetworking.send(new SkillCastPayload(slot));
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
