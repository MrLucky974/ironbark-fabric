package io.github.mrlucky974.ironbark.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusEffectMerger {
    public enum DurationMergeStrategy {
        MAX_DURATION,
        MIN_DURATION,
        COMBINE_DURATION,
    }

    public static List<StatusEffectInstance> mergeEffects(List<StatusEffectInstance> effects) {
        return mergeEffects(effects, DurationMergeStrategy.MAX_DURATION);
    }

    public static List<StatusEffectInstance> mergeEffects(List<StatusEffectInstance> effects, DurationMergeStrategy durationMergeStrategy) {
        Map<StatusEffect, StatusEffectInstance> mergedMap = new HashMap<>();

        for (StatusEffectInstance effect : effects) {
            StatusEffect type = effect.getEffectType().value();

            if (mergedMap.containsKey(type)) {
                // Merge with existing effect
                mergedMap.computeIfPresent(type, (k, existing) -> mergeInstances(existing, effect, durationMergeStrategy));
            } else {
                // Add new effect
                mergedMap.put(type, effect);
            }
        }

        return new ArrayList<>(mergedMap.values());
    }

    private static StatusEffectInstance mergeInstances(StatusEffectInstance first, StatusEffectInstance second, DurationMergeStrategy durationMergeStrategy) {
        int duration = switch (durationMergeStrategy) {
            case MIN_DURATION -> Math.min(first.getDuration(), second.getDuration());
            case COMBINE_DURATION -> first.getDuration() + second.getDuration();
            default -> Math.max(first.getDuration(), second.getDuration());
        };

        int amplifier = Math.max(first.getAmplifier(), second.getAmplifier());
        boolean ambient = first.isAmbient() || second.isAmbient();
        boolean showParticles = first.shouldShowParticles() || second.shouldShowParticles();
        boolean showIcon = first.shouldShowIcon() || second.shouldShowIcon();

        return new StatusEffectInstance(
                first.getEffectType(),
                duration,
                amplifier,
                ambient,
                showParticles,
                showIcon
        );
    }
}
