package io.github.mrlucky974.ironbark.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class CoinSackComponent
{
    public static final Codec<CoinSackComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("amount").forGetter(CoinSackComponent::getAmount))
            .apply(instance, CoinSackComponent::new));

    public static final PacketCodec<RegistryByteBuf, CoinSackComponent> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, CoinSackComponent::getAmount,
                    CoinSackComponent::new
            );

    private int amount;

    private CoinSackComponent(int amount) {
        this.amount = amount;
    }

    public static CoinSackComponent create(int amount) {
        return new CoinSackComponent(amount);
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
