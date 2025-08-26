package io.github.mrlucky974.ironbark.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public record SpiceEffectsComponent(List<SpiceEffect> effects) {
    public static final Codec<SpiceEffectsComponent> CODEC = SpiceEffect.CODEC.listOf()
            .xmap(SpiceEffectsComponent::new, SpiceEffectsComponent::effects);
    public static final PacketCodec<RegistryByteBuf, SpiceEffectsComponent> PACKET_CODEC = SpiceEffect.PACKET_CODEC.collect(PacketCodecs.toList())
            .xmap(SpiceEffectsComponent::new, SpiceEffectsComponent::effects);

    public static record SpiceEffect(RegistryEntry<StatusEffect> effect, int duration) {
        public static final int DEFAULT_DURATION = 160;

        public static final Codec<SpiceEffect> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(SpiceEffect::effect),
                        Codec.INT.lenientOptionalFieldOf("duration", DEFAULT_DURATION).forGetter(SpiceEffect::duration)
                ).apply(instance, SpiceEffect::new)
        );

        public static final PacketCodec<RegistryByteBuf, SpiceEffect> PACKET_CODEC =
                PacketCodec.tuple(
                        StatusEffect.ENTRY_PACKET_CODEC, SpiceEffect::effect,
                        PacketCodecs.VAR_INT, SpiceEffect::duration,
                        SpiceEffect::new
                );

        public StatusEffectInstance createStatusEffectInstance() {
            return new StatusEffectInstance(this.effect, this.duration);
        }
    }
}
