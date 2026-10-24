package com.aincrad.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record StatsPayload(int level, double mana, double maxMana, long col) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("aincrad", "stats");
    public static final Type<StatsPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, StatsPayload> CODEC =
            StreamCodec.of(StatsPayload::write, StatsPayload::read);

    private static void write(RegistryFriendlyByteBuf buf, StatsPayload p) {
        buf.writeInt(p.level);
        buf.writeDouble(p.mana);
        buf.writeDouble(p.maxMana);
        buf.writeLong(p.col);
    }

    private static StatsPayload read(RegistryFriendlyByteBuf buf) {
        return new StatsPayload(buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readLong());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
