package io.github.mrlucky974.ironbark.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class CoinContainerComponent
{
    public static final Codec<CoinContainerComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("amount").forGetter(CoinContainerComponent::getAmount))
            .apply(instance, CoinContainerComponent::new));

    public static final PacketCodec<RegistryByteBuf, CoinContainerComponent> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, CoinContainerComponent::getAmount,
                    CoinContainerComponent::new
            );

    private int amount;

    private CoinContainerComponent(int amount) {
        this.amount = amount;
    }

    public static CoinContainerComponent create(int amount) {
        return new CoinContainerComponent(amount);
    }

    public int getAmount() {
        return this.amount;
    }

    public void add(int toAdd) {
        this.amount += toAdd;
    }

    public boolean remove(int toRemove) {
        if (this.amount < toRemove) return false;
        this.amount -= toRemove;
        return true;
    }
}
