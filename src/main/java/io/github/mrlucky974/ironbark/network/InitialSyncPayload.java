package io.github.mrlucky974.ironbark.network;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record InitialSyncPayload(int coins) implements CustomPayload {
    public static final Id<InitialSyncPayload> ID = new Id<>(Ironbark.id("initial_sync"));

    public static final PacketCodec<PacketByteBuf, InitialSyncPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, InitialSyncPayload::coins,
            InitialSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
