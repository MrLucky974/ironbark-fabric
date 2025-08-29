package io.github.mrlucky974.ironbark.spice;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;

public class SpiceEffect {
    public static final int DEFAULT_DURATION = 60;

    public static final Codec<SpiceEffect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(SpiceEffect::getEffect),
                    Codec.INT.lenientOptionalFieldOf("duration", DEFAULT_DURATION).forGetter(SpiceEffect::getDuration)
            ).apply(instance, SpiceEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, SpiceEffect> PACKET_CODEC =
            PacketCodec.tuple(
                    StatusEffect.ENTRY_PACKET_CODEC, SpiceEffect::getEffect,
                    PacketCodecs.VAR_INT, SpiceEffect::getDuration,
                    SpiceEffect::new
            );

    private final RegistryEntry<StatusEffect> effect;
    private final int duration;

    private SpiceEffect(RegistryEntry<StatusEffect> effect, int duration) {
        this.effect = effect;
        this.duration = duration;
    }

    public static SpiceEffect of(RegistryEntry<StatusEffect> effect, int duration) {
        return new SpiceEffect(effect, duration);
    }

    public static SpiceEffect of(RegistryEntry<StatusEffect> effect) {
        return new SpiceEffect(effect, DEFAULT_DURATION);
    }

    public RegistryEntry<StatusEffect> getEffect() {
        return this.effect;
    }

    public int getDuration() {
        return this.duration;
    }

    public StatusEffectInstance createStatusEffectInstance() {
        return new StatusEffectInstance(this.effect, this.duration);
    }
}
