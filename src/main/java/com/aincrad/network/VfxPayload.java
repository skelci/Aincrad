package com.aincrad.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record VfxPayload(int effect, double x, double y, double z, float power) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("aincrad", "vfx");
    public static final Type<VfxPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, VfxPayload> CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        buf.writeByte(p.effect());
                        buf.writeDouble(p.x());
                        buf.writeDouble(p.y());
                        buf.writeDouble(p.z());
                        buf.writeFloat(p.power());
                    },
                    buf -> new VfxPayload(
                            buf.readByte(),
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readFloat()));

    @Override
    public Type<VfxPayload> type() {
        return TYPE;
    }
}
