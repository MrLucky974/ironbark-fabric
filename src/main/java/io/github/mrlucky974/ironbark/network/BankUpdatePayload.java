package io.github.mrlucky974.ironbark.network;

import io.github.mrlucky974.ironbark.Ironbark;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record BankUpdatePayload(int coins) implements CustomPayload {
    public static final Id<BankUpdatePayload> ID = new Id<>(Ironbark.id("coins_changed"));

    public static final PacketCodec<PacketByteBuf, BankUpdatePayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, BankUpdatePayload::coins,
            BankUpdatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}