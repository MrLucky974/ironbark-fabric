package io.github.mrlucky974.ironbark.component;

import com.mojang.serialization.Codec;
import io.github.mrlucky974.ironbark.spice.Spice;
import io.github.mrlucky974.ironbark.spice.SpiceEffect;
import io.github.mrlucky974.ironbark.util.StatusEffectMerger;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public record SpiceContainerComponent(List<Spice> spices) {
    public static final Codec<SpiceContainerComponent> CODEC = Spice.CODEC.listOf()
            .xmap(SpiceContainerComponent::new, SpiceContainerComponent::spices);

    public static final PacketCodec<RegistryByteBuf, SpiceContainerComponent> PACKET_CODEC = Spice.PACKET_CODEC.collect(PacketCodecs.toList())
            .xmap(SpiceContainerComponent::new, SpiceContainerComponent::spices);

    public List<StatusEffectInstance> createStatusEffectInstances() {
        List<StatusEffectInstance> statusEffectInstances = new ArrayList<>();
        for (Spice spice : this.spices) {
            for (SpiceEffect effect : spice.getEffects()) {
                statusEffectInstances.add(effect.createStatusEffectInstance());
            }
        }

        return StatusEffectMerger.mergeEffects(statusEffectInstances, StatusEffectMerger.DurationMergeStrategy.COMBINE_DURATION);
    }

    public void apply(LivingEntity user) {
        List<StatusEffectInstance> statusEffectInstances = createStatusEffectInstances();
        statusEffectInstances.forEach(user::addStatusEffect);
    }
}
